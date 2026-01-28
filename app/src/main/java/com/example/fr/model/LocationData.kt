package com.example.fr.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LocationData(
    val id: Int? = null,
    @SerialName("incident_type") val type: String,
    val latitude: Double,
    val longitude: Double,
    @SerialName("report_time") val reportedTime: String,
    @SerialName("user_name") val reportedBy: String,
    val description: String = "",
    @SerialName("verification_count") val verificationCount: Int = 0
)