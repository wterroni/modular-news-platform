package com.wterroni.news.feature.auth.di

import com.wterroni.news.core.data.datastore.AuthDataStore
import com.wterroni.news.core.data.datastore.SessionDataStore
import com.wterroni.news.core.data.security.HashUtils
import com.wterroni.news.feature.auth.data.AuthRepositoryImpl
import com.wterroni.news.feature.auth.domain.repository.AuthRepository
import com.wterroni.news.feature.auth.domain.usecase.CheckSessionUseCase
import com.wterroni.news.feature.auth.domain.usecase.LoginUseCase
import com.wterroni.news.feature.auth.domain.usecase.SignUpUseCase
import com.wterroni.news.feature.auth.domain.usecase.LogoutUseCase
import com.wterroni.news.feature.auth.presentation.viewmodel.AuthViewModel
import com.wterroni.news.feature.auth.presentation.viewmodel.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    
    single<AuthRepository> { 
        AuthRepositoryImpl(
            authDataStore = get(),
            sessionDataStore = get(),
            hashUtils = get()
        )
    }
    
    single { LoginUseCase(get()) }
    single { SignUpUseCase(get()) }
    single { CheckSessionUseCase(get()) }
    single { LogoutUseCase(get()) }
    
    viewModel { AuthViewModel(get(), get(), get()) }
    viewModel { SplashViewModel(get()) }
}
