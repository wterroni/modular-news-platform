package com.wterroni.news.feature.stories.presentation.state

import com.wterroni.news.feature.stories.domain.model.Story

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val favorites: List<Story> = emptyList(),
    val error: String? = null
)
