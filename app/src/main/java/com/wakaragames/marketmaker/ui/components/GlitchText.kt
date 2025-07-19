package com.wakaragames.marketmaker.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.delay
import kotlin.random.Random

// Using our neon green and a magenta for a classic "glitch" look
private val glitchColor1 = Color(0xFF00C853) // Neon Green
private val glitchColor2 = Color(0xFFFF00FF) // Magenta

/**
 * A Text composable that applies a more pronounced and visible "glitch" effect.
 *
 * @param text The string to display.
 * @param modifier The modifier to be applied to the component.
 * @param style The text style to be applied.
 */
@Composable
fun GlitchText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle
) {
    // State for the glitch effect's parameters
    var glitchOffset1 by remember { mutableStateOf(IntOffset.Zero) }
    var glitchOffset2 by remember { mutableStateOf(IntOffset.Zero) }
    var mainTextAlpha by remember { mutableStateOf(1f) }
    var glitchLayersAlpha by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            // Wait for a random period (1.5s to 4s) before the next glitch
            delay(Random.nextLong(1000, 2000))

            // --- Start Glitch Sequence ---
            repeat(Random.nextInt(4, 8)) {
                // Generate larger, more noticeable offsets
                val x1 = Random.nextInt(-20, 20)
                val y1 = Random.nextInt(-10, 10)
                val x2 = Random.nextInt(-20, 20)
                val y2 = Random.nextInt(-10, 10)

                glitchOffset1 = IntOffset(x1, y1)
                glitchOffset2 = IntOffset(x2, y2)

                // Make the glitch layers visible and the main text semi-transparent
                glitchLayersAlpha = 0.8f
                mainTextAlpha = 0.6f

                // Hold the glitch frame for a very short duration
                delay(Random.nextLong(30, 80))
            }

            // --- Reset to Normal State ---
            glitchOffset1 = IntOffset.Zero
            glitchOffset2 = IntOffset.Zero
            glitchLayersAlpha = 0f // Make glitch layers invisible
            mainTextAlpha = 1f     // Make main text fully visible
        }
    }

    Box(modifier) {
        // Glitch Layer 2 (Magenta) - Drawn only when glitching
        Text(
            text = text,
            style = style,
            modifier = Modifier
                .offset { glitchOffset2 }
                .alpha(glitchLayersAlpha),
            color = glitchColor2
        )
        // Glitch Layer 1 (Neon Green) - Drawn only when glitching
        Text(
            text = text,
            style = style,
            modifier = Modifier
                .offset { glitchOffset1 }
                .alpha(glitchLayersAlpha),
            color = glitchColor1
        )
        // Main Text Layer (White) - Becomes semi-transparent during glitch
        Text(
            text = text,
            style = style,
            modifier = Modifier.alpha(mainTextAlpha),
            color = style.color
        )
    }
}