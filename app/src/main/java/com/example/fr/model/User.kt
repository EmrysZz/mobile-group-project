package com.example.fr.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class User(
    val id: Int? = null,
    val name: String,
    val email: String,
    val password: String = "", // For local use only, not sent in API responses
    val phone: String? = null,
    val bio: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class AuthResponse(
    val message: String,
    val user: User,
    val token: String
)