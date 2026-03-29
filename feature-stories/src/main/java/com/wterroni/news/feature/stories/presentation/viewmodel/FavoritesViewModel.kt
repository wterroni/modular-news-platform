package com.wterroni.news.feature.stories.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wterroni.news.feature.stories.domain.model.Story
import com.wterroni.news.feature.stories.domain.usecase.GetFavoritesUseCase
import com.wterroni.news.feature.stories.domain.usecase.ToggleFavoriteUseCase
import com.wterroni.news.feature.stories.presentation.state.FavoritesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()
    
    init {
        observeFavorites()
    }
    
    private fun observeFavorites() {
        viewModelScope.launch {
            try {
                getFavoritesUseCase().collect { favorites ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        favorites = favorites,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun toggleFavorite(story: Story) {
        viewModelScope.launch {
            toggleFavoriteUseCase(story)
        }
    }
}
