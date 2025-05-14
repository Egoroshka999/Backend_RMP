package com.Backend_RMP.clients

import com.Backend_RMP.models.UserDTO
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class DatabaseClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }
    private val baseUrl = "http://database-service:8080" // адаптировать при необходимости

    suspend fun findUserByUsername(username: String): UserDTO? {
        return try {
            client.get("$baseUrl/users/by-username/$username").body()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createUser(
        username: String,
        password: String,
        age: Int? = null,
        weight: Float? = null,
        height: Float? = null,
        gender: String? = null,
        goal: String? = null
    ): String {
            val response: HttpResponse = client.post("$baseUrl/users") {
                contentType(ContentType.Application.Json)
                setBody(
                    UserDTO(
                        null,
                        username,
                        password,
                        age,
                        weight,
                        height,
                        gender,
                        goal
                    )
                )
            }
            val responseBody = response.bodyAsText()
            val created: UserDTO = Json.decodeFromString(responseBody)
            return created.id ?: ""
    }


    suspend fun checkUserCredentials(username: String, password: String): String? {
        return try {
            val response: HttpResponse = client.post("$baseUrl/users/check") {
                contentType(ContentType.Application.Json)
                setBody(UserDTO(null, username, password))
            }

            val body = response.bodyAsText()
            val user: UserDTO = Json.decodeFromString(body)
            user.id
        } catch (e: Exception) {
            println("${e.message}")
            null
        }
    }

}