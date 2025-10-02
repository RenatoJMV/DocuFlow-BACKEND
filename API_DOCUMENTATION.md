# DocuFlow Backend - API Documentation

## Información General
- **Base URL**: `http://localhost:8080` (desarrollo local)
- **Autenticación**: JWT Bearer Token
- **Content-Type**: `application/json`
- **Framework**: Spring Boot 3.5.5 con Kotlin

## Autenticación

### Headers Requeridos (para endpoints protegidos)
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

### Obtener Token JWT
1. Registrarse o hacer login
2. Usar el token JWT en el header `Authorization: Bearer {token}`
3. El token se debe renovar usando el refresh token antes de que expire

---

## 🔐 ENDPOINTS DE AUTENTICACIÓN

### 1. Registro de Usuario
**POST** `/auth/register`

**Request Body:**
```json
{
    "username": "string",
    "email": "string",
    "password": "string",
    "firstName": "string",
    "lastName": "string"
}
```

**Response (200):**
```json
{
    "message": "Usuario registrado exitosamente",
    "userId": "long"
}
```

### 2. Login
**POST** `/auth/login`

**Request Body:**
```json
{
    "username": "string",
    "password": "string"
}
```

**Response (200):**
```json
{
    "accessToken": "string",
    "refreshToken": "string",
    "tokenType": "Bearer",
    "expiresIn": 3600
}
```

### 3. Refrescar Token
**POST** `/auth/refresh`

**Request Body:**
```json
{
    "refreshToken": "string"
}
```

**Response (200):**
```json
{
    "accessToken": "string",
    "refreshToken": "string",
    "tokenType": "Bearer",
    "expiresIn": 3600
}
```

### 4. Logout
**POST** `/auth/logout`
- **Autenticación**: Requerida
- **Response**: `200 OK`

---

## 📁 ENDPOINTS DE GESTIÓN DE ARCHIVOS

### 1. Listar Todos los Archivos
**GET** `/files`
- **Autenticación**: Requerida
- **Response**: Array de objetos Document

### 2. Obtener Archivo por ID
**GET** `/files/{id}`
- **Autenticación**: Requerida
- **Parámetros**: `id` (Long)
- **Response**: Objeto Document

### 3. Subir Archivo
**POST** `/files/upload`
- **Autenticación**: Requerida
- **Content-Type**: `multipart/form-data`
- **Form Data**: `file` (MultipartFile)
- **Response**: Objeto Document creado

### 4. Descargar Archivo
**GET** `/files/{id}/download`
- **Autenticación**: Requerida
- **Parámetros**: `id` (Long)
- **Response**: Archivo binario

### 5. Actualizar Archivo
**PUT** `/files/{id}`
- **Autenticación**: Requerida
- **Parámetros**: `id` (Long)
- **Request Body**: Objeto Document
- **Response**: Objeto Document actualizado

### 6. Eliminar Archivo
**DELETE** `/files/{id}`
- **Autenticación**: Requerida
- **Parámetros**: `id` (Long)
- **Response**: `204 No Content`

### 7. Buscar Archivos
**GET** `/files/search`
- **Autenticación**: Requerida
- **Query Params**: `query` (String)
- **Response**: Array de objetos Document

### 8. Obtener Archivos por Usuario
**GET** `/files/user/{userId}`
- **Autenticación**: Requerida
- **Parámetros**: `userId` (Long)
- **Response**: Array de objetos Document

### 9. Obtener Archivos Recientes
**GET** `/files/recent`
- **Autenticación**: Requerida
- **Query Params**: `limit` (Int, opcional)
- **Response**: Array de objetos Document

### 10. Compartir Archivo
**POST** `/files/{id}/share`
- **Autenticación**: Requerida
- **Parámetros**: `id` (Long)
- **Request Body**: 
```json
{
    "userIds": ["long"],
    "permissions": "string"
}
```

### 11. Obtener Metadatos
**GET** `/files/{id}/metadata`
- **Autenticación**: Requerida
- **Parámetros**: `id` (Long)
- **Response**: Metadatos del archivo

### 12. Actualizar Metadatos
**PUT** `/files/{id}/metadata`
- **Autenticación**: Requerida
- **Parámetros**: `id` (Long)
- **Request Body**: Objeto con metadatos

### 13. Obtener Versiones
**GET** `/files/{id}/versions`
- **Autenticación**: Requerida
- **Parámetros**: `id` (Long)
- **Response**: Array de versiones del archivo

---

## 💬 ENDPOINTS DE COMENTARIOS

### 1. Crear Comentario
**POST** `/comments`
- **Autenticación**: Requerida
- **Request Body**:
```json
{
    "documentId": "long",
    "content": "string"
}
```

### 2. Obtener Comentarios por Documento
**GET** `/comments/document/{documentId}`
- **Autenticación**: Requerida
- **Parámetros**: `documentId` (Long)
- **Response**: Array de comentarios

