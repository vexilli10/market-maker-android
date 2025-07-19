package com.wakaragames.marketmaker.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wakaragames.marketmaker.R
import com.wakaragames.marketmaker.ui.components.GlitchText
import com.wakaragames.marketmaker.ui.theme.MarketMakerTheme
import com.wakaragames.marketmaker.ui.theme.orbitronFontFamily
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onScreenTapped: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 2000)
    )
    val pulseAnim = remember { Animatable(0.3f) }

    LaunchedEffect(Unit) {
        startAnimation = true
        pulseAnim.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onScreenTapped
            )
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
                .padding(horizontal = 24.dp, vertical = 48.dp)
                .alpha(alphaAnim.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- FIXED LAYOUT ---

            // 1. Top spacer to position the title. This now dictates the position.
            Spacer(Modifier.weight(0.1f))

            // 2. The title itself.
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

            // 3. A container for the content that takes up the rest of the screen.
            Box(
                modifier = Modifier.weight(0.6f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Make the Market",
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(pulseAnim.value),
                    color = Color.White.copy(alpha = 1.2f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    fontFamily = orbitronFontFamily
                )
            }
        }
    }
}


// --- Preview --- //

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SplashScreenPreview() {
    MarketMakerTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SplashScreen(onScreenTapped = {})
        }
    }
}