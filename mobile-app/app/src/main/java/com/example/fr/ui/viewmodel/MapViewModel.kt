package com.example.fr.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fr.data.models.Report
import com.example.fr.data.models.Shelter
import com.example.fr.data.repository.ReportRepository
import com.example.fr.data.repository.ShelterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MapUiState(
    val isLoading: Boolean = false,
    val shelters: List<Shelter> = emptyList(),
    val reports: List<Report> = emptyList(),
    val error: String? = null,
    val shelterSubmitSuccess: Boolean = false
)

class MapViewModel : ViewModel() {
    private val shelterRepository = ShelterRepository()
    private val reportRepository = ReportRepository()

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun setDeviceId(deviceId: String) {
        shelterRepository.setDeviceId(deviceId)
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val sheltersResult = shelterRepository.getShelters()
            val reportsResult = reportRepository.getReports()

            val shelters = sheltersResult.getOrDefault(emptyList())
            val reports = reportsResult.getOrDefault(emptyList())

            val error = when {
                sheltersResult.isFailure && reportsResult.isFailure -> "Failed to load data"
                sheltersResult.isFailure -> "Failed to load shelters"
                reportsResult.isFailure -> "Failed to load reports"
                else -> null
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                shelters = shelters,
                reports = reports,
                error = error
            )
        }
    }

    fun submitShelter(
        name: String,
        description: String,
        address: String?,
        phone: String?,
        latitude: Double,
        longitude: Double,
        capacity: Int?
    ) {
        if (name.isBlank() || description.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please fill in name and description")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, shelterSubmitSuccess = false)

            val result = shelterRepository.submitShelter(
                name = name,
                description = description,
                address = address,
                phone = phone,
                latitude = latitude,
                longitude = longitude,
                capacity = capacity
            )

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        shelterSubmitSuccess = true
                    )
                    loadData() // Reload to get updated list
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to submit shelter"
                    )
                }
            )
        }
    }

    fun deleteShelter(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = shelterRepository.deleteShelter(id)

            result.fold(
                onSuccess = {
                    loadData() // Reload to get updated list
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to delete shelter"
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearShelterSubmitSuccess() {
        _uiState.value = _uiState.value.copy(shelterSubmitSuccess = false)
    }

    fun refresh() {
        loadData()
    }
}
