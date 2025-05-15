import auth.TokenProvider
import com.Backend_RMP.config.AppConfig
import com.Backend_RMP.models.HealthRequest
import com.Backend_RMP.services.RequestGenerator
import com.Backend_RMP.utils.ResultLogger
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import log.LogMessageProducer
import models.LogMessage
import tester.LoadTester
import java.io.IOException

class MainRunner {

    private val config = AppConfig
    private val logger = ResultLogger(config.LOG_DIR_PATH)
    private var userId: String = ""
    private val logMessageProducer: LogMessageProducer = LogMessageProducer(config)

    private val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = config.REQUEST_TIMEOUT
        }
        install(ContentNegotiation) {
            json()
        }
    }

    fun run(): Unit = runBlocking {
        logMessageProducer.start()

        println("Load testing started")

        try {
            userId = registerTestUser(client)
            println("Created test user ${config.TEST_USER}")

            val requestGenerator = RequestGenerator(userId)
            val loadTester = LoadTester(config, client, requestGenerator)

            val result = loadTester.runTest()

            println("Load testing ended, writing results to file...")
            val log = logger.logToFile(result)

            logMessageProducer.sendMessage(
                Json.encodeToString(
                    LogMessage(
                        message = log,
                        source = logger.fullPath
                    )
                )
            )
            println("Finished. See logs: ${logger.fullPath}")

        } catch (e: IOException) {
            println("Network error: ${e.message}")
        } catch (e: java.lang.IllegalStateException) {
            println("Network error: ${e.message}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            deleteTestUser(client)
            println("Deleted test user ${config.TEST_USER}")
            logMessageProducer.stop()
            client.close()
        }
    }

    private suspend fun registerTestUser(client: HttpClient): String {
        val registerRequest: HealthRequest.AuthRequest = HealthRequest.AuthRequest("/auth/register")

        val response: HttpResponse = client.post("${AppConfig.TARGET_URL}${registerRequest.path}") {
            contentType(ContentType.Application.Json)
            setBody(registerRequest.body)
        }

        val responseBody = response.bodyAsText()
        val json = Json.parseToJsonElement(responseBody).jsonObject

        return json["id"]?.jsonPrimitive?.content
            ?: throw IllegalStateException("User ID not found in register response")
    }

    private suspend fun deleteTestUser(client: HttpClient) {
        if (userId.isBlank()) {
            return
        }
        val deletePath = "/users/$userId"
        val response: HttpResponse = client.delete("${AppConfig.TARGET_URL}${deletePath}") {
            header(HttpHeaders.Authorization, "Bearer ${TokenProvider.getToken()}")
        }

        if (!response.status.isSuccess()) {
            throw IllegalStateException("Failed to delete test user with id=${userId}")
        }
    }
}