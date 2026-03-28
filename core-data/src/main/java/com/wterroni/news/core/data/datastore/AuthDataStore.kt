package com.wterroni.news.core.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.wterroni.news.core.data.security.HashUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthDataStore(context: Context) {
    
    private val dataStore: DataStore<Preferences> = context.dataStore
    
    companion object {
        private val EMAIL_KEY = stringPreferencesKey("user_email")
        private val PASSWORD_HASH_KEY = stringPreferencesKey("password_hash")
        private val SALT_KEY = stringPreferencesKey("salt")
    }
    
    suspend fun saveUser(email: String, password: String) {
        val salt = HashUtils.generateSalt()
        val passwordHash = HashUtils.hashPassword(password, salt)
        
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = email
            preferences[PASSWORD_HASH_KEY] = passwordHash
            preferences[SALT_KEY] = salt
        }
    }
    
    suspend fun getUserEmail(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[EMAIL_KEY]
        }
    }
    
    suspend fun getPasswordHash(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[PASSWORD_HASH_KEY]
        }
    }
    
    suspend fun getSalt(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[SALT_KEY]
        }
    }
}
