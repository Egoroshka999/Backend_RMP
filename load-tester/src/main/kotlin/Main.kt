import com.Backend_RMP.config.AppConfig
import com.Backend_RMP.services.RequestGenerator
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

suspend fun main() {
    val config = AppConfig

    val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = config.REQUEST_TIMEOUT
        }
    }

    val userId = registerUser(client)

    val requestGenerator = RequestGenerator(config, userId)

    val loadTester = LoadTester(config, client, requestGenerator)

    val result = loadTester.runTest()
    println("Тест завершён: $result")
}

suspend fun registerUser(client: HttpClient): String {
    val response: HttpResponse = client.post("${AppConfig.TARGET_URL}/auth/register") {
        contentType(ContentType.Application.Json)
        setBody(
            mapOf(
                "username" to AppConfig.TEST_USER,
                "password" to AppConfig.TEST_PASSWORD
            )
        )
    }

    val responseBody = response.bodyAsText()
    val json = Json.parseToJsonElement(responseBody).jsonObject

    return json["id"]?.jsonPrimitive?.content
        ?: throw IllegalStateException("User ID not found in register response")
}
