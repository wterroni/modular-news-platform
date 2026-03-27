package com.wterroni.news.feature.stories.data.repository

import com.wterroni.news.feature.stories.data.api.HackerNewsApi
import com.wterroni.news.feature.stories.data.model.toDomain
import com.wterroni.news.feature.stories.domain.model.Story
import com.wterroni.news.feature.stories.domain.repository.StoriesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class StoriesRepositoryImpl(
    private val api: HackerNewsApi
) : StoriesRepository {
    
    override suspend fun getTopStories(limit: Int, offset: Int): List<Story> {
        val storyIds = api.getTopStories()
        
        val paginatedIds = storyIds.drop(offset).take(limit)
        
        return coroutineScope {
            val storyDeferreds = paginatedIds.map { id ->
                async {
                    api.getStory(id)
                }
            }
            
            storyDeferreds.awaitAll()
                .map { it.toDomain() }
        }
    }
}
