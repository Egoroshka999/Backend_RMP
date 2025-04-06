package com.Backend_RMP.models

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(val id: String?, val username: String, val password: String)