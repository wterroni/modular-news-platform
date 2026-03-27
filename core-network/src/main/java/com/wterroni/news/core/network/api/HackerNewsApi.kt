package com.wterroni.news.core.network.api

import com.wterroni.news.core.network.model.dto.StoryDto

interface HackerNewsApi {
    suspend fun getTopStories(): List<Long>
    suspend fun getStory(id: Long): StoryDto
}
