package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

object JwtConfig {
    private const val secret = "secret"
    private const val issuer = "ktor.io"
    private const val audience = "ktor-audience"
    private const val validityInMs = 36_000_00 * 10 // 10 hours
    private const val refreshValidityInMs = 36_000_00 * 24 * 7 // 7 days

    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .withAudience(audience)
        .build()

    // تولید Access Token با مدت زمان کمتر
    fun generateToken(username: String): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withAudience(audience)
        .withClaim("username", username)
        .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
        .sign(algorithm)

    // تولید Refresh Token با مدت زمان طولانی‌تر
    fun generateRefreshToken(username: String): String = JWT.create()
        .withSubject("Refresh")
        .withIssuer(issuer)
        .withAudience(audience)
        .withClaim("username", username)
        .withExpiresAt(Date(System.currentTimeMillis() + refreshValidityInMs))
        .sign(algorithm)

    // اعتبارسنجی Refresh Token و برگشت نام کاربری یا null اگر نامعتبر بود
    fun verifyRefreshToken(token: String): String? {
        return try {
            val decodedJWT = verifier.verify(token)
            // چک می‌کنیم که این توکن Refresh باشه (می‌توانیم Subject را چک کنیم)
            if (decodedJWT.subject != "Refresh") return null

            val expiresAt = decodedJWT.expiresAt ?: return null
            if (expiresAt.before(Date())) return null // منقضی شده

            decodedJWT.getClaim("username").asString()
        } catch (e: Exception) {
            null
        }
    }
}
