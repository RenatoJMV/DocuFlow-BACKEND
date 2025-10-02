# Guía de Integración para el Frontend

Este documento resume los endpoints disponibles en el backend de DocuFlow y cómo debe consumirlos la SPA. Todo el backend está desplegado en Render; las rutas se construyen con la variable `BACKEND_URL`.

## 1. Autenticación

Todas las rutas de autenticación están bajo `${BACKEND_URL}/auth`. Utilizan JSON y retornan errores en el formato:

```json
{
  "error": "Mensaje descriptivo",
  "details": { ... opcional ... }
}
```

### 1.1 Registro
- **Método:** `POST`
- **Endpoint:** `/auth/register`
- **Requiere token?:** No
- **Body esperado:**
  ```json
  {
    "email": "usuario@example.com",
    "password": "Contraseña123",
    "name": "Nombre completo"
  }
  ```
- **Validaciones:**
  - Email obligatorio con formato válido (`^[^@\s]+@[^@\s]+\.[^@\s]+$`)
  - Password de mínimo 8 caracteres
  - Nombre de mínimo 2 caracteres
  - 400 si el email ya existe
  - 422 si fallan las reglas anteriores
- **Respuesta exitosa (201):**
  ```json
  {
    "success": true,
    "message": "User registered successfully"
  }
  ```
- **Rol y permisos iniciales:**
  - Rol: `viewer`
  - Permisos: `files.read`, `files.download`, `comments.read`

### 1.2 Login
- **Método:** `POST`
- **Endpoint:** `/auth/login`
- **Requiere token?:** No
- **Body esperado:**
  ```json
  {
    "username": "usuario@example.com",
    "password": "Contraseña123"
  }
  ```
- **Notas:**
  - El backend normaliza el `username` a minúsculas.
  - Si coincide con `APP_USER`/`APP_PASS`, se crea (o reutiliza) un usuario admin con permisos completos.
  - Contraseñas antiguas sin hash se re-hashean automáticamente al primer login exitoso.
- **Respuesta exitosa (200):**
  ```json
  {
    "success": true,
    "token": "<JWT acceso válido 1 hora>",
    "refreshToken": "<token de refresco válido 14 días>",
    "expiresIn": 3600,
    "user": {
      "username": "usuario@example.com",
      "name": "Nombre completo",
      "role": "viewer",
      "permissions": ["files.read", "files.download", "comments.read"]
    },
    "message": "Login exitoso"
  }
  ```
- **Errores:**
  - 400 si faltan campos
  - 401 si credenciales inválidas

### 1.3 Logout
- **Método:** `POST`
- **Endpoint:** `/auth/logout`
- **Requiere token?:** Sí (`Authorization: Bearer <token>`)
- **Comportamiento:**
  - Revoca el access token (lista negra en tabla `revoked_tokens`).
  - Revoca todos los refresh tokens activos del usuario (`refresh_tokens.revoked = true`).
  - Registra un log con acción `logout`.
- **Respuesta exitosa (200):**
  ```json
  {
    "success": true,
    "message": "Logged out successfully"
  }
  ```
- **Errores:** 401 si el token es faltante, inválido o ya revocado.

### 1.4 Refresh
- **Método:** `POST`
- **Endpoint:** `/auth/refresh`
- **Requiere token?:** No (usa refresh token)
- **Body esperado:**
  ```json
  {
    "refreshToken": "<token de refresco vigente>"
  }
  ```
- **Comportamiento:**
  - Valida que el refresh token exista, no esté revocado ni vencido.
  - Revoca el refresh token utilizado (modelo rolling).
  - Genera un nuevo access token (1 hora) y un nuevo refresh token (14 días).
- **Respuesta exitosa (200):**
  ```json
  {
    "success": true,
    "token": "<nuevo JWT>",
    "refreshToken": "<nuevo refresh token>",
    "expiresIn": 3600
  }
  ```
- **Errores:**
  - 400 si no se envía el campo
  - 401 si el refresh token es inválido o expirado (detalle `code = INVALID_REFRESH_TOKEN`)

### 1.5 Buenas prácticas en el frontend
- Guardar `token` y `refreshToken` en `localStorage` (claves sugeridas: `authToken`, `token`, `refreshToken`).
- Al recibir un 401 en llamadas protegidas, intentar `POST /auth/refresh`. Si el refresh falla, limpiar storage y redirigir a login.
- En logout, enviar la solicitud al backend y luego limpiar `localStorage`.

