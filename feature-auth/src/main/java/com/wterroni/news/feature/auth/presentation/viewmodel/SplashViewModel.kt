package com.wterroni.news.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wterroni.news.feature.auth.domain.usecase.CheckSessionUseCase
import com.wterroni.news.core.common.constants.AppConstants
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
                delay(AppConstants.SPLASH_DELAY_MS)
                
                val isLoggedIn = checkSessionUseCase().first()
                
                _uiState.value = SplashUiState(
                    isLoading = false,
                    destination = if (isLoggedIn) AppConstants.DESTINATION_STORIES else AppConstants.DESTINATION_LOGIN
                )
            } catch (e: Exception) {
                _uiState.value = SplashUiState(
                    isLoading = false,
                    destination = AppConstants.DESTINATION_LOGIN
                )
            }
        }
    }
}
