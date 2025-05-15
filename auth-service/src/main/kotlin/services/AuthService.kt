package com.Backend_RMP.services

import com.Backend_RMP.clients.DatabaseClient
import com.Backend_RMP.models.AuthRequest
import com.Backend_RMP.models.AuthResponse
import com.Backend_RMP.storage.RedisStorage
import io.ktor.http.*
import java.util.*

class AuthService {
    private val db = DatabaseClient()
    private val redis = RedisStorage()

    suspend fun register(request: AuthRequest): Pair<HttpStatusCode, AuthResponse> {
        val existing = db.findUserByUsername(request.username)
        if (existing != null) {
            return HttpStatusCode.Conflict to AuthResponse(null, "User already exists", null)
        }
        val userId = db.createUser(
            request.username,
            request.password,
            request.age,
            request.weight,
            request.height,
            request.gender,
            request.goal
        )
        val token = UUID.randomUUID().toString()
        redis.saveToken(token, userId)
        return HttpStatusCode.Created to AuthResponse(userId, "User registered", token)
    }

    suspend fun login(request: AuthRequest): Pair<HttpStatusCode, AuthResponse> {
        val userId = db.checkUserCredentials(request.username, request.password)
            ?: return HttpStatusCode.Unauthorized to AuthResponse(null, "Invalid credentials", null)
        val token = UUID.randomUUID().toString()
        redis.saveToken(token, userId)
        return HttpStatusCode.OK to AuthResponse(userId, "Login successful", token)
    }

    suspend fun verify(token: String?): Pair<HttpStatusCode, AuthResponse> {
        if (token == null) return HttpStatusCode.BadRequest to AuthResponse(null, "Missing token", null)
        val userId = redis.getUserIdByToken(token)
        return if (userId != null) {
            HttpStatusCode.OK to AuthResponse(userId, "Token valid", token)
        } else {
            HttpStatusCode.Unauthorized to AuthResponse(null, "Invalid token", null)
        }
    }
}