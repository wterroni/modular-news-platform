package com.wterroni.news.core.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionDataStore(context: Context) {
    
    private val dataStore: DataStore<Preferences> = context.dataStore
    
    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }
    
    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
        }
    }
    
    val isLoggedInFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }
}
