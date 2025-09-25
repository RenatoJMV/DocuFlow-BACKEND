package com.docuflow.backend.service

import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream

object GcsUtil {
    fun uploadFile(file: MultipartFile, bucketName: String, credentialsJson: String): String {
        val storage: Storage = StorageOptions.newBuilder()
            .setCredentials(ServiceAccountCredentials.fromStream(ByteArrayInputStream(credentialsJson.toByteArray())))
            .build()
            .service

        val blobInfo = BlobInfo.newBuilder(bucketName, file.originalFilename!!).build()
        storage.create(blobInfo, file.bytes)
        return "gs://$bucketName/${file.originalFilename}"
    }
}