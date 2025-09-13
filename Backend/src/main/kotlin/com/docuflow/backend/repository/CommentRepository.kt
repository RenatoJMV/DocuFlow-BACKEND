package com.docuflow.backend.repository

import com.docuflow.backend.model.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : JpaRepository<Comment, Long> {
    fun findByDocumentId(documentId: Long): List<Comment>
}