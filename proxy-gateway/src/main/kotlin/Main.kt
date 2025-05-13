package com.Backend_RMP

import com.Backend_RMP.config.AppConfig
import com.Backend_RMP.plugins.configureAuth
import com.Backend_RMP.plugins.configureProxy
import com.Backend_RMP.service.AuthService
import com.Backend_RMP.service.ProxyService
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlin.system.exitProcess

fun main() {
    try {
        println("Запуск Gateway сервиса...")
        val config = AppConfig.load()
        val authService = AuthService(config)
        val proxyService = ProxyService(config)

        embeddedServer(Netty, port = config.serverPort) {
            install(ContentNegotiation) {
                json()
            }

            configureAuth(authService)
            configureProxy(proxyService)

            println("Gateway сервис запущен на порту ${config.serverPort}")
        }.start(wait = true)
    } catch (e: Exception) {
        println("Ошибка запуска Gateway: ${e.message}")
        e.printStackTrace()
        exitProcess(1)
    }
}