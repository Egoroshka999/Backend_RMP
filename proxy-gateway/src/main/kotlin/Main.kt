package com.backend_rmp

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

fun main() {
    embeddedServer(Netty, port = 8080, module = { proxyGatewayModule() }).start(wait = true)
}

fun Application.proxyGatewayModule() {
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
    }

    routing {
        // Маршрут для проверки доступности прокси
        get("/health") {
            call.respondText("Proxy Gateway is running", ContentType.Text.Plain)
        }

        // Маршруты для Auth Service
        route("/auth") {
            handle {
                forwardTo("auth-service", 8081, "/auth")
            }
        }

        // Маршруты для Profile Service
        route("/profile") {
            handle {
                forwardTo("profile-service", 8080, "/profile")
            }
        }

        // Маршруты для Data Service
        route("/data") {
            handle {
                forwardTo("data-service", 8080, "/data")
            }
        }

        // Маршруты для Report Service
        route("/report") {
            handle {
                forwardTo("report-service", 8080, "/report")
            }
        }

        // Маршруты для Log Service
        route("/log") {
            handle {
                forwardTo("log-service", 8080, "/log")
            }
        }

        // Прямой доступ к Database Service (для разработки, в продакшн лучше убрать)
        route("/db") {
            handle {
                forwardTo("database-service", 8080, "/")
            }
        }
    }
}

// Клиент HTTP для проксирования запросов
private val client = HttpClient(CIO) {
    followRedirects = false
}

// Функция для проксирования запросов на другие сервисы
@OptIn(InternalAPI::class)
private suspend fun PipelineContext<Unit, ApplicationCall>.forwardTo(host: String, port: Int, pathPrefix: String) {
    // Получаем оригинальный запрос
    val originalRequest = call.request

    // Получаем путь после префикса
    val originalPath = originalRequest.path()
    val routePrefix = pathPrefix.removeSuffix("/")
    val pathSuffix = if (originalPath.length > routePrefix.length) {
        originalPath.substring(routePrefix.length)
    } else {
        ""
    }
    val targetPath = if (pathSuffix.startsWith("/") || pathSuffix.isEmpty()) pathSuffix else "/$pathSuffix"

    // Формируем целевой URL
    val url = "http://$host:$port$pathPrefix$targetPath${originalRequest.queryString()}"

    try {
        // Создаем и выполняем проксированный запрос
        val response = client.request(url) {
            method = originalRequest.httpMethod
            headers {
                // Копируем заголовки из оригинального запроса
                originalRequest.headers.forEach { name, values ->
                    if (!name.equals(HttpHeaders.ContentLength, ignoreCase = true)) {
                        values.forEach { value ->
                            append(name, value)
                        }
                    }
                }
            }

            // Копируем тело запроса, если оно есть
            if (originalRequest.httpMethod != HttpMethod.Get && originalRequest.httpMethod != HttpMethod.Head) {
                try {
                    val requestBody = withContext(Dispatchers.IO) {
                        originalRequest.receiveChannel().readRemaining().readBytes()
                    }
                    body = ByteArrayContent(requestBody, originalRequest.contentType() ?: ContentType.Any)
                } catch (e: IOException) {
                    call.respond(HttpStatusCode.InternalServerError, "Ошибка чтения тела запроса: ${e.message}")
                    return
                }
            }
        }

        // Отправляем ответ клиенту
        // Устанавливаем статус ответа
        call.response.status(response.status)

        // Копируем заголовки ответа
        response.headers.forEach { name, values ->
            if (!name.equals(HttpHeaders.ContentLength, ignoreCase = true) &&
                !name.equals(HttpHeaders.TransferEncoding, ignoreCase = true)) {
                values.forEach { value ->
                    call.response.headers.append(name, value)
                }
            }
        }

        // Копируем тело ответа
        call.respondBytes(
            bytes = response.readBytes(),
            contentType = response.contentType() ?: ContentType.Application.OctetStream
        )
    } catch (e: Exception) {
        call.respond(HttpStatusCode.InternalServerError, "Ошибка прокси: ${e.message}")
    }
}

private fun ApplicationRequest.queryString(): String {
    val queryParameters = queryParameters
    return if (queryParameters.isEmpty()) {
        ""
    } else {
        "?" + queryParameters.entries()
            .flatMap { (key, values) ->
                values.map { value ->
                    "${key.encodeURLParameter()}=${value.encodeURLParameter()}"
                }
            }
            .joinToString("&")
    }
}