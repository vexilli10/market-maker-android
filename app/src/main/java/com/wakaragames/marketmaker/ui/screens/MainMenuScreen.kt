package com.wakaragames.marketmaker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wakaragames.marketmaker.R
import com.wakaragames.marketmaker.ui.components.GlitchText
import com.wakaragames.marketmaker.ui.theme.MarketMakerTheme

/**
 * The main menu screen, serving as the central hub for the game.
 *
 * @param modifier Modifier for this composable.
 * @param onNewGameClicked Lambda invoked when the "New Game" button is clicked.
 * @param onSettingsClicked Lambda invoked when the "Settings" button is clicked.
 */
@Composable
fun MainMenuScreen(
    modifier: Modifier = Modifier,
    isContinueEnabled: Boolean, // New parameter
    onContinueClicked: () -> Unit,
    onNewGameClicked: () -> Unit,
    onSettingsClicked: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_background),
            contentDescription = "Command Center Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- FIXED LAYOUT ---

            // 1. Top spacer (identical weight to SplashScreen)
            Spacer(Modifier.weight(0.1f))

            // 2. The title (identical)
            GlitchText(
                text = "MARKET MAKER",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp,
                    color = Color.White,
                    fontSize = 48.sp
                )
            )

            // 3. A container for the content (identical weight to SplashScreen)
            Box(
                modifier = Modifier.weight(0.6f),
                contentAlignment = Alignment.Center
            ) {
                // The content inside this box is different, but the box's size
                // in the layout is the same, fixing the issue.
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (isContinueEnabled) {
                        MenuButton(
                            text = "Continue",
                            onClick = onContinueClicked
                        )
                    }
                    MenuButton(
                        text = "New Game",
                        onClick = onNewGameClicked
                    )
                    MenuButton(
                        text = "Settings",
                        onClick = onSettingsClicked
                    )
                }
            }
        }
    }
}

/**
 * A private, styled button Composable for the Main Menu.
 */
@Composable
private fun MenuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp)) // Rounded corners
            .background(Color.Black.copy(alpha = 0.8f)) // Semi-transparent background
            .clickable(onClick = onClick) // Click action
            .padding(horizontal = 48.dp, vertical = 16.dp)
    ) {
        Text(
            text = text.uppercase(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            letterSpacing = 1.2.sp,
            // You can use a specific font from your theme if you defined one for buttons
            // style = MaterialTheme.typography.labelLarge
        )
    }
}


// --- Preview --- //

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainMenuScreenPreview() {
    MarketMakerTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            MainMenuScreen(
                onNewGameClicked = {},
                onSettingsClicked = {},
                isContinueEnabled = false,
                onContinueClicked = {}
            )
        }
    }
}