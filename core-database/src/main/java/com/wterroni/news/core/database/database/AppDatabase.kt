package com.wterroni.news.core.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.wterroni.news.core.database.dao.FavoriteStoryDao
import com.wterroni.news.core.database.entity.FavoriteStoryEntity

@Database(entities = [FavoriteStoryEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteStoryDao
}
