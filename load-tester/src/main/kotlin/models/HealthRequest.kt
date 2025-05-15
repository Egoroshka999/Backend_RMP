package com.Backend_RMP.models

import auth.TokenProvider
import com.Backend_RMP.config.AppConfig
import com.Backend_RMP.config.RequestCategory
import io.ktor.http.*
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random
import kotlin.time.Duration.Companion.hours

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
        null
    ) {
        val authHeader = "Bearer ${TokenProvider.getToken()}"
        private val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        private val nowMinusHours = Clock.System.now().minus(Random.nextLong(3, 11).hours).toLocalDateTime(TimeZone.currentSystemDefault())

        private val nowNoNanos = LocalTime(now.hour, now.minute, now.second)
        private val nowMinusHoursNoNanos = LocalTime(nowMinusHours.hour, nowMinusHours.minute, nowMinusHours.second)

        override val body = """{
            "userId": "$userId",
            "date": "${Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date}",
            "startTime": "${nowMinusHoursNoNanos}",
            "endTime": "${nowNoNanos}",
            "quality": ${Random.nextInt(1, 6)},
        }""".trimIndent()
    }

    data class PostActivitiesData(val userId: String) : HealthRequest(
        RequestCategory.POST_ACTIVITIES,
        HttpMethod.Post,
        "/activities",
        null
    ) {
        val authHeader = "Bearer ${TokenProvider.getToken()}"
        override val body = """{
            "userId": "$userId",
            "date": "${Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date}",
            "type": ${arrayOf("walking", "running").random()},
            "duration": "${Random.nextInt(5, 150)}",
            "steps": "${Random.nextInt(100, 15000)}"
        }""".trimIndent()
    }

}