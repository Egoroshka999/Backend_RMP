package com.Backend_RMP

import io.github.cdimascio.dotenv.dotenv

data class AppConfig(
    val rabbitHost: String,
    val rabbitPort: Int,
    val rabbitUsername: String,
    val rabbitPassword: String,
    val rabbitQueueName: String,
    val rabbitVirtualHost: String,
    val serverPort: Int,
    val postgresDsn: String,
    var postgresUser: String,
    var postgresPass: String,
    var postgresHost: String
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
                postgresDsn = dotenv["POSTGRES_DSN"] ?: "jdbc:postgresql://postgres:5432/app_db",
                postgresUser = dotenv["POSTGRES_USER"] ?: "admin",
                postgresPass = dotenv["POSTGRES_PASS"] ?: "secret",
                postgresHost= dotenv["POSTGRES_HOST"] ?: "postgres",
            )
        }
    }
}
