@file:OptIn(io.lettuce.core.ExperimentalLettuceCoroutinesApi::class)

package com.Backend_RMP.storage

import io.lettuce.core.RedisClient
import io.lettuce.core.api.coroutines

class RedisStorage {
    private val client = RedisClient.create("redis://redis:6379")
    private val connection = client.connect().coroutines()
    private val commands = connection

    suspend fun saveToken(token: String, userId: String) {
        commands.set(token, userId)
    }

    suspend fun getUserIdByToken(token: String): String? {
        return commands.get(token)
    }
}