package com.wterroni.news.feature.stories.domain.usecase

import com.wterroni.news.feature.stories.domain.repository.StoriesRepository

class RefreshStoriesUseCase(
    private val repository: StoriesRepository
) {
    suspend operator fun invoke() {
        repository.refreshStories()
    }
}
