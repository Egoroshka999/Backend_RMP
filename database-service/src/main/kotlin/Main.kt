package com.Backend_RMP

import com.Backend_RMP.config.AppConfig
import com.Backend_RMP.entity.*
import com.Backend_RMP.routes.*
import com.Backend_RMP.service.MessageProducerService
import com.Backend_RMP.tables.Users
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.coroutines.*
import java.net.Socket

fun main(): Unit = runBlocking {
    val config = AppConfig.load()

    val host = config.postgresHost
    waitForPostgres(host, 5432)

    Database.connect(
        url = config.postgresDsn,
        driver = "org.postgresql.Driver",
        user = config.postgresUser,
        password = config.postgresPass
    )

    val messageHandler = MessageProducerService(config)

    transaction {
        SchemaUtils.create(
            Activities,
            Articles,
            HealthMetrics,
            Meals,
            SleepRecords,
            Users,
            WaterIntake
        )
    }

    embeddedServer(Netty, port = config.serverPort) {
        install(ContentNegotiation) {
            json()
        }
        routing {
            activityRoutes(messageHandler)
            articleRoutes(messageHandler)
            healthRoutes(messageHandler)
            mealRoutes(messageHandler)
            sleepRoutes(messageHandler)
            userRoutes(messageHandler)
            waterRoutes(messageHandler)
        }
    }.start(wait = true)
}

suspend fun waitForPostgres(host: String, port: Int) {
    repeat(20) {
        try {
            Socket(host, port).use {
                println("PostgreSQL доступен!")
                return
            }
        } catch (e: Exception) {
            println("Ожидание PostgreSQL (${it + 1}/20)...")
            delay(1000)
        }
    }
    throw RuntimeException("Не удалось подключиться к PostgreSQL")
}
