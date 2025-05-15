package com.Backend_RMP.services

import com.Backend_RMP.config.AppConfig
import com.Backend_RMP.models.HealthRequest
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class RequestHandler(
    private val client: HttpClient,
    private val statsCollector: StatisticsCollector
) {
    suspend fun executeRequest(request: HealthRequest) {
        try {
            val response = when (request) {
                is HealthRequest.AuthRequest -> handleAuthRequest(request)
                is HealthRequest.GetSleepStats -> handleGetRequest(request)
                is HealthRequest.GetActivitiesStats -> handleGetRequest(request)
                is HealthRequest.PostSleepData -> handlePostRequest(request)
                is HealthRequest.PostActivitiesData -> handlePostRequest(request)
            }

            if (response.status.isSuccess()) {
                statsCollector.incrementSuccess(request.category)
            } else {
                statsCollector.incrementFailure(request.category)
            }
        } catch (e: Exception) {
            statsCollector.incrementFailure(request.category)
        }
    }

    private suspend fun handleAuthRequest(request: HealthRequest.AuthRequest): HttpResponse {
        return client.post("${AppConfig.TARGET_URL}${request.path}") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(request.body)
        }
    }

    private suspend fun handleGetRequest(request: HealthRequest): HttpResponse {
        return client.get("${AppConfig.TARGET_URL}${request.path}") {
            when (request) {
                is HealthRequest.GetSleepStats -> header(HttpHeaders.Authorization, request.authHeader)
                is HealthRequest.GetActivitiesStats -> header(HttpHeaders.Authorization, request.authHeader)
                else -> throw IllegalArgumentException("Invalid GET request type")
            }
        }
    }

    private suspend fun handlePostRequest(request: HealthRequest): HttpResponse {
        return client.post("${AppConfig.TARGET_URL}${request.path}") {
            when (request) {
                is HealthRequest.PostSleepData -> header(HttpHeaders.Authorization, request.authHeader)
                is HealthRequest.PostActivitiesData -> header(HttpHeaders.Authorization, request.authHeader)
                else -> throw IllegalArgumentException("Invalid POST request type")
            }
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(request.body)
        }
    }
}