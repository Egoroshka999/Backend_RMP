package com.Backend_RMP.entity

import com.Backend_RMP.tables.Users
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.kotlin.datetime.time

object Meals : IntIdTable() {
    val user = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val datetime: Column<LocalDateTime> = datetime("datetime")
    val mealType: Column<String> = varchar("meal_type", 20)
    val calories: Column<Int> = integer("calories")
    val proteins: Column<Int> = integer("proteins")
    val fats: Column<Int> = integer("fats")
    val carbs: Column<Int> = integer("carbs")
}