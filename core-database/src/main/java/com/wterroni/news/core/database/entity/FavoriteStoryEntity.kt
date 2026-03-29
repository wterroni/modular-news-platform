package com.wterroni.news.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_stories")
data class FavoriteStoryEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val author: String,
    val score: Int,
    val time: Long,
    val userEmail: String
)
