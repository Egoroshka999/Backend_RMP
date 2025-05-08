package com.Backend_RMP.routes

import com.Backend_RMP.entity.Activities
import com.Backend_RMP.models.ActivityDTO
import com.Backend_RMP.models.LogDTO
import com.Backend_RMP.tables.Logs
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.activityRoutes() {
    route("/activities") {
        post {
            val activity = call.receive<ActivityDTO>()
            val id = transaction {
                Activities.insertAndGetId {
                    it[user] = activity.userId.toInt()
                    it[date] = activity.date
                    it[type] = activity.type
                    it[duration] = activity.duration
                    it[steps] = activity.steps
                }.value
            }
            call.respond(mapOf("id" to id))
        }

        get("/{userId}") {
            val userId = call.parameters["userId"] ?: return@get call.respondText("Missing user ID")
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
            call.respond(activities)
        }
    }
}