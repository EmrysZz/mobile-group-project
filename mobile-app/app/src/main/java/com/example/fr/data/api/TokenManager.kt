package com.example.fr.data.api

import android.content.Context
import android.content.SharedPreferences
import com.example.fr.data.models.User
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private const val PREFS_NAME = "fr_auth_prefs"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER = "user_data"
    }

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
        ApiClient.setAuthToken(token)
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun saveUser(user: User) {
        val userJson = json.encodeToString(user)
        prefs.edit().putString(KEY_USER, userJson).apply()
    }

    fun getUser(): User? {
        val userJson = prefs.getString(KEY_USER, null) ?: return null
        return try {
            json.decodeFromString<User>(userJson)
        } catch (e: Exception) {
            null
        }
    }

    fun clearAuth() {
        prefs.edit().clear().apply()
        ApiClient.setAuthToken(null)
    }

    fun isLoggedIn(): Boolean {
        val token = getToken()
        if (token != null) {
            ApiClient.setAuthToken(token)
            return true
        }
        return false
    }
}
