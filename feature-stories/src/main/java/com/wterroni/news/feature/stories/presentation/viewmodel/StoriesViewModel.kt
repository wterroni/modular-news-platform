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
    
    private var currentPage = 1  // Começar em 1 (já que a primeira página é carregada no refresh)
    private val pageSize = 20
    
    init {
        observeStories()
        loadStories()
    }
    
    private fun observeStories() {
        viewModelScope.launch {
            try {
                getStoriesUseCase().collect { stories ->
                    Log.d("STATE", "collect chamado, mantendo isLoading=${_uiState.value.isLoading}")
                    _uiState.value = _uiState.value.copy(
                        // NÃO mexer em isLoading aqui - apenas atualizar stories
                        stories = stories,
                        error = null
                    )
                    Log.d("STATE", "collect concluído, isLoading=${_uiState.value.isLoading}")
                }
            } catch (e: Exception) {
                Log.d("STATE", "collect erro, mantendo isLoading=${_uiState.value.isLoading}")
                _uiState.value = _uiState.value.copy(
                    // NÃO mexer em isLoading aqui - apenas error
                    error = e.message
                )
            }
        }
    }
    
    fun loadStories() {
        viewModelScope.launch {
            try {
                Log.d("STATE", "loadStories iniciado, setting isLoading=true")
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                refreshStoriesUseCase()
                currentPage = 1
                Log.d("Pagination", "loadStories() concluído - currentPage inicializado para $currentPage")
            } catch (e: Exception) {
                Log.e("Pagination", "Erro no refresh inicial", e)
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
                Log.d("STATE", "🔥 ESSENCIAL: isLoading resetado para false no loadStories()")
            }
        }
    }
    
    fun loadMore() {
        if (_uiState.value.isLoadingMore) {
            Log.d("Pagination", "loadMore() ignorado - já está carregando")
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoadingMore = true)
                
                val offset = currentPage * pageSize
                Log.d("Pagination", "LOAD MORE offset=$offset")
                
                loadMoreStoriesUseCase(offset, pageSize)
                currentPage++
                
                Log.d("Pagination", "loadMore() concluído - nova currentPage=$currentPage")
            } catch (e: Exception) {
                Log.e("Pagination", "Erro no loadMore()", e)
            } finally {
                _uiState.value = _uiState.value.copy(isLoadingMore = false)
                Log.d("Pagination", "isLoadingMore resetado para false")
            }
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
