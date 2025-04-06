package com.Backend_RMP.routes

import com.Backend_RMP.models.AuthRequest
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.Backend_RMP.services.AuthService

fun Route.authRoutes() {
    val authService = AuthService()

    route("/auth") {
        post("/register") {
            val request = call.receive<AuthRequest>()
            val (status, result) = authService.register(request)
            call.respond(status, result)
        }

        post("/login") {
            val request = call.receive<AuthRequest>()
            val (status, result) = authService.login(request)
            call.respond(status, result)
        }

        get("/verify") {
            val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")
            val (status, result) = authService.verify(token)
            call.respond(status, result)
        }
    }
}