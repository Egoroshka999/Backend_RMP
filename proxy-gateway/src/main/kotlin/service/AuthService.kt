package com.Backend_RMP.service

import com.Backend_RMP.config.AppConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

class AuthService(private val config: AppConfig) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                // Важное изменение: игнорирование неизвестных ключей
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun verifyToken(token: String?): Boolean {
        if (token == null) return false

        return try {
            val response = client.get("${config.authServiceUrl}/auth/verify") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }

            // Отладочный вывод для проверки ответа
            val responseBody = response.bodyAsText()
            println("Ответ от verify: $responseBody")

            response.status.isSuccess()
        } catch (e: Exception) {
            println("Ошибка при проверке токена: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}