package com.docuflow.backend.model

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "documents")
data class Document(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val filename: String,

    @Column(nullable = false)
    val fileType: String,

    @Column(nullable = false)
    val filePath: String,

    @Column(nullable = false)
    val size: Long,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val uploadDate: LocalDateTime? = null
)   
