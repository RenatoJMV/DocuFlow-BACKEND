package com.docuflow.backend.controller

import com.docuflow.backend.service.GcsUtil
import com.docuflow.backend.security.JwtUtil
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/gcs")
class GcsController(
    private val gcsUtil: GcsUtil
) {

    @GetMapping("/files")
    fun getFilesFromGcs(
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<Map<String, Any>> {
        val token = authHeader?.removePrefix("Bearer ") 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token faltante"))
        
        val username = JwtUtil.validateToken(token) 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token inválido"))

        try {
            val bucketName = System.getenv("GCP_BUCKET_NAME") ?: "docuflow-files"
            val files = gcsUtil.listAllFilesInBucket(bucketName)
            
            val filesInfo = files.map { blob ->
                mapOf<String, Any>(
                    "name" to blob.name,
                    "size" to blob.size,
                    "contentType" to (blob.contentType ?: "unknown"),
                    "created" to blob.createTime,
                    "updated" to blob.updateTime,
                    "md5Hash" to (blob.md5 ?: ""),
                    "generation" to blob.generation,
                    "isDirectory" to blob.isDirectory,
                    "downloadUrl" to "https://storage.googleapis.com/$bucketName/${blob.name}"
                )
            }
            
            val response: Map<String, Any> = mapOf(
                "success" to true,
                "bucket" to bucketName,
                "totalFiles" to files.size,
                "totalSize" to files.sumOf { it.size },
                "files" to filesInfo,
                "timestamp" to LocalDateTime.now()
            )
            
            return ResponseEntity.ok(response)
            
        } catch (e: Exception) {
            return ResponseEntity.status(500).body(mapOf(
                "success" to false,
                "error" to "Error al obtener archivos desde GCS: ${e.message}",
                "timestamp" to LocalDateTime.now()
            ))
        }
    }

    @GetMapping("/files/stats")
    fun getGcsStats(
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<Map<String, Any>> {
        val token = authHeader?.removePrefix("Bearer ") 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token faltante"))
        
        val username = JwtUtil.validateToken(token) 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token inválido"))

        try {
            val bucketName = System.getenv("GCP_BUCKET_NAME") ?: "docuflow-files"
            val files = gcsUtil.listAllFilesInBucket(bucketName)
            
            val totalSize = files.sumOf { it.size }
            val totalFiles = files.size
            val largestFile = files.maxByOrNull { it.size }
            
            val typeDistribution = files.groupBy { 
                it.contentType?.split("/")?.get(0) ?: "unknown" 
            }.mapValues { it.value.size }
            
            val sizeDistribution = files.groupBy {
                when {
                    it.size < 1024 * 1024 -> "< 1MB"
                    it.size < 10 * 1024 * 1024 -> "1-10MB"
                    it.size < 100 * 1024 * 1024 -> "10-100MB"
                    else -> "> 100MB"
                }
            }.mapValues { it.value.size }
            
            val response: Map<String, Any> = mapOf(
                "success" to true,
                "bucket" to bucketName,
                "stats" to mapOf(
                    "totalFiles" to totalFiles,
                    "totalSizeBytes" to totalSize,
                    "totalSizeFormatted" to formatFileSize(totalSize),
                    "averageFileSize" to if (totalFiles > 0) totalSize / totalFiles else 0,
                    "largestFile" to mapOf(
                        "name" to (largestFile?.name ?: ""),
                        "size" to (largestFile?.size ?: 0),
                        "sizeFormatted" to formatFileSize(largestFile?.size ?: 0)
                    ),
                    "typeDistribution" to typeDistribution,
                    "sizeDistribution" to sizeDistribution
                ),
                "timestamp" to LocalDateTime.now()
            )
            
            return ResponseEntity.ok(response)
            
        } catch (e: Exception) {
            return ResponseEntity.status(500).body(mapOf(
                "success" to false,
                "error" to "Error al obtener estadísticas de GCS: ${e.message}",
                "timestamp" to LocalDateTime.now()
            ))
        }
    }

    @GetMapping("/files/orphaned")
    fun getOrphanedFiles(
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<Map<String, Any>> {
        val token = authHeader?.removePrefix("Bearer ") 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token faltante"))
        
        val username = JwtUtil.validateToken(token) 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token inválido"))

        try {
            // Este endpoint identifica archivos en GCS que no están registrados en la BD
            // Por ahora devolvemos una estructura de ejemplo
            // En una implementación completa, compararías los archivos de GCS con los de la BD
            
            val response: Map<String, Any> = mapOf(
                "success" to true,
                "message" to "Función en desarrollo - detectar archivos huérfanos",
                "orphanedFiles" to emptyList<Map<String, Any>>(),
                "timestamp" to LocalDateTime.now()
            )
            
            return ResponseEntity.ok(response)
            
        } catch (e: Exception) {
            return ResponseEntity.status(500).body(mapOf(
                "success" to false,
                "error" to "Error al buscar archivos huérfanos: ${e.message}",
                "timestamp" to LocalDateTime.now()
            ))
        }
    }

    private fun formatFileSize(bytes: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var size = bytes.toDouble()
        var unitIndex = 0
        
        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }
        
        return "%.1f %s".format(size, units[unitIndex])
    }
}