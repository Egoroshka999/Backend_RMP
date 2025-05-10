package com.Backend_RMP.utils

import com.Backend_RMP.config.AppConfig
import com.Backend_RMP.models.LoadTestResult
import io.ktor.http.ContentDisposition.Companion.File
import java.io.File

object ResultLogger {
    fun logToFile(result: LoadTestResult) {
        val logEntry = buildString {
            appendLine("[${result.timestamp}] Load test completed:")
            appendLine("Total requests: ${result.totalRequests}")
            appendLine("Duration:      ${result.duration}")
            appendLine()
            appendLine("By categories:")
            result.byCategory.forEach { (category, stats) ->
                appendLine("${category.name.padEnd(15)} Success: ${stats.first.toString().padEnd(6)} Failed: ${stats.second}")
            }
            appendLine("-".repeat(50))
        }

        File("logs").mkdirs()
        File(AppConfig.LOG_FILE_PATH).appendText("$logEntry\n")
    }
}