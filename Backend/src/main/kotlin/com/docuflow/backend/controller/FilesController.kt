package com.docuflow.backend.controller

import com.docuflow.backend.model.Document
import com.docuflow.backend.repository.DocumentRepository
import com.docuflow.backend.repository.LogEntryRepository
import com.docuflow.backend.model.LogEntry
import com.docuflow.backend.security.JwtUtil
import com.docuflow.backend.service.GcsUtil
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import com.google.cloud.storage.BlobId
import java.io.ByteArrayInputStream
import org.springframework.http.HttpHeaders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@RestController
@RequestMapping("/files")
class FilesController(
    private val documentRepository: DocumentRepository,
    @Autowired private val logEntryRepository: LogEntryRepository
) {

    // 🟢 Listar todos los archivos
    @GetMapping
    fun listFiles(
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<Any> {
        // Validar token JWT
        val token = authHeader?.removePrefix("Bearer ") 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token faltante"))
        
        val username = JwtUtil.validateToken(token) 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token inválido"))

        val files = documentRepository.findAll()
        return ResponseEntity.ok(mapOf("success" to true, "files" to files))
    }

    // 🟢 Obtener metadatos por ID
    @GetMapping("/{id}")
    fun getFile(
        @PathVariable id: Long,
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<Any> {
        // Validar token JWT
        val token = authHeader?.removePrefix("Bearer ") 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token faltante"))
        
        val username = JwtUtil.validateToken(token) 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token inválido"))

        val file = documentRepository.findById(id)
        return if (file.isPresent) {
            ResponseEntity.ok(mapOf("success" to true, "file" to file.get()))
        } else {
            ResponseEntity.status(404).body(mapOf("error" to "Archivo no encontrado"))
        }
    }

    // 🆕 Subir un archivo a Google Cloud Storage
    @PostMapping
    fun uploadFile(
        @RequestParam("file") file: MultipartFile,
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<Map<String, Any>> {
        // Validar token JWT
        val token = authHeader?.removePrefix("Bearer ") 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token faltante"))
        
        val username = JwtUtil.validateToken(token) 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token inválido"))

        return try {
            // Validar archivo
            if (file.isEmpty) {
                return ResponseEntity.badRequest()
                    .body(mapOf("error" to "El archivo está vacío"))
            }

            // Validar tamaño (máximo 20MB)
            if (file.size > 20 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                    .body(mapOf("error" to "El archivo excede el tamaño máximo de 20MB"))
            }

            // Obtener credenciales y nombre del bucket desde variables de entorno
            val bucketName = System.getenv("GCP_BUCKET_NAME") 
                ?: return ResponseEntity.status(500)
                    .body(mapOf("error" to "GCP_BUCKET_NAME no configurado"))
            
            val credentialsJson = System.getenv("GCP_KEY_JSON") 
                ?: return ResponseEntity.status(500)
                    .body(mapOf("error" to "GCP_KEY_JSON no configurado"))

            // Subir archivo a Google Cloud Storage
            val gcsPath = GcsUtil.uploadFile(file, bucketName, credentialsJson)

            // Guardar metadatos en la base de datos
            val document = Document(
                filename = file.originalFilename ?: "archivo-sin-nombre",
                fileType = file.contentType ?: "application/octet-stream",
                filePath = gcsPath,
                size = file.size
            )
            val savedDocument = documentRepository.save(document)

            // Registrar log de subida
            logEntryRepository.save(
                LogEntry(
                    action = "upload",
                    username = username,
                    documentId = savedDocument.id,
                    timestamp = LocalDateTime.now()
                )
            )

            ResponseEntity.ok(mapOf(
                "success" to true, 
                "mensaje" to "Archivo subido exitosamente",
                "fileId" to savedDocument.id,
                "filename" to savedDocument.filename
            ))
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.status(500).body(mapOf(
                "error" to "Error al subir el archivo: ${e.message}"
            ))
        }
    }

    // 🟢 Descargar un archivo desde Google Cloud Storage
    @GetMapping("/{id}/download")
    fun downloadFile(
        @PathVariable id: Long,
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<Any> {
        // Validar token JWT
        val token = authHeader?.removePrefix("Bearer ") 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token faltante"))
        
        val username = JwtUtil.validateToken(token) 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token inválido"))

        val document = documentRepository.findById(id)
        if (document.isEmpty) {
            return ResponseEntity.status(404).body(mapOf("error" to "Archivo no encontrado"))
        }

        return try {
            // Extraer bucket y nombre de archivo desde filePath (ej: gs://bucket/filename)
            val filePath = document.get().filePath
            val regex = Regex("""gs://([^/]+)/(.+)""")
            val match = regex.matchEntire(filePath)
            if (match == null) {
                return ResponseEntity.status(500)
                    .body(mapOf("error" to "Ruta de archivo inválida"))
            }
            val bucketName = match.groupValues[1]
            val fileName = match.groupValues[2]

            // Obtener credenciales de GCS
            val credentialsJson = System.getenv("GCP_KEY_JSON") 
                ?: return ResponseEntity.status(500)
                    .body(mapOf("error" to "GCP_KEY_JSON no configurado"))

            val storage: Storage = StorageOptions.newBuilder()
                .setCredentials(
                    ServiceAccountCredentials.fromStream(
                        ByteArrayInputStream(credentialsJson.toByteArray())
                    )
                )
                .build()
                .service

            // Obtener el archivo de GCS
            val blob = storage.get(bucketName, fileName)
            if (blob == null) {
                return ResponseEntity.status(404)
                    .body(mapOf("error" to "Archivo no encontrado en GCS"))
            }
            val fileBytes = blob.getContent()

            // Registrar log de descarga
            logEntryRepository.save(
                LogEntry(
                    action = "download",
                    username = username,
                    documentId = document.get().id,
                    timestamp = LocalDateTime.now()
                )
            )

            // Devolver el archivo
            ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"${document.get().filename}\"")
                .header(HttpHeaders.CONTENT_TYPE, document.get().fileType)
                .body(fileBytes)
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.status(500).body(mapOf(
                "error" to "Error al descargar el archivo: ${e.message}"
            ))
        }
    }

    // 🟢 Eliminar un archivo (de GCS y de la BD)
    @DeleteMapping("/{id}")
    fun deleteFile(
        @PathVariable id: Long,
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<Map<String, Any>> {
        // Validar token JWT
        val token = authHeader?.removePrefix("Bearer ") 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token faltante"))
        
        val username = JwtUtil.validateToken(token) 
            ?: return ResponseEntity.status(401).body(mapOf("error" to "Token inválido"))

        val document = documentRepository.findById(id)
        if (document.isEmpty) {
            return ResponseEntity.status(404).body(mapOf("error" to "Archivo no encontrado"))
        }

        return try {
            val doc = document.get()
            
            // Extraer bucket y nombre de archivo desde filePath
            val filePath = doc.filePath
            val regex = Regex("""gs://([^/]+)/(.+)""")
            val match = regex.matchEntire(filePath)
            
            if (match != null) {
                val bucketName = match.groupValues[1]
                val fileName = match.groupValues[2]

                // Obtener credenciales de GCS
                val credentialsJson = System.getenv("GCP_KEY_JSON")
                if (credentialsJson != null) {
                    val storage: Storage = StorageOptions.newBuilder()
                        .setCredentials(
                            ServiceAccountCredentials.fromStream(
                                ByteArrayInputStream(credentialsJson.toByteArray())
                            )
                        )
                        .build()
                        .service

                    // Eliminar archivo de Google Cloud Storage
                    val blobId = BlobId.of(bucketName, fileName)
                    val deleted = storage.delete(blobId)
                    
                    if (!deleted) {
                        println("⚠️ Advertencia: No se pudo eliminar el archivo de GCS")
                    }
                }
            }

            // Eliminar registro de la base de datos
            documentRepository.deleteById(id)

            // Registrar log de eliminación
            logEntryRepository.save(
                LogEntry(
                    action = "delete",
                    username = username,
                    documentId = id,
                    timestamp = LocalDateTime.now()
                )
            )

            ResponseEntity.ok(mapOf(
                "success" to true, 
                "mensaje" to "Archivo eliminado correctamente"
            ))
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.status(500).body(mapOf(
                "error" to "Error al eliminar el archivo: ${e.message}"
            ))
        }
    }
}
