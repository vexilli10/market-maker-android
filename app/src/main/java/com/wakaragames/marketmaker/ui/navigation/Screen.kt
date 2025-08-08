package com.wakaragames.marketmaker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * A sealed class to define the navigation routes in a type-safe way.
 */
sealed class Screen( val route: String,
                     val title: String,
                     val icon: ImageVector? = null) {
    data object Splash : Screen("splash_screen", "Splash")
    data object MainMenu : Screen("main_menu_screen", "Main Menu")
    data object MainApp : Screen("main_app_screen", "Main App")

    // Bottom Navigation Screens
    data object Game : Screen("game_screen", "Game", Icons.Default.KeyboardArrowUp)
    data object Technology : Screen("tech_screen", "Technology", Icons.Default.AccountBox)
    data object News : Screen("news_screen", "News", Icons.Default.Home)
    // Future screens like GameScreen, SettingsScreen, etc., will be added here.
}

val bottomNavItems = listOf(
    Screen.Game,
    Screen.Technology,
    Screen.News
)