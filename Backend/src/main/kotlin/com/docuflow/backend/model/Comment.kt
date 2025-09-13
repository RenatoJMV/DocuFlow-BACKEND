package com.docuflow.backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "comments")
data class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val content: String,

    @Column(nullable = false)
    val author: String, // username

    @Column(nullable = false)
    val documentId: Long,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)