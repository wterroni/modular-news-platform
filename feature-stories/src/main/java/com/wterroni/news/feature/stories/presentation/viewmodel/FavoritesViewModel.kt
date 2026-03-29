package com.wterroni.news.feature.stories.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wterroni.news.feature.stories.domain.model.Story
import com.wterroni.news.feature.stories.domain.usecase.GetFavoritesUseCase
import com.wterroni.news.feature.stories.domain.usecase.ToggleFavoriteUseCase
import com.wterroni.news.feature.stories.domain.usecase.IsFavoriteUseCase
import com.wterroni.news.feature.stories.presentation.state.FavoritesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull
import android.util.Log

class FavoritesViewModel(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()
    
    // Cache local de estados de favoritos para UI reativa
    private val _favoriteStates = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val favoriteStates: StateFlow<Map<Long, Boolean>> = _favoriteStates.asStateFlow()
    
    init {
        observeFavorites()
    }
    
    private fun observeFavorites() {
        viewModelScope.launch {
            try {
                getFavoritesUseCase().collect { favorites ->
                    Log.d("STATE", "collect favorites chamado, mantendo isLoading=${_uiState.value.isLoading}")
                    _uiState.value = _uiState.value.copy(
                        // NÃO mexer em isLoading aqui - apenas atualizar favorites
                        favorites = favorites,
                        error = null
                    )
                    Log.d("STATE", "collect favorites concluído, isLoading=${_uiState.value.isLoading}")
                }
            } catch (e: Exception) {
                Log.d("STATE", "collect favorites erro, mantendo isLoading=${_uiState.value.isLoading}")
                _uiState.value = _uiState.value.copy(
                    // NÃO mexer em isLoading aqui - apenas error
                    error = e.message
                )
            }
        }
    }
    
    fun toggleFavorite(story: Story) {
        viewModelScope.launch {
            try {
                val isCurrentlyFavorite = isFavoriteUseCase(story.id).firstOrNull() ?: false
                Log.d("NewsApp", "Favorites: Toggle favorite for story ${story.id}: currentlyFavorite=$isCurrentlyFavorite")
                
                toggleFavoriteUseCase(story)
                
                val newFavoriteState = isFavoriteUseCase(story.id).firstOrNull() ?: false
                Log.d("NewsApp", "Favorites: After toggle: newFavoriteState=$newFavoriteState")
                
                // Atualizar cache local de favoritos
                val currentStates = _favoriteStates.value.toMutableMap()
                currentStates[story.id] = newFavoriteState
                _favoriteStates.value = currentStates
                Log.d("NewsApp", "Favorites: Favorite cache updated for story ${story.id}: $newFavoriteState")
                
                // Se removeu dos favoritos, atualizar a lista de favoritos para remover o item
                if (!newFavoriteState) {
                    val currentFavorites = _uiState.value.favorites.toMutableList()
                    currentFavorites.removeAll { it.id == story.id }
                    _uiState.value = _uiState.value.copy(favorites = currentFavorites)
                    Log.d("NewsApp", "Favorites: Story ${story.id} removido da lista de favoritos")
                }
                
            } catch (e: Exception) {
                Log.e("NewsApp", "Favorites: Error toggling favorite for story ${story.id}", e)
            }
        }
    }
    
    fun isFavorite(id: Long) = isFavoriteUseCase(id)
}
