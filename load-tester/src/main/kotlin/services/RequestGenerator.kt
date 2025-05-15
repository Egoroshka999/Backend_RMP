package com.Backend_RMP.services

import com.Backend_RMP.models.HealthRequest
import kotlin.random.Random

class RequestGenerator(
    private val userId: String
) {
    fun generateRandomRequest(): HealthRequest {
        return when (Random.nextInt(100)) {
            in 0..19 -> HealthRequest.AuthRequest("/auth/login")
            in 20..39 -> HealthRequest.GetSleepStats(userId)
            in 40..59 -> HealthRequest.GetActivitiesStats(userId)
            in 60..79 -> HealthRequest.PostActivitiesData(userId)
            else -> HealthRequest.PostSleepData(userId)
        }
    }
}