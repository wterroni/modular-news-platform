package com.wterroni.news.feature.stories.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToStoryDetail: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Article, contentDescription = "Stories") },
                    label = { Text("Stories") },
                    selected = currentRoute == "stories",
                    onClick = {
                        if (currentRoute != "stories") {
                            navController.navigate("stories") {
                                popUpTo("stories") { inclusive = true }
                            }
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                    label = { Text("Favorites") },
                    selected = currentRoute == "favorites",
                    onClick = {
                        if (currentRoute != "favorites") {
                            navController.navigate("favorites") {
                                popUpTo("favorites") { inclusive = true }
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "stories",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("stories") {
                StoriesScreen(
                    onNavigateToStoryDetail = onNavigateToStoryDetail,
                    onLogout = onLogout
                )
            }
            composable("favorites") {
                FavoritesScreen(
                    onNavigateToStoryDetail = onNavigateToStoryDetail
                )
            }
        }
    }
}
