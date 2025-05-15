package auth

import com.Backend_RMP.config.AppConfig
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*


object TokenProvider {
    private var token: String? = null

    suspend fun init(client: HttpClient) {
        val response = client.post("${AppConfig.TARGET_URL}/auth/login") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody("""{"username": "${AppConfig.TEST_USER}", "password": "${AppConfig.TEST_PASSWORD}"}""")
        }

        if (!response.status.isSuccess()) {
            error("Ошибка авторизации: ${response.status}")
        }

        val body = response.bodyAsText()
        token = Regex("\"token\"\\s*:\\s*\"([^\"]+)\"")
            .find(body)
            ?.groupValues?.get(1)
            ?: error("Токен не найден в ответе")
    }

    fun getToken(): String {
        return token ?: error("Token not initialized. Call init() first.")
    }
}