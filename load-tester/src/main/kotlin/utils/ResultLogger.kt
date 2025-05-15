package com.Backend_RMP.utils

import com.Backend_RMP.models.LoadTestResult
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ResultLogger(private val dirPath: String) {

    val fullPath: String

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")

    init {
        val timestamp = dateFormat.format(Date())
        val fileName = "load_test_$timestamp.log"
        fullPath = "$dirPath/$fileName"
    }

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

        File(dirPath).mkdirs()
        File(fullPath).writeText(logEntry)
    }
}