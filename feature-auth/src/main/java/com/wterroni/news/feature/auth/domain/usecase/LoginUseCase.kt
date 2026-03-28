package com.wterroni.news.feature.auth.domain.usecase

import com.wterroni.news.feature.auth.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Boolean {
        return repository.login(email, password)
    }
}
