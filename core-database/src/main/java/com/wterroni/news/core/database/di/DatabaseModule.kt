package com.wterroni.news.core.database.di

import android.content.Context
import androidx.room.Room
import com.wterroni.news.core.database.dao.FavoriteStoryDao
import com.wterroni.news.core.database.database.AppDatabase
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            get<Context>(),
            AppDatabase::class.java,
            "news_database"
        ).build()
    }
    
    single { get<AppDatabase>().favoriteDao() }
}
