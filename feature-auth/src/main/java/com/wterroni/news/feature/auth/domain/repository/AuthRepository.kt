package com.wterroni.news.feature.auth.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signUp(name: String, email: String, password: String)
    suspend fun login(email: String, password: String): Boolean
    fun isLoggedIn(): Flow<Boolean>
    suspend fun logout()
}
