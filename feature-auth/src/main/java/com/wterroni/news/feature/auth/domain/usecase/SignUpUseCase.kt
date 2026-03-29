package com.wterroni.news.feature.auth.domain.usecase

import com.wterroni.news.feature.auth.domain.repository.AuthRepository

class SignUpUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(name: String, email: String, password: String) {
        repository.signUp(name, email, password)
    }
}
