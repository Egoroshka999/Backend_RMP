package com.Backend_RMP.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class WaterIntakeDTO(
    val userId: String,
    val datetime: LocalDateTime,
    val amount: Int
)