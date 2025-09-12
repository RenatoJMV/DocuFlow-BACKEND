package com.docuflow.backend.controller

import com.docuflow.backend.security.JwtUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class LoginRequest(val username: String, val password: String)

@RestController
@RequestMapping("/login")
class LoginController {

    @PostMapping
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Map<String, String>> {
        if (request.username == "estudiante" && request.password == "123456") {
            val token = JwtUtil.generateToken(request.username)
            return ResponseEntity.ok(mapOf("token" to token))
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(mapOf("error" to "Credenciales inválidas"))
    }
}
