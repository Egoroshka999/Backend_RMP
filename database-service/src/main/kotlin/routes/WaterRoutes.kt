package com.Backend_RMP.routes

import com.Backend_RMP.entity.WaterIntake
import com.Backend_RMP.models.UserDTO
import com.Backend_RMP.models.WaterIntakeDTO
import com.Backend_RMP.tables.Users
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
// Routes
fun Route.waterRoutes() {
    route("/water") {
        post {
            val water = call.receive<WaterIntakeDTO>()
            val id = transaction {
                WaterIntake.insertAndGetId {
                    it[user] = water.userId.toInt()
                    it[datetime] = water.datetime
                    it[amount] = water.amount
                }.value
            }
            call.respond(mapOf("id" to id))
        }

        get("/{userId}") {
            val userId = call.parameters["userId"] ?: return@get call.respondText("Missing user ID")
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
            call.respond(records)
        }
    }
}