package com.wterroni.news.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wterroni.news.feature.auth.domain.usecase.CheckSessionUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SplashUiState(
    val isLoading: Boolean = true,
    val destination: String? = null
)

class SplashViewModel(
    private val checkSessionUseCase: CheckSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    fun checkSession() {
        viewModelScope.launch {
            try {
                // Delay mínimo para UX suave (800ms - 1200ms)
                delay(1000)
                
                val isLoggedIn = checkSessionUseCase().first()
                
                _uiState.value = SplashUiState(
                    isLoading = false,
                    destination = if (isLoggedIn) "stories" else "login"
                )
            } catch (e: Exception) {
                // Em caso de erro, direcionar para login
                _uiState.value = SplashUiState(
                    isLoading = false,
                    destination = "login"
                )
            }
        }
    }
}
