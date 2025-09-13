package com.docuflow.backend.controller

import com.docuflow.backend.model.Document
import com.docuflow.backend.repository.DocumentRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import java.io.ByteArrayInputStream
import org.springframework.http.HttpHeaders

@RestController
@RequestMapping("/files")
class DocumentController(private val documentRepository: DocumentRepository) {

    // 🟢 Listar todos los archivos
    @GetMapping
    fun listFiles(): ResponseEntity<List<Document>> {
        val files = documentRepository.findAll()
        return ResponseEntity.ok(files)
    }

    // 🟢 Obtener metadatos por ID
    @GetMapping("/{id}")
    fun getFile(@PathVariable id: Long): ResponseEntity<Any> {
        val file = documentRepository.findById(id)
        return if (file.isPresent) {
            ResponseEntity.ok(file.get())
        } else {
            ResponseEntity.status(404).body(mapOf("error" to "Archivo no encontrado"))
        }
    }

    // 🟢 Descargar un archivo desde Google Cloud Storage
    @GetMapping("/{id}/download")
    fun downloadFile(@PathVariable id: Long): ResponseEntity<Any> {
        val document = documentRepository.findById(id)
        if (document.isEmpty) {
            return ResponseEntity.status(404).body(mapOf("error" to "Archivo no encontrado"))
        }

        // Extraer bucket y nombre de archivo desde filePath (ej: gs://bucket/filename)
        val filePath = document.get().filePath
        val regex = Regex("""gs://([^/]+)/(.+)""")
        val match = regex.matchEntire(filePath)
        if (match == null) {
            return ResponseEntity.status(500).body(mapOf("error" to "Ruta de archivo inválida"))
        }
        val bucketName = match.groupValues[1]
        val fileName = match.groupValues[2]

        val credentialsJson = System.getenv("GCP_KEY_JSON")
        val storage: Storage = StorageOptions.newBuilder()
            .setCredentials(ServiceAccountCredentials.fromStream(ByteArrayInputStream(credentialsJson.toByteArray())))
            .build()
            .service

        val blob = storage.get(bucketName, fileName)
        if (blob == null) {
            return ResponseEntity.status(404).body(mapOf("error" to "Archivo no encontrado en GCS"))
        }
        val fileBytes = blob.getContent()

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${document.get().filename}\"")
            .header(HttpHeaders.CONTENT_TYPE, document.get().fileType)
            .body(fileBytes)
    }

    // 🟢 Eliminar un archivo
    @DeleteMapping("/{id}")
    fun deleteFile(@PathVariable id: Long): ResponseEntity<Map<String, String>> {
        return if (documentRepository.existsById(id)) {
            documentRepository.deleteById(id)
            ResponseEntity.ok(mapOf("mensaje" to "Archivo eliminado"))
        } else {
            ResponseEntity.status(404).body(mapOf("error" to "Archivo no encontrado"))
        }
    }
}