package com.wterroni.news.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wterroni.news.feature.auth.domain.usecase.CheckSessionUseCase
import com.wterroni.news.feature.auth.domain.usecase.LoginUseCase
import com.wterroni.news.feature.auth.domain.usecase.SignUpUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val checkSessionUseCase: CheckSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkSession()
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val success = loginUseCase(email, password)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = success,
                    error = if (!success) "Invalid credentials" else null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                when {
                    name.isBlank() -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Name is required"
                        )
                        return@launch
                    }
                    email.isBlank() || !email.contains("@") -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Invalid email"
                        )
                        return@launch
                    }
                    password.length < 6 -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Password must be at least 6 characters"
                        )
                        return@launch
                    }
                }
                
                signUpUseCase(name, email, password)
                _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private fun checkSession() {
        viewModelScope.launch {
            checkSessionUseCase().collect { isLoggedIn ->
                _uiState.value = _uiState.value.copy(isLoggedIn = isLoggedIn)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
