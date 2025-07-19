package com.wakaragames.marketmaker.ui.navigation

/**
 * A sealed class to define the navigation routes in a type-safe way.
 */
sealed class Screen(val route: String) {
    data object Splash : Screen("splash_screen")
    data object MainMenu : Screen("main_menu_screen")
    data object Game : Screen("game_screen")
    // Future screens like GameScreen, SettingsScreen, etc., will be added here.
}