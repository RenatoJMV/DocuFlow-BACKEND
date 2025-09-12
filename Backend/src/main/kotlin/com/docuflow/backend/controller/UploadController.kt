package com.docuflow.backend.controller

import com.docuflow.backend.security.JwtUtil
import com.docuflow.backend.model.Document
import com.docuflow.backend.repository.DocumentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File

@RestController
@RequestMapping("/upload")
class UploadController {

    @Autowired
    lateinit var documentRepository: DocumentRepository

    @PostMapping
    fun uploadFile(
        @RequestHeader("Authorization") authHeader: String?,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<Map<String, String>> {

        // 1. Validar token
        val token = authHeader?.removePrefix("Bearer ") ?: return ResponseEntity.status(401)
            .body(mapOf("error" to "Token faltante"))

        val username = JwtUtil.validateToken(token) ?: return ResponseEntity.status(401)
            .body(mapOf("error" to "Token inválido"))

        // 2. Validar archivo
        val allowedExtensions = listOf("pdf", "docx", "xlsx")
        val extension = file.originalFilename?.substringAfterLast(".")?.lowercase()

        if (extension !in allowedExtensions) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Formato no permitido"))
        }
        if (file.size > 20 * 1024 * 1024) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Archivo demasiado grande"))
        }

        // 3. Guardar físicamente en /uploads
        val uploadDir = File("uploads")
        if (!uploadDir.exists()) uploadDir.mkdirs()
        val filePath = "uploads/${file.originalFilename}" // 👈 solo ruta relativa
        file.transferTo(File(filePath))

        // 4. Guardar metadatos en la BD
        val document = Document(
            filename = file.originalFilename!!,
            fileType = file.contentType ?: "desconocido",
            filePath = filePath,
            size = file.size
        )
        documentRepository.save(document)

        println("Usuario $username subió ${file.originalFilename} (${file.size} bytes)")

        return ResponseEntity.ok(mapOf("mensaje" to "Archivo subido exitosamente"))
    }
}
