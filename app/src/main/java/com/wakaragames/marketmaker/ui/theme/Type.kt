package com.wakaragames.marketmaker.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.wakaragames.marketmaker.R // This import points to your resources

// 1. Define the custom font family for Orbitron.
// This assumes you have 'orbitron_regular.ttf' and 'orbitron_bold.ttf' in your res/font folder.
val orbitronFontFamily = FontFamily(
    Font(R.font.orbitron_regular, FontWeight.Normal),
    Font(R.font.orbitron_bold, FontWeight.Bold)
)

// 2. Set of Material typography styles, now with Orbitron for headers.
val Typography = Typography(
    // Style for the main screen title, e.g., "MARKET MAKER"
    displayLarge = TextStyle(
        fontFamily = orbitronFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = 1.5.sp
    ),
    // Style for slightly smaller, but still prominent headers
    displayMedium = TextStyle(
        fontFamily = orbitronFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        lineHeight = 48.sp,
        letterSpacing = 0.sp
    ),
    // Style for standard screen titles or large section headers
    titleLarge = TextStyle(
        fontFamily = orbitronFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    // Your original body style remains unchanged
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default, // Or another font like Roboto Mono if you add it
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)