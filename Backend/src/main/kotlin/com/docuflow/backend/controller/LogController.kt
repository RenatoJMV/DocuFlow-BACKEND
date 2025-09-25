package com.docuflow.backend.controller

import com.docuflow.backend.model.LogEntry
import com.docuflow.backend.repository.LogEntryRepository
import com.docuflow.backend.repository.DocumentRepository
import com.docuflow.backend.security.JwtUtil
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/logs")
class LogController(
    private val logEntryRepository: LogEntryRepository,
    private val documentRepository: DocumentRepository
) {

    @GetMapping
    fun getLogs(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<Map<String, Any>> {
        val token = authHeader?.removePrefix("Bearer ") 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token faltante"))
        
        val username = JwtUtil.validateToken(token) 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token inválido"))

        try {
            val allLogs = logEntryRepository.findAll()
                .sortedByDescending { it.timestamp }
            
            val totalLogs = allLogs.size
            val totalPages = (totalLogs + size - 1) / size
            val startIndex = page * size
            val endIndex = minOf(startIndex + size, totalLogs)
            
            val paginatedLogs = if (startIndex < totalLogs) {
                allLogs.subList(startIndex, endIndex).map { log ->
                    val document = documentRepository.findById(log.documentId ?: 0L).orElse(null)
                    mapOf<String, Any>(
                        "id" to (log.id ?: 0L),
                        "action" to log.action,
                        "username" to log.username,
                        "documentName" to (document?.filename ?: "N/A"),
                        "timestamp" to log.timestamp,
                        "level" to getLogLevel(log.action),
                        "details" to getLogDetails(log.action, document?.filename)
                    )
                }
            } else {
                emptyList()
            }
            
            val response: Map<String, Any> = mapOf(
                "logs" to paginatedLogs,
                "pagination" to mapOf(
                    "currentPage" to page,
                    "totalPages" to totalPages,
                    "totalLogs" to totalLogs,
                    "hasNext" to (page < totalPages - 1),
                    "hasPrevious" to (page > 0)
                )
            )
            
            return ResponseEntity.ok(response)
        } catch (e: Exception) {
            return ResponseEntity.status(500)
                .body(mapOf("error" to "Error al obtener logs"))
        }
    }

    @GetMapping("/recent")
    fun getRecentLogs(
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<List<Map<String, Any>>> {
        val token = authHeader?.removePrefix("Bearer ") 
            ?: return ResponseEntity.status(401).body(emptyList())
        
        val username = JwtUtil.validateToken(token) 
            ?: return ResponseEntity.status(401).body(emptyList())

        try {
            val recentLogs = logEntryRepository.findAll()
                .sortedByDescending { it.timestamp }
                .take(limit)
                .map { log ->
                    val document = documentRepository.findById(log.documentId ?: 0L).orElse(null)
                    mapOf<String, Any>(
                        "id" to (log.id ?: 0L),
                        "action" to log.action,
                        "username" to log.username,
                        "documentName" to (document?.filename ?: "N/A"),
                        "timestamp" to log.timestamp,
                        "level" to getLogLevel(log.action),
                        "details" to getLogDetails(log.action, document?.filename)
                    )
                }
            
            return ResponseEntity.ok(recentLogs)
        } catch (e: Exception) {
            return ResponseEntity.ok(emptyList<Map<String, Any>>())
        }
    }

    @GetMapping("/count")
    fun getLogsCount(
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<Map<String, Any>> {
        val token = authHeader?.removePrefix("Bearer ") 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token faltante"))
        
        val username = JwtUtil.validateToken(token) 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token inválido"))

        try {
            val totalCount = logEntryRepository.count()
            return ResponseEntity.ok(mapOf("count" to totalCount))
        } catch (e: Exception) {
            return ResponseEntity.status(500)
                .body(mapOf("error" to "Error al obtener conteo de logs"))
        }
    }

    @GetMapping("/user/{username}")
    fun getLogsByUser(
        @PathVariable username: String,
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<List<Map<String, Any>>> {
        val token = authHeader?.removePrefix("Bearer ") 
            ?: return ResponseEntity.status(401).body(emptyList())
        
        val requestingUser = JwtUtil.validateToken(token) 
            ?: return ResponseEntity.status(401).body(emptyList())

        try {
            val userLogs = logEntryRepository.findAll()
                .filter { it.username == username }
                .sortedByDescending { it.timestamp }
                .map { log ->
                    val document = documentRepository.findById(log.documentId ?: 0L).orElse(null)
                    mapOf<String, Any>(
                        "id" to (log.id ?: 0L),
                        "action" to log.action,
                        "username" to log.username,
                        "documentName" to (document?.filename ?: "N/A"),
                        "timestamp" to log.timestamp,
                        "level" to getLogLevel(log.action),
                        "details" to getLogDetails(log.action, document?.filename)
                    )
                }
            
            return ResponseEntity.ok(userLogs)
        } catch (e: Exception) {
            return ResponseEntity.ok(emptyList<Map<String, Any>>())
        }
    }

    private fun getLogLevel(action: String): String {
        return when (action) {
            "login", "upload" -> "info"
            "download" -> "success"
            "delete" -> "warning"
            "error", "failed_login" -> "danger"
            "comment" -> "info"
            else -> "info"
        }
    }

    private fun getLogDetails(action: String, filename: String?): String {
        return when (action) {
            "login" -> "Usuario autenticado exitosamente"
            "upload" -> "Subió archivo: ${filename ?: "desconocido"}"
            "download" -> "Descargó archivo: ${filename ?: "desconocido"}"
            "delete" -> "Eliminó archivo: ${filename ?: "desconocido"}"
            "comment" -> "Agregó comentario en: ${filename ?: "documento"}"
            "failed_login" -> "Intento de login fallido"
            else -> "Acción realizada: $action"
        }
    }
}