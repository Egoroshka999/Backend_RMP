package com.Backend_RMP.config

import io.github.cdimascio.dotenv.dotenv

data class AppConfig(
    val serverPort: Int,
    val authServiceUrl: String,
    val databaseServiceUrl: String,
    val logServiceUrl: String,
    val profileServiceUrl: String,
    val dataServiceUrl: String,
    val reportServiceUrl: String
) {
    companion object {
        fun load(): AppConfig {
            val dotenv = dotenv()

            return AppConfig(
                serverPort = dotenv["SERVER_PORT"]?.toIntOrNull() ?: 8080,
                authServiceUrl = dotenv["AUTH_SERVICE_URL"] ?: "http://auth-service:8081",
                databaseServiceUrl = dotenv["DATABASE_SERVICE_URL"] ?: "http://database-service:8080",
                logServiceUrl = dotenv["LOG_SERVICE_URL"] ?: "http://log-service:8081",
                profileServiceUrl = dotenv["PROFILE_SERVICE_URL"] ?: "http://profile-service:8080",
                dataServiceUrl = dotenv["DATA_SERVICE_URL"] ?: "http://data-service:8080",
                reportServiceUrl = dotenv["REPORT_SERVICE_URL"] ?: "http://report-service:8080"
            )
        }
    }
}