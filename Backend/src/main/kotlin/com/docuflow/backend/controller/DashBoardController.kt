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
import java.time.LocalDate

@RestController
@RequestMapping("/dashboard")
class DashboardController(
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository,
    private val logEntryRepository: LogEntryRepository,
    private val documentRepository: DocumentRepository // <--- nuevo
) {

    @GetMapping("/users")
    fun getAllUsers(): List<User> = userRepository.findAll()

    @GetMapping("/comments")
    fun getAllComments(): List<Comment> = commentRepository.findAll()

    @GetMapping("/logs")
    fun getAllLogs(): List<LogEntry> = logEntryRepository.findAll()

    @GetMapping("/downloads/today")
    fun getDownloadsToday(): Map<String, Int> {
        val today = LocalDate.now()
        val count = logEntryRepository.findAll()
            .count { it.action == "download" && it.timestamp.toLocalDate() == today }
        return mapOf("count" to count)
    }

    @GetMapping("/files")
    fun getAllFiles(): List<Document> = documentRepository.findAll()
}