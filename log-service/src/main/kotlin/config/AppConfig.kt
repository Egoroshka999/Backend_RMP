package com.Backend_RMP.config

import io.github.cdimascio.dotenv.dotenv

data class AppConfig(
    val rabbitHost: String,
    val rabbitPort: Int,
    val rabbitUsername: String,
    val rabbitPassword: String,
    val rabbitQueueName: String,
    val rabbitVirtualHost: String,
    val serverPort: Int,
    val logServiceUrl: String,
    var clickHouseUrl: String
) {
    companion object {
        fun load(): AppConfig {
            val dotenv = dotenv()

            return AppConfig(
                rabbitHost = dotenv["RABBITMQ_HOST"] ?: "localhost",
                rabbitPort = dotenv["RABBITMQ_PORT"]?.toIntOrNull() ?: 5672,
                rabbitUsername = dotenv["RABBITMQ_USERNAME"] ?: "guest",
                rabbitPassword = dotenv["RABBITMQ_PASSWORD"] ?: "guest",
                rabbitQueueName = dotenv["RABBITMQ_QUEUE_NAME"] ?: "log_queue",
                rabbitVirtualHost = dotenv["RABBITMQ_VIRTUAL_HOST"] ?: "/",
                serverPort = dotenv["SERVER_PORT"]?.toIntOrNull() ?: 8081,
                logServiceUrl = dotenv["LOG_SERVICE_URL"] ?: "http://localhost:8080/logs",
                clickHouseUrl = dotenv["CLICKHOUSE_URL"] ?: "jdbc:clickhouse://localhost:8123/default"
            )
        }
    }
}
