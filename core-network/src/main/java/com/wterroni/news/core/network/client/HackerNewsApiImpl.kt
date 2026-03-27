package com.wterroni.news.core.network.client

import com.wterroni.news.core.network.api.HackerNewsApi
import com.wterroni.news.core.network.model.dto.StoryDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class HackerNewsApiImpl(
    private val httpClient: HttpClient = HttpClientProvider().provideHttpClient()
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
}
