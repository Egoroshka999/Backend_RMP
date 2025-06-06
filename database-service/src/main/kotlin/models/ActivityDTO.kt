package com.Backend_RMP.models

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class ActivityDTO(
    val userId: String,
    val date: LocalDate,
    val type: String,
    val duration: Int,
    val steps: Int?
)