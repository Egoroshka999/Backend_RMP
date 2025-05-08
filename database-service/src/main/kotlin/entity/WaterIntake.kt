package com.Backend_RMP.entity

import com.Backend_RMP.tables.Users
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object WaterIntake : IntIdTable() {
    val user = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val datetime: Column<LocalDateTime> = datetime("datetime")
    val amount: Column<Int> = integer("amount_ml")
}