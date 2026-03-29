package com.wterroni.news.feature.stories.presentation.viewmodel

import android.util.Log
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
import kotlinx.coroutines.flow.firstOrNull

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
    
    // Cache local de estados de favoritos para UI reativa
    private val _favoriteStates = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val favoriteStates: StateFlow<Map<Long, Boolean>> = _favoriteStates.asStateFlow()
    
    private var currentPage = 0
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
            currentPage = 1  // Já carregamos a primeira página
            // Não tratamos erro aqui - refreshStories é best effort
            // observeStories() vai atualizar a UI quando tiver dados
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
    
    fun loadMore() {
        if (_uiState.value.isLoadingMore) {
            Log.d("NewsApp", "loadMore() chamado mas já está carregando, ignorando...")
            return
        }
        
        viewModelScope.launch {
            Log.d("NewsApp", "Iniciando loadMore() - currentPage=$currentPage, pageSize=$pageSize")
            _uiState.value = _uiState.value.copy(isLoadingMore = true)
            
            val offset = currentPage * pageSize
            Log.d("NewsApp", "Chamando loadMoreStoriesUseCase com offset=$offset, limit=$pageSize")
            
            loadMoreStoriesUseCase(offset, pageSize)
            currentPage++
            
            Log.d("NewsApp", "loadMore() concluído - nova currentPage=$currentPage")
            _uiState.value = _uiState.value.copy(isLoadingMore = false)
        }
    }
    
    fun toggleFavorite(story: Story) {
        viewModelScope.launch {
            try {
                val isCurrentlyFavorite = isFavoriteUseCase(story.id).first()
                Log.d("NewsApp", "Toggle favorite for story ${story.id}: currentlyFavorite=$isCurrentlyFavorite")

                toggleFavoriteUseCase(story)

                val newFavoriteState = isFavoriteUseCase(story.id).first()
                Log.d("NewsApp", "After toggle: newFavoriteState=$newFavoriteState")
                
                // Atualizar cache local de favoritos
                val currentStates = _favoriteStates.value.toMutableMap()
                currentStates[story.id] = newFavoriteState
                _favoriteStates.value = currentStates
                Log.d("NewsApp", "Favorite cache updated for story ${story.id}: $newFavoriteState")
                
            } catch (e: Exception) {
                Log.e("NewsApp", "Error toggling favorite for story ${story.id}", e)
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
