package com.wakaragames.marketmaker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.wakaragames.marketmaker.data.persistence.GameStateManager
import com.wakaragames.marketmaker.ui.navigation.Screen
import com.wakaragames.marketmaker.ui.screens.MainAppScreen
import com.wakaragames.marketmaker.ui.screens.MainMenuScreen
import com.wakaragames.marketmaker.ui.screens.SplashScreen
import com.wakaragames.marketmaker.ui.theme.MarketMakerTheme
import com.wakaragames.marketmaker.viewmodels.GameViewModel

class MainActivity : ComponentActivity() {

    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // This is still correct and necessary for edge-to-edge display.
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MarketMakerTheme {
                // --- THIS IS THE PRIMARY FIX ---
                // We control the system bars here, at the highest level of our app's UI.
                val systemUiController = rememberSystemUiController()
                LaunchedEffect(systemUiController) {
                    // Set the status bar to be transparent, revealing the app's background.
                    systemUiController.setStatusBarColor(
                        color = Color(0xFF0D1B2A),
                        darkIcons = false // We need light icons on our dark background
                    )
                    // You can do the same for the navigation bar if you wish.
                    systemUiController.setNavigationBarColor(
                        color = Color(0xFF0D1B2A),
                        darkIcons = false
                    )
                }

                // The root Surface now provides the background that will be seen
                // through the transparent status bar.
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0D1B2A)
                ) {
                    AppNavigation(gameViewModel = gameViewModel)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        gameViewModel.saveGame()
    }
}

@Composable
private fun AppNavigation(gameViewModel: GameViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onScreenTapped = {
                navController.navigate(Screen.MainMenu.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.MainMenu.route) {
            val hasSavedGame = remember { GameStateManager.hasSavedGame(context) }
            MainMenuScreen(
                isContinueEnabled = hasSavedGame,
                onContinueClicked = {
                    gameViewModel.loadGame()
                    navController.navigate(Screen.MainApp.route) {
                        popUpTo(Screen.MainMenu.route) { inclusive = true }
                    }
                },
                onNewGameClicked = {
                    gameViewModel.startNewGame()
                    navController.navigate(Screen.MainApp.route) {
                        popUpTo(Screen.MainMenu.route) { inclusive = true }
                    }
                },
                onSettingsClicked = {
                    Toast.makeText(context, "Settings not implemented yet.", Toast.LENGTH_SHORT).show()
                }
            )
        }
        composable(Screen.MainApp.route) {
            MainAppScreen(gameViewModel = gameViewModel)
        }
    }
}