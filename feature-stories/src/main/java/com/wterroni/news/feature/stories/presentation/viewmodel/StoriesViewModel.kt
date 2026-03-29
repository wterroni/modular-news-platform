package com.wterroni.news.feature.stories.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wterroni.news.feature.stories.domain.model.Story
import com.wterroni.news.feature.stories.domain.usecase.GetStoriesUseCase
import com.wterroni.news.feature.stories.domain.usecase.RefreshStoriesUseCase
import com.wterroni.news.feature.stories.domain.usecase.ToggleFavoriteUseCase
import com.wterroni.news.feature.stories.domain.usecase.IsFavoriteUseCase
import com.wterroni.news.feature.auth.domain.usecase.LogoutUseCase
import com.wterroni.news.feature.stories.presentation.state.StoriesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StoriesViewModel(
    private val getStoriesUseCase: GetStoriesUseCase,
    private val refreshStoriesUseCase: RefreshStoriesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(StoriesUiState())
    val uiState: StateFlow<StoriesUiState> = _uiState.asStateFlow()
    
    init {
        observeStories()
        loadStories()
    }
    
    private fun observeStories() {
        viewModelScope.launch {
            try {
                getStoriesUseCase().collect { stories ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        stories = stories,
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
    
    fun loadStories() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            refreshStoriesUseCase()
            // Não tratamos erro aqui - refreshStories é best effort
            // observeStories() vai atualizar a UI quando tiver dados
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
    
    fun toggleFavorite(story: Story) {
        viewModelScope.launch {
            toggleFavoriteUseCase(story)
        }
    }
    
    fun isFavorite(id: Long) = isFavoriteUseCase(id)
    
    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}
