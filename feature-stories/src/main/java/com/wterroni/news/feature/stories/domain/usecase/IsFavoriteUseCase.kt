package com.wterroni.news.feature.stories.domain.usecase

import com.wterroni.news.feature.stories.domain.repository.StoriesRepository
import kotlinx.coroutines.flow.Flow

class IsFavoriteUseCase(
    private val repository: StoriesRepository
) {
    operator fun invoke(id: Long): Flow<Boolean> {
        return repository.isFavorite(id)
    }
}
