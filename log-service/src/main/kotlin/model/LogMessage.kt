package com.Backend_RMP.model

import kotlinx.serialization.Serializable

@Serializable
data class LogMessage(
    val timestamp: String,
    val message: String,
    val level: String = "INFO",
    var source : String,
) 