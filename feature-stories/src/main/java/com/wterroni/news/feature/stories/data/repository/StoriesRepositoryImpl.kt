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
import kotlinx.coroutines.flow.flatMapLatest
import android.util.Log
import kotlinx.coroutines.flow.Flow

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