### 3. Actualizar Comentario
**PUT** `/comments/{id}`
- **Autenticación**: Requerida
- **Parámetros**: `id` (Long)
- **Request Body**: Objeto CommentRequestDTO

### 4. Eliminar Comentario
**DELETE** `/comments/{id}`
- **Autenticación**: Requerida
- **Parámetros**: `id` (Long)

---

## 📊 ENDPOINTS DE DASHBOARD

### 1. Obtener Estadísticas Generales
**GET** `/dashboard/stats`
- **Autenticación**: Requerida
- **Response**: Objeto con estadísticas

### 2. Obtener Actividad Reciente
**GET** `/dashboard/activity`
- **Autenticación**: Requerida
- **Response**: Array de actividades recientes

### 3. Obtener Archivos Populares
**GET** `/dashboard/popular-files`
- **Autenticación**: Requerida
- **Response**: Array de archivos más populares

### 4. Obtener Resumen de Usuario
**GET** `/dashboard/user-summary`
- **Autenticación**: Requerida
- **Response**: Resumen de actividad del usuario

---

## 📋 ENDPOINTS DE LOGS

### 1. Obtener Todos los Logs
**GET** `/logs`
- **Autenticación**: Requerida (Admin)
- **Response**: Array de logs

### 2. Obtener Logs por Usuario
**GET** `/logs/user/{userId}`
- **Autenticación**: Requerida
- **Parámetros**: `userId` (Long)
- **Response**: Array de logs del usuario

### 3. Obtener Logs por Tipo
**GET** `/logs/type/{type}`
- **Autenticación**: Requerida
- **Parámetros**: `type` (String)
- **Response**: Array de logs filtrados

### 4. Crear Log
**POST** `/logs`
- **Autenticación**: Requerida
- **Request Body**: Objeto LogEntry

---

## ✅ ENDPOINTS DE SALUD

### 1. Health Check
**GET** `/health`
- **Autenticación**: No requerida
- **Response**: Estado del sistema

### 2. Health Check Detallado
**GET** `/health/detailed`
- **Autenticación**: Requerida (Admin)
- **Response**: Estado detallado de componentes

---

## ☁️ ENDPOINTS DE GOOGLE CLOUD STORAGE

### 1. Subir a GCS
**POST** `/gcs/upload`
- **Autenticación**: Requerida
- **Content-Type**: `multipart/form-data`
- **Form Data**: `file` (MultipartFile)

### 2. Descargar de GCS
**GET** `/gcs/download/{fileName}`
- **Autenticación**: Requerida
- **Parámetros**: `fileName` (String)

### 3. Eliminar de GCS
**DELETE** `/gcs/delete/{fileName}`
- **Autenticación**: Requerida
- **Parámetros**: `fileName` (String)

### 4. Listar Archivos GCS
**GET** `/gcs/list`
- **Autenticación**: Requerida
- **Response**: Lista de archivos en GCS

---

## 👤 ENDPOINTS DE PERFIL DE USUARIO

### 1. Obtener Perfil
**GET** `/profile`
- **Autenticación**: Requerida
- **Response**: Datos del perfil del usuario

### 2. Actualizar Perfil
**PUT** `/profile`
- **Autenticación**: Requerida
- **Request Body**: Datos del perfil a actualizar

### 3. Cambiar Contraseña
**PUT** `/profile/password`
- **Autenticación**: Requerida
- **Request Body**:
```json
{
    "currentPassword": "string",
    "newPassword": "string"
}
```

### 4. Subir Avatar
**POST** `/profile/avatar`
- **Autenticación**: Requerida
- **Content-Type**: `multipart/form-data`
- **Form Data**: `avatar` (MultipartFile)

---

## 📤 ENDPOINTS DE EXPORTACIÓN

### 1. Exportar Archivos
**GET** `/export/files`
- **Autenticación**: Requerida
- **Query Params**: formato, filtros
- **Response**: Archivo exportado

### 2. Exportar Logs
**GET** `/export/logs`
- **Autenticación**: Requerida (Admin)
- **Query Params**: formato, fechas
- **Response**: Logs exportados

### 3. Exportar Estadísticas
**GET** `/export/stats`
- **Autenticación**: Requerida
- **Response**: Estadísticas exportadas

---

## 🔒 ENDPOINTS DE PERMISOS

### 1. Obtener Permisos de Archivo
**GET** `/permissions/file/{fileId}`
- **Autenticación**: Requerida
- **Parámetros**: `fileId` (Long)

### 2. Asignar Permisos
**POST** `/permissions/assign`
- **Autenticación**: Requerida
- **Request Body**: Objeto con permisos

### 3. Revocar Permisos
**DELETE** `/permissions/revoke`
- **Autenticación**: Requerida
- **Request Body**: Datos de permisos a revocar

### 4. Obtener Permisos de Usuario
**GET** `/permissions/user/{userId}`
- **Autenticación**: Requerida
- **Parámetros**: `userId` (Long)

---

## 🔔 ENDPOINTS DE NOTIFICACIONES

### 1. Obtener Notificaciones
**GET** `/notifications`
- **Autenticación**: Requerida
- **Response**: Array de notificaciones del usuario

### 2. Marcar como Leída
**PUT** `/notifications/{id}/read`
- **Autenticación**: Requerida
- **Parámetros**: `id` (Long)

### 3. Crear Notificación
**POST** `/notifications`
- **Autenticación**: Requerida
- **Request Body**: Objeto Notification

### 4. Eliminar Notificación
**DELETE** `/notifications/{id}`
- **Autenticación**: Requerida
- **Parámetros**: `id` (Long)

---

## 👥 ENDPOINTS DE GESTIÓN DE USUARIOS (ADMIN)

### 1. Listar Usuarios
**GET** `/admin/users`
- **Autenticación**: Requerida (Admin)
- **Response**: Array de usuarios

### 2. Obtener Usuario por ID
**GET** `/admin/users/{id}`
- **Autenticación**: Requerida (Admin)
- **Parámetros**: `id` (Long)

### 3. Crear Usuario
**POST** `/admin/users`
- **Autenticación**: Requerida (Admin)
- **Request Body**: Datos del usuario

### 4. Actualizar Usuario
**PUT** `/admin/users/{id}`
- **Autenticación**: Requerida (Admin)
- **Parámetros**: `id` (Long)
- **Request Body**: Datos a actualizar

### 5. Eliminar Usuario
**DELETE** `/admin/users/{id}`
- **Autenticación**: Requerida (Admin)
- **Parámetros**: `id` (Long)

### 6. Activar/Desactivar Usuario
**PUT** `/admin/users/{id}/status`
- **Autenticación**: Requerida (Admin)
- **Parámetros**: `id` (Long)
- **Request Body**: `{"active": boolean}`

---

## 📎 ENDPOINTS DE SUBIDA LEGACY

### 1. Subir Archivo Legacy
**POST** `/upload`
- **Autenticación**: Requerida
- **Content-Type**: `multipart/form-data`
- **Form Data**: `file` (MultipartFile)

### 2. Subir Múltiples Archivos
**POST** `/upload/multiple`
- **Autenticación**: Requerida
- **Content-Type**: `multipart/form-data`
- **Form Data**: `files` (Array de MultipartFile)

---

## 📝 MODELOS DE DATOS

### User
```json
{
    "id": "long",
    "username": "string",
    "email": "string",
    "firstName": "string",
    "lastName": "string",
    "role": "string",
    "active": "boolean",
    "createdAt": "datetime",
    "updatedAt": "datetime"
}
```

### Document
```json
{
    "id": "long",
    "fileName": "string",
    "filePath": "string",
    "fileSize": "long",
    "mimeType": "string",
    "ownerId": "long",
    "createdAt": "datetime",
    "updatedAt": "datetime",
    "description": "string"
}
```

### Comment
```json
{
    "id": "long",
    "content": "string",
    "documentId": "long",
    "userId": "long",
    "createdAt": "datetime",
    "updatedAt": "datetime"
}
```

### Notification
```json
{
    "id": "long",
    "userId": "long",
    "title": "string",
    "message": "string",
    "read": "boolean",
    "createdAt": "datetime"
}
```

---

## 🚨 CÓDIGOS DE ERROR COMUNES

- **400 Bad Request**: Datos de entrada inválidos
- **401 Unauthorized**: Token JWT inválido o expirado
- **403 Forbidden**: Sin permisos para acceder al recurso
- **404 Not Found**: Recurso no encontrado
- **409 Conflict**: Conflicto en los datos (ej: usuario ya existe)
- **500 Internal Server Error**: Error interno del servidor

---

## 📌 NOTAS IMPORTANTES

1. **Todos los endpoints (excepto `/health`) requieren autenticación JWT**
2. **Los endpoints de admin requieren rol de administrador**
3. **Los archivos se almacenan en Google Cloud Storage**
4. **Las contraseñas se encriptan con BCrypt**
5. **Los tokens JWT expiran en 1 hora**
6. **Los refresh tokens tienen mayor duración**
7. **El sistema registra todas las acciones en logs**
8. **Usar HTTPS en producción**

---

## 🔧 CONFIGURACIÓN DE DESARROLLO

### Variables de Entorno Necesarias:
```
SPRING_PROFILES_ACTIVE=dev
DATABASE_URL=jdbc:postgresql://localhost:5432/docuflow
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_key
GOOGLE_CLOUD_PROJECT_ID=your_project_id
GOOGLE_CLOUD_BUCKET_NAME=your_bucket_name
```

### Comando para ejecutar:
```bash
./mvnw spring-boot:run
```

---

**Fecha de generación**: 2 de octubre de 2025
**Versión del Backend**: Spring Boot 3.5.5
**Total de Endpoints**: 51 endpoints funcionales