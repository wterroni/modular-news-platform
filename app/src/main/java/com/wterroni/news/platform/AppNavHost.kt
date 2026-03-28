package com.wterroni.news.platform

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wterroni.news.feature.stories.presentation.ui.StoriesScreen
import com.wterroni.news.feature.auth.presentation.ui.LoginScreen
import com.wterroni.news.feature.auth.presentation.ui.SignUpScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
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
            StoriesScreen()
        }
    }
}
