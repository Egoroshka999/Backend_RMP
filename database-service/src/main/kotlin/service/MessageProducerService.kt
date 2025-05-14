package com.Backend_RMP.service

import com.Backend_RMP.AppConfig
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory

class MessageProducerService(private val config: AppConfig) {

    private lateinit var connection: Connection
    private lateinit var channel: Channel

    fun start() {
        val factory = ConnectionFactory().apply {
            host = config.rabbitHost
            port = config.rabbitPort
            username = config.rabbitUsername
            password = config.rabbitPassword
            virtualHost = config.rabbitVirtualHost
        }

        connection = factory.newConnection()
        channel = connection.createChannel()

        
        channel.queueDeclare(
            config.rabbitQueueName,
            true,  
            false, 
            false, 
            null   
        )
    }

    fun sendMessage(message: String) {
        if (!this::channel.isInitialized) {
            throw IllegalStateException("Producer service not started. Call start() first.")
        }

        channel.basicPublish(
            "", 
            config.rabbitQueueName,
            null, 
            message.toByteArray()
        )

        println(" [x] Sent message: '$message' to queue '${config.rabbitQueueName}'")
    }

    fun stop() {
        if (this::channel.isInitialized) {
            channel.close()
        }
        if (this::connection.isInitialized) {
            connection.close()
        }
    }
}
