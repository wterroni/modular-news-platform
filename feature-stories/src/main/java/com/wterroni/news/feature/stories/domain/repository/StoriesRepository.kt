package com.wterroni.news.feature.stories.domain.repository

import com.wterroni.news.feature.stories.domain.model.Story
import kotlinx.coroutines.flow.Flow

interface StoriesRepository {
    fun getStories(): Flow<List<Story>>
    suspend fun refreshStories()
    suspend fun toggleFavorite(story: Story)
    suspend fun isFavorite(id: Long): Boolean
    suspend fun getFavorites(): List<Story>
}
