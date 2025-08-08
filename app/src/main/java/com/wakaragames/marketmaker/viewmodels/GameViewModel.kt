package com.wakaragames.marketmaker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wakaragames.marketmaker.data.EventsDatabase
import com.wakaragames.marketmaker.data.UpgradesDatabase
import com.wakaragames.marketmaker.data.models.ActiveEffect
import com.wakaragames.marketmaker.data.models.CandleData
import com.wakaragames.marketmaker.data.models.GameEvent
import com.wakaragames.marketmaker.data.models.GameState
import com.wakaragames.marketmaker.data.models.NewsItem
import com.wakaragames.marketmaker.data.models.PlayerPortfolio
import com.wakaragames.marketmaker.data.models.TriggerCondition
import com.wakaragames.marketmaker.data.models.TriggerConditionType
import com.wakaragames.marketmaker.data.persistence.GameStateManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val _gameState = MutableStateFlow(createDefaultGameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    init {
        // Start generating candles as soon as the ViewModel is created.
        startCandleGeneration()
    }

    // --- PUBLIC ACTIONS (Events Up) ---

    fun performBuyTransaction() {
        val currentState = _gameState.value
        val currentPrice = currentState.candleHistory.lastOrNull()?.close?.toDouble() ?: return
        val cost = 100 * currentPrice

        if (currentState.playerPortfolio.cash >= cost) {
            _gameState.update {
                it.copy(
                    playerPortfolio = it.playerPortfolio.copy(
                        cash = it.playerPortfolio.cash - cost,
                        coins = it.playerPortfolio.coins + 100
                    )
                )
            }
        }
    }

    fun performSellTransaction() {
        val currentState = _gameState.value
        val currentPrice = currentState.candleHistory.lastOrNull()?.close?.toDouble() ?: return
        val revenue = 100 * currentPrice

        if (currentState.playerPortfolio.coins >= 100) {
            _gameState.update {
                it.copy(
                    playerPortfolio = it.playerPortfolio.copy(
                        cash = it.playerPortfolio.cash + revenue,
                        coins = it.playerPortfolio.coins - 100
                    )
                )
            }
        }
    }

    fun purchaseUpgrade(upgradeId: String) {
        val upgrade = UpgradesDatabase.allUpgrades[upgradeId] ?: return // Invalid ID
        val currentState = _gameState.value

        // Validation: Check cost and if already purchased
        if (currentState.playerPortfolio.cash < upgrade.cost) return
        if (currentState.purchasedUpgradeIds.contains(upgradeId)) return

        _gameState.update { state ->
            // Perform transaction
            val newPortfolio = state.playerPortfolio.copy(
                cash = state.playerPortfolio.cash - upgrade.cost
            )
            // Add to purchased set
            val newPurchasedIds = state.purchasedUpgradeIds + upgradeId
            // Add new active effect if it has a duration
            val newActiveEffects = if (upgrade.effectDurationInCandles > 0) {
                state.activeEffects + ActiveEffect(upgradeId, upgrade.effectDurationInCandles)
            } else {
                state.activeEffects
            }

            // TODO: Handle instant and probabilistic effects here

            state.copy(
                playerPortfolio = newPortfolio,
                purchasedUpgradeIds = newPurchasedIds,
                activeEffects = newActiveEffects
            )
        }
    }

    // --- SAVE / LOAD LOGIC ---

    fun loadGame() = viewModelScope.launch {
        val loadedState = GameStateManager.loadGameState(getApplication())
        if (loadedState != null) {
            _gameState.value = loadedState
        }
    }

    fun startNewGame() {
        GameStateManager.deleteSavedGame(getApplication())
        _gameState.value = createDefaultGameState()
    }

    fun saveGame() = viewModelScope.launch {
        GameStateManager.saveGameState(getApplication(), _gameState.value)
    }

    // --- PRIVATE LOGIC (MOVED FROM GAMESCREEN) ---

    private fun startCandleGeneration() = viewModelScope.launch {
        while (true) {
            delay(2000)

            processEffectsTick()
            checkForNewEvents()

            val dynamicGrowthRate = calculateCurrentGrowthRate()
            val lastClose = _gameState.value.candleHistory.lastOrNull()?.close ?: 1f
            val newCandle = generateNextCandle(lastClose, dynamicGrowthRate)

            _gameState.update { currentState ->
                val updatedHistory = (currentState.candleHistory + newCandle).takeLast(30)
                currentState.copy(
                    candleHistory = updatedHistory,
                    historicalCandleCount = currentState.historicalCandleCount + 1,
                    currentGrowthRate = dynamicGrowthRate
                )
            }
        }
    }

    private fun processEffectsTick() {
        _gameState.update { currentState ->
            if (currentState.activeEffects.isEmpty()) return@update currentState

            val updatedEffects = currentState.activeEffects
                .map { it.copy(durationRemainingInCandles = it.durationRemainingInCandles - 1) } // Decrement duration
                .filter { it.durationRemainingInCandles > 0 } // Remove expired effects

            currentState.copy(activeEffects = updatedEffects)
        }
    }

    private fun checkForNewEvents() {
        val currentState = _gameState.value

        EventsDatabase.allEvents.forEach { event ->
            // Skip one-time events that have already triggered
            if (event.isOneTime && currentState.triggeredOneTimeEventIds.contains(event.id)) {
                return@forEach
            }

            val conditionsMet = checkAllConditions(event.triggerConditions, currentState)

            if (conditionsMet) {
                if (Random.nextFloat() < event.triggerChance) {
                    // --- TRIGGER THE EVENT ---
                    triggerEvent(event)
                }
            }
        }
    }

    private fun checkAllConditions(conditions: List<TriggerCondition>, state: GameState): Boolean {
        if (conditions.isEmpty()) return false
        return conditions.all { condition ->
            when (condition.type) {
                TriggerConditionType.ALWAYS -> true
                TriggerConditionType.MARKET_CAP_ABOVE -> {
                    val marketCap = (state.candleHistory.lastOrNull()?.close ?: 0f) * 100_000_000 // Example: total coin supply
                    marketCap >= condition.value
                }
                TriggerConditionType.UPGRADE_IS_PURCHASED -> {
                    condition.stringValue != null && state.purchasedUpgradeIds.contains(condition.stringValue)
                }
                // TODO: Implement other condition checks (Hype, Interest Rate, etc.)
                else -> false
            }
        }
    }

    private fun triggerEvent(event: GameEvent) {
        _gameState.update { currentState ->
            // Add news to the feed
            val newNewsItem = NewsItem(eventId = event.id,event.headline, currentState.historicalCandleCount)
            val updatedNewsFeed = (listOf(newNewsItem) + currentState.newsFeed).take(50) // Keep feed to 50 items
            val dynamicGrowthRate = calculateCurrentGrowthRate()

            val lastClose = _gameState.value.candleHistory.lastOrNull()?.close ?: 1f
            val newCandle = generateNextCandle(lastClose, dynamicGrowthRate)
            // Add new active effect if it's not a dummy event
            val updatedEffects = if (event.isDummy || event.effectType == null) {
                currentState.activeEffects
            } else {
                // TODO: This needs to be expanded to handle different effect types
                currentState.activeEffects + ActiveEffect(event.id, event.effectDurationInCandles ?: 0)
            }

            // Add to triggered set if it's a one-time event
            val updatedOneTimeEvents = if (event.isOneTime) {
                currentState.triggeredOneTimeEventIds + event.id
            } else {
                currentState.triggeredOneTimeEventIds
            }

            currentState.copy(
                newsFeed = updatedNewsFeed,
                activeEffects = updatedEffects,
                triggeredOneTimeEventIds = updatedOneTimeEvents
            )
        }
    }

    private fun calculateCurrentGrowthRate(): Float {
        val currentState = _gameState.value
        val baseRate = 0.65f
        var modifiedRate = baseRate

        // This is where you apply modifiers from active effects
        currentState.activeEffects.forEach { effect ->
            // In the future, you'll look up the effect by its ID
            // For now, this is a placeholder for the PoS Consensus Update
            if (effect.upgradeId == "rd_pos_consensus") {
                modifiedRate *= 1.20f // Apply +20% bonus
            }
            // Add other "if" statements for other effects here
        }
        return modifiedRate
    }

    private fun generateNextCandle(previousClose: Float, baselineIncrease: Float): CandleData {
        val open = previousClose
        // It no longer defines its own baseline; it uses the one passed to it.
        val isGreen = Random.nextFloat() < 0.70f
        val isDramatic = Random.nextFloat() < 0.15f
        val dramaticMultiplier = 3.5f
        val normalMultiplier = 0.8f
        val change = baselineIncrease * if (isDramatic) (Random.nextFloat() - 0.3f) * dramaticMultiplier else (Random.nextFloat() - 0.45f) * normalMultiplier
        val close = if (isGreen) open + abs(change) else open - abs(change)
        val high = max(open, close) * (1 + Random.nextFloat() * 0.03f)
        val low = min(open, close) * (1 - Random.nextFloat() * 0.03f)
        return CandleData(open, high, low, max(0.1f, close))
    }

    private fun createDefaultGameState(): GameState {
        return GameState(
            playerPortfolio = PlayerPortfolio(cash = 2000.0, coins = 10000),
            candleHistory = listOf(CandleData(open = 1f, high = 1.03f, low = 0.97f, close = 1f)),
            historicalCandleCount = 1,
            purchasedUpgradeIds = emptySet(),
            activeEffects = emptyList(),
            newsFeed = emptyList(),
            triggeredOneTimeEventIds = emptySet(),
            currentGrowthRate = 0.65f
        )
    }


}