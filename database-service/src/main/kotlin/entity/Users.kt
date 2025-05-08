package com.Backend_RMP.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object Users : IntIdTable() {
    val username = varchar("username", 50).uniqueIndex()
    val password = varchar("password", 100)
//    val weight: Column<Float> = float("weight")
//    val height: Column<Float> = float("height")
//    val age: Column<Int> = integer("age")
//    val goal: Column<String> = varchar("goal", 50)
}