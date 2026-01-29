package com.example.fr.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fr.data.models.Report
import com.example.fr.data.repository.AlreadyVotedException
import com.example.fr.data.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReportUiState(
    val isLoading: Boolean = false,
    val reports: List<Report> = emptyList(),
    val submitSuccess: Boolean = false,
    val error: String? = null
)

class ReportViewModel : ViewModel() {
    private val repository = ReportRepository()

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    init {
        loadReports()
    }

    fun setDeviceId(deviceId: String) {
        repository.setDeviceId(deviceId)
        loadReports() // Reload to get vote status
    }

    fun loadReports() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.getReports()
            result.fold(
                onSuccess = { reportsList ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        reports = reportsList
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load reports"
                    )
                }
            )
        }
    }

    fun submitReport(
        userName: String,
        incidentType: String,
        description: String,
        latitude: Double,
        longitude: Double
    ) {
        if (userName.isBlank() || incidentType.isBlank() || description.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please fill in all fields")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, submitSuccess = false)
            val result = repository.submitReport(userName, incidentType, description, latitude, longitude)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        submitSuccess = true
                    )
                    loadReports()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to submit report"
                    )
                }
            )
        }
    }

    fun verifyReport(reportId: Int, upvote: Boolean) {
        viewModelScope.launch {
            val value = if (upvote) 1 else -1
            val result = repository.verifyReport(reportId, value)
            result.fold(
                onSuccess = {
                    loadReports() // Reload to get updated vote status
                },
                onFailure = { error ->
                    val message = when (error) {
                        is AlreadyVotedException -> error.message ?: "You have already voted on this report"
                        else -> error.message ?: "Failed to verify report"
                    }
                    _uiState.value = _uiState.value.copy(error = message)
                }
            )
        }
    }

    fun clearSubmitSuccess() {
        _uiState.value = _uiState.value.copy(submitSuccess = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun refresh() {
        loadReports()
    }
}
