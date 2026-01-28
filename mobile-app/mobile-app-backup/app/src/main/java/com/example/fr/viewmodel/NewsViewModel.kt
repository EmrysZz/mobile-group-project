package com.example.fr.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fr.model.News
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class NewsViewModel : ViewModel() {

    private val _newsList = MutableStateFlow<List<News>>(emptyList())
    val newsList = _newsList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // HTTP Client
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private val BASE_URL = "http://10.0.2.2:8000/api/"

    init {
        fetchNews()
    }

    /**
     * Fetch news/announcements from backend API
     */
    fun fetchNews() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // GET request to /api/news
                val response: List<News> = client.get("${BASE_URL}news").body()
                _newsList.value = response
                Log.d("NewsViewModel", "Fetched ${response.size} news items from API")
            } catch (e: Exception) {
                Log.e("NewsViewModel", "Error fetching news", e)
                _errorMessage.value = "Failed to load news: ${e.message}"

                // Fallback to mock data if API fails
                val mockNews = listOf(
                    News(
                        id = 1,
                        title = "Flood Warning Issued for East Coast",
                        content = "Heavy rainfall expected over the next 48 hours. Residents in low-lying areas are advised to evacuate immediately.",
                        publishedAt = "2024-12-25 08:00 AM"
                    ),
                    News(
                        id = 2,
                        title = "New Relief Centers Opened",
                        content = "Three new evacuation centers have been opened in the Hulu Langat district to accommodate displaced families.",
                        publishedAt = "2024-12-24 06:30 PM"
                    )
                )
                _newsList.value = mockNews
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        client.close()
    }
}
