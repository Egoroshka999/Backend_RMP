package com.Backend_RMP.clients

import com.Backend_RMP.models.UserDTO
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class DatabaseClient {
    private val client = HttpClient(CIO)
    private val baseUrl = "http://database-service:8080" // адаптировать при необходимости

    suspend fun findUserByUsername(username: String): UserDTO? {
        return try {
            client.get("$baseUrl/users/by-username/$username").body()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createUser(username: String, password: String): String {
        val response: HttpResponse = client.post("$baseUrl/users") {
            setBody(UserDTO(null, username, password))
        }
        val created: UserDTO = response.body()
        return created.id ?: ""
    }

    suspend fun checkUserCredentials(username: String, password: String): String? {
        return try {
            val response: UserDTO = client.post("$baseUrl/users/check") {
                setBody(UserDTO(null, username, password))
            }.body()
            response.id
        } catch (e: Exception) {
            null
        }
    }
}