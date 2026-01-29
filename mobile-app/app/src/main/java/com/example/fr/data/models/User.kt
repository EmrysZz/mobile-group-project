package com.example.fr.data.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String? = null,
    val bio: String? = null,
    val avatar: String? = null,
    val address: String? = null,
    val emergency_contact: String? = null,
    val emergency_contact_name: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

@Serializable
data class AuthResponse(
    val message: String,
    val user: User,
    val token: String
)

@Serializable
data class ProfileResponse(
    val user: User
)

@Serializable
data class ProfileUpdateResponse(
    val message: String,
    val user: User
)

@Serializable
data class AvatarUploadResponse(
    val message: String,
    val avatar_url: String,
    val user: User
)

@Serializable
data class MessageResponse(
    val message: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class ProfileUpdateRequest(
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val bio: String? = null,
    val address: String? = null,
    val emergency_contact: String? = null,
    val emergency_contact_name: String? = null
)

@Serializable
data class ChangePasswordRequest(
    val current_password: String,
    val new_password: String,
    val new_password_confirmation: String
)

@Serializable
data class DeleteAccountRequest(
    val password: String
)
