package com.wterroni.news.feature.stories.data.api

import com.wterroni.news.core.common.constants.AppConstants
import com.wterroni.news.feature.stories.data.model.StoryDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class HackerNewsApiImpl(
    private val httpClient: HttpClient
) : HackerNewsApi {
    
    companion object {
        private const val BASE_URL = "https://hacker-news.firebaseio.com/v0/"
    }
    
    override suspend fun getTopStories(): List<Long> {
        return httpClient.get("${BASE_URL}topstories.json").body()
    }
    
    override suspend fun getStory(id: Long): StoryDto {
        return httpClient.get("${BASE_URL}item/$id.json").body()
    }
    
    override suspend fun getTopStoriesBatch(): List<Long> {
        return getTopStories().take(AppConstants.STORIES_BATCH_SIZE)
    }
    
    override suspend fun getStoriesBatch(offset: Int): List<Long> {
        return getTopStories().drop(offset).take(AppConstants.STORIES_LOAD_MORE_LIMIT)
    }
}
