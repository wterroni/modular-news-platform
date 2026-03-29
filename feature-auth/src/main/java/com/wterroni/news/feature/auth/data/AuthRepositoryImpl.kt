package com.wterroni.news.feature.auth.data

import com.wterroni.news.core.data.datastore.AuthDataStore
import com.wterroni.news.core.data.datastore.SessionDataStore
import com.wterroni.news.core.data.security.HashUtils
import com.wterroni.news.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AuthRepositoryImpl(
    private val authDataStore: AuthDataStore,
    private val sessionDataStore: SessionDataStore,
    private val hashUtils: HashUtils
) : AuthRepository {

    override suspend fun signUp(name: String, email: String, password: String) {
        authDataStore.saveUser(name, email, password)
        sessionDataStore.setLoggedIn(true)
    }

    override suspend fun login(email: String, password: String): Boolean {
        val savedEmail = authDataStore.getUserEmail().first()
        val savedPasswordHash = authDataStore.getPasswordHash().first()
        val savedSalt = authDataStore.getSalt().first()
        
        if (savedEmail == email && savedPasswordHash != null && savedSalt != null) {
            val inputPasswordHash = hashUtils.hashPassword(password, savedSalt)
            
            if (inputPasswordHash == savedPasswordHash) {
                sessionDataStore.setLoggedIn(true)
                return true
            }
        }
        
        return false
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return sessionDataStore.isLoggedInFlow
    }

    override suspend fun logout() {
        sessionDataStore.setLoggedIn(false)
    }
}
