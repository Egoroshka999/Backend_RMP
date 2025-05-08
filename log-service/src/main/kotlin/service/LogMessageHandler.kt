package com.Backend_RMP.service

import com.Backend_RMP.config.AppConfig
import com.Backend_RMP.model.LogMessage
import io.ktor.util.Identity.encode

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.time.LocalDateTime
import java.sql.Timestamp

class LogMessageHandler(private val config: AppConfig) {
    init {
        Class.forName("com.clickhouse.jdbc.ClickHouseDriver")
        createLogsTableIfNotExists()
    }

    private fun getConnection(): Connection {
        return DriverManager.getConnection(config.clickHouseUrl)
    }

    suspend fun handleMessage(logMessage: LogMessage) {
        withContext(Dispatchers.IO) {
            try {
                getConnection().use { conn ->
                    val sql = """
                        INSERT INTO logs (timestamp, level, message, source)
                        VALUES (?, ?, ?, ?)
                    """.trimIndent()
                    val stmt: PreparedStatement = conn.prepareStatement(sql)
                    val timestamp = Timestamp.valueOf(LocalDateTime.now())
                    stmt.setTimestamp(1, timestamp)
                    stmt.setString(2, logMessage.level)
                    stmt.setString(3, logMessage.message)
                    stmt.setString(4, logMessage.source)

                    stmt.executeUpdate()
                    println("Successfully inserted log message into ClickHouse")
                }
            } catch (e: Exception) {
                println("Error writing log to ClickHouse: ${e.message}")
            }
        }
    }

    private fun createLogsTableIfNotExists() {
        val createTableSQL = """
        CREATE TABLE IF NOT EXISTS logs (
            timestamp DateTime,
            level String,
            message String,
            source String
        ) ENGINE = MergeTree()
        ORDER BY timestamp
    """.trimIndent()

        try {
            getConnection().use { conn ->
                conn.createStatement().use { stmt ->
                    stmt.execute(createTableSQL)
                    println("Ensured 'logs' table exists in ClickHouse.")
                }
            }
        } catch (e: Exception) {
            println("Failed to create 'logs' table: ${e.message}")
        }
    }
}

