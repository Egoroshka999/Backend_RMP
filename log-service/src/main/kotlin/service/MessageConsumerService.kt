package com.Backend_RMP.service

import com.Backend_RMP.config.AppConfig
import com.Backend_RMP.model.LogMessage
import com.rabbitmq.client.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.IOException
import java.util.concurrent.TimeoutException

class MessageConsumerService(private val config: AppConfig) {
    private var connection: Connection? = null
    private var channel: Channel? = null
    private val messageHandler = LogMessageHandler(config)
    private val json = Json { ignoreUnknownKeys = true }

    fun start() {
        try {
            val factory = ConnectionFactory().apply {
                host = config.rabbitHost
                port = config.rabbitPort
                username = config.rabbitUsername
                password = config.rabbitPassword
                virtualHost = config.rabbitVirtualHost
            }

            connection = factory.newConnection()
            channel = connection?.createChannel()
            channel?.queueDeclare(config.rabbitQueueName, true, false, false, null)

            println("Connected to RabbitMQ and listening on queue: ${config.rabbitQueueName}")

            channel?.basicConsume(config.rabbitQueueName, true, object : DefaultConsumer(channel) {
                override fun handleDelivery(
                    consumerTag: String,
                    envelope: Envelope,
                    properties: AMQP.BasicProperties,
                    body: ByteArray
                ) {
                    val messageJson = String(body, Charsets.UTF_8)
                    CoroutineScope(Dispatchers.IO).launch {
                        handleMessage(messageJson)
                    }
                }
            })
        } catch (e: IOException) {
            println("Error connecting to RabbitMQ: ${e.message}")
        } catch (e: TimeoutException) {
            println("Connection timeout: ${e.message}")
        }
    }

    private suspend fun handleMessage(messageJson: String) {
        try {
            val logMessage = json.decodeFromString<LogMessage>(messageJson)
            messageHandler.handleMessage(logMessage)
        } catch (e: Exception) {
            println("Error deserializing message: ${e.message}")
        }
    }

    fun stop() {
        try {
            channel?.close()
            connection?.close()
        } catch (e: Exception) {
            println("Error closing RabbitMQ connection: ${e.message}")
        }
    }
} 