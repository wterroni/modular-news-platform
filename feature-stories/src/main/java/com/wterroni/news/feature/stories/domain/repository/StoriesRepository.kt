package com.wterroni.news.feature.stories.domain.repository

import com.wterroni.news.feature.stories.domain.model.Story
import kotlinx.coroutines.flow.Flow

interface StoriesRepository {
    suspend fun getTopStories(limit: Int, offset: Int): List<Story>
    suspend fun toggleFavorite(story: Story)
    fun isFavorite(id: Long): Flow<Boolean>
    fun getFavorites(): Flow<List<Story>>
}
