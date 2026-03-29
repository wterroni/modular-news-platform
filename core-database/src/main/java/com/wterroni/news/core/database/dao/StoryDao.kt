package com.wterroni.news.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wterroni.news.core.database.entity.StoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stories: List<StoryEntity>)

    @Query("SELECT * FROM stories ORDER BY time DESC")
    fun getAllStories(): Flow<List<StoryEntity>>

    @Query("DELETE FROM stories")
    suspend fun clearAll()
}
