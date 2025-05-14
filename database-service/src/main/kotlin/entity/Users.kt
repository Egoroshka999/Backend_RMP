package com.Backend_RMP.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object Users : IntIdTable() {
    val username = varchar("username", 50).uniqueIndex()
    val password = varchar("password", 100)
    val weight = float("weight").nullable()
    val height = float("height").nullable()
    val age = integer("age").nullable()
    val gender = varchar("gender", 10).nullable()
    val goal = varchar("goal", 50).nullable()
}