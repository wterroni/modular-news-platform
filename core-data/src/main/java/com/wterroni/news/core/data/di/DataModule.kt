package com.wterroni.news.core.data.di

import com.wterroni.news.core.data.datastore.AuthDataStore
import com.wterroni.news.core.data.datastore.SessionDataStore
import com.wterroni.news.core.data.security.HashUtils
import org.koin.dsl.module

val dataModule = module {
    
    single { HashUtils }
    
    single { SessionDataStore(get()) }
    
    single { AuthDataStore(get()) }
}
