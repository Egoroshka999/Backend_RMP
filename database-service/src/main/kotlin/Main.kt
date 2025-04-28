package com.Backend_RMP

import com.Backend_RMP.routes.userRoutes
import com.Backend_RMP.routes.logRoutes
import com.Backend_RMP.tables.Users
import com.Backend_RMP.tables.Logs
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
    val host = System.getenv("POSTGRES_HOST") ?: "localhost"

    waitForPostgres(host, 5432)

    Database.connect(
        url = "jdbc:postgresql://$host:5432/app_db",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "postgres"
    )

    transaction {
        SchemaUtils.create(Users, Logs)
    }

    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json()
        }
        routing {
            userRoutes()
            logRoutes()
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
