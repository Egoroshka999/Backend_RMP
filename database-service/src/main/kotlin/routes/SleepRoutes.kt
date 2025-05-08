package com.Backend_RMP.routes

import com.Backend_RMP.entity.SleepRecords
import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.Backend_RMP.models.SleepDTO
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.sleepRoutes() {
    route("/sleep") {
        post {
            val sleep = call.receive<SleepDTO>()
            val id = transaction {
                SleepRecords.insertAndGetId {
                    it[user] = sleep.userId.toInt()
                    it[date] = sleep.date
                    it[startTime] = sleep.startTime
                    it[endTime] = sleep.endTime
                    it[quality] = sleep.quality
                }.value
            }
            call.respond(mapOf("id" to id))
        }

        get("/{userId}") {
            val userId = call.parameters["userId"] ?: return@get call.respondText("Missing user ID")
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
            call.respond(records)
        }
        put("/{id}") { //not tested
            val sleepId = call.parameters["id"]?.toIntOrNull()
                ?: return@put call.respondText("Invalid ID")

            val updatedSleep = call.receive<SleepDTO>()

            transaction {
                SleepRecords.update({ SleepRecords.id eq sleepId }) {
                    it[date] = updatedSleep.date
                    it[startTime] = updatedSleep.startTime
                    it[endTime] = updatedSleep.endTime
                    it[quality] = updatedSleep.quality
                }
            }

            call.respond(mapOf("status" to "updated"))
        }
    }
}
