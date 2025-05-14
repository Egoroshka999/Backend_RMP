package com.Backend_RMP.routes

import com.Backend_RMP.models.UserDTO
import com.Backend_RMP.tables.Users
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


fun Route.userRoutes() {
    route("/users") {
        post {
            val user = call.receive<UserDTO>()
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
            val result = transaction {
                Users.select { Users.username eq username }.singleOrNull()
            }
            if (result != null) {
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
                call.respondText("User not found")
            }
        }

        post("/check") {
            val user = call.receive<UserDTO>()
            val result = transaction {
                Users.select {
                    Users.username eq user.username and (Users.password eq user.password)
                }.singleOrNull()
            }
            if (result != null) {
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
                call.respondText("Unauthorized")
            }
        }

        delete("/{id}") {
            val userId = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respondText("Invalid ID")
            val deleted = transaction {
                Users.deleteWhere { Users.id eq userId } > 0
            }
            if (deleted) {
                call.respond(mapOf("message" to "User deleted successfully"))
            } else {
                call.respondText("User not found", status = io.ktor.http.HttpStatusCode.NotFound)
            }
        }
    }
}