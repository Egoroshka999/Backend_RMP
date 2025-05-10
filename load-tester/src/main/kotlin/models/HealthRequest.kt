package com.Backend_RMP.models

import com.Backend_RMP.config.AppConfig
import com.Backend_RMP.config.RequestCategory
import io.ktor.http.*
import java.util.*

sealed class HealthRequest(
    open val category: RequestCategory,
    open val method: HttpMethod,
    open val path: String
) {

    /*
     TODO Тут представлены примеры запросов я их НЕ ТЕСТИЛ СДЕЛАНО ТОЛЬКО СТРУКТУРА т.к. НА момент написания нет GATEWAY
     */
    data class AuthRequest(
        private val user: String
    ) : HealthRequest(
        RequestCategory.AUTH,
        HttpMethod.Post,
        "/auth/login"
    ) {
        val body = """{"username": "$user", "password": "${AppConfig.TEST_PASSWORD}"}"""
    }

    data object GetSleepStats : HealthRequest(
        RequestCategory.GET_SLEEP,
        HttpMethod.Get,
        "/api/sleep/stats"
    ) {
        val authHeader = "Сюда вставить токен после авторизации"
    }

    data object GetActivitiesStats : HealthRequest(
        RequestCategory.GET_ACTIVITIES,
        HttpMethod.Get,
        "/api/activities/stats"
    ) {
        val authHeader = "Сюда вставить токен после авторизации"
    }

    data object PostSleepData : HealthRequest(
        RequestCategory.POST_SLEEP,
        HttpMethod.Post,
        "/api/sleep"
    ) {
        val authHeader = "Сюда вставить токен после авторизации"
        val body = """{
            "hours": 8,
            "quality": 1
        }""".trimIndent()
    }


}