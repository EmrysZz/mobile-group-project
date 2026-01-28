package com.example.fr.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fr.model.LocationData
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class MapViewModel : ViewModel() {

    // Shared state across ViewModel instances
    companion object {
        private val _allLocations = MutableStateFlow<List<LocationData>>(emptyList())
        private val _selectedFilters = MutableStateFlow<Set<String>>(setOf("Flood", "Shelter", "Blocked"))
    }
    
    val selectedFilters = _selectedFilters.asStateFlow()

    // Derived state for filtered locations
    private val _locations = MutableStateFlow<List<LocationData>>(emptyList())
    val locations = _locations.asStateFlow()

    // Loading and error states
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // HTTP Client with JSON serialization
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            })
        }
    }

    // Replace with your actual local IP if running on physical device
    // Emulator: use 10.0.2.2 | Physical device: use your computer's LAN IP
    private val BASE_URL = "http://10.0.2.2:8000/api/"

    init {
        // Observe changes to allLocations or selectedFilters and update _locations
        viewModelScope.launch {
            kotlinx.coroutines.flow.combine(_allLocations, _selectedFilters) { all, filters ->
                if (filters.isEmpty()) {
                    emptyList() 
                } else {
                    val normalizedFilters = filters.map { it.lowercase() }.toSet()
                    all.filter { it.type.lowercase() in normalizedFilters }
                }
            }.collect { filtered ->
                _locations.value = filtered
            }
        }
        
        // Fetch locations from server on init
        fetchLocations()
    }

    fun toggleFilter(type: String) {
        val current = _selectedFilters.value
        if (current.contains(type)) {
            _selectedFilters.value = current - type
        } else {
            _selectedFilters.value = current + type
        }
    }

    /**
     * Fetch all incident reports from the backend API
     */
    fun fetchLocations() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                // GET request to /api/reports
                val response: List<LocationData> = client.get("${BASE_URL}reports").body()
                _allLocations.value = response
                Log.d("MapViewModel", "Fetched ${response.size} locations from API")
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error fetching locations", e)
                _errorMessage.value = "Failed to load reports: ${e.message}"
                
                // Fallback to mock data if API fails
                val fallbackData = listOf(
                    LocationData(
                        type = "Flood",
                        latitude = 3.140853,
                        longitude = 101.693207,
                        reportedTime = "2024-12-20 10:00",
                        reportedBy = "Admin",
                        description = "Flooding at Dataran Merdeka (Offline Mode)"
                    )
                )
                _allLocations.value = fallbackData
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Submit a new incident report to the backend API
     */
    fun reportIncident(type: String, description: String, lat: Double, lng: Double, user: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                // Create request body matching backend expectations
                val requestBody = mapOf(
                    "user_name" to user,
                    "incident_type" to type,
                    "description" to description,
                    "latitude" to lat,
                    "longitude" to lng
                )
                
                // POST request to /api/reports
                val response: LocationData = client.post("${BASE_URL}reports") {
                    contentType(ContentType.Application.Json)
                    setBody(requestBody)
                }.body()
                
                // Add the new report to local state
                _allLocations.value = _allLocations.value + response
                Log.d("MapViewModel", "Report submitted successfully: ${response.id}")
                
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error submitting report", e)
                _errorMessage.value = "Failed to submit report: ${e.message}"
                
                // Fallback: Add locally if API fails  
                val fallbackLocation = LocationData(
                    type = type,
                    latitude = lat,
                    longitude = lng,
                    reportedTime = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date()),
                    reportedBy = user,
                    description = "$description (Pending Sync)"
                )
                _allLocations.value = _allLocations.value + fallbackLocation
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Verify (upvote/downvote) an incident report
     * Calls backend API to update verification_count
     */
    fun verifyLocation(location: LocationData, isUpvote: Boolean) {
        viewModelScope.launch {
            try {
                val change = if (isUpvote) 1 else -1
                
                // API call to backend verification endpoint
                val response: LocationData = client.put("${BASE_URL}reports/${location.id}/verify") {
                    contentType(ContentType.Application.Json)
                    setBody(mapOf("value" to change))
                }.body()
                
                // Update local state with verified report from server
                val updatedList = _allLocations.value.map {
                    if (it.id == location.id) response else it
                }
                _allLocations.value = updatedList
                
                Log.d("MapViewModel", "Verification updated successfully via API")
                
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error verifying location via API", e)
                _errorMessage.value = "Failed to verify report"
                
                // Fallback: Update locally if API call fails
                val updatedList = _allLocations.value.map {
                    if (it.id == location.id) {
                        it.copy(verificationCount = it.verificationCount + (if (isUpvote) 1 else -1))
                    } else {
                        it
                    }
                }
                _allLocations.value = updatedList
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        client.close()
    }
}
