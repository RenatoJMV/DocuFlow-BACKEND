# 📋 CHANGELOG - Refactorización del Backend DocuFlow

**Fecha:** 1 de octubre de 2025  
**Autor:** GitHub Copilot  
**Objetivo:** Centralizar gestión de archivos con Google Cloud Storage y JWT

---

## ✅ Cambios Implementados

### 1. **Nuevo Controlador Unificado: `FilesController.kt`**
   - ✨ **Ruta base:** `/files` (anteriormente `/documents` y `/upload`)
   - 🔐 **Autenticación JWT en todos los endpoints**
   - 📦 **Integración completa con Google Cloud Storage (GCS)**

### 2. **Endpoints Implementados**

#### 📂 **GET /files**
- Lista todos los archivos
- Requiere token JWT en header `Authorization`
- Respuesta: `{"success": true, "files": [...]}`

#### 📄 **GET /files/{id}**
- Obtiene metadatos de un archivo por ID
- Requiere token JWT
- Respuesta: `{"success": true, "file": {...}}`

#### ⬆️ **POST /files** _(NUEVO)_
- Sube archivos a Google Cloud Storage
- Validaciones:
  - Archivo no vacío
  - Tamaño máximo: 20MB
  - Requiere variables de entorno: `GCP_BUCKET_NAME`, `GCP_KEY_JSON`
- Guarda metadatos en BD
- Registra log de acción "upload" con username del JWT
- Respuesta: `{"success": true, "mensaje": "Archivo subido exitosamente", "fileId": 1, "filename": "..."}`

#### ⬇️ **GET /files/{id}/download**
- Descarga archivos desde Google Cloud Storage
- Extrae bucket y nombre del archivo desde `filePath` (formato: `gs://bucket/file`)
- Registra log de acción "download" con username del JWT
- Devuelve el archivo como `attachment`

#### 🗑️ **DELETE /files/{id}** _(MEJORADO)_
- **Elimina archivo de GCS primero** ⬅️ **CRÍTICO** (antes solo borraba de BD)
- Luego elimina registro de la base de datos
- Registra log de acción "delete" con username del JWT
- Respuesta: `{"success": true, "mensaje": "Archivo eliminado correctamente"}`

---

## 🗑️ Archivos Eliminados

### ❌ **DocumentController.kt**
- **Razón:** Reemplazado por `FilesController.kt`
- **Problemas del archivo anterior:**
  - Username hardcodeado como `"estudiante"`
  - Sin autenticación JWT en todos los métodos
  - Ruta antigua `/documents`

### ❌ **UploadController.kt**
- **Razón:** Funcionalidad integrada en `FilesController.kt`
- **Problemas del archivo anterior:**
  - Lógica de subida incompleta
  - Validación de extensiones demasiado restrictiva
  - Redundante con el nuevo controlador unificado

---

## 🔐 Seguridad Mejorada

### **Validación JWT en Todos los Endpoints**
```kotlin
// Patrón aplicado en todos los métodos:
val token = authHeader?.removePrefix("Bearer ") 
    ?: return ResponseEntity.status(401).body(mapOf("error" to "Token faltante"))

val username = JwtUtil.validateToken(token) 
    ?: return ResponseEntity.status(401).body(mapOf("error" to "Token inválido"))
```

### **SecurityConfig.kt Actualizado**
- Protección explícita de rutas `/files/**`
- Compatibilidad mantenida con `/documents/**` (opcional)
- CORS configurado correctamente

---

## 📊 Comparativa: Antes vs Ahora

| Aspecto | **Antes** | **Ahora** |
|---------|-----------|-----------|
| **Controladores** | 2 archivos separados | 1 archivo unificado |
| **Ruta Base** | `/documents` + `/upload` | `/files` |
| **Autenticación** | Parcial, username hardcodeado | JWT completo en todos los endpoints |
| **Subida de Archivos** | Incompleta en `UploadController` | Completamente funcional |
| **Eliminación** | Solo BD (archivos huérfanos en GCS) | **GCS + BD** (limpieza completa) |
| **Logs** | Username "estudiante" hardcodeado | Username real del token JWT |
| **Respuestas JSON** | Inconsistentes | Formato estándar: `{"success": ...}` o `{"error": ...}` |
| **Almacenamiento** | Mix local/GCS | **100% Google Cloud Storage** |

---

## 🚀 Beneficios de la Refactorización

### 1. **Centralización**
   - Un solo punto de entrada para operaciones de archivos
   - Más fácil de mantener y depurar
   - Código más limpio y organizado

### 2. **Seguridad**
   - Autenticación JWT obligatoria en todos los endpoints
   - Trazabilidad completa de acciones por usuario
   - Logs con username real del token

### 3. **Integridad de Datos**
   - Eliminación completa de archivos (GCS + BD)
   - Sin archivos huérfanos en la nube
   - Gestión de errores robusta

### 4. **Escalabilidad**
   - Almacenamiento 100% en la nube (GCS)
   - No depende de almacenamiento local del servidor
   - Fácil integración con frontend

---

## 🛠️ Configuración Requerida

### Variables de Entorno Necesarias:
```bash
GCP_BUCKET_NAME=tu-bucket-name          # Nombre del bucket de GCS
GCP_KEY_JSON={"type":"service_account"...}  # Credenciales JSON de GCP
JWT_SECRET=tu-secreto-jwt               # Secret para generar/validar tokens
```

---

## 📝 Notas Importantes

1. **Todos los endpoints requieren header `Authorization: Bearer <token>`**
2. **El tamaño máximo de archivo es 20MB** (configurable en el código)
3. **Los archivos se almacenan con su nombre original** en GCS
4. **Formato de `filePath` en BD:** `gs://bucket-name/nombre-archivo.ext`
5. **Los logs registran la acción, username, documentId y timestamp**

---

## 🔄 Migración desde el Código Antiguo

### Frontend que usaba `/documents`:
```javascript
// ANTES:
fetch('/documents', { headers: { Authorization: token } })

// AHORA (recomendado):
fetch('/files', { headers: { Authorization: `Bearer ${token}` } })

// O mantener compatibilidad (si SecurityConfig permite):
fetch('/documents', { headers: { Authorization: `Bearer ${token}` } })
```

### Subida de archivos:
```javascript
// ANTES (en /upload):
const formData = new FormData();
formData.append('file', file);
fetch('/upload', {
  method: 'POST',
  headers: { Authorization: `Bearer ${token}` },
  body: formData
});

// AHORA (en /files):
const formData = new FormData();
formData.append('file', file);
fetch('/files', {
  method: 'POST',
  headers: { Authorization: `Bearer ${token}` },
  body: formData
});
```

---

## ✅ Estado Final

- ✅ `FilesController.kt` creado con todas las funcionalidades
- ✅ `DocumentController.kt` eliminado
- ✅ `UploadController.kt` eliminado
- ✅ `SecurityConfig.kt` actualizado
- ✅ Sin errores de compilación
- ✅ Integración GCS completa
- ✅ Autenticación JWT en todos los endpoints
- ✅ Logs con usuario real del token

---

**🎉 Refactorización completada exitosamente!**
