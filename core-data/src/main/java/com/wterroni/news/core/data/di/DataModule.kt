package com.wterroni.news.core.data.di

import com.wterroni.news.core.data.datastore.AuthDataStore
import com.wterroni.news.core.data.datastore.SessionDataStore
import org.koin.dsl.module

val dataModule = module {
    
    single { SessionDataStore(get()) }
    
    single { AuthDataStore(get()) }
}
