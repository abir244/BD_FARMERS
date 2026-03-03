package com.example.bd_farmers.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.bd_farmers.data.model.LoginRequest
import com.example.bd_farmers.data.model.RegisterRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Note: ApiService needs to be correctly defined in a network package.
// For now, I'm assuming it exists or will be created to use the models in data.model.

class AuthRepository(private val api: Any, context: Context) {

    private val Context.dataStore by preferencesDataStore(name = "auth_prefs")
    private val TOKEN_KEY = stringPreferencesKey("auth_token")
    private val dataStore = context.dataStore

    suspend fun login(email: String, password: String): Result<String> {
        // Implementation would call api.login(LoginRequest(email, password))
        // and handle the response.
        return Result.failure(Exception("Not implemented"))
    }

    suspend fun register(name: String, email: String, password: String): Result<String> {
        // Implementation would call api.register(RegisterRequest(name, email, password))
        return Result.failure(Exception("Not implemented"))
    }

    suspend fun logout() {
        dataStore.edit { it.remove(TOKEN_KEY) }
    }

    private suspend fun saveToken(token: String) {
        dataStore.edit { it[TOKEN_KEY] = token }
    }

    val tokenFlow: Flow<String?> = dataStore.data.map { it[TOKEN_KEY] }
}
