package com.Backend_RMP.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object Users : IntIdTable() {
    val username: Column<String> = varchar("username", 50).uniqueIndex()
    val password: Column<String> = varchar("password", 100)
    val weight: Column<Float?> = float("weight").nullable()
    val height: Column<Float?> = float("height").nullable()
    val age: Column<Int?> = integer("age").nullable()
    val gender: Column<String?> = varchar("gender", 10).nullable()
    val goal: Column<String?> = varchar("goal", 50).nullable()
}