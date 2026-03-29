package com.wterroni.news.feature.stories.data.local

import com.wterroni.news.core.database.dao.StoryDao
import com.wterroni.news.core.database.entity.StoryEntity
import com.wterroni.news.feature.stories.domain.model.Story
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoryLocalDataSource(
    private val storyDao: StoryDao
) {
    suspend fun saveStories(stories: List<Story>) {
        val entities = stories.map { story ->
            StoryEntity(
                id = story.id,
                title = story.title ?: "",
                author = story.author ?: "",
                score = story.score ?: 0,
                time = story.time ?: 0L
            )
        }
        storyDao.insertAll(entities)
    }

    fun getStories(): Flow<List<Story>> {
        return storyDao.getAllStories().map { entities ->
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

    suspend fun clearAll() {
        storyDao.clearAll()
    }
}
