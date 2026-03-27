package com.wterroni.news.feature.stories.domain.usecase

import com.wterroni.news.feature.stories.domain.model.Story
import com.wterroni.news.feature.stories.domain.repository.StoriesRepository

class GetTopStoriesUseCase(
    private val repository: StoriesRepository
) {
    suspend operator fun invoke(limit: Int, offset: Int): List<Story> {
        return repository.getTopStories(limit, offset)
    }
}
