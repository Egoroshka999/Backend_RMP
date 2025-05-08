package com.Backend_RMP.routes

import com.Backend_RMP.model.LogMessage
import com.Backend_RMP.service.LogMessageHandler
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class LogRoutes(private val messageHandler: LogMessageHandler) {
    fun configure(routing: Routing) {
        routing {
            post("/logs") {
                try {
                    val logMessage = call.receive<LogMessage>()
                    messageHandler.handleMessage(logMessage)
                    call.respond(HttpStatusCode.OK, "Log message received successfully")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Error processing log message: ${e.message}")
                }
            }
            get("/logs") {
                val logs = messageHandler.getTop100Logs()
                call.respond(HttpStatusCode.OK, logs)
            }
        }
    }
} 