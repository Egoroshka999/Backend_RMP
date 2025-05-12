package com.Backend_RMP.routes
import com.Backend_RMP.entity.Activities
import com.Backend_RMP.entity.HealthMetrics
import com.Backend_RMP.models.HealthMetricDTO
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

fun Route.healthRoutes() {
    route("/health") {
        post {
            val metric = call.receive<HealthMetricDTO>()
            val id = transaction {
                HealthMetrics.insertAndGetId {
                    it[user] = metric.userId.toInt()
                    it[datetime] = metric.datetime
                    it[pulse] = metric.pulse
                    it[bloodPressure] = metric.bloodPressure
                    it[deviceData] = metric.deviceData
                }.value
            }
            call.respond(mapOf("id" to id))
        }

        get("/{userId}") {
            val userId = call.parameters["userId"] ?: return@get call.respondText("Missing user ID")
            val metrics = transaction {
                HealthMetrics.select { HealthMetrics.user eq userId.toInt() }
                    .map {
                        HealthMetricDTO(
                            userId = it[HealthMetrics.user].value.toString(),
                            datetime = it[HealthMetrics.datetime],
                            pulse = it[HealthMetrics.pulse],
                            bloodPressure = it[HealthMetrics.bloodPressure],
                            deviceData = it[HealthMetrics.deviceData]
                        )
                    }
            }
            call.respond(metrics)
        }

        delete("/{id}") {
            val metricId = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respondText("Invalid ID")
            val deleted = transaction {
                HealthMetrics.deleteWhere { HealthMetrics.id eq metricId } > 0
            }
            if (deleted) {
                call.respond(mapOf("message" to "Health metric deleted successfully"))
            } else {
                call.respondText("Health metric not found", status = io.ktor.http.HttpStatusCode.NotFound)
            }
        }
    }
}