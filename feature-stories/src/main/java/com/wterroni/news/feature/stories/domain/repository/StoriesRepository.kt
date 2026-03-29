package com.wterroni.news.feature.stories.domain.repository

import com.wterroni.news.feature.stories.domain.model.Story
import kotlinx.coroutines.flow.Flow

interface StoriesRepository {
    fun getStories(): Flow<List<Story>>
    suspend fun refreshStories()
    suspend fun toggleFavorite(story: Story)
    fun isFavorite(id: Long): Flow<Boolean>
    fun getFavorites(): Flow<List<Story>>
}
