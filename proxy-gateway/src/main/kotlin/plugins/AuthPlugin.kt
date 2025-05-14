package com.Backend_RMP.plugins

import com.Backend_RMP.service.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.*


val UserIdKey = AttributeKey<String>("userId")

fun Application.configureAuth(authService: AuthService) {
    intercept(ApplicationCallPipeline.Call) {
        val path = call.request.path()

        println("Обработка запроса: $path")

        if (path.startsWith("/auth/login") || path.startsWith("/auth/register")) {
            println("Пропускаем проверку токена для: $path")
            return@intercept
        }

        val token = call.request.header("Authorization")?.removePrefix("Bearer ")
        println("Токен: ${token?.take(5)}...")

        if (token != null) {
            val isTokenValid = authService.verifyToken(token)
            println("Токен валиден: $isTokenValid")

            if (isTokenValid) {
                call.attributes.put(UserIdKey, "user-id")
                return@intercept
            }
        }

        // Если токен не валидный или отсутствует
        println("Токен недействителен, возвращаем ошибку 403")
        call.respond(HttpStatusCode.Forbidden, "Недействительный или отсутствующий токен")
        finish()
    }
}