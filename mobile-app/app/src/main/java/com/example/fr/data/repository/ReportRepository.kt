package com.example.fr.data.repository

import com.example.fr.data.api.ApiClient
import com.example.fr.data.models.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class ReportRepository {

    private var deviceId: String? = null

    fun setDeviceId(id: String) {
        deviceId = id
    }

    suspend fun getReports(): Result<List<Report>> {
        return try {
            val response = ApiClient.httpClient.get("${ApiClient.BASE_URL}/reports") {
                deviceId?.let { header("X-Device-Id", it) }
                ApiClient.getAuthToken()?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }
            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body<List<Report>>())
            } else {
                Result.failure(Exception("Failed to fetch reports: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitReport(
        userName: String,
        incidentType: String,
        description: String,
        latitude: Double,
        longitude: Double
    ): Result<Report> {
        return try {
            val response = ApiClient.httpClient.post("${ApiClient.BASE_URL}/reports") {
                deviceId?.let { header("X-Device-Id", it) }
                ApiClient.getAuthToken()?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(ReportRequest(userName, incidentType, description, latitude, longitude))
            }
            if (response.status == HttpStatusCode.Created) {
                Result.success(response.body<Report>())
            } else {
                Result.failure(Exception("Failed to submit report: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyReport(reportId: Int, value: Int): Result<VerifyResponse> {
        return try {
            val response = ApiClient.httpClient.put("${ApiClient.BASE_URL}/reports/$reportId/verify") {
                deviceId?.let { header("X-Device-Id", it) }
                ApiClient.getAuthToken()?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(VerifyRequest(value))
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    Result.success(response.body<VerifyResponse>())
                }
                HttpStatusCode.Conflict -> {
                    // User has already voted
                    val errorResponse = response.body<VerifyResponse>()
                    Result.failure(AlreadyVotedException(errorResponse.message, errorResponse.vote_type))
                }
                else -> {
                    Result.failure(Exception("Failed to verify report: ${response.status}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserVotes(): Result<Map<Int, String>> {
        return try {
            val response = ApiClient.httpClient.get("${ApiClient.BASE_URL}/votes") {
                deviceId?.let { header("X-Device-Id", it) }
                ApiClient.getAuthToken()?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }
            if (response.status == HttpStatusCode.OK) {
                val responseText = response.bodyAsText()
                // Parse the JSON map manually since it's a simple key-value map
                val votes = mutableMapOf<Int, String>()
                if (responseText.isNotEmpty() && responseText != "{}") {
                    val cleanJson = responseText.trim().removeSurrounding("{", "}")
                    if (cleanJson.isNotEmpty()) {
                        cleanJson.split(",").forEach { pair ->
                            val parts = pair.split(":")
                            if (parts.size == 2) {
                                val reportId = parts[0].trim().removeSurrounding("\"").toIntOrNull()
                                val voteType = parts[1].trim().removeSurrounding("\"")
                                if (reportId != null) {
                                    votes[reportId] = voteType
                                }
                            }
                        }
                    }
                }
                Result.success(votes)
            } else {
                Result.failure(Exception("Failed to fetch votes: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class AlreadyVotedException(message: String, val voteType: String?) : Exception(message)
