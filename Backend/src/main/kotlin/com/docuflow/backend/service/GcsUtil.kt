package com.docuflow.backend.service

import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import com.google.cloud.storage.Blob
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream

@Service
object GcsUtil {
    
    private fun getStorageService(): Storage {
        val credentialsJson = System.getenv("GCP_KEY_JSON") ?: ""
        return StorageOptions.newBuilder()
            .setCredentials(ServiceAccountCredentials.fromStream(ByteArrayInputStream(credentialsJson.toByteArray())))
            .build()
            .service
    }
    
    fun uploadFile(file: MultipartFile, bucketName: String): String {
        val storage = getStorageService()
        val blobInfo = BlobInfo.newBuilder(bucketName, file.originalFilename!!).build()
        storage.create(blobInfo, file.bytes)
        return "gs://$bucketName/${file.originalFilename}"
    }
    
    fun listAllFilesInBucket(bucketName: String): List<Blob> {
        return try {
            val storage = getStorageService()
            val blobs = storage.list(bucketName)
            blobs.iterateAll().toList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun getFileInfo(bucketName: String, fileName: String): Blob? {
        return try {
            val storage = getStorageService()
            storage.get(bucketName, fileName)
        } catch (e: Exception) {
            null
        }
    }
    
    fun deleteFile(bucketName: String, fileName: String): Boolean {
        return try {
            val storage = getStorageService()
            storage.delete(bucketName, fileName)
        } catch (e: Exception) {
            false
        }
    }
}