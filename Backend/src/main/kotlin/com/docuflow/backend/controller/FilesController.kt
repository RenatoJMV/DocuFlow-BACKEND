package com.docuflow.backend.controller

import com.docuflow.backend.model.Document
import com.docuflow.backend.repository.DocumentRepository
import com.docuflow.backend.repository.LogEntryRepository
import com.docuflow.backend.model.LogEntry
import com.docuflow.backend.security.JwtUtil
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import java.io.File
import java.time.LocalDateTime

@RestController
@RequestMapping("/files")
class FilesController(
    private val documentRepository: DocumentRepository,
    private val logEntryRepository: LogEntryRepository
) {

    @GetMapping
    fun getAllFiles(
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<Map<String, Any>> {
        // Validar token
        val token = authHeader?.removePrefix("Bearer ") 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token faltante"))

        val username = JwtUtil.validateToken(token) 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token inválido"))

        val files = documentRepository.findAll().map { doc ->
            mapOf(
                "id" to doc.id,
                "filename" to doc.filename,
                "fileType" to doc.fileType,
                "size" to doc.size,
                "filePath" to doc.filePath,
                "uploadDate" to LocalDateTime.now().minusDays((1..30).random().toLong()),
                "formattedSize" to formatFileSize(doc.size),
                "extension" to doc.filename.substringAfterLast(".").uppercase(),
                "canPreview" to canPreview(doc.fileType),
                "status" to "active"
            )
        }

        return ResponseEntity.ok(mapOf("files" to files))
    }

    @GetMapping("/{id}")
    fun getFileById(
        @PathVariable id: Long,
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<Map<String, Any>> {
        // Validar token
        val token = authHeader?.removePrefix("Bearer ") 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token faltante"))

        val username = JwtUtil.validateToken(token) 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token inválido"))

        val document = documentRepository.findById(id).orElse(null)
            ?: return ResponseEntity.notFound().build()

        val fileData = mapOf(
            "id" to document.id,
            "filename" to document.filename,
            "fileType" to document.fileType,
            "size" to document.size,
            "filePath" to document.filePath,
            "formattedSize" to formatFileSize(document.size),
            "extension" to document.filename.substringAfterLast(".").uppercase(),
            "canPreview" to canPreview(document.fileType)
        )

        return ResponseEntity.ok(fileData)
    }

    @GetMapping("/{id}/download")
    fun downloadFile(
        @PathVariable id: Long,
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<Resource> {
        // Validar token
        val token = authHeader?.removePrefix("Bearer ") 
            ?: return ResponseEntity.status(401).build()

        val username = JwtUtil.validateToken(token) 
            ?: return ResponseEntity.status(401).build()

        val document = documentRepository.findById(id).orElse(null)
            ?: return ResponseEntity.notFound().build()

        return try {
            // Para archivos locales (en la carpeta uploads)
            val file = File("uploads/${document.filename}")
            val resource: Resource = if (file.exists()) {
                FileSystemResource(file)
            } else {
                // Si no existe en uploads, buscar en classpath como fallback
                ClassPathResource("static/files/${document.filename}")
            }

            if (resource.exists()) {
                // Registrar log de descarga
                logEntryRepository.save(
                    LogEntry(
                        action = "download",
                        username = username,
                        documentId = document.id,
                        timestamp = LocalDateTime.now()
                    )
                )

                ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${document.filename}\"")
                    .header(HttpHeaders.CONTENT_TYPE, document.fileType)
                    .body(resource)
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteFile(
        @PathVariable id: Long,
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<Map<String, String>> {
        // Validar token
        val token = authHeader?.removePrefix("Bearer ") 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token faltante"))

        val username = JwtUtil.validateToken(token) 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token inválido"))

        val document = documentRepository.findById(id).orElse(null)
            ?: return ResponseEntity.notFound().build()

        return try {
            // Intentar eliminar archivo físico
            val file = File("uploads/${document.filename}")
            if (file.exists()) {
                file.delete()
            }

            // Eliminar registro de la BD
            documentRepository.deleteById(id)

            // Registrar log de eliminación
            logEntryRepository.save(
                LogEntry(
                    action = "delete",
                    username = username,
                    documentId = document.id,
                    timestamp = LocalDateTime.now()
                )
            )

            ResponseEntity.ok(mapOf("message" to "Archivo eliminado correctamente"))
        } catch (e: Exception) {
            ResponseEntity.internalServerError()
                .body(mapOf("error" to "Error al eliminar el archivo"))
        }
    }

    @PutMapping("/{id}")
    fun updateFile(
        @PathVariable id: Long,
        @RequestBody updateData: Map<String, Any>,
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<Map<String, Any>> {
        // Validar token
        val token = authHeader?.removePrefix("Bearer ") 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token faltante"))

        val username = JwtUtil.validateToken(token) 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token inválido"))

        val document = documentRepository.findById(id).orElse(null)
            ?: return ResponseEntity.notFound().build()

        // Actualizar nombre si se proporciona
        val newFilename = updateData["filename"] as? String
        if (newFilename != null && newFilename.isNotBlank()) {
            val oldFile = File("uploads/${document.filename}")
            val newFile = File("uploads/$newFilename")
            
            if (oldFile.exists()) {
                oldFile.renameTo(newFile)
            }
            
            document.filename = newFilename
            documentRepository.save(document)
        }

        val updatedFileData = mapOf(
            "id" to document.id,
            "filename" to document.filename,
            "fileType" to document.fileType,
            "size" to document.size,
            "formattedSize" to formatFileSize(document.size)
        )

        return ResponseEntity.ok(updatedFileData)
    }

    private fun formatFileSize(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val k = 1024.0
        val sizes = arrayOf("B", "KB", "MB", "GB", "TB")
        val i = (Math.log(bytes.toDouble()) / Math.log(k)).toInt()
        return String.format("%.1f %s", bytes / Math.pow(k, i.toDouble()), sizes[i])
    }

    private fun canPreview(fileType: String): Boolean {
        return fileType.startsWith("image/") || 
               fileType == "application/pdf" ||
               fileType.startsWith("text/")
    }
}