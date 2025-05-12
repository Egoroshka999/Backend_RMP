package com.Backend_RMP.routes
import MealDTO
import com.Backend_RMP.entity.Meals
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

fun Route.mealRoutes() {
    route("/meals") {
        post {
            val meal = call.receive<MealDTO>()
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
            call.respond(mapOf("id" to id))
        }

        get("/{userId}") {
            val userId = call.parameters["userId"] ?: return@get call.respondText("Missing user ID")
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
            call.respond(meals)
        }

        delete("/{mealId}") {
            val mealId = call.parameters["mealId"] ?: return@delete call.respondText("Missing meal ID")
            val deleted = transaction {
                Meals.deleteWhere { Meals.id eq mealId.toInt() }
            }
            if (deleted > 0) {
                call.respond(mapOf("message" to "Meal deleted successfully"))
            } else {
                call.respondText("Meal not found", status = io.ktor.http.HttpStatusCode.NotFound)
            }
        }
    }
}