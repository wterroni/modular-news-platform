package com.wterroni.news.feature.stories.di

import com.wterroni.news.core.network.client.HttpClientProvider
import com.wterroni.news.feature.stories.data.api.HackerNewsApi
import com.wterroni.news.feature.stories.data.api.HackerNewsApiImpl
import com.wterroni.news.feature.stories.data.local.FavoriteStoriesLocalDataSource
import com.wterroni.news.feature.stories.data.local.StoryLocalDataSource
import com.wterroni.news.feature.stories.data.repository.StoriesRepositoryImpl
import com.wterroni.news.feature.stories.domain.repository.StoriesRepository as DomainStoriesRepository
import com.wterroni.news.core.data.datastore.AuthDataStore
import com.wterroni.news.feature.stories.domain.usecase.GetFavoritesUseCase
import com.wterroni.news.feature.stories.domain.usecase.GetStoriesUseCase
import com.wterroni.news.feature.stories.domain.usecase.RefreshStoriesUseCase
import com.wterroni.news.feature.stories.domain.usecase.IsFavoriteUseCase
import com.wterroni.news.feature.stories.domain.usecase.ToggleFavoriteUseCase
import com.wterroni.news.feature.auth.domain.usecase.LogoutUseCase
import com.wterroni.news.feature.stories.presentation.viewmodel.StoriesViewModel
import com.wterroni.news.feature.stories.presentation.viewmodel.FavoritesViewModel
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val storiesModule = module {
    
    single<HttpClient> { 
        HttpClientProvider().provideHttpClient() 
    }
    
    single<HackerNewsApi> { HackerNewsApiImpl(get()) }
    
    single { StoryLocalDataSource(get()) }
    single { FavoriteStoriesLocalDataSource(get()) }
    
    single<DomainStoriesRepository> { StoriesRepositoryImpl(get(), get(), get(), get()) }
    
    single { GetStoriesUseCase(get()) }
    single { RefreshStoriesUseCase(get()) }
    single { ToggleFavoriteUseCase(get()) }
    single { GetFavoritesUseCase(get()) }
    single { IsFavoriteUseCase(get()) }
    single { LogoutUseCase(get()) }
    
    viewModel { StoriesViewModel(get(), get(), get(), get(), get()) }
    viewModel { FavoritesViewModel(get(), get()) }
}
