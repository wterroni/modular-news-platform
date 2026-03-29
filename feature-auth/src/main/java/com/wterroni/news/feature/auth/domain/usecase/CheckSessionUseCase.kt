package com.wterroni.news.feature.auth.domain.usecase

import com.wterroni.news.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class CheckSessionUseCase(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.isLoggedIn()
    }
}
