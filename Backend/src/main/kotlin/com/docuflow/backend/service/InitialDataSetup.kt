package com.docuflow.backend.service

import com.docuflow.backend.model.User
import com.docuflow.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class InitialDataSetup : CommandLineRunner {

    @Autowired
    private lateinit var userRepository: UserRepository

    override fun run(vararg args: String?) {
        createInitialAdminUser()
    }

    private fun createInitialAdminUser() {
        try {
            // Variables de entorno para el usuario admin inicial
            val adminUsername = System.getenv("ADMIN_USERNAME") ?: "admin"
            val adminPassword = System.getenv("ADMIN_PASSWORD") ?: "DocuFlow2025!"
            val adminFullName = System.getenv("ADMIN_FULLNAME") ?: "Administrador del Sistema"

            // Verificar si ya existe un usuario admin
            val existingAdmin = userRepository.findByUsername(adminUsername)
            
            if (existingAdmin == null) {
                // Crear usuario admin inicial
                val adminUser = User(
                    username = adminUsername,
                    password = adminPassword, // En producción esto debería estar hasheado
                    role = "admin",
                    fullName = adminFullName,
                    permissions = setOf(
                        "download", "delete", "comment", "edit", "share", 
                        "admin", "view_logs", "manage_users", "export_data",
                        "manage_permissions", "manage_notifications"
                    )
                )
                
                userRepository.save(adminUser)
                println("✅ Usuario admin inicial creado: $adminUsername")
                println("🔑 Password: $adminPassword")
                println("� Nombre: $adminFullName")
                println("⚠️  IMPORTANTE: Cambia la contraseña después del primer login")
            } else {
                println("ℹ️  Usuario admin ya existe: $adminUsername")
            }
            
        } catch (e: Exception) {
            println("❌ Error al crear usuario admin inicial: ${e.message}")
        }
    }
}