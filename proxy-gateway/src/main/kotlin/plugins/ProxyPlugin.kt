package com.Backend_RMP.plugins

import com.Backend_RMP.service.ProxyService
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureProxy(proxyService: ProxyService) {
    routing {
        // Auth routes
        route("/auth") {
            post("/login") {
                proxyRequest(call, "${proxyService.getServiceUrl("/auth")}/auth/login", proxyService)
            }
            post("/register") {
                proxyRequest(call, "${proxyService.getServiceUrl("/auth")}/auth/register", proxyService)
            }
            get("/verify") {
                proxyRequest(call, "${proxyService.getServiceUrl("/auth")}/auth/verify", proxyService)
            }
        }

        // Users routes
        route("/users") {
            post {
                proxyRequest(call, "${proxyService.getServiceUrl("/users")}/users", proxyService)
            }
            get("/by-username/{username}") {
                val username = call.parameters["username"] ?: ""
                proxyRequest(call, "${proxyService.getServiceUrl("/users")}/users/by-username/$username", proxyService)
            }
            post("/check") {
                proxyRequest(call, "${proxyService.getServiceUrl("/users")}/users/check", proxyService)
            }
            delete ("/{userId}") {
                val userId = call.parameters["userId"] ?: ""
                proxyRequest(call, "${proxyService.getServiceUrl("/users")}/users/$userId", proxyService)
            }
        }

        // Meals routes
        route("/meals") {
            post {
                proxyRequest(call, "${proxyService.getServiceUrl("/meals")}/meals", proxyService)
            }
            get("/{userId}") {
                val userId = call.parameters["userId"] ?: ""
                proxyRequest(call, "${proxyService.getServiceUrl("/meals")}/meals/$userId", proxyService)
            }
            delete("/{id}") {
                val id = call.parameters["id"] ?: ""
                proxyRequest(call, "${proxyService.getServiceUrl("/meals")}/meals/$id", proxyService)
            }
        }

        // Sleep routes
        route("/sleep") {
            post {
                proxyRequest(call, "${proxyService.getServiceUrl("/sleep")}/sleep", proxyService)
            }
            get("/{userId}") {
                val userId = call.parameters["userId"] ?: ""
                proxyRequest(call, "${proxyService.getServiceUrl("/sleep")}/sleep/$userId", proxyService)
            }
            put("/{id}") {
                val id = call.parameters["id"] ?: ""
                proxyRequest(call, "${proxyService.getServiceUrl("/sleep")}/sleep/$id", proxyService)
            }
            delete("/{id}") {
                val id = call.parameters["id"] ?: ""
                proxyRequest(call, "${proxyService.getServiceUrl("/sleep")}/sleep/$id", proxyService)
            }
        }

        // Activities routes
        route("/activities") {
            post {
                proxyRequest(call, "${proxyService.getServiceUrl("/activities")}/activities", proxyService)
            }
            get("/{userId}") {
                val userId = call.parameters["userId"] ?: ""
                proxyRequest(call, "${proxyService.getServiceUrl("/activities")}/activities/$userId", proxyService)
            }
            delete("/{id}") {
                val id = call.parameters["id"] ?: ""
                proxyRequest(call, "${proxyService.getServiceUrl("/activities")}/activities/$id", proxyService)
            }
        }

        // Water routes
        route("/water") {
            post {
                proxyRequest(call, "${proxyService.getServiceUrl("/water")}/water", proxyService)
            }
            get("/{userId}") {
                val userId = call.parameters["userId"] ?: ""
                proxyRequest(call, "${proxyService.getServiceUrl("/water")}/water/$userId", proxyService)
            }
            delete("/{id}") {
                val id = call.parameters["id"] ?: ""
                proxyRequest(call, "${proxyService.getServiceUrl("/water")}/water/$id", proxyService)
            }
        }

        // Health routes
        route("/health") {
            post {
                proxyRequest(call, "${proxyService.getServiceUrl("/health")}/health", proxyService)
            }
            get("/{userId}") {
                val userId = call.parameters["userId"] ?: ""
                proxyRequest(call, "${proxyService.getServiceUrl("/health")}/health/$userId", proxyService)
            }
            delete("/{id}") {
                val id = call.parameters["id"] ?: ""
                proxyRequest(call, "${proxyService.getServiceUrl("/health")}/health/$id", proxyService)
            }
        }

        // Articles routes
        route("/articles") {
            post {
                proxyRequest(call, "${proxyService.getServiceUrl("/articles")}/articles", proxyService)
            }
            get {
                proxyRequest(call, "${proxyService.getServiceUrl("/articles")}/articles", proxyService)
            }
            delete("/{id}") {
                val id = call.parameters["id"] ?: ""
                proxyRequest(call, "${proxyService.getServiceUrl("/articles")}/articles/$id", proxyService)
            }
        }
    }
}

private suspend fun proxyRequest(call: ApplicationCall, targetUrl: String, proxyService: ProxyService) {
    try {
        println("Проксирование запроса на: $targetUrl")

        val proxyResponse = proxyService.proxyRequest(call, targetUrl)
        val responseBody = proxyResponse.bodyAsText()

        println("Получен ответ со статусом: ${proxyResponse.status}. Длина тела ответа: ${proxyResponse.bodyAsText().length}")

        call.respond(
            status = proxyResponse.status,
            message = responseBody
        )
    } catch (e: Exception) {
        e.printStackTrace()

        call.respond(
            HttpStatusCode.InternalServerError,
            "Ошибка при проксировании запроса: ${e.message}"
        )
    }
}