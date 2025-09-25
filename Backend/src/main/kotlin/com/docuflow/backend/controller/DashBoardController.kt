package com.docuflow.backend.controller

import com.docuflow.backend.model.User
import com.docuflow.backend.model.Comment
import com.docuflow.backend.model.LogEntry
import com.docuflow.backend.model.Document
import com.docuflow.backend.repository.UserRepository
import com.docuflow.backend.repository.CommentRepository
import com.docuflow.backend.repository.LogEntryRepository
import com.docuflow.backend.repository.DocumentRepository
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import java.time.LocalDate
import java.time.LocalDateTime

@RestController
@RequestMapping("/dashboard")
class DashboardController(
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository,
    private val logEntryRepository: LogEntryRepository,
    private val documentRepository: DocumentRepository
) {

    @GetMapping("/stats")
    fun getDashboardStats(): ResponseEntity<Map<String, Any>> {
        val totalFiles = documentRepository.count()
        val totalUsers = userRepository.count()
        val today = LocalDate.now()
        val downloadsToday = logEntryRepository.findAll()
            .count { it.action == "download" && it.timestamp.toLocalDate() == today }
        
        // Calcular tamaño total de archivos
        val totalSize = documentRepository.findAll().sumOf { it.size }
        val storageSizeFormatted = formatFileSize(totalSize)
        
        val stats = mapOf(
            "totalFiles" to totalFiles,
            "totalUsers" to totalUsers,
            "downloadsToday" to downloadsToday,
            "storageUsed" to storageSizeFormatted,
            "pendingTasks" to commentRepository.findAll().count { 
                it.content.contains("task") || it.content.contains("tarea") 
            },
            "totalComments" to commentRepository.count()
        )
        
        return ResponseEntity.ok(stats)
    }

    @GetMapping("/activity")
    fun getRecentActivity(): ResponseEntity<List<Map<String, Any>>> {
        val recentLogs = logEntryRepository.findAll()
            .sortedByDescending { it.timestamp }
            .take(20)
            .map { log ->
                val document = documentRepository.findById(log.documentId ?: 0L).orElse(null)
                mapOf<String, Any>(
                    "id" to (log.id ?: 0L),
                    "action" to log.action,
                    "username" to log.username,
                    "documentName" to (document?.filename ?: "Unknown"),
                    "timestamp" to log.timestamp,
                    "status" to getActionStatus(log.action),
                    "details" to getActionDetails(log.action, document?.filename)
                )
            }
        
        return ResponseEntity.ok(recentLogs)
    }

    @GetMapping("/users")
    fun getAllUsers(): ResponseEntity<List<Map<String, Any>>> {
        val users = userRepository.findAll().map { user ->
            mapOf<String, Any>(
                "id" to (user.id ?: 0L),
                "username" to user.username,
                "email" to (user.username + "@docuflow.com"), // Temporal
                "role" to "user", // Por ahora role básico
                "status" to "active",
                "lastLogin" to LocalDateTime.now().minusDays((1..30).random().toLong())
            )
        }
        return ResponseEntity.ok(users)
    }

    @GetMapping("/comments")
    fun getAllComments(): ResponseEntity<List<Comment>> {
        return ResponseEntity.ok(commentRepository.findAll())
    }

    @GetMapping("/logs")
    fun getAllLogs(): ResponseEntity<List<Map<String, Any>>> {
        val logs = logEntryRepository.findAll().map { log ->
            val document = documentRepository.findById(log.documentId ?: 0L).orElse(null)
            mapOf<String, Any>(
                "id" to (log.id ?: 0L),
                "action" to log.action,
                "username" to log.username,
                "documentId" to (log.documentId ?: 0L),
                "documentName" to (document?.filename ?: "Unknown"),
                "timestamp" to log.timestamp,
                "level" to mapActionToLevel(log.action),
                "details" to getActionDetails(log.action, document?.filename),
                "ip" to "127.0.0.1" // Por ahora IP simulada
            )
        }
        return ResponseEntity.ok(logs)
    }

    @GetMapping("/downloads/today")
    fun getDownloadsToday(): ResponseEntity<Map<String, Int>> {
        val today = LocalDate.now()
        val count = logEntryRepository.findAll()
            .count { it.action == "download" && it.timestamp.toLocalDate() == today }
        return ResponseEntity.ok(mapOf("count" to count))
    }

    @GetMapping("/files")
    fun getAllFiles(): ResponseEntity<List<Map<String, Any>>> {
        val files = documentRepository.findAll().map { doc ->
            mapOf<String, Any>(
                "id" to (doc.id ?: 0L),
                "filename" to doc.filename,
                "fileType" to doc.fileType,
                "size" to doc.size,
                "filePath" to doc.filePath,
                "uploadDate" to LocalDateTime.now().minusDays((1..30).random().toLong()), // Temporal
                "formattedSize" to formatFileSize(doc.size),
                "extension" to doc.filename.substringAfterLast(".").uppercase()
            )
        }
        return ResponseEntity.ok(files)
    }

    private fun formatFileSize(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val k = 1024.0
        val sizes = arrayOf("B", "KB", "MB", "GB", "TB")
        val i = (Math.log(bytes.toDouble()) / Math.log(k)).toInt()
        return String.format("%.1f %s", bytes / Math.pow(k, i.toDouble()), sizes[i])
    }

    private fun getActionStatus(action: String): String {
        return when (action) {
            "upload" -> "success"
            "download" -> "info"
            "delete" -> "warning"
            "login" -> "success"
            else -> "info"
        }
    }

    private fun getActionDetails(action: String, filename: String?): String {
        return when (action) {
            "upload" -> "Archivo '$filename' subido exitosamente"
            "download" -> "Archivo '$filename' descargado"
            "delete" -> "Archivo '$filename' eliminado"
            "login" -> "Usuario autenticado correctamente"
            else -> "Acción '$action' realizada"
        }
    }

    private fun mapActionToLevel(action: String): String {
        return when (action) {
            "upload", "login" -> "success"
            "download" -> "info"
            "delete" -> "warning"
            "error" -> "error"
            else -> "info"
        }
    }
}