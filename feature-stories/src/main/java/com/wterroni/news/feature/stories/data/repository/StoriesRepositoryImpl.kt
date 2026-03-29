package com.wterroni.news.feature.stories.data.repository

import com.wterroni.news.feature.stories.data.api.HackerNewsApi
import com.wterroni.news.feature.stories.data.local.FavoriteStoriesLocalDataSource
import com.wterroni.news.feature.stories.data.local.StoryLocalDataSource
import com.wterroni.news.feature.stories.data.model.toDomain
import com.wterroni.news.feature.stories.domain.model.Story
import com.wterroni.news.feature.stories.domain.repository.StoriesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first

class StoriesRepositoryImpl(
    private val api: HackerNewsApi,
    private val storyLocalDataSource: StoryLocalDataSource,
    private val favoriteStoriesLocalDataSource: FavoriteStoriesLocalDataSource
) : StoriesRepository {
    
    override fun getStories() = storyLocalDataSource.getStories()
    
    override suspend fun refreshStories() {
        coroutineScope {
            val storyIds = api.getTopStories()
            val paginatedIds = storyIds.take(20) // Pegar os primeiros 20
            
            val storyDeferreds = paginatedIds.map { id ->
                async {
                    api.getStory(id)
                }
            }
            
            val stories = storyDeferreds.awaitAll().map { it.toDomain() }
            
            // Limpar e salvar novas stories
            storyLocalDataSource.clearAll()
            storyLocalDataSource.saveStories(stories)
        }
    }
    
    override suspend fun toggleFavorite(story: Story) {
        val isCurrentlyFavorite = favoriteStoriesLocalDataSource.isFavorite(story.id).first()
        if (isCurrentlyFavorite) {
            favoriteStoriesLocalDataSource.removeFavorite(story)
        } else {
            favoriteStoriesLocalDataSource.addFavorite(story)
        }
    }
    
    override fun isFavorite(id: Long) = favoriteStoriesLocalDataSource.isFavorite(id)
    
    override fun getFavorites() = favoriteStoriesLocalDataSource.getFavorites()
}
