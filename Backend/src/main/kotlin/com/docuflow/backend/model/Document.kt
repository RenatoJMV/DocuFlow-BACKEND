package com.docuflow.backend.model

import jakarta.persistence.*

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
    val size: Long // 🆕 Tamaño del archivo en bytes
)   
