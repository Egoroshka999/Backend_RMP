package com.Backend_RMP.entity

import com.Backend_RMP.tables.Users
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object HealthMetrics : IntIdTable() {
    val user = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val datetime: Column<LocalDateTime> = datetime("datetime")
    val pulse: Column<Int?> = integer("pulse").nullable()
    val bloodPressure: Column<String?> = varchar("blood_pressure", 10).nullable()
    val deviceData: Column<String?> = text("device_data").nullable()
}