package com.Backend_RMP.models

import com.Backend_RMP.entity.SleepRecords
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow

@Serializable
data class SleepDTO(
    val userId: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val quality: Int
)

//// Маппинг из ResultRow в DTO
//fun ResultRow.toSleepDTO() = SleepDTO(
//    userId = this[SleepRecords.user].value.toString(),
//    date = this[SleepRecords.date],
//    startTime = this[SleepRecords.startTime],
//    endTime = this[SleepRecords.endTime],
//    quality = this[SleepRecords.quality]
//)