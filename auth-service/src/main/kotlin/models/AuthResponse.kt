package com.Backend_RMP.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(val message: String, val token: String?)