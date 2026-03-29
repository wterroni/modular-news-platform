package com.wterroni.news.feature.stories.data.local

import com.wterroni.news.core.database.dao.FavoriteStoryDao
import com.wterroni.news.core.database.entity.FavoriteStoryEntity
import com.wterroni.news.feature.stories.domain.model.Story
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteStoriesLocalDataSource(
    private val favoriteStoryDao: FavoriteStoryDao
) {
    suspend fun addFavorite(story: Story, userEmail: String) {
        val entity = FavoriteStoryEntity(
            id = story.id,
            title = story.title ?: "",
            author = story.author ?: "",
            score = story.score ?: 0,
            time = story.time ?: 0L,
            userEmail = userEmail
        )
        favoriteStoryDao.insert(entity)
    }

    suspend fun removeFavorite(story: Story, userEmail: String) {
        val entity = FavoriteStoryEntity(
            id = story.id,
            title = story.title ?: "",
            author = story.author ?: "",
            score = story.score ?: 0,
            time = story.time ?: 0L,
            userEmail = userEmail
        )
        favoriteStoryDao.delete(entity)
    }

    fun getFavorites(userEmail: String): Flow<List<Story>> {
        return favoriteStoryDao.getFavoritesByUser(userEmail).map { entities ->
            entities.map { entity ->
                Story(
                    id = entity.id,
                    author = entity.author,
                    title = entity.title,
                    score = entity.score,
                    time = entity.time,
                    commentCount = null,
                    url = null
                )
            }
        }
    }

    fun isFavorite(id: Long, userEmail: String): Flow<Boolean> {
        return favoriteStoryDao.isFavoriteByUser(id, userEmail)
    }

    fun isFavorite(id: Long): Flow<Boolean> {
        return favoriteStoryDao.isFavorite(id)
    }
}
