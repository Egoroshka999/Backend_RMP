package com.Backend_RMP.routes

import com.Backend_RMP.entity.LogMessage
import com.Backend_RMP.entity.WaterIntake
import com.Backend_RMP.models.UserDTO
import com.Backend_RMP.models.WaterIntakeDTO
import com.Backend_RMP.service.MessageProducerService
import com.Backend_RMP.tables.Users
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


// Routes
fun Route.waterRoutes(producer: MessageProducerService) {
    route("/water") {
        post {
            val water = call.receive<WaterIntakeDTO>()

            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Received water intake with user ID: ${water.userId}",
                source = "POST /water"
            )))

            val id = transaction {
                WaterIntake.insertAndGetId {
                    it[user] = water.userId.toInt()
                    it[datetime] = water.datetime
                    it[amount] = water.amount
                }.value
            }

            val log = LogMessage(
                message = "Created water intake with ID: $id",
                source = "POST /water"
            )
            producer.sendMessage(Json.encodeToString(log))

            call.respond(mapOf("id" to id))
        }

        get("/{userId}") {
            val userId = call.parameters["userId"] ?: return@get call.respondText("Missing user ID")
            
            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Fetching water intake records for user ID: $userId",
                source = "GET /water/{userId}"
            )))

            val records = transaction {
                WaterIntake.select { WaterIntake.user eq userId.toInt() }
                    .map {
                        WaterIntakeDTO(
                            userId = it[WaterIntake.user].value.toString(),
                            datetime = it[WaterIntake.datetime],
                            amount = it[WaterIntake.amount]
                        )
                    }
            }

            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Retrieved ${records.size} water intake records for user ID: $userId",
                source = "GET /water/{userId}"
            )))

            call.respond(records)
        }

        delete("/{id}") {
            val waterId = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respondText("Invalid ID")
            
            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Attempting to delete water intake record with ID: $waterId",
                source = "DELETE /water/{id}"
            )))

            val deleted = transaction {
                WaterIntake.deleteWhere { WaterIntake.id eq waterId } > 0
            }

            if (deleted) {
                producer.sendMessage(Json.encodeToString(LogMessage(
                    message = "Successfully deleted water intake record with ID: $waterId",
                    source = "DELETE /water/{id}"
                )))
                call.respond(mapOf("message" to "Water intake record deleted successfully"))
            } else {
                producer.sendMessage(Json.encodeToString(LogMessage(
                    message = "Failed to delete water intake record with ID: $waterId - not found",
                    source = "DELETE /water/{id}"
                )))
                call.respondText("Water intake record not found", status = io.ktor.http.HttpStatusCode.NotFound)
            }
        }
    }
}