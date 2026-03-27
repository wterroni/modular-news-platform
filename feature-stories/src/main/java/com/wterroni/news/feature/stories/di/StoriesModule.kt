package com.wterroni.news.feature.stories.di

import com.wterroni.news.core.network.client.HttpClientProvider
import com.wterroni.news.feature.stories.data.api.HackerNewsApi
import com.wterroni.news.feature.stories.data.api.HackerNewsApiImpl
import com.wterroni.news.feature.stories.data.repository.StoriesRepositoryImpl
import com.wterroni.news.feature.stories.domain.repository.StoriesRepository as DomainStoriesRepository
import com.wterroni.news.feature.stories.domain.usecase.GetTopStoriesUseCase
import com.wterroni.news.feature.stories.presentation.viewmodel.StoriesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val storiesModule = module {
    
    single { HttpClientProvider() }
    
    single<HackerNewsApi> { HackerNewsApiImpl(get()) }
    
    single<DomainStoriesRepository> { StoriesRepositoryImpl(get()) }
    
    single { GetTopStoriesUseCase(get()) }
    
    viewModel { StoriesViewModel(get()) }
}
