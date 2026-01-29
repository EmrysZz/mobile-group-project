package com.example.fr.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Report(
    val id: Int,
    val user_id: Int? = null,
    val user_name: String,
    val incident_type: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val user_agent: String? = null,
    val verification_count: Int = 0,
    val status: String? = null,
    val report_time: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    // Vote status from backend
    val user_vote: String? = null,
    val has_voted: Boolean = false
)

@Serializable
data class ReportRequest(
    val user_name: String,
    val incident_type: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class VerifyRequest(
    val value: Int
)

@Serializable
data class VerifyResponse(
    val message: String,
    val vote_type: String? = null,
    val report: Report
)

@Serializable
data class VoteStatusResponse(
    val has_voted: Boolean,
    val vote_type: String? = null
)
