package com.Backend_RMP.entity

import com.Backend_RMP.tables.Users
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.time

object SleepRecords : IntIdTable() {
    val user = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val date: Column<LocalDate> = date("date")
    val startTime: Column<LocalTime> = time("start_time")
    val endTime: Column<LocalTime> = time("end_time")
    val quality: Column<Int> = integer("quality")
}