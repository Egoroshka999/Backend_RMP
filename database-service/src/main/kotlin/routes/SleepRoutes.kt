package com.Backend_RMP.routes

import com.Backend_RMP.entity.LogMessage
import com.Backend_RMP.entity.SleepRecords
import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.Backend_RMP.models.SleepDTO
import com.Backend_RMP.service.MessageProducerService
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

fun Route.sleepRoutes(producer: MessageProducerService) {
    route("/sleep") {
        post {
            val sleep = call.receive<SleepDTO>()

            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Received sleep record with user ID: ${sleep.userId}",
                source = "POST /sleep"
            )))

            val id = transaction {
                SleepRecords.insertAndGetId {
                    it[user] = sleep.userId.toInt()
                    it[date] = sleep.date
                    it[startTime] = sleep.startTime
                    it[endTime] = sleep.endTime
                    it[quality] = sleep.quality
                }.value
            }

            val log = LogMessage(
                message = "Created sleep record with ID: $id",
                source = "POST /sleep"
            )
            producer.sendMessage(Json.encodeToString(log))

            call.respond(mapOf("id" to id))
        }

        get("/{userId}") {
            val userId = call.parameters["userId"] ?: return@get call.respondText("Missing user ID")
            
            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Fetching sleep records for user ID: $userId",
                source = "GET /sleep/{userId}"
            )))

            val records = transaction {
                SleepRecords.select { SleepRecords.user eq userId.toInt() }
                    .map {
                        SleepDTO(
                            userId = it[SleepRecords.user].value.toString(),
                            date = it[SleepRecords.date],
                            startTime = it[SleepRecords.startTime],
                            endTime = it[SleepRecords.endTime],
                            quality = it[SleepRecords.quality]
                        )
                    }
            }

            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Retrieved ${records.size} sleep records for user ID: $userId",
                source = "GET /sleep/{userId}"
            )))

            call.respond(records)
        }

        put("/{id}") {
            val sleepId = call.parameters["id"]?.toIntOrNull()
                ?: return@put call.respondText("Invalid ID")

            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Attempting to update sleep record with ID: $sleepId",
                source = "PUT /sleep/{id}"
            )))

            val updatedSleep = call.receive<SleepDTO>()

            val updated = transaction {
                SleepRecords.update({ SleepRecords.id eq sleepId }) {
                    it[date] = updatedSleep.date
                    it[startTime] = updatedSleep.startTime
                    it[endTime] = updatedSleep.endTime
                    it[quality] = updatedSleep.quality
                }
            }

            if (updated > 0) {
                producer.sendMessage(Json.encodeToString(LogMessage(
                    message = "Successfully updated sleep record with ID: $sleepId",
                    source = "PUT /sleep/{id}"
                )))
                call.respond(mapOf("status" to "updated"))
            } else {
                producer.sendMessage(Json.encodeToString(LogMessage(
                    message = "Failed to update sleep record with ID: $sleepId - not found",
                    source = "PUT /sleep/{id}"
                )))
                call.respondText("Sleep record not found", status = io.ktor.http.HttpStatusCode.NotFound)
            }
        }

        delete("/{id}") {
            val sleepId = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respondText("Invalid ID")
            
            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Attempting to delete sleep record with ID: $sleepId",
                source = "DELETE /sleep/{id}"
            )))

            val deleted = transaction {
                SleepRecords.deleteWhere { SleepRecords.id eq sleepId } > 0
            }

            if (deleted) {
                producer.sendMessage(Json.encodeToString(LogMessage(
                    message = "Successfully deleted sleep record with ID: $sleepId",
                    source = "DELETE /sleep/{id}"
                )))
                call.respond(mapOf("message" to "Sleep record deleted successfully"))
            } else {
                producer.sendMessage(Json.encodeToString(LogMessage(
                    message = "Failed to delete sleep record with ID: $sleepId - not found",
                    source = "DELETE /sleep/{id}"
                )))
                call.respondText("Sleep record not found", status = io.ktor.http.HttpStatusCode.NotFound)
            }
        }
    }
}
