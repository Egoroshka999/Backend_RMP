package com.Backend_RMP.routes

import com.Backend_RMP.models.LogDTO
import com.Backend_RMP.tables.Logs
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

fun Route.logRoutes() {
    route("/logs") {
        // Create a new log
        post {
            val log = call.receive<LogDTO>()
            val id = transaction {
                Logs.insertAndGetId {
                    it[message] = log.message
                    it[level] = log.level
                }.value
            }
            
            val createdLog = transaction {
                Logs.select { Logs.id eq id }
                    .map {
                        LogDTO(
                            id = it[Logs.id].value.toString(),
                            message = it[Logs.message],
                            level = it[Logs.level],
                            createdAt = it[Logs.createdAt].toString()
                        )
                    }
                    .single()
            }
            call.respond(createdLog)
        }

        // Get all logs
        get {
            val logs = transaction {
                Logs.selectAll()
                    .map {
                        LogDTO(
                            id = it[Logs.id].value.toString(),
                            message = it[Logs.message],
                            level = it[Logs.level],
                            createdAt = it[Logs.createdAt].toString()
                        )
                    }
            }
            call.respond(logs)
        }
    }
} 