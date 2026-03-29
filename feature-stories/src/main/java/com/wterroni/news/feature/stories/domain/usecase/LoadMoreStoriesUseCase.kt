package com.wterroni.news.feature.stories.domain.usecase

import com.wterroni.news.feature.stories.domain.repository.StoriesRepository

class LoadMoreStoriesUseCase(
    private val repository: StoriesRepository
) {
    suspend operator fun invoke(offset: Int, limit: Int) =
        repository.loadMoreStories(offset, limit)

}
