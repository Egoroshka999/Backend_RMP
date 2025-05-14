package com.Backend_RMP.routes
import MealDTO
import com.Backend_RMP.entity.LogMessage
import com.Backend_RMP.entity.Meals
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

fun Route.mealRoutes(producer: MessageProducerService) {
    route("/meals") {
        post {
            val meal = call.receive<MealDTO>()

            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Received meal with user ID: ${meal.userId}",
                source = "POST /meals"
            )))

            val id = transaction {
                Meals.insertAndGetId {
                    it[user] = meal.userId.toInt()
                    it[datetime] = meal.datetime
                    it[mealType] = meal.mealType
                    it[calories] = meal.calories
                    it[proteins] = meal.proteins
                    it[fats] = meal.fats
                    it[carbs] = meal.carbs
                }.value
            }

            val log = LogMessage(
                message = "Created meal with ID: $id",
                source = "POST /meals"
            )
            producer.sendMessage(Json.encodeToString(log))

            call.respond(mapOf("id" to id))
        }

        get("/{userId}") {
            val userId = call.parameters["userId"] ?: return@get call.respondText("Missing user ID")
            
            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Fetching meals for user ID: $userId",
                source = "GET /meals/{userId}"
            )))

            val meals = transaction {
                Meals.select { Meals.user eq userId.toInt() }
                    .map {
                        MealDTO(
                            userId = it[Meals.user].value.toString(),
                            datetime = it[Meals.datetime],
                            mealType = it[Meals.mealType],
                            calories = it[Meals.calories],
                            proteins = it[Meals.proteins],
                            fats = it[Meals.fats],
                            carbs = it[Meals.carbs]
                        )
                    }
            }

            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Retrieved ${meals.size} meals for user ID: $userId",
                source = "GET /meals/{userId}"
            )))

            call.respond(meals)
        }

        delete("/{mealId}") {
            val mealId = call.parameters["mealId"] ?: return@delete call.respondText("Missing meal ID")
            
            producer.sendMessage(Json.encodeToString(LogMessage(
                message = "Attempting to delete meal with ID: $mealId",
                source = "DELETE /meals/{mealId}"
            )))

            val deleted = transaction {
                Meals.deleteWhere { Meals.id eq mealId.toInt() }
            }

            if (deleted > 0) {
                producer.sendMessage(Json.encodeToString(LogMessage(
                    message = "Successfully deleted meal with ID: $mealId",
                    source = "DELETE /meals/{mealId}"
                )))
                call.respond(mapOf("message" to "Meal deleted successfully"))
            } else {
                producer.sendMessage(Json.encodeToString(LogMessage(
                    message = "Failed to delete meal with ID: $mealId - not found",
                    source = "DELETE /meals/{mealId}"
                )))
                call.respondText("Meal not found", status = io.ktor.http.HttpStatusCode.NotFound)
            }
        }
    }
}