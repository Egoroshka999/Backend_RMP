package com.Backend_RMP.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(val username: String, val password: String)