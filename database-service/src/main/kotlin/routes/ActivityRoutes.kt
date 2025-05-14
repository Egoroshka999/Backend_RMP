package com.Backend_RMP.routes

import com.Backend_RMP.entity.Activities
import com.Backend_RMP.entity.LogMessage
import com.Backend_RMP.models.ActivityDTO
import com.Backend_RMP.service.MessageProducerService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

fun Route.activityRoutes(producer: MessageProducerService) {
    route("/activities") {
        post {
            val activity = call.receive<ActivityDTO>()

            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Received activity with user ID: ${activity.userId}",
                source = "POST /activities"
            )))

            val id = transaction {
                Activities.insertAndGetId {
                    it[user] = activity.userId.toInt()
                    it[date] = activity.date
                    it[type] = activity.type
                    it[duration] = activity.duration
                    it[steps] = activity.steps
                }.value
            }

            val log = LogMessage(
                message = "Created activity with ID: $id",
                source = "POST /activities"
            )
            producer.sendMessage(Json.encodeToString(log))

            call.respond(mapOf("id" to id))
        }

        get("/{userId}") {
            val userId = call.parameters["userId"] ?: return@get call.respondText("Missing user ID")
            
            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Fetching activities for user ID: $userId",
                source = "GET /activities/{userId}"
            )))

            val activities = transaction {
                Activities.select { Activities.user eq userId.toInt() }
                    .map {
                        ActivityDTO(
                            userId = it[Activities.user].value.toString(),
                            date = it[Activities.date],
                            type = it[Activities.type],
                            duration = it[Activities.duration],
                            steps = it[Activities.steps]
                        )
                    }
            }

            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Retrieved ${activities.size} activities for user ID: $userId",
                source = "GET /activities/{userId}"
            )))

            call.respond(activities)
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respondText("Invalid ID")
            
            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Attempting to delete activity with ID: $id",
                source = "DELETE /activities/{id}"
            )))

            val deleted = transaction {
                Activities.deleteWhere { Activities.id eq id } > 0
            }

            if (deleted) {
                producer.sendMessage(Json.encodeToString(LogMessage(
                    message = "Successfully deleted activity with ID: $id",
                    source = "DELETE /activities/{id}"
                )))
                call.respondText("Activity deleted successfully")
            } else {
                producer.sendMessage(Json.encodeToString(LogMessage(
                    message = "Failed to delete activity with ID: $id - not found",
                    source = "DELETE /activities/{id}"
                )))
                call.respondText("Activity not found")
            }
        }
    }
}