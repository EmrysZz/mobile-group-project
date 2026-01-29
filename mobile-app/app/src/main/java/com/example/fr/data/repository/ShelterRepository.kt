package com.example.fr.data.repository

import com.example.fr.data.api.ApiClient
import com.example.fr.data.models.Shelter
import com.example.fr.data.models.ShelterRequest
import com.example.fr.data.models.ShelterResponse
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class ShelterRepository {

    private var deviceId: String? = null

    fun setDeviceId(id: String) {
        deviceId = id
    }

    suspend fun getShelters(): Result<List<Shelter>> {
        return try {
            val response = ApiClient.httpClient.get("${ApiClient.BASE_URL}/shelters")
            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body<List<Shelter>>())
            } else {
                Result.failure(Exception("Failed to fetch shelters: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getShelter(id: Int): Result<Shelter> {
        return try {
            val response = ApiClient.httpClient.get("${ApiClient.BASE_URL}/shelters/$id")
            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body<Shelter>())
            } else {
                Result.failure(Exception("Failed to fetch shelter: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitShelter(
        name: String,
        description: String,
        address: String?,
        phone: String?,
        latitude: Double,
        longitude: Double,
        capacity: Int?
    ): Result<Shelter> {
        return try {
            val response = ApiClient.httpClient.post("${ApiClient.BASE_URL}/shelters") {
                deviceId?.let { header("X-Device-Id", it) }
                ApiClient.getAuthToken()?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(ShelterRequest(name, description, address, phone, latitude, longitude, capacity))
            }

            val responseText = response.bodyAsText()

            when (response.status) {
                HttpStatusCode.Created -> {
                    val shelterResponse = kotlinx.serialization.json.Json {
                        ignoreUnknownKeys = true
                    }.decodeFromString<ShelterResponse>(responseText)
                    Result.success(shelterResponse.shelter)
                }
                HttpStatusCode.UnprocessableEntity -> {
                    Result.failure(Exception(parseValidationError(responseText)))
                }
                else -> {
                    Result.failure(Exception("Failed to submit shelter: ${response.status}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateShelter(
        id: Int,
        name: String?,
        description: String?,
        address: String?,
        phone: String?,
        latitude: Double?,
        longitude: Double?,
        capacity: Int?,
        status: String?
    ): Result<Shelter> {
        return try {
            val response = ApiClient.httpClient.put("${ApiClient.BASE_URL}/shelters/$id") {
                deviceId?.let { header("X-Device-Id", it) }
                ApiClient.getAuthToken()?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                setBody(mapOf(
                    "name" to name,
                    "description" to description,
                    "address" to address,
                    "phone" to phone,
                    "latitude" to latitude,
                    "longitude" to longitude,
                    "capacity" to capacity,
                    "status" to status
                ).filterValues { it != null })
            }

            val responseText = response.bodyAsText()

            when (response.status) {
                HttpStatusCode.OK -> {
                    val shelterResponse = kotlinx.serialization.json.Json {
                        ignoreUnknownKeys = true
                    }.decodeFromString<ShelterResponse>(responseText)
                    Result.success(shelterResponse.shelter)
                }
                HttpStatusCode.Forbidden -> {
                    Result.failure(Exception("You don't have permission to update this shelter"))
                }
                HttpStatusCode.NotFound -> {
                    Result.failure(Exception("Shelter not found"))
                }
                else -> {
                    Result.failure(Exception("Failed to update shelter: ${response.status}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteShelter(id: Int): Result<Unit> {
        return try {
            val response = ApiClient.httpClient.delete("${ApiClient.BASE_URL}/shelters/$id") {
                deviceId?.let { header("X-Device-Id", it) }
                ApiClient.getAuthToken()?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }

            when (response.status) {
                HttpStatusCode.OK -> Result.success(Unit)
                HttpStatusCode.Forbidden -> {
                    Result.failure(Exception("You don't have permission to delete this shelter"))
                }
                HttpStatusCode.NotFound -> {
                    Result.failure(Exception("Shelter not found"))
                }
                else -> {
                    Result.failure(Exception("Failed to delete shelter: ${response.status}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseValidationError(errorBody: String): String {
        return try {
            if (errorBody.contains("\"message\"")) {
                val messageStart = errorBody.indexOf("\"message\":\"") + 11
                val messageEnd = errorBody.indexOf("\"", messageStart)
                if (messageStart > 10 && messageEnd > messageStart) {
                    return errorBody.substring(messageStart, messageEnd)
                }
            }
            "Validation failed"
        } catch (e: Exception) {
            "Validation failed"
        }
    }
}
