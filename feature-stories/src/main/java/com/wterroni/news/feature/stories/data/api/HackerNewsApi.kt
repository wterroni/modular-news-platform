package com.wterroni.news.feature.stories.data.api

import com.wterroni.news.feature.stories.data.model.StoryDto

interface HackerNewsApi {
    suspend fun getTopStories(): List<Long>
    suspend fun getStory(id: Long): StoryDto
    suspend fun getTopStoriesBatch(): List<Long>
    suspend fun getStoriesBatch(offset: Int): List<Long>
}
