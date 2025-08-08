package com.wakaragames.marketmaker.ui.screens

import android.R
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.wakaragames.marketmaker.ui.navigation.Screen
import com.wakaragames.marketmaker.ui.navigation.bottomNavItems
import com.wakaragames.marketmaker.viewmodels.GameViewModel

@Composable
fun MainAppScreen(gameViewModel: GameViewModel) {
    val navController = rememberNavController()

    Scaffold(
        containerColor = Color(0xFF0D1B2A) ,
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0D1B2A)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon!!, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            // This sets the text color for the selected item.
                            selectedTextColor = Color.Green,
                            // This sets the icon color for the selected item.
                            selectedIconColor = Color.Green,
                            // This sets the text color for unselected items.
                            unselectedTextColor = Color.White,
                            // This sets the icon color for unselected items.
                            unselectedIconColor = Color.White,
                            // Optional: If you want an indicator behind the selected icon.
                            indicatorColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                LocalAbsoluteTonalElevation.current
                            )
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Game.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.Game.route) {
                GameScreen(
                    viewModel = gameViewModel,
                )
            }
            composable(Screen.Technology.route) { TechnologyScreen(viewModel = gameViewModel) }
            composable(Screen.News.route) { val gameState by gameViewModel.gameState.collectAsState()

                // 2. Map the game data (List<NewsItem>) to the UI data (List<NewsPost>).
                //    This is where you could add logic to assign different authors/avatars
                //    based on the news content in the future.
                val newsPosts = gameState.newsFeed.map { newsItem ->
                    NewsPost(
                        id = "${newsItem.eventId}-${newsItem.timestamp}", // Use timestamp as a unique ID
                        authorName = "Market Feed", // A generic author for now
                        content = newsItem.headline
                    )
                }

                // 3. Pass the correctly mapped list to the NewsFeedScreen.
                NewsFeedScreen(newsPosts = newsPosts) }
        }
    }
}