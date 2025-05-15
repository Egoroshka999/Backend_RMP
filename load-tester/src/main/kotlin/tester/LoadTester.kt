package tester

import auth.TokenProvider
import com.Backend_RMP.config.AppConfig
import com.Backend_RMP.models.LoadTestResult
import com.Backend_RMP.services.RequestGenerator
import com.Backend_RMP.services.RequestHandler
import com.Backend_RMP.services.StatisticsCollector
import io.ktor.client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.measureTime


class LoadTester(
    private val config: AppConfig,
    private val client: HttpClient,
    private val requestGenerator: RequestGenerator
) {

    private val statsCollector = StatisticsCollector()
    private val requestHandler = RequestHandler(client, statsCollector)

    fun runTest(): LoadTestResult {
        val durationSeconds = measureTime {
            runBlocking {
                TokenProvider.init(client)

                val requestsPerCoroutine = config.TOTAL_REQUESTS / config.PARALLEL_COROUTINES

                (1..config.PARALLEL_COROUTINES).map {
                    launch(Dispatchers.IO) {
                        repeat(requestsPerCoroutine) {
                            requestHandler.executeRequest(requestGenerator.generateRandomRequest())
                        }
                    }
                }.forEach { it.join() }
            }
        }

        return LoadTestResult(
            timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            totalRequests = config.TOTAL_REQUESTS,
            byCategory = statsCollector.getResults(),
            duration = durationSeconds.toString()
        )
    }

    fun shutdown() {
        client.close()
    }
}