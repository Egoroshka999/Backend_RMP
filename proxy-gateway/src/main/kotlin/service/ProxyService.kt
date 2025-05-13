package com.Backend_RMP.service

import com.Backend_RMP.config.AppConfig
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import kotlinx.serialization.json.*

class ProxyService(private val config: AppConfig) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        // Настраиваем таймауты
        engine {
            requestTimeout = 60000
        }
    }

    suspend fun proxyRequest(call: ApplicationCall, targetUrl: String): HttpResponse {
        val requestBodyText = if (call.request.httpMethod !in listOf(HttpMethod.Get, HttpMethod.Delete)) {
            try {
                call.receiveText()
            } catch (e: Exception) {
                println("Ошибка при получении тела запроса: ${e.message}")
                ""
            }
        } else ""

        println("Проксирование запроса на: $targetUrl")
        println("HTTP метод: ${call.request.httpMethod}")
        println("Content-Type: ${call.request.contentType()}")
        println("Тело запроса (длина: ${requestBodyText.length}): $requestBodyText")

        if (targetUrl.contains("/auth/login") || targetUrl.contains("/auth/register")) {
            return proxyAuthRequest(targetUrl, requestBodyText)
        }

        return client.request(targetUrl) {
            method = call.request.httpMethod

            headers {
                call.request.headers.forEach { name, values ->
                    if (name != HttpHeaders.ContentLength) {
                        values.forEach { value ->
                            append(name, value)
                        }
                    }
                }

                if (requestBodyText.isNotEmpty() && call.request.httpMethod !in listOf(HttpMethod.Get, HttpMethod.Delete)) {
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
            }

            if (requestBodyText.isNotEmpty() && call.request.httpMethod !in listOf(HttpMethod.Get, HttpMethod.Delete)) {
                setBody(requestBodyText)
            }
        }
    }

    private suspend fun proxyAuthRequest(targetUrl: String, bodyText: String): HttpResponse {
        println("Специальное проксирование запроса аутентификации на: $targetUrl")
        println("Тело запроса аутентификации: $bodyText")

        return client.post(targetUrl) {
            contentType(ContentType.Application.Json)
            // Напрямую передаем тело запроса без преобразований
            if (bodyText.isNotEmpty()) {
                setBody(bodyText)
            }
        }
    }

    fun getServiceUrl(path: String): String {
        return when {
            path.startsWith("/auth") -> config.authServiceUrl
            path.startsWith("/users") ||
                    path.startsWith("/meals") ||
                    path.startsWith("/sleep") ||
                    path.startsWith("/activities") ||
                    path.startsWith("/water") ||
                    path.startsWith("/health") ||
                    path.startsWith("/articles") -> config.databaseServiceUrl
            else -> config.databaseServiceUrl
        }
    }
}