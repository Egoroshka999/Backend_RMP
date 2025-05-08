package com.Backend_RMP.entity


import com.Backend_RMP.tables.Users
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.date

object Activities : IntIdTable() {
    val user = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val date: Column<LocalDate> = date("date")
    val type: Column<String> = varchar("type", 50)
    val duration: Column<Int> = integer("duration_minutes")
    val steps: Column<Int?> = integer("steps").nullable()
}
