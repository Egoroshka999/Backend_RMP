package com.Backend_RMP.service

import com.Backend_RMP.config.AppConfig
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class LogMessage(
    val timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
    val message: String,
    val level: String = "INFO",
    val source: String = "proxy-gateway"
)

class LoggingService(private val config: AppConfig) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun logRequest(path: String, method: String, userId: String?, body: String?) {
        val sanitizedBody = when {
            path.contains("/auth") -> sanitizeAuthData(body)
            else -> body
        }

        val message = "Запрос: $method $path" +
                (userId?.let { " от пользователя ID=$it" } ?: " от неавторизованного пользователя") +
                (sanitizedBody?.let { " с данными: $it" } ?: "")

        sendLog(message, "INFO")
    }

    suspend fun logResponse(path: String, method: String, userId: String?, statusCode: Int, body: String?) {
        val sanitizedBody = when {
            path.contains("/auth") -> sanitizeAuthData(body)
            else -> body
        }

        val message = "Ответ: $method $path, статус=$statusCode" +
                (userId?.let { " пользователю ID=$it" } ?: " неавторизованному пользователю") +
                (sanitizedBody?.let { " с данными: $it" } ?: "")

        val level = if (statusCode >= 400) "ERROR" else "INFO"
        sendLog(message, level)
    }

    private fun sanitizeAuthData(body: String?): String? {
        if (body == null) return null

        try {
            val jsonElement = Json.parseToJsonElement(body)
            if (jsonElement !is JsonObject) return "[Нетекстовый JSON]"

            val jsonObj = jsonElement.toMutableMap()

            if (jsonObj.containsKey("password")) {
                jsonObj["password"] = JsonPrimitive("*****")
            }

            if (jsonObj.containsKey("token")) {
                val token = jsonObj["token"]?.jsonPrimitive?.content
                if (token != null && token.length > 8) {
                    jsonObj["token"] = JsonPrimitive(token.substring(0, 4) + "..." + token.substring(token.length - 4))
                } else {
                    jsonObj["token"] = JsonPrimitive("*****")
                }
            }

            return Json.encodeToString(JsonObject.serializer(), JsonObject(jsonObj))
        } catch (e: Exception) {
            return "[Ошибка обработки JSON]"
        }
    }

    private suspend fun sendLog(message: String, level: String) {
        try {
            val logMessage = LogMessage(
                message = message,
                level = level
            )

            println("Отправка лога: $message (уровень: $level)")

            client.post("${config.logServiceUrl}/logs") {
                contentType(ContentType.Application.Json)
                setBody(logMessage)
            }

            println("Лог успешно отправлен")
        } catch (e: Exception) {
            println("Ошибка при отправке лога: ${e.message}")
            e.printStackTrace()
        }
    }
}