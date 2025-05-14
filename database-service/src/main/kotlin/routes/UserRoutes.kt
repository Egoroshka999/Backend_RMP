package com.Backend_RMP.routes

import com.Backend_RMP.entity.LogMessage
import com.Backend_RMP.models.UserDTO
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


fun Route.userRoutes(producer: MessageProducerService) {
    route("/users") {
        post {
            val user = call.receive<UserDTO>()

            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Received user with username: ${user.username}",
                source = "POST /users"
            )))

            val id = transaction {
                Users.insertAndGetId {
                    it[username] = user.username
                    it[password] = user.password
                    it[age] = user.age
                    it[weight] = user.weight
                    it[height] = user.height
                    it[gender] = user.gender
                    it[goal] = user.goal
                }.value
            }

            val log = LogMessage(
                message = "Created user with ID: $id",
                source = "POST /users"
            )
            producer.sendMessage(Json.encodeToString(log))

            call.respond(
                UserDTO(
                    id.toString(),
                    user.username,
                    user.password,
                    user.age,
                    user.weight,
                    user.height,
                    user.gender,
                    user.goal
                )
            )
        }

        get("/by-username/{username}") {
            val username = call.parameters["username"] ?: return@get call.respondText("Missing username")
            
            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Fetching user with username: $username",
                source = "GET /users/by-username/{username}"
            )))

            val result = transaction {
                Users.select { Users.username eq username }.singleOrNull()
            }

            if (result != null) {
                producer.sendMessage(Json.encodeToString(LogMessage(
                    message = "Found user with username: $username",
                    source = "GET /users/by-username/{username}"
                )))
                call.respond(
                    UserDTO(
                        result[Users.id].value.toString(),
                        result[Users.username],
                        result[Users.password],
                        result[Users.age],
                        result[Users.weight],
                        result[Users.height],
                        result[Users.gender],
                        result[Users.goal]
                    )
                )
            } else {
                producer.sendMessage(Json.encodeToString(LogMessage(
                    message = "User not found with username: $username",
                    source = "GET /users/by-username/{username}"
                )))
                call.respondText("User not found")
            }
        }

        post("/check") {
            val user = call.receive<UserDTO>()
            
            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Checking credentials for user: ${user.username}",
                source = "POST /users/check"
            )))

            val result = transaction {
                Users.select {
                    Users.username eq user.username and (Users.password eq user.password)
                }.singleOrNull()
            }

            if (result != null) {
                producer.sendMessage(Json.encodeToString(LogMessage(
                    message = "Successfully authenticated user: ${user.username}",
                    source = "POST /users/check"
                )))
                call.respond(
                    UserDTO(
                        result[Users.id].value.toString(),
                        result[Users.username],
                        result[Users.password],
                        result[Users.age],
                        result[Users.weight],
                        result[Users.height],
                        result[Users.gender],
                        result[Users.goal]
                    )
                )
            } else {
                producer.sendMessage(Json.encodeToString(LogMessage(
                    message = "Authentication failed for user: ${user.username}",
                    source = "POST /users/check"
                )))
                call.respondText("Unauthorized")
            }
        }

        delete("/{id}") {
            val userId = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respondText("Invalid ID")
            
            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Attempting to delete user with ID: $userId",
                source = "DELETE /users/{id}"
            )))

            val deleted = transaction {
                Users.deleteWhere { Users.id eq userId } > 0
            }

            if (deleted) {
                producer.sendMessage(Json.encodeToString(LogMessage(
                    message = "Successfully deleted user with ID: $userId",
                    source = "DELETE /users/{id}"
                )))
                call.respond(mapOf("message" to "User deleted successfully"))
            } else {
                producer.sendMessage(Json.encodeToString(LogMessage(
                    message = "Failed to delete user with ID: $userId - not found",
                    source = "DELETE /users/{id}"
                )))
                call.respondText("User not found", status = io.ktor.http.HttpStatusCode.NotFound)
            }
        }
    }
}