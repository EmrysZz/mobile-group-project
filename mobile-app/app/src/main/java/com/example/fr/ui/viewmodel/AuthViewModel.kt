package com.example.fr.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fr.data.api.TokenManager
import com.example.fr.data.models.ProfileUpdateRequest
import com.example.fr.data.models.User
import com.example.fr.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val profileUpdateSuccess: Boolean = false,
    val passwordChangeSuccess: Boolean = false,
    val accountDeleted: Boolean = false
)

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private var tokenManager: TokenManager? = null

    fun setTokenManager(manager: TokenManager) {
        tokenManager = manager
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        tokenManager?.let { manager ->
            if (manager.isLoggedIn()) {
                val user = manager.getUser()
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = true,
                    user = user
                )
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please fill in all fields")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.login(email, password)
            result.fold(
                onSuccess = { response ->
                    tokenManager?.saveToken(response.token)
                    tokenManager?.saveUser(response.user)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = response.user,
                        isLoggedIn = true
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Login failed"
                    )
                }
            )
        }
    }

    fun register(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please fill in all fields")
            return
        }

        if (password.length < 8) {
            _uiState.value = _uiState.value.copy(error = "Password must be at least 8 characters")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.register(name, email, password)
            result.fold(
                onSuccess = { response ->
                    tokenManager?.saveToken(response.token)
                    tokenManager?.saveUser(response.user)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = response.user,
                        isLoggedIn = true
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Registration failed"
                    )
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.logout()
            tokenManager?.clearAuth()
            _uiState.value = AuthUiState()
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.getProfile()
            result.fold(
                onSuccess = { user ->
                    tokenManager?.saveUser(user)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = user
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load profile"
                    )
                }
            )
        }
    }

    fun updateProfile(
        name: String? = null,
        email: String? = null,
        phone: String? = null,
        bio: String? = null,
        address: String? = null,
        emergencyContact: String? = null,
        emergencyContactName: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                profileUpdateSuccess = false
            )

            val request = ProfileUpdateRequest(
                name = name,
                email = email,
                phone = phone,
                bio = bio,
                address = address,
                emergency_contact = emergencyContact,
                emergency_contact_name = emergencyContactName
            )

            val result = repository.updateProfile(request)
            result.fold(
                onSuccess = { user ->
                    tokenManager?.saveUser(user)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = user,
                        profileUpdateSuccess = true
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to update profile"
                    )
                }
            )
        }
    }

    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        if (currentPassword.isBlank() || newPassword.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please fill in all password fields")
            return
        }

        if (newPassword != confirmPassword) {
            _uiState.value = _uiState.value.copy(error = "New passwords do not match")
            return
        }

        if (newPassword.length < 8) {
            _uiState.value = _uiState.value.copy(error = "New password must be at least 8 characters")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                passwordChangeSuccess = false
            )

            val result = repository.changePassword(currentPassword, newPassword)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        passwordChangeSuccess = true
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to change password"
                    )
                }
            )
        }
    }

    fun deleteAccount(password: String) {
        if (password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please enter your password")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.deleteAccount(password)
            result.fold(
                onSuccess = {
                    tokenManager?.clearAuth()
                    _uiState.value = AuthUiState(accountDeleted = true)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to delete account"
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearProfileUpdateSuccess() {
        _uiState.value = _uiState.value.copy(profileUpdateSuccess = false)
    }

    fun clearPasswordChangeSuccess() {
        _uiState.value = _uiState.value.copy(passwordChangeSuccess = false)
    }
}
