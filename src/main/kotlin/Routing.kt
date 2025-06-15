package com.example

import com.example.model.LoginRequest
import com.example.model.LoginResponse
import com.example.model.MobileApiRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        post("/login") {
            val data = call.receive<MobileApiRequest<LoginRequest>>()
            println(data.toString())
            val request = data.data

            if (request.username == "admin" && request.password == "1234") {
                val accessToken = JwtConfig.generateToken(request.username)
                val refreshToken = JwtConfig.generateRefreshToken(request.username)

                println("Access token: $accessToken")
                println("Refresh token: $refreshToken")

                val loginResponse =
                    LoginResponse(
                        accessToken = accessToken,
//                        expiresIn = ,
                        refreshToken = refreshToken
                    )

                call.respond(loginResponse)
            } else {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }

        post("/refresh") {
            val refreshRequest = call.receive<RefreshRequest>()
            val username = JwtConfig.verifyRefreshToken(refreshRequest.refresh_token)
            if (username != null) {
                val newAccessToken = JwtConfig.generateToken(username)
                val newRefreshToken = JwtConfig.generateRefreshToken(username)

                println("New access token: $newAccessToken")
                println("New refresh token: $newRefreshToken")

                call.respond(
                    mapOf(
                        "access_token" to newAccessToken,
                        "refresh_token" to newRefreshToken
                    )
                )
            } else {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }


        authenticate("auth-jwt") {
            get("/profile") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal?.payload?.getClaim("username")?.asString()
                call.respond(mapOf("username" to username))
            }
        }
    }
}

@Serializable
data class RefreshRequest(val refresh_token: String)