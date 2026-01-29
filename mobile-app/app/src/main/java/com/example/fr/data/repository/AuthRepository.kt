package com.example.fr.data.repository

import android.util.Log
import com.example.fr.data.api.ApiClient
import com.example.fr.data.models.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*

class AuthRepository {

    companion object {
        private const val TAG = "AuthRepository"
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            Log.d(TAG, "Attempting login for: $email")
            val response = ApiClient.httpClient.post("${ApiClient.BASE_URL}/login") {
                setBody(LoginRequest(email, password))
            }

            val responseText = response.bodyAsText()
            Log.d(TAG, "Login response status: ${response.status}")
            Log.d(TAG, "Login response body: $responseText")

            when (response.status) {
                HttpStatusCode.OK -> {
                    try {
                        val authResponse = kotlinx.serialization.json.Json {
                            ignoreUnknownKeys = true
                            isLenient = true
                        }.decodeFromString<AuthResponse>(responseText)
                        ApiClient.setAuthToken(authResponse.token)
                        Result.success(authResponse)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse login response", e)
                        Result.failure(Exception("Failed to parse response: ${e.message}"))
                    }
                }
                HttpStatusCode.UnprocessableEntity -> {
                    Result.failure(Exception(parseValidationError(responseText)))
                }
                HttpStatusCode.Unauthorized -> {
                    Result.failure(Exception("Invalid email or password"))
                }
                else -> {
                    Result.failure(Exception("Login failed: ${response.status.value} - ${response.status.description}"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login exception", e)
            when {
                e.message?.contains("Connection refused") == true ->
                    Result.failure(Exception("Cannot connect to server. Make sure the backend is running."))
                e.message?.contains("timeout") == true ->
                    Result.failure(Exception("Connection timed out. Check your network."))
                else -> Result.failure(Exception("Network error: ${e.message}"))
            }
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<AuthResponse> {
        return try {
            Log.d(TAG, "Attempting registration for: $email")
            val response = ApiClient.httpClient.post("${ApiClient.BASE_URL}/register") {
                setBody(RegisterRequest(name, email, password))
            }

            val responseText = response.bodyAsText()
            Log.d(TAG, "Register response status: ${response.status}")
            Log.d(TAG, "Register response body: $responseText")

            when (response.status) {
                HttpStatusCode.Created -> {
                    try {
                        val authResponse = kotlinx.serialization.json.Json {
                            ignoreUnknownKeys = true
                            isLenient = true
                        }.decodeFromString<AuthResponse>(responseText)
                        ApiClient.setAuthToken(authResponse.token)
                        Result.success(authResponse)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse register response", e)
                        Result.failure(Exception("Failed to parse response: ${e.message}"))
                    }
                }
                HttpStatusCode.UnprocessableEntity -> {
                    Result.failure(Exception(parseValidationError(responseText)))
                }
                else -> {
                    Result.failure(Exception("Registration failed: ${response.status.value} - ${response.status.description}"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Register exception", e)
            when {
                e.message?.contains("Connection refused") == true ->
                    Result.failure(Exception("Cannot connect to server. Make sure the backend is running."))
                e.message?.contains("timeout") == true ->
                    Result.failure(Exception("Connection timed out. Check your network."))
                else -> Result.failure(Exception("Network error: ${e.message}"))
            }
        }
    }

    suspend fun logout(): Result<MessageResponse> {
        return try {
            val client = ApiClient.createAuthenticatedClient()
            val response = client.post("${ApiClient.BASE_URL}/logout")
            val responseText = response.bodyAsText()

            if (response.status == HttpStatusCode.OK) {
                ApiClient.setAuthToken(null)
                try {
                    val messageResponse = kotlinx.serialization.json.Json {
                        ignoreUnknownKeys = true
                    }.decodeFromString<MessageResponse>(responseText)
                    Result.success(messageResponse)
                } catch (e: Exception) {
                    Result.success(MessageResponse("Logged out successfully"))
                }
            } else {
                Result.failure(Exception("Logout failed: ${response.status}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Logout exception", e)
            // Still clear local token even if server call fails
            ApiClient.setAuthToken(null)
            Result.success(MessageResponse("Logged out locally"))
        }
    }

    suspend fun getProfile(): Result<User> {
        return try {
            val client = ApiClient.createAuthenticatedClient()
            val response = client.get("${ApiClient.BASE_URL}/profile")
            val responseText = response.bodyAsText()

            if (response.status == HttpStatusCode.OK) {
                val profileResponse = kotlinx.serialization.json.Json {
                    ignoreUnknownKeys = true
                }.decodeFromString<ProfileResponse>(responseText)
                Result.success(profileResponse.user)
            } else {
                Result.failure(Exception("Failed to get profile: ${response.status}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "GetProfile exception", e)
            Result.failure(e)
        }
    }

    suspend fun updateProfile(request: ProfileUpdateRequest): Result<User> {
        return try {
            val client = ApiClient.createAuthenticatedClient()
            val response = client.put("${ApiClient.BASE_URL}/profile") {
                setBody(request)
            }
            val responseText = response.bodyAsText()
            Log.d(TAG, "UpdateProfile response: $responseText")

            when (response.status) {
                HttpStatusCode.OK -> {
                    val profileResponse = kotlinx.serialization.json.Json {
                        ignoreUnknownKeys = true
                    }.decodeFromString<ProfileUpdateResponse>(responseText)
                    Result.success(profileResponse.user)
                }
                HttpStatusCode.UnprocessableEntity -> {
                    Result.failure(Exception(parseValidationError(responseText)))
                }
                else -> {
                    Result.failure(Exception("Failed to update profile: ${response.status}"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "UpdateProfile exception", e)
            Result.failure(e)
        }
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Result<MessageResponse> {
        return try {
            val client = ApiClient.createAuthenticatedClient()
            val response = client.post("${ApiClient.BASE_URL}/profile/password") {
                setBody(ChangePasswordRequest(
                    current_password = currentPassword,
                    new_password = newPassword,
                    new_password_confirmation = newPassword
                ))
            }
            val responseText = response.bodyAsText()

            when (response.status) {
                HttpStatusCode.OK -> {
                    val messageResponse = kotlinx.serialization.json.Json {
                        ignoreUnknownKeys = true
                    }.decodeFromString<MessageResponse>(responseText)
                    Result.success(messageResponse)
                }
                HttpStatusCode.UnprocessableEntity -> {
                    Result.failure(Exception(parseValidationError(responseText)))
                }
                else -> {
                    Result.failure(Exception("Failed to change password: ${response.status}"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "ChangePassword exception", e)
            Result.failure(e)
        }
    }

    suspend fun deleteAccount(password: String): Result<MessageResponse> {
        return try {
            val client = ApiClient.createAuthenticatedClient()
            val response = client.delete("${ApiClient.BASE_URL}/profile") {
                setBody(DeleteAccountRequest(password))
            }
            val responseText = response.bodyAsText()

            when (response.status) {
                HttpStatusCode.OK -> {
                    ApiClient.setAuthToken(null)
                    val messageResponse = kotlinx.serialization.json.Json {
                        ignoreUnknownKeys = true
                    }.decodeFromString<MessageResponse>(responseText)
                    Result.success(messageResponse)
                }
                HttpStatusCode.UnprocessableEntity -> {
                    Result.failure(Exception(parseValidationError(responseText)))
                }
                else -> {
                    Result.failure(Exception("Failed to delete account: ${response.status}"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "DeleteAccount exception", e)
            Result.failure(e)
        }
    }

    private fun parseValidationError(errorBody: String): String {
        return try {
            // Laravel returns: {"message":"...","errors":{"field":["error1","error2"]}}
            if (errorBody.contains("\"message\"")) {
                val messageStart = errorBody.indexOf("\"message\":\"") + 11
                val messageEnd = errorBody.indexOf("\"", messageStart)
                if (messageStart > 10 && messageEnd > messageStart) {
                    return errorBody.substring(messageStart, messageEnd)
                }
            }
            // Try to extract first error from errors object
            if (errorBody.contains("\"errors\"")) {
                val errorsStart = errorBody.indexOf("[\"") + 2
                val errorsEnd = errorBody.indexOf("\"]", errorsStart)
                if (errorsStart > 1 && errorsEnd > errorsStart) {
                    return errorBody.substring(errorsStart, errorsEnd)
                }
            }
            "Validation failed"
        } catch (e: Exception) {
            "Validation failed"
        }
    }
}
