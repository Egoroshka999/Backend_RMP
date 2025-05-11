package com.Backend_RMP.models

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: String?,
    val username: String,
    val password: String,
    val age: Int? = null,
    val weight: Float? = null,
    val height: Float? = null,
    val gender: String? = null,
    val goal: String? = null
)