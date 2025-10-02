# 🏗️ Configuración Multi-Ambiente para DocuFlow Backend

## 🌍 Ambientes Soportados

Tu backend está diseñado para funcionar en **dos ambientes**:

1. **🏠 Desarrollo Local** (tu máquina)
2. **☁️ Producción Render** (tu servidor en la nube)

---

## 📋 Variables de Entorno por Ambiente

### **☁️ PRODUCCIÓN (Render)**

Configura estas variables en el Dashboard de Render:

```bash
# Base de Datos PostgreSQL (Render PostgreSQL)
DATABASE_URL=postgresql://usuario:password@dpg-xxxxx.oregon-postgres.render.com/docuflow

# Google Cloud Storage
GCP_BUCKET_NAME=docuflow-prod-bucket
GCP_KEY_JSON={"type":"service_account","project_id":"tu-proyecto","private_key_id":"...","private_key":"-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n","client_email":"service-account@tu-proyecto.iam.gserviceaccount.com","client_id":"...","auth_uri":"https://accounts.google.com/o/oauth2/auth","token_uri":"https://oauth2.googleapis.com/token","auth_provider_x509_cert_url":"https://www.googleapis.com/oauth2/v1/certs","client_x509_cert_url":"..."}

# JWT
JWT_SECRET=tu-secreto-super-seguro-produccion-2024

# Spring Profile (opcional)
SPRING_PROFILES_ACTIVE=production
```

---

### **🏠 DESARROLLO LOCAL**

#### **Opción 1: Con GCS (Recomendado para pruebas completas)**

Crea un archivo `application-local.properties` en `src/main/resources/`:

```properties
# Base de Datos PostgreSQL Local
spring.datasource.url=jdbc:postgresql://localhost:5432/docuflow_local
spring.datasource.username=postgres
spring.datasource.password=tu_password_local
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Puerto
server.port=8080

# CORS (permite localhost)
cors.allowed-origins=http://localhost:5500,http://127.0.0.1:5500,http://localhost:3000
```

Y configura variables de entorno locales (en IntelliJ Run Configuration o archivo `.env`):

```bash
# Google Cloud Storage (mismo que producción o bucket de desarrollo)
GCP_BUCKET_NAME=docuflow-dev-bucket
GCP_KEY_JSON={"type":"service_account",...}  # Mismas credenciales o diferentes

# JWT (puede ser diferente)
JWT_SECRET=secreto-desarrollo-local
```

---

#### **Opción 2: Sin GCS (Solo para probar otras funcionalidades)**

Si quieres probar tu backend **sin** subir archivos a GCS, puedo modificar el código para que:

1. Detecte si `GCP_KEY_JSON` está configurado
2. Si NO está, guarde los archivos localmente en carpeta `uploads/`
3. Si SÍ está, use GCS normalmente

**¿Quieres que implemente esta funcionalidad?**

Sería algo así:

```kotlin
// En FilesController.kt - Método uploadFile
val gcsPath = if (System.getenv("GCP_KEY_JSON") != null) {
    // MODO PRODUCCIÓN: Usar Google Cloud Storage
    GcsUtil.uploadFile(file, bucketName, credentialsJson)
} else {
    // MODO DESARROLLO: Guardar localmente
    val uploadDir = File("uploads")
    if (!uploadDir.exists()) uploadDir.mkdirs()
    
    val savedFile = File(uploadDir, file.originalFilename ?: "file")
    file.transferTo(savedFile)
    
    "file://uploads/${file.originalFilename}"  // Path local
}
```

---

## 🔄 URLs por Ambiente

### **Frontend (JavaScript)**

```javascript
// Detectar ambiente automáticamente
const API_URL = window.location.hostname === 'localhost' 
    ? 'http://localhost:8080'  // Desarrollo local
    : 'https://tu-backend.onrender.com';  // Producción Render

// O usar variable de entorno en tu bundler:
const API_URL = import.meta.env.VITE_API_URL;  // Vite
const API_URL = process.env.REACT_APP_API_URL;  // Create React App
```

### **Backend (CORS ya configurado)**

Tu `SecurityConfig.kt` ya permite múltiples orígenes:
```kotlin
configuration.allowedOriginPatterns = listOf(
    "http://127.0.0.1:5500",      // ✅ Local
    "http://localhost:5500",       // ✅ Local
    "http://localhost:3000",       // ✅ Local React/Vite
    "https://renatojmv.github.io", // ✅ GitHub Pages
    "https://docuflow-frontend.onrender.com",  // ✅ Render
    // ... otros
)
```

---

## 🎯 Respuestas a tus Preguntas

### **1. ¿Por qué mencionas `localhost` en los ejemplos?**
- Son ejemplos **didácticos** para la documentación
- Tu backend funciona igual con cualquier URL
- El frontend decide qué URL usar según el ambiente

### **2. ¿El código funciona en Render?**
- **SÍ**, el código está listo para Render
- Solo necesitas configurar las variables de entorno en Render
- No hay código específico de `localhost`

### **3. ¿Puedo probar localmente sin GCS?**
- **SÍ**, hay 2 opciones:
  - **A)** Usar las mismas credenciales GCS en local (recomendado)
  - **B)** Modificar el código para guardar localmente en desarrollo (puedo implementarlo)

### **4. ¿Necesitas mi lista de variables de entorno?**
- **Opcional pero útil** para verificar que todo esté correcto
- Si compartes las variables (sin valores sensibles), puedo:
  - Verificar que el código las use correctamente
  - Sugerirte mejoras
  - Detectar variables faltantes

---

## 🛠️ Checklist de Configuración

### **Para Render (Producción):**
- [ ] Variables de entorno configuradas en Render Dashboard
- [ ] Base de datos PostgreSQL de Render conectada
- [ ] GCS configurado con credenciales de producción
- [ ] JWT_SECRET configurado
- [ ] Frontend apunta a `https://tu-backend.onrender.com`

### **Para Local (Desarrollo):**
- [ ] PostgreSQL local corriendo (`localhost:5432`)
- [ ] Base de datos local creada (`docuflow_local`)
- [ ] Variables de entorno configuradas (IntelliJ o `.env`)
- [ ] GCS configurado (opcional si quieres probar subida)
- [ ] Frontend apunta a `http://localhost:8080`

---

## 📝 Ejemplo de Variables para Compartir (Sin Valores Sensibles)

Si quieres que revise tu configuración, comparte algo así:

```bash
# ✅ PUEDES COMPARTIR (nombres y estructura)
DATABASE_URL=postgresql://[USUARIO]:[PASSWORD]@[HOST]/[DB_NAME]
GCP_BUCKET_NAME=[NOMBRE_BUCKET]
GCP_KEY_JSON=[INDICA SI LO TIENES CONFIGURADO: SÍ/NO]
JWT_SECRET=[INDICA SI LO TIENES CONFIGURADO: SÍ/NO]

# ❌ NO COMPARTAS (valores reales)
# Nunca compartas passwords, keys, secrets reales
```

---

## 🚀 Próximos Pasos

**¿Qué necesitas?**

1. **¿Implementar modo desarrollo sin GCS?** → Dime y modifico el código
2. **¿Revisar tus variables de entorno?** → Comparte la estructura (sin valores sensibles)
3. **¿Ayuda para configurar Render?** → Te guío paso a paso
4. **¿Está todo listo?** → ¡Entonces a probar! 🎉

---

**💡 Conclusión:** Tu backend ya funciona en ambos ambientes. Los ejemplos con `localhost` son solo para la documentación, no limitan tu código.
