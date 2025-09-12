package com.docuflow.backend.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object JwtUtil {
    private const val SECRET = "mi_clave_secreta" // ⚠️ Mejor usar variable de entorno
    private val algorithm = Algorithm.HMAC256(SECRET)

    fun generateToken(username: String): String {
        return JWT.create()
            .withSubject(username)
            .withExpiresAt(Date(System.currentTimeMillis() + 3600_000)) // 1 hora
            .sign(algorithm)
    }

    fun validateToken(token: String): String? {
        return try {
            val verifier = JWT.require(algorithm).build()
            val decoded = verifier.verify(token)
            decoded.subject
        } catch (e: Exception) {
            null
        }
    }
}
