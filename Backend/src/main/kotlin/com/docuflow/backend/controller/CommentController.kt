package com.docuflow.backend.controller

import com.docuflow.backend.dto.CommentRequestDTO
import com.docuflow.backend.dto.CommentResponseDTO
import com.docuflow.backend.model.Comment
import com.docuflow.backend.model.LogEntry
import com.docuflow.backend.repository.CommentRepository
import com.docuflow.backend.repository.LogEntryRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/comments")
class CommentController(
    private val commentRepository: CommentRepository,
    private val logEntryRepository: LogEntryRepository
) {

    @GetMapping("/count")
    fun getTotalComments(): ResponseEntity<Long> =
        ResponseEntity.ok(commentRepository.count())

    // Utilidad para obtener el usuario autenticado desde JWT
    private fun getCurrentUsername(): String =
        SecurityContextHolder.getContext().authentication?.principal?.toString() ?: "unknown"

    @PostMapping
    fun createComment(@RequestBody request: CommentRequestDTO): ResponseEntity<CommentResponseDTO> {
        val author = getCurrentUsername()
        val comment = Comment(
            content = request.content,
            author = author,
            documentId = request.documentId,
            isTask = request.isTask,
            assignees = request.assignees,
            createdAt = LocalDateTime.now()
        )
        val saved = commentRepository.save(comment)
        logEntryRepository.save(LogEntry(
            action = "comment_create",
            username = author,
            documentId = saved.documentId,
            timestamp = LocalDateTime.now()
        ))
        return ResponseEntity.ok(saved.toResponseDTO())
    }

    @GetMapping("/document/{documentId}")
    fun getCommentsByDocument(@PathVariable documentId: Long): List<CommentResponseDTO> =
        commentRepository.findByDocumentId(documentId).map { it.toResponseDTO() }

    @PutMapping("/{id}/assign")
    fun assignUsers(
        @PathVariable id: Long,
        @RequestBody assignees: Set<String>
    ): ResponseEntity<CommentResponseDTO> {
        val editor = getCurrentUsername()
        val comment = commentRepository.findById(id).orElseThrow()
        val updated = comment.copy(
            assignees = assignees,
            updatedAt = LocalDateTime.now(),
            lastEditedBy = editor
        )
        val saved = commentRepository.save(updated)
        logEntryRepository.save(LogEntry(
            action = "comment_assign",
            username = editor,
            documentId = saved.documentId,
            timestamp = LocalDateTime.now()
        ))
        return ResponseEntity.ok(saved.toResponseDTO())
    }

    @PutMapping("/{id}")
    fun editComment(
        @PathVariable id: Long,
        @RequestBody request: CommentRequestDTO
    ): ResponseEntity<CommentResponseDTO> {
        val editor = getCurrentUsername()
        val comment = commentRepository.findById(id).orElseThrow()
        val updated = comment.copy(
            content = request.content,
            isTask = request.isTask,
            assignees = request.assignees,
            updatedAt = LocalDateTime.now(),
            lastEditedBy = editor
        )
        val saved = commentRepository.save(updated)
        logEntryRepository.save(LogEntry(
            action = "comment_edit",
            username = editor,
            documentId = saved.documentId,
            timestamp = LocalDateTime.now()
        ))
        return ResponseEntity.ok(saved.toResponseDTO())
    }

    @DeleteMapping("/{id}")
    fun deleteComment(@PathVariable id: Long): ResponseEntity<Void> {
        val deleter = getCurrentUsername()
        val comment = commentRepository.findById(id).orElseThrow()
        commentRepository.deleteById(id)
        logEntryRepository.save(LogEntry(
            action = "comment_delete",
            username = deleter,
            documentId = comment.documentId,
            timestamp = LocalDateTime.now()
        ))
        return ResponseEntity.noContent().build()
    }

    // Utilidad para mapear a DTO
    private fun Comment.toResponseDTO() = CommentResponseDTO(
        id = id!!,
        content = content,
        author = author,
        isTask = isTask,
        assignees = assignees,
        documentId = documentId,
        createdAt = createdAt,
        updatedAt = updatedAt,
        lastEditedBy = lastEditedBy
    )
}