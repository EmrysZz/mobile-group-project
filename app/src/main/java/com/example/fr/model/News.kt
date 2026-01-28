package com.example.fr.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class News(
    val id: Int? = null,
    val title: String,
    val content: String,
    @SerialName("published_at") val publishedAt: String
)
