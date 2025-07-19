package com.wakaragames.marketmaker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.wakaragames.marketmaker.data.models.CandleData
import com.wakaragames.marketmaker.data.models.GameState
import com.wakaragames.marketmaker.data.models.PlayerPortfolio
import com.wakaragames.marketmaker.data.persistence.GameStateManager
import com.wakaragames.marketmaker.ui.components.MockPriceChart
import com.wakaragames.marketmaker.ui.theme.MarketMakerTheme
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

// A default state for starting a brand new game.
private val defaultGameState = GameState(
    playerPortfolio = PlayerPortfolio(cash = 20000.0, coins = 10000),
    candleHistory = listOf(CandleData(open = 1f, high = 1.03f, low = 0.97f, close = 1f))
)

/**
 * The main game screen where the player interacts with the price chart and portfolio.
 * This composable is now the state owner for the entire game session.
 */
@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    loadFromSave: Boolean // Decides whether to load a saved game or start fresh.
) {
    val context = LocalContext.current

    // The single source of truth for the entire game state.
    var gameState by remember {
        mutableStateOf(
            if (loadFromSave) {
                GameStateManager.loadGameState(context) ?: defaultGameState
            } else {
                defaultGameState
            }
        )
    }

    // --- AUTO-SAVE LOGIC ---
    // This effect observes the app's lifecycle and saves the game state
    // automatically when the app goes into the background (ON_PAUSE).
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                GameStateManager.saveGameState(context, gameState)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        // Clean up the observer when the composable is disposed.
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // --- CANDLE GENERATION LOGIC ---
    // This effect runs continuously to generate new candles and update the game state.
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            val lastClose = gameState.candleHistory.last().close
            val newCandle = generateNextCandle(lastClose)

            // Add the new candle and ensure the history list doesn't exceed 30 items.
            val updatedHistory = (gameState.candleHistory + newCandle).takeLast(30)
            gameState = gameState.copy(candleHistory = updatedHistory)
        }
    }

    // --- DERIVED STATE & VALIDATION ---
    val latestCandle = gameState.candleHistory.lastOrNull()
    val currentPrice = latestCandle?.close?.toDouble() ?: 0.0
    val transactionAmount = 100
    val buyCost = transactionAmount * currentPrice
    val sellRevenue = transactionAmount * currentPrice

    val canAffordToBuy = gameState.playerPortfolio.cash >= buyCost && currentPrice > 0
    val hasCoinsToSell = gameState.playerPortfolio.coins >= transactionAmount

    // --- UI ---
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Portfolio Display
            PortfolioDisplay(portfolio = gameState.playerPortfolio)

            // The Price Chart (now stateless, just draws the data)
            MockPriceChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp),
                candles = gameState.candleHistory // Pass the candle history from our state
            )

            // Live Price & Buy/Sell Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LivePriceDisplay(latestCandle = latestCandle)
                Spacer(modifier = Modifier.height(16.dp))
                BuySellControls(
                    onBuyClicked = {
                        val newPortfolio = gameState.playerPortfolio.copy(
                            cash = gameState.playerPortfolio.cash - buyCost,
                            coins = gameState.playerPortfolio.coins + transactionAmount
                        )
                        gameState = gameState.copy(playerPortfolio = newPortfolio)
                    },
                    onSellClicked = {
                        val newPortfolio = gameState.playerPortfolio.copy(
                            cash = gameState.playerPortfolio.cash + sellRevenue,
                            coins = gameState.playerPortfolio.coins - transactionAmount
                        )
                        gameState = gameState.copy(playerPortfolio = newPortfolio)
                    },
                    isBuyEnabled = canAffordToBuy,
                    isSellEnabled = hasCoinsToSell
                )
            }
        }
    }
}

// --- HELPER FUNCTION FOR CANDLE GENERATION ---

private fun generateNextCandle(previousClose: Float): CandleData {
    val open = previousClose
    val baselineIncrease = 0.65f
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

// --- SUPPORTING UI COMPOSABLES ---

@Composable
private fun PortfolioDisplay(portfolio: PlayerPortfolio) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "CASH",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = NumberFormat.getCurrencyInstance(Locale.US).format(portfolio.cash),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "COINS",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "%,d".format(portfolio.coins),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun LivePriceDisplay(latestCandle: CandleData?) {
    Text(
        text = latestCandle?.let { "$%.2f".format(it.close) } ?: "---",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = if (latestCandle == null || latestCandle.close >= latestCandle.open) {
            Color(0xFF00C853)
        } else {
            Color(0xFFD50000)
        }
    )
}

@Composable
private fun BuySellControls(
    onBuyClicked: () -> Unit,
    onSellClicked: () -> Unit,
    isBuyEnabled: Boolean,
    isSellEnabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
    ) {
        Button(
            onClick = onBuyClicked,
            enabled = isBuyEnabled,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
            modifier = Modifier.width(140.dp)
        ) {
            Text(text = "BUY x100", fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = onSellClicked,
            enabled = isSellEnabled,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD50000)),
            modifier = Modifier.width(140.dp)
        ) {
            Text(text = "SELL x100", fontWeight = FontWeight.Bold)
        }
    }
}

// --- PREVIEW ---

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GameScreenPreview() {
    MarketMakerTheme {
        GameScreen(loadFromSave = false)
    }
}