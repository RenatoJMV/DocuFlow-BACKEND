package com.docuflow.backend.controller

import com.docuflow.backend.model.User
import com.docuflow.backend.repository.UserRepository
import com.docuflow.backend.security.JwtUtil
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/users")
class UserManagementController(
    private val userRepository: UserRepository
) {

    @GetMapping
    fun getAllUsers(
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<Map<String, Any>> {
        // Validar token
        val token = authHeader?.removePrefix("Bearer ") 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token faltante"))

        val username = JwtUtil.validateToken(token) 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token inválido"))

        val users = userRepository.findAll().map { user ->
            mapOf<String, Any>(
                "id" to (user.id ?: 0L),
                "username" to user.username,
                "email" to "${user.username}@docuflow.com", // Email simulado
                "role" to (user.role ?: "viewer"),
                "status" to "active",
                "permissions" to user.permissions,
                "lastLogin" to LocalDateTime.now().minusDays((1..30).random().toLong()),
                "createdAt" to LocalDateTime.now().minusMonths((1..12).random().toLong())
            )
        }

        return ResponseEntity.ok(mapOf("success" to true, "users" to users))
    }

    @GetMapping("/{id}")
    fun getUserById(
        @PathVariable id: Long,
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<Map<String, Any>> {
        // Validar token
        val token = authHeader?.removePrefix("Bearer ") 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token faltante"))

        val username = JwtUtil.validateToken(token) 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token inválido"))

        val user = userRepository.findById(id).orElse(null)
            ?: return ResponseEntity.notFound().build()

        val userData = mapOf<String, Any>(
            "id" to (user.id ?: 0L),
            "username" to user.username,
            "email" to "${user.username}@docuflow.com",
            "role" to (user.role ?: "viewer"),
            "status" to "active",
            "permissions" to user.permissions,
            "lastLogin" to LocalDateTime.now().minusDays((1..30).random().toLong()),
            "createdAt" to LocalDateTime.now().minusMonths((1..12).random().toLong())
        )

        return ResponseEntity.ok(userData)
    }

    // Listar roles disponibles
    @GetMapping("/roles")
    fun getRoles(
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<Map<String, Any>> {
        // Validar token
        val token = authHeader?.removePrefix("Bearer ") 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token faltante"))

        val username = JwtUtil.validateToken(token) 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token inválido"))

        val roles = listOf(
            mapOf(
                "id" to "admin",
                "name" to "Administrador",
                "description" to "Acceso completo al sistema",
                "permissions" to getAllPermissions()
            ),
            mapOf(
                "id" to "editor",
                "name" to "Editor",
                "description" to "Puede editar documentos y comentarios",
                "permissions" to listOf(
                    "files.read", "files.upload", "files.edit", "files.delete",
                    "comments.read", "comments.create", "comments.edit"
                )
            ),
            mapOf(
                "id" to "viewer",
                "name" to "Visualizador",
                "description" to "Solo puede ver documentos",
                "permissions" to listOf("files.read", "comments.read")
            ),
            mapOf(
                "id" to "guest",
                "name" to "Invitado",
                "description" to "Acceso limitado",
                "permissions" to listOf("files.read")
            )
        )

        return ResponseEntity.ok(mapOf("success" to true, "roles" to roles))
    }

    // Cambiar el rol de un usuario
    @PutMapping("/{id}/role")
    fun setUserRole(
        @PathVariable id: Long, 
        @RequestBody body: Map<String, String>,
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<Map<String, Any>> {
        // Validar token
        val token = authHeader?.removePrefix("Bearer ") 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token faltante"))

        val username = JwtUtil.validateToken(token) 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token inválido"))

        val user = userRepository.findById(id).orElse(null)
            ?: return ResponseEntity.notFound().build()

        val newRole = body["role"] ?: return ResponseEntity.badRequest()
            .body(mapOf("error" to "Role no especificado"))

        user.role = newRole
        // Asignar permisos automáticamente según el rol
        user.permissions = getPermissionsForRole(newRole)
        userRepository.save(user)

        return ResponseEntity.ok(mapOf<String, Any>(
            "success" to true, 
            "message" to "Rol actualizado correctamente",
            "user" to mapOf<String, Any>(
                "id" to (user.id ?: 0L),
                "username" to user.username,
                "role" to (user.role ?: "viewer"),
                "permissions" to user.permissions
            )
        ))
    }

    // Obtener permisos de un usuario
    @GetMapping("/{id}/permissions")
    fun getUserPermissions(
        @PathVariable id: Long,
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<Map<String, Any>> {
        // Validar token
        val token = authHeader?.removePrefix("Bearer ") 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token faltante"))

        val username = JwtUtil.validateToken(token) 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token inválido"))

        val user = userRepository.findById(id).orElse(null)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(mapOf(
            "success" to true,
            "permissions" to user.permissions,
            "role" to user.role
        ))
    }

    // Actualizar permisos de un usuario
    @PutMapping("/{id}/permissions")
    fun setUserPermissions(
        @PathVariable id: Long, 
        @RequestBody body: Map<String, List<String>>,
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<Map<String, Any>> {
        // Validar token
        val token = authHeader?.removePrefix("Bearer ") 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token faltante"))

        val username = JwtUtil.validateToken(token) 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token inválido"))

        val user = userRepository.findById(id).orElse(null)
            ?: return ResponseEntity.notFound().build()

        val newPerms = body["permissions"] ?: return ResponseEntity.badRequest()
            .body(mapOf("error" to "Permisos no especificados"))

        user.permissions = newPerms.toSet()
        userRepository.save(user)

        return ResponseEntity.ok(mapOf(
            "success" to true,
            "message" to "Permisos actualizados correctamente",
            "permissions" to user.permissions
        ))
    }

    private fun getAllPermissions(): List<String> {
        return listOf(
            "files.read", "files.upload", "files.edit", "files.delete", "files.download",
            "comments.read", "comments.create", "comments.edit", "comments.delete",
            "users.read", "users.edit", "users.delete", "users.permissions",
            "dashboard.read", "logs.read", "system.admin"
        )
    }

    private fun getPermissionsForRole(role: String): Set<String> {
        return when (role) {
            "admin" -> getAllPermissions().toSet()
            "editor" -> setOf(
                "files.read", "files.upload", "files.edit", "files.delete", "files.download",
                "comments.read", "comments.create", "comments.edit", "comments.delete",
                "dashboard.read"
            )
            "viewer" -> setOf("files.read", "files.download", "comments.read", "dashboard.read")
            "guest" -> setOf("files.read")
            else -> setOf("files.read")
        }
    }
}