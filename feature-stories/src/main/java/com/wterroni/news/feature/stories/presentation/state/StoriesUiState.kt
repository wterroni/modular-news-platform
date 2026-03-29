package com.wterroni.news.feature.stories.presentation.state

import com.wterroni.news.feature.stories.domain.model.Story

data class StoriesUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val stories: List<Story> = emptyList(),
    val error: String? = null
)
