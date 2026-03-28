package com.wterroni.news.platform

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wterroni.news.feature.stories.presentation.ui.StoriesScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "stories"
    ) {
        composable("stories") {
            StoriesScreen()
        }
    }
}
