package com.docuflow.backend.controller

import com.docuflow.backend.security.JwtUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class LoginRequest(val username: String, val password: String)

@RestController
@RequestMapping("/")
class LoginController {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Map<String, Any>> {
        // Obtener credenciales desde variables de entorno
        val userEnv = System.getenv("APP_USER") ?: "estudiante"
        val passEnv = System.getenv("APP_PASS") ?: "123456"

        if (request.username == userEnv && request.password == passEnv) {
            val token = JwtUtil.generateToken(request.username)
            
            // Formato de respuesta esperado por el frontend
            val response: Map<String, Any> = mapOf(
                "success" to true,
                "token" to token,
                "user" to mapOf(
                    "username" to request.username,
                    "role" to "admin",
                    "permissions" to listOf("download", "delete", "comment", "edit", "share", "admin", "view_logs", "manage_users")
                ),
                "message" to "Login exitoso"
            )
            
            return ResponseEntity.ok(response)
        }
        
        // Error de credenciales
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(mapOf(
                "success" to false,
                "error" to "Credenciales inválidas",
                "code" to "INVALID_CREDENTIALS"
            ))
    }
}
