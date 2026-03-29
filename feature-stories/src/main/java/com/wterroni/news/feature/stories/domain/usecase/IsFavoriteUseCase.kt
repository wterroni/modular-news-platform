package com.wterroni.news.feature.stories.domain.usecase

import com.wterroni.news.feature.stories.domain.repository.StoriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class IsFavoriteUseCase(
    private val repository: StoriesRepository
) {
    operator fun invoke(id: Long): Flow<Boolean> {
        return flow {
            emit(repository.isFavorite(id))
        }
    }
}
