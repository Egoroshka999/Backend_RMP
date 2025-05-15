package com.Backend_RMP.models

import auth.TokenProvider
import com.Backend_RMP.config.AppConfig
import com.Backend_RMP.config.RequestCategory
import io.ktor.http.*
import java.time.LocalDate
import java.time.LocalTime
import kotlin.random.Random

sealed class HealthRequest(
    open val category: RequestCategory,
    open val method: HttpMethod,
    open val path: String,
    open val body: String?
) {

    data class AuthRequest(override val path: String) : HealthRequest(
        RequestCategory.AUTH,
        HttpMethod.Post,
        path,
        """{
                "username": "${AppConfig.TEST_USER}",
                "password": "${AppConfig.TEST_PASSWORD}",
                "age": "${AppConfig.TEST_AGE}",
                "weight": "${AppConfig.TEST_WEIGHT}",
                "height": "${AppConfig.TEST_HEIGHT}",
                "gender": "${AppConfig.TEST_GENDER}",
                "goal": "${AppConfig.TEST_GOAL}"
            }""".trimIndent()
    )

    data class GetSleepStats(val userId: String) : HealthRequest(
        RequestCategory.GET_SLEEP,
        HttpMethod.Get,
        "/sleep/${userId}",
        null
    ) {
        val authHeader = "Bearer ${TokenProvider.getToken()}"
    }

    data class GetActivitiesStats(val userId: String) : HealthRequest(
        RequestCategory.GET_ACTIVITIES,
        HttpMethod.Get,
        "/activities/${userId}",
        null
    ) {
        val authHeader = "Bearer ${TokenProvider.getToken()}"
    }

    data class PostSleepData(val userId: String) : HealthRequest(
        RequestCategory.POST_SLEEP,
        HttpMethod.Post,
        "/sleep",
        """{
            "userId": "$userId",
            "date": "${LocalDate.now()}",
            "startTime": "${LocalTime.now().minusHours(Random.nextLong(3, 11))}",
            "endTime": "${LocalTime.now()}",
            "quality": "${Random.nextInt(1, 6)}",
        }""".trimIndent()
    ) {
        val authHeader = "Bearer ${TokenProvider.getToken()}"
    }

    data class PostActivitiesData(val userId: String) : HealthRequest(
        RequestCategory.POST_ACTIVITIES,
        HttpMethod.Post,
        "/activities",
        """{
            "userId": "$userId",
            "date": "${LocalDate.now()}",
            "type": ${arrayOf("walking", "running").random()},
            "duration": "${Random.nextInt(5, 150)}",
            "steps": "${Random.nextInt(100, 15000)}"
        }""".trimIndent()
    ) {
        val authHeader = "Bearer ${TokenProvider.getToken()}"
    }

}