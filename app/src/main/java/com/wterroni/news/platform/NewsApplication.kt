package com.wterroni.news.platform

import android.app.Application
import com.wterroni.news.feature.auth.di.authModule
import com.wterroni.news.feature.stories.di.storiesModule
import com.wterroni.news.core.data.di.dataModule
import com.wterroni.news.core.database.di.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NewsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@NewsApplication)
            modules(
                dataModule,
                databaseModule,
                storiesModule,
                authModule
            )
        }
    }
}
