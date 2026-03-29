package com.wterroni.news.feature.stories.data.repository

import com.wterroni.news.feature.stories.data.api.HackerNewsApi
import com.wterroni.news.feature.stories.data.local.FavoriteStoriesLocalDataSource
import com.wterroni.news.feature.stories.data.model.toDomain
import com.wterroni.news.feature.stories.domain.model.Story
import com.wterroni.news.feature.stories.domain.repository.StoriesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first

class StoriesRepositoryImpl(
    private val api: HackerNewsApi,
    private val localDataSource: FavoriteStoriesLocalDataSource
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
    
    override suspend fun toggleFavorite(story: Story) {
        val isCurrentlyFavorite = localDataSource.isFavorite(story.id).first()
        if (isCurrentlyFavorite) {
            localDataSource.removeFavorite(story)
        } else {
            localDataSource.addFavorite(story)
        }
    }
    
    override fun isFavorite(id: Long) = localDataSource.isFavorite(id)
    
    override fun getFavorites() = localDataSource.getFavorites()
}