## 2. Endpoints de archivos (compatibilidad SPA)

Todos bajo `${BACKEND_URL}/files`, con `Authorization: Bearer <token>`.

### 2.1 Estadísticas rápidas
1. `GET /files/stats`
   ```json
   {
     "success": true,
     "totalFiles": 32,
     "totalSizeBytes": 48972544,
     "formattedTotalSize": "46.72 MB",
     "averageFileSizeBytes": 1530392,
     "largestFile": {
       "id": 12,
       "filename": "reporte.pdf",
       "size": 9812736,
       "formattedSize": "9.36 MB"
     },
     "fileTypeDistribution": {
       "pdf": 12,
       "doc": 6,
       "spreadsheet": 5,
       "image": 4,
       "other": 5
     }
   }
   ```

2. `GET /files/count`
   ```json
   {
     "success": true,
     "count": 32
   }
   ```

3. `GET /files/total-size`
   ```json
   {
     "success": true,
     "totalSizeBytes": 48972544,
     "formattedTotalSize": "46.72 MB"
   }
   ```

> Nota: El frontend ya consumía `/api/dashboard/files/stats`; estos nuevos endpoints son alias sencillos alineados a la estructura esperada en la SPA.

### 2.2 Flujo CRUD existente (recordatorio)
- `GET /files`
- `GET /files/{id}`
- `POST /files` (multipart `file`)
- `GET /files/{id}/download`
- `DELETE /files/{id}`

Todos requieren el token y mantienen el formato `{ success: true, ... }`.

## 3. Otros endpoints de soporte

### 3.1 Usuarios y roles
Todos bajo `${BACKEND_URL}/users` (con token):
- `GET /users` → Devuelve `name` (fullName) además de `username`.
- `GET /users/{id}` → Incluye `name`.
- `GET /users/roles`
- `PUT /users/{id}/role`
- `GET /users/{id}/permissions`
- `PUT /users/{id}/permissions`

### 3.2 Comentarios
`${BACKEND_URL}/api/comments` + subrutas siguen intactas (create/edit/assign/delete/complete).

### 3.3 Dashboard & Logs
`${BACKEND_URL}/api/dashboard/**` y `${BACKEND_URL}/api/logs/**` mantienen estructura y campos descritos anteriormente.

## 4. Integración sugerida en la SPA

- **Registro:** `POST /auth/register` y mostrar mensaje de éxito.
- **Inicio de sesión:** Guardar `token`, `refreshToken`, `user`.
- **Middleware de autenticación:** Añadir `Authorization` a cada request; ante 401 intentar refresh.
- **Logout:** Llamar a `/auth/logout`, luego limpiar storage.
- **Actualización de dashboards:** Consumir `/files/stats`, `/files/count`, `/files/total-size` para alimentar widgets.
- **Mostrado de usuarios:** Aprovechar el campo `name` para nombre completo.

## 5. Notas operativas

- El backend se ejecuta en Render; no es necesario correr Maven localmente para validar despliegues.
- Hibernate (`spring.jpa.hibernate.ddl-auto=update`) se encarga de crear:
  - Tabla `refresh_tokens`
  - Tabla `revoked_tokens`
  - Nueva columna `full_name` en `users`
- Mantener configuradas las variables de entorno claves en Render:
  - `JWT_SECRET`
  - `APP_USER` / `APP_PASS` (opcional, para el administrador por defecto)
  - Credenciales de PostgreSQL (`DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD`)

## 6. Resumen rápido para compartir

1. **Registro:** `POST /auth/register` (valida email/password/name, responde 201 con mensaje)
2. **Login:** `POST /auth/login` → `{ token, refreshToken, expiresIn, user }`
3. **Logout:** `POST /auth/logout` (Bearer token) → revoca access+refresh
4. **Refresh:** `POST /auth/refresh` con `refreshToken` → nuevos tokens
5. **Estadísticas archivos:** `GET /files/stats`, `/files/count`, `/files/total-size`
6. **Campos extra:** usuario ahora tiene `name` basado en `fullName`

Comparte este archivo directamente con la persona/IA a cargo del frontend para alinear la integración.
