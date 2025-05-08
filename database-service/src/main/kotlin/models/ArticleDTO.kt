package com.Backend_RMP.models

import kotlinx.serialization.Serializable

@Serializable
data class ArticleDTO(
    val title: String,
    val content: String,
    val author: String,
    val imageUrl: String?
)