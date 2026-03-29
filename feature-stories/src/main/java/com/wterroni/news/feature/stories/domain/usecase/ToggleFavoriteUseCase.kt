package com.wterroni.news.feature.stories.domain.usecase

import com.wterroni.news.feature.stories.domain.model.Story
import com.wterroni.news.feature.stories.domain.repository.StoriesRepository

class ToggleFavoriteUseCase(
    private val repository: StoriesRepository
) {
    suspend operator fun invoke(story: Story) {
        repository.toggleFavorite(story)
    }
}
