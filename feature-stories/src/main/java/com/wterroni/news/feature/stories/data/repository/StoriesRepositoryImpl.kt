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
                Log.d("NewsApp", "API retornou ${storyIds.size} IDs totais")

                val storyDeferreds = storyIds.take(20).map { id ->
                    async {
                        api.getStory(id)
                    }
                }

                val stories = storyDeferreds
                    .awaitAll()
                    .mapNotNull { dto ->
                        val story = dto.toDomain()
                        Log.d("NewsApp", "Story ${story.id}: title=${story.title}, url=${story.url}")
                        story
                    }
                
                Log.d("NewsApp", "Primeiras stories: ${stories.size} de ${storyDeferreds.size} processadas")

                // Limpar e salvar novas stories apenas se API funcionou
                storyLocalDataSource.clearAll()
                storyLocalDataSource.saveStories(stories)
                Log.d("NewsApp", "Stories atualizadas com sucesso da API")
            }
        } catch (e: Exception) {
            // Silenciosamente falhar - não limpar cache local
            Log.d("NewsApp", "Offline: usando cache local - ${e.message}")
            // Não lançar exceção para não quebrar o fluxo
        }
    }
    
    override suspend fun loadMoreStories(offset: Int, limit: Int) {
        try {
            coroutineScope {
                val storyIds = api.getTopStories()
                Log.d("NewsApp", "API tem ${storyIds.size} IDs totais, carregando offset=$offset, limit=$limit")
                
                val nextIds = storyIds.drop(offset).take(limit)
                Log.d("NewsApp", "IDs selecionados para esta página: ${nextIds.size}")

                if (nextIds.isEmpty()) {
                    Log.d("NewsApp", "Não há mais stories para carregar - chegamos ao fim da lista")
                    return@coroutineScope
                }

                val storyDeferreds = nextIds.map { id ->
                    async {
                        api.getStory(id)
                    }
                }

                val stories = storyDeferreds
                    .awaitAll()
                    .mapNotNull { dto ->
                        val story = dto.toDomain()
                        Log.d("NewsApp", "Story ${story.id}: title=${story.title}, url=${story.url}")
                        story
                    }
                
                Log.d("NewsApp", "Carregadas mais ${stories.size} stories (offset=$offset, limit=$limit)")

                // Inserir sem limpar - append
                storyLocalDataSource.insertStories(stories)
                Log.d("NewsApp", "Stories inseridas com sucesso")
            }
        } catch (e: Exception) {
            Log.d("NewsApp", "Erro ao carregar mais stories - ${e.message}")
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
