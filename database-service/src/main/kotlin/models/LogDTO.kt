package com.Backend_RMP.models

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class LogDTO(
    val id: String? = null,
    val message: String,
    val level: String = "INFO",
    val createdAt: String? = null
)