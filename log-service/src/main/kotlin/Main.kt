package com.Backend_RMP

import com.Backend_RMP.config.AppConfig
import com.Backend_RMP.routes.LogRoutes
import com.Backend_RMP.service.LogMessageHandler
import com.Backend_RMP.service.MessageConsumerService
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    println("Log Service started")
    
    val config = AppConfig.load()

    val messageHandler = LogMessageHandler(config)
    val consumerService = MessageConsumerService(config)
    val logRoutes = LogRoutes(messageHandler)
    
    try {
        consumerService.start()
        
        embeddedServer(Netty, port = config.serverPort) {
            install(ContentNegotiation) {
                json()
            }
            routing {
                logRoutes.configure(this)
            }
        }.start(wait = true)
    } catch (e: Exception) {
        println("Service error: ${e.message}")
    } finally {
        consumerService.stop()
    }
}
