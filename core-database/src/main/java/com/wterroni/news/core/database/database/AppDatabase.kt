package com.wterroni.news.core.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.wterroni.news.core.database.dao.FavoriteStoryDao
import com.wterroni.news.core.database.dao.StoryDao
import com.wterroni.news.core.database.entity.FavoriteStoryEntity
import com.wterroni.news.core.database.entity.StoryEntity

@Database(entities = [FavoriteStoryEntity::class, StoryEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteStoryDao
    abstract fun storyDao(): StoryDao
}
