package com.wterroni.news.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.wterroni.news.core.database.entity.FavoriteStoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteStoryDao {
    @Insert
    suspend fun insert(favorite: FavoriteStoryEntity)

    @Delete
    suspend fun delete(favorite: FavoriteStoryEntity)

    @Query("SELECT * FROM favorite_stories WHERE userEmail = :userEmail")
    fun getFavoritesByUser(userEmail: String): Flow<List<FavoriteStoryEntity>>

    @Query("SELECT * FROM favorite_stories")
    fun getAllFavorites(): Flow<List<FavoriteStoryEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_stories WHERE id = :id AND userEmail = :userEmail)")
    fun isFavoriteByUser(id: Long, userEmail: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_stories WHERE id = :id)")
    fun isFavorite(id: Long): Flow<Boolean>
}
