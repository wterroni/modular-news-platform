package com.wterroni.news.feature.auth.presentation.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wterroni.news.feature.auth.presentation.viewmodel.SplashViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    onNavigateToStories: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SplashViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800),
        label = "alpha"
    )
    
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 600, delayMillis = 200),
        label = "scale"
    )
    
    LaunchedEffect(Unit) {
        viewModel.checkSession()
    }
    
    LaunchedEffect(uiState.destination) {
        uiState.destination?.let { destination ->
            scope.launch {
                delay(200)
                when (destination) {
                    "stories" -> onNavigateToStories()
                    "login" -> onNavigateToLogin()
                }
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alpha)
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.Article,
                contentDescription = "App Icon",
                modifier = Modifier
                    .size(80.dp)
                    .alpha(scale),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Modular News",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.alpha(scale)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp
                )
            }
        }
    }
}
