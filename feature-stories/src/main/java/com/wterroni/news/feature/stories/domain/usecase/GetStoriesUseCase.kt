package com.wterroni.news.feature.stories.domain.usecase

import com.wterroni.news.feature.stories.domain.model.Story
import com.wterroni.news.feature.stories.domain.repository.StoriesRepository
import kotlinx.coroutines.flow.Flow

class GetStoriesUseCase(
    private val repository: StoriesRepository
) {
    operator fun invoke(): Flow<List<Story>> {
        return repository.getStories()
    }
}
