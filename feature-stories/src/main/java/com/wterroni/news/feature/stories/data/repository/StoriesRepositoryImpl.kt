package com.wterroni.news.feature.stories.data.repository

import com.wterroni.news.feature.stories.data.api.HackerNewsApi
import com.wterroni.news.feature.stories.data.local.FavoriteStoriesLocalDataSource
import com.wterroni.news.feature.stories.data.local.StoryLocalDataSource
import com.wterroni.news.feature.stories.data.model.toDomain
import com.wterroni.news.feature.stories.domain.model.Story
import com.wterroni.news.feature.stories.domain.repository.StoriesRepository
import com.wterroni.news.core.data.datastore.AuthDataStore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first

class StoriesRepositoryImpl(
    private val api: HackerNewsApi,
    private val storyLocalDataSource: StoryLocalDataSource,
    private val favoriteStoriesLocalDataSource: FavoriteStoriesLocalDataSource,
    private val authDataStore: AuthDataStore
) : StoriesRepository {
    
    override fun getStories() = storyLocalDataSource.getStories()
    
    override suspend fun refreshStories() {
        try {
            coroutineScope {
                val storyIds = api.getTopStoriesBatch()

                val storyDeferreds = storyIds.map { id ->
                    async {
                        api.getStory(id)
                    }
                }

                val stories = storyDeferreds
                    .awaitAll()
                    .map { dto ->
                        dto.toDomain()
                    }

                storyLocalDataSource.clearAll()
                storyLocalDataSource.saveStories(stories)
            }
        } catch (e: Exception) {
        }
    }
    
    override suspend fun loadMoreStories(offset: Int, limit: Int) {
        try {
            coroutineScope {
                val storyIds = api.getStoriesBatch(offset)
                
                if (storyIds.isEmpty()) {
                    return@coroutineScope
                }

                val storyDeferreds = storyIds.map { id ->
                    async {
                        api.getStory(id)
                    }
                }

                val stories = storyDeferreds
                    .awaitAll()
                    .map { dto ->
                        dto.toDomain()
                    }

                storyLocalDataSource.insertStories(stories)
            }
        } catch (e: Exception) {

        }
    }
    
    override suspend fun toggleFavorite(story: Story) {
        val userEmail = authDataStore.getUserEmail().first() ?: return
        val isCurrentlyFavorite = favoriteStoriesLocalDataSource.isFavorite(story.id, userEmail).first()
        if (isCurrentlyFavorite) {
            favoriteStoriesLocalDataSource.removeFavorite(story, userEmail)
        } else {
            favoriteStoriesLocalDataSource.addFavorite(story, userEmail)
        }
    }
    
    override suspend fun isFavorite(id: Long): Boolean {
        return try {
            val userEmail = authDataStore.getUserEmail().first()
            if (userEmail != null) {
                favoriteStoriesLocalDataSource.isFavorite(id, userEmail).first()
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun getFavorites(): List<Story> {
        return try {
            val userEmail = authDataStore.getUserEmail().first()
            if (userEmail != null) {
                favoriteStoriesLocalDataSource.getFavorites(userEmail).first()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
