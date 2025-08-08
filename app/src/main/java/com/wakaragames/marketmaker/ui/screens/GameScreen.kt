package com.wakaragames.marketmaker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wakaragames.marketmaker.data.models.CandleData
import com.wakaragames.marketmaker.data.models.PlayerPortfolio
import com.wakaragames.marketmaker.ui.components.MockPriceChart
import com.wakaragames.marketmaker.ui.theme.MarketMakerTheme
import com.wakaragames.marketmaker.viewmodels.GameViewModel
import java.text.NumberFormat
import java.util.*
import kotlin.math.max

/**
 * The main game screen. This is now a "dumb" UI component that observes the
 * GameViewModel and sends user events up to it.
 */
@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel // Its only parameter is the shared ViewModel
) {
    // Collect the game state as state. The UI will automatically
    // recompose whenever the gameState in the ViewModel changes.
    val gameState by viewModel.gameState.collectAsState()

    // --- DERIVED STATE & VALIDATION ---
    // These values are derived from the state on each recomposition.
    val latestCandle = gameState.candleHistory.lastOrNull()
    val currentPrice = latestCandle?.close?.toDouble() ?: 0.0
    val buyCost = 100 * currentPrice
    val startIndex = max(0, gameState.historicalCandleCount - gameState.candleHistory.size)

    val canAffordToBuy = gameState.playerPortfolio.cash >= buyCost && currentPrice > 0
    val hasCoinsToSell = gameState.playerPortfolio.coins >= 100

    // --- UI ---
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF0D1B2A)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            // The chart is a stateless composable that just draws the data it's given
            MockPriceChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp),
                candles = gameState.candleHistory,
                startIndex = startIndex
            )

            // The controls at the bottom of the screen
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PortfolioDisplay(portfolio = gameState.playerPortfolio)
                Spacer(modifier = Modifier.height(16.dp))
                LivePriceDisplay(
                    latestCandle = latestCandle,
                    growthRate = gameState.currentGrowthRate
                )
                Spacer(modifier = Modifier.height(16.dp))
                BuySellControls(
                    onBuyClicked = { viewModel.performBuyTransaction() }, // Send "buy" event up
                    onSellClicked = { viewModel.performSellTransaction() }, // Send "sell" event up
                    isBuyEnabled = canAffordToBuy,
                    isSellEnabled = hasCoinsToSell
                )
            }
        }
    }
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
                color = Color.White
            )
            Text(
                text = NumberFormat.getCurrencyInstance(Locale.US).format(portfolio.cash),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "COINS",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )
            Text(
                text = "%,d".format(portfolio.coins), // Adds thousand separators
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun LivePriceDisplay(
    latestCandle: CandleData?,
    growthRate: Float // Add the growth rate as a parameter
) {
    // We'll use a Row to place the price and growth rate side-by-side.
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Live Price Text
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

        // --- NEW GROWTH RATE DISPLAY ---
        // Convert the raw growth rate (e.g., 0.65) to a percentage string (e.g., "+1.52%")
        // This is a simplified calculation for display purposes.
        val growthPercent = (growthRate / (latestCandle?.close ?: 1f)) * 100
        val growthText = String.format("%.2f%%", growthPercent)
        val growthColor = if (growthPercent >= 0) Color(0xFF00C853) else Color(0xFFD50000)

        Text(
            text = growthText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = growthColor,
            modifier = Modifier.padding(bottom = 4.dp) // Align baseline with price
        )
    }
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
        // Since we can't instantiate a ViewModel in a Preview, we build the UI
        // with static data to see what it looks like.
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                PortfolioDisplay(portfolio = PlayerPortfolio(20000.0, 10000))
                // The chart will be empty in the preview, which is acceptable.
                MockPriceChart(
                    modifier = Modifier.weight(1f),
                    candles = listOf(),
                    startIndex = 0
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LivePriceDisplay(latestCandle = null,growthRate = 1f)
                    Spacer(modifier = Modifier.height(16.dp))
                    BuySellControls(
                        onBuyClicked = {},
                        onSellClicked = {},
                        isBuyEnabled = true,
                        isSellEnabled = true
                    )
                }
            }
        }
    }
}