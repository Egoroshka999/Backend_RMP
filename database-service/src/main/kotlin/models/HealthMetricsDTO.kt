package com.Backend_RMP.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class HealthMetricDTO(
    val userId: String,
    val datetime: LocalDateTime,
    val pulse: Int?,
    val bloodPressure: String?,
    val deviceData: String?
)