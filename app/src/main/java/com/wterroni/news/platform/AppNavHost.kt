package com.wterroni.news.platform

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.wterroni.news.feature.stories.presentation.ui.StoriesScreen
import com.wterroni.news.feature.stories.presentation.ui.StoryDetailScreen
import com.wterroni.news.feature.auth.presentation.ui.LoginScreen
import com.wterroni.news.feature.auth.presentation.ui.SignUpScreen
import com.wterroni.news.feature.auth.presentation.ui.SplashScreen
import java.net.URLEncoder
import java.net.URLDecoder

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onNavigateToStories = { 
                    navController.navigate("stories") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToLogin = { 
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        composable("login") {
            LoginScreen(
                onNavigateToSignUp = { 
                    navController.navigate("signup") 
                },
                onLoginSuccess = { 
                    navController.navigate("stories") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        
        composable("signup") {
            SignUpScreen(
                onNavigateBack = { 
                    navController.popBackStack() 
                },
                onSignUpSuccess = { 
                    navController.navigate("stories") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        
        composable("stories") {
            StoriesScreen(
                onNavigateToStoryDetail = { url ->
                    navController.navigate("storyDetail/${URLEncoder.encode(url, "UTF-8")}")
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("stories") { inclusive = true }
                    }
                }
            )
        }
        
        composable(
            route = "storyDetail/{url}",
            arguments = listOf(navArgument("url") { type = NavType.StringType })
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url")?.let { 
                URLDecoder.decode(it, "UTF-8")
            }
            if (!url.isNullOrBlank()) {
                StoryDetailScreen(
                    url = url,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
