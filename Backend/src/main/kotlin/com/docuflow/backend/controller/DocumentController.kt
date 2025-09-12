package com.docuflow.backend.controller

import com.docuflow.backend.model.Document
import com.docuflow.backend.repository.DocumentRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.File

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

    // 🟢 Descargar un archivo
    @GetMapping("/{id}/download")
    fun downloadFile(@PathVariable id: Long): ResponseEntity<Any> {
        val document = documentRepository.findById(id)
        if (document.isEmpty) {
            return ResponseEntity.status(404).body(mapOf("error" to "Archivo no encontrado"))
        }

        val file = File(document.get().filePath)
        if (!file.exists()) {
            return ResponseEntity.status(404).body(mapOf("error" to "Archivo no encontrado físicamente"))
        }

        val resource = file.inputStream().readBytes()
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=${document.get().filename}")
            .header("Content-Type", document.get().fileType)
            .body(resource)
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
