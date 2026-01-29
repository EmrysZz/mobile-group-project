package com.example.fr.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Shelter(
    val id: Int,
    val name: String,
    val description: String? = null,
    val address: String? = null,
    val phone: String? = null,
    val latitude: Double,
    val longitude: Double,
    val capacity: Int? = null,
    val current_occupancy: Int = 0,
    val available_space: Int? = null,
    val status: String = "open",
    val is_verified: Boolean = false,
    val created_at: String? = null,
    val updated_at: String? = null,
    // Legacy fields for backward compatibility
    val incident_type: String = "Shelter",
    val report_time: String? = null,
    val user_name: String? = null,
    val verification_count: Int = 0
)

@Serializable
data class ShelterRequest(
    val name: String,
    val description: String,
    val address: String? = null,
    val phone: String? = null,
    val latitude: Double,
    val longitude: Double,
    val capacity: Int? = null
)

@Serializable
data class ShelterResponse(
    val message: String,
    val shelter: Shelter
)
