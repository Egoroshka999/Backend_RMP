package com.Backend_RMP.services

import com.Backend_RMP.config.AppConfig
import com.Backend_RMP.models.HealthRequest
import io.ktor.util.reflect.*
import kotlin.random.Random

class RequestGenerator(
    private val config: AppConfig,
    private val userId: String
) {
    fun generateRandomRequest(): HealthRequest {
        return when (Random.nextInt(100)) {
            in 0..24 -> HealthRequest.AuthRequest(config.TEST_USER)
            in 25..49 -> HealthRequest.GetSleepStats(userId)
            in 50..74 -> HealthRequest.GetActivitiesStats(userId)
            else -> HealthRequest.PostSleepData
        }
    }
}