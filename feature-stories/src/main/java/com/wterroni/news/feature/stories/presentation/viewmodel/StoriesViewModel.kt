package com.wterroni.news.feature.stories.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wterroni.news.feature.stories.domain.model.Story
import com.wterroni.news.feature.stories.domain.usecase.GetStoriesUseCase
import com.wterroni.news.feature.stories.domain.usecase.RefreshStoriesUseCase
import com.wterroni.news.feature.stories.domain.usecase.ToggleFavoriteUseCase
import com.wterroni.news.feature.stories.domain.usecase.IsFavoriteUseCase
import com.wterroni.news.feature.stories.domain.usecase.LoadMoreStoriesUseCase
import com.wterroni.news.feature.auth.domain.usecase.LogoutUseCase
import com.wterroni.news.feature.stories.presentation.state.StoriesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StoriesViewModel(
    private val getStoriesUseCase: GetStoriesUseCase,
    private val refreshStoriesUseCase: RefreshStoriesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val loadMoreStoriesUseCase: LoadMoreStoriesUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(StoriesUiState())
    val uiState: StateFlow<StoriesUiState> = _uiState.asStateFlow()
    
    private val _favoriteStates = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val favoriteStates: StateFlow<Map<Long, Boolean>> = _favoriteStates.asStateFlow()
    
    private var currentPage = 1
    private val pageSize = 20
    
    init {
        observeStories()
        loadStories()
    }
    
    private fun observeStories() {
        viewModelScope.launch {
            try {
                getStoriesUseCase().collect { stories ->
                    _uiState.value = _uiState.value.copy(
                        stories = stories,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }
    
    fun loadStories() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                refreshStoriesUseCase()
                currentPage = 1
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
    
    fun loadMore() {
        if (_uiState.value.isLoadingMore) {
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoadingMore = true)
                
                val offset = currentPage * pageSize
                
                loadMoreStoriesUseCase(offset, pageSize)
                currentPage++
            } catch (e: Exception) {
            } finally {
                _uiState.value = _uiState.value.copy(isLoadingMore = false)
            }
        }
    }
    
    fun toggleFavorite(story: Story) {
        viewModelScope.launch {
            try {
                val isCurrentlyFavorite = isFavoriteUseCase(story.id).first()

                toggleFavoriteUseCase(story)

                val newFavoriteState = isFavoriteUseCase(story.id).first()
                
                val currentStates = _favoriteStates.value.toMutableMap()
                currentStates[story.id] = newFavoriteState
                _favoriteStates.value = currentStates
                
            } catch (e: Exception) {
            }
        }
    }
    
    fun isFavorite(id: Long) = isFavoriteUseCase(id)
    
    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}
