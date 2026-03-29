package com.wterroni.news.feature.auth.domain.usecase

import com.wterroni.news.feature.auth.domain.repository.AuthRepository

class LogoutUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() {
        repository.logout()
    }
}
