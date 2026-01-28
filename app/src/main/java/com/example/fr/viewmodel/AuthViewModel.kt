package com.example.fr.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fr.model.AuthResponse
import com.example.fr.model.User
import com.example.fr.util.TokenManager
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    
    private val tokenManager = TokenManager(application)
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // HTTP Client
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private val BASE_URL = "http://10.0.2.2:8000/api/"

    init {
        // Check if user is already logged in
        if (tokenManager.isLoggedIn()) {
            val id = tokenManager.getUserId()
            val name = tokenManager.getUserName()
            val email = tokenManager.getUserEmail()
            
            if (id != -1 && name != null && email != null) {
                _currentUser.value = User(
                    id = id,
                    name = name,
                    email = email
                )
            }
        }
    }

    /**
     * Register a new user
     */
    fun register(name: String, email: String, password: String, phone: String = "", bio: String = "") {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val requestBody = mapOf(
                    "name" to name,
                    "email" to email,
                    "password" to password,
                    "phone" to phone,
                    "bio" to bio
                )

                val response: AuthResponse = client.post("${BASE_URL}register") {
                    contentType(ContentType.Application.Json)
                    setBody(requestBody)
                }.body()

                // Save token and user info
                tokenManager.saveToken(response.token)
                tokenManager.saveUser(
                    response.user.id ?: 0,
                    response.user.name,
                    response.user.email
                )

                _currentUser.value = response.user
                Log.d("AuthViewModel", "Registration successful: ${response.user.name}")

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Registration failed", e)
                _errorMessage.value = "Registration failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Login user
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val requestBody = mapOf(
                    "email" to email,
                    "password" to password
                )

                val response: AuthResponse = client.post("${BASE_URL}login") {
                    contentType(ContentType.Application.Json)
                    setBody(requestBody)
                }.body()

                // Save token and user info
                tokenManager.saveToken(response.token)
                tokenManager.saveUser(
                    response.user.id ?: 0,
                    response.user.name,
                    response.user.email
                )

                _currentUser.value = response.user
                Log.d("AuthViewModel", "Login successful: ${response.user.name}")

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login failed", e)
                _errorMessage.value = "Invalid email or password"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Logout user
     */
    fun logout() {
        viewModelScope.launch {
            try {
                val token = tokenManager.getToken()
                if (token != null) {
                    // Call backend logout endpoint
                    client.post("${BASE_URL}logout") {
                        header("Authorization", "Bearer $token")
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Logout API call failed", e)
            } finally {
                // Clear local storage regardless
                tokenManager.clearAll()
                _currentUser.value = null
            }
        }
    }

    /**
     * Update user profile
     */
    fun updateUserProfile(name: String, email: String, phone: String, bio: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val token = tokenManager.getToken() ?: throw Exception("Not authenticated")

                val requestBody = mapOf(
                    "name" to name,
                    "email" to email,
                    "phone" to phone,
                    "bio" to bio
                )

                val response: Map<String, Any> = client.put("${BASE_URL}profile") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(requestBody)
                }.body()

                // Update local user info
                tokenManager.saveUser(
                    tokenManager.getUserId(),
                    name,
                    email
                )

                _currentUser.value = _currentUser.value?.copy(
                    name = name,
                    email = email,
                    phone = phone,
                    bio = bio
                )

                Log.d("AuthViewModel", "Profile updated successfully")

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Profile update failed", e)
                _errorMessage.value = "Failed to update profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun getAuthToken(): String? {
        return tokenManager.getToken()
    }

    override fun onCleared() {
        super.onCleared()
        client.close()
    }
}