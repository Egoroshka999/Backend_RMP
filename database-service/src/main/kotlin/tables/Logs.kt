package com.Backend_RMP.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import java.sql.Timestamp

object Logs : IntIdTable() {
    val message: Column<String> = text("message")
    val level: Column<String> = varchar("level", 20).default("INFO")
    val createdAt: Column<String> = varchar("created_at", 50).default(Timestamp(System.currentTimeMillis()).toString())
}
