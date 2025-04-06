package com.Backend_RMP

import com.Backend_RMP.routes.userRoutes
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

fun main() {
    Database.connect(
        url = "jdbc:postgresql://postgres:5432/app_db",
        driver = "org.postgresql.Driver",
        user = "admin",
        password = "secret"
    )

    transaction {
        SchemaUtils.create(Users)
    }

    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json()
        }
        routing {
            userRoutes()
        }
    }.start(wait = true)
}