package com.Backend_RMP.service

import com.Backend_RMP.config.AppConfig
import com.Backend_RMP.model.LogMessage
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LogMessageHandler(private val config: AppConfig) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun handleMessage(logMessage: LogMessage) {
        try {
            val response = withContext(Dispatchers.IO) {
                client.post(config.logServiceUrl) {
                    contentType(ContentType.Application.Json)
                    setBody(logMessage)
                }
            }

            if (response.status.isSuccess()) {
                println("Successfully sent log message to endpoint")
            } else {
                println("Failed to send log message. Status: ${response.status}")
            }
        } catch (e: Exception) {
            println("Error processing log message: ${e.message}")
        }
    }
}
