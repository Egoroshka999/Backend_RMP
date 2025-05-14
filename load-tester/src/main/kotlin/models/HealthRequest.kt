package com.Backend_RMP.models

import auth.TokenProvider
import com.Backend_RMP.config.AppConfig
import com.Backend_RMP.config.RequestCategory
import io.ktor.http.*
import java.util.*

sealed class HealthRequest(
    open val category: RequestCategory,
    open val method: HttpMethod,
    open val path: String
) {

//    data class RegisterRequest(
//        private val user: String,
//        private val password: String
//    ) : HealthRequest(
//        RequestCategory.AUTH,
//        HttpMethod.Post,
//        "/auth/register"
//    ) {
//        val body = """{"username": "$user", "password": "$password"}"""
//    }

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

    data class GetSleepStats(val userId: String) : HealthRequest(
        RequestCategory.GET_SLEEP,
        HttpMethod.Get,
        "/sleep/${userId}"
    ) {
        val authHeader = "Bearer ${TokenProvider.getToken()}"
    }

    data class GetActivitiesStats(val userId: String) : HealthRequest(
        RequestCategory.GET_ACTIVITIES,
        HttpMethod.Get,
        "/activities/${userId}"
    ) {
        val authHeader = "Bearer ${TokenProvider.getToken()}"
    }

    data object PostSleepData : HealthRequest(
        RequestCategory.POST_SLEEP,
        HttpMethod.Post,
        "/sleep"
    ) {
        val authHeader = "Bearer ${TokenProvider.getToken()}"
        val body = """{
            "hours": 8,
            "quality": 1
        }""".trimIndent()
    }


}