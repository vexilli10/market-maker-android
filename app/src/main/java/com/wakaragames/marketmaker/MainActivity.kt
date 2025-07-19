package com.wakaragames.marketmaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.wakaragames.marketmaker.data.persistence.GameStateManager
import com.wakaragames.marketmaker.ui.navigation.Screen
import com.wakaragames.marketmaker.ui.screens.GameScreen
import com.wakaragames.marketmaker.ui.screens.MainMenuScreen
import com.wakaragames.marketmaker.ui.screens.SplashScreen
import com.wakaragames.marketmaker.ui.theme.MarketMakerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            MarketMakerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val hasSavedGame = GameStateManager.hasSavedGame(this)
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Splash.route
                    ) {
                        // Splash Screen
                        composable(Screen.Splash.route) {
                            SplashScreen(
                                onScreenTapped = {
                                    navController.navigate(Screen.MainMenu.route) {
                                        popUpTo(Screen.Splash.route) {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }

                        // Main Menu Screen
                        composable(Screen.MainMenu.route) {
                            MainMenuScreen(
                                isContinueEnabled = hasSavedGame,
                                onContinueClicked = {
                                    // Navigate to GameScreen and tell it to load state
                                    navController.navigate("${Screen.Game.route}/true")
                                },
                                onNewGameClicked = {
                                    // *** NAVIGATION ACTION UPDATED HERE ***
                                    GameStateManager.deleteSavedGame(this@MainActivity)
                                    navController.navigate("${Screen.Game.route}/false")
                                },
                                onSettingsClicked = {
                                    // This can navigate to a settings screen later
                                }
                            )
                        }

                        // *** NEW GAME SCREEN ADDED TO GRAPH HERE ***
                        composable(
                            route = "${Screen.Game.route}/{loadState}",
                            arguments = listOf(navArgument("loadState") { type = NavType.BoolType })
                        ) { backStackEntry ->
                            val loadState = backStackEntry.arguments?.getBoolean("loadState") ?: !true
                            GameScreen(loadFromSave = loadState)
                        }
                    }
                }
            }
        }
    }
}