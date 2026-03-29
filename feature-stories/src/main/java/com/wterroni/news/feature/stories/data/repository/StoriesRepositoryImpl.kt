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
import android.util.Log

class StoriesRepositoryImpl(
    private val api: HackerNewsApi,
    private val storyLocalDataSource: StoryLocalDataSource,
    private val favoriteStoriesLocalDataSource: FavoriteStoriesLocalDataSource
) : StoriesRepository {
    
    override fun getStories() = storyLocalDataSource.getStories()
    
    override suspend fun refreshStories() {
    try {
        coroutineScope {
            val storyIds = api.getTopStories()
            val paginatedIds = storyIds.take(50)

            val storyDeferreds = paginatedIds.map { id ->
                async {
                    api.getStory(id)
                }
            }

            val stories = storyDeferreds
                .awaitAll()
                .mapNotNull { dto ->
                    val story = dto.toDomain()
                    Log.d("Stories", "Story ${story.id}: title=${story.title}, url=${story.url}")
                    story.takeIf { !it.url.isNullOrBlank() }
                }
            
            Log.d("Stories", "Stories com URL: ${stories.size} de ${storyDeferreds.size} processadas")

            // Limpar e salvar novas stories apenas se API funcionou
            storyLocalDataSource.clearAll()
            storyLocalDataSource.saveStories(stories)
            Log.d("Stories", "Stories atualizadas com sucesso da API")
        }
    } catch (e: Exception) {
        // Silenciosamente falhar - não limpar cache local
        Log.d("Stories", "Offline: usando cache local - ${e.message}")
        // Não lançar exceção para não quebrar o fluxo
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
