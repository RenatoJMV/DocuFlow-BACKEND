# DocuFlow Backend · Documentación de API

## Información general
- **Base URL (desarrollo)**: `http://localhost:8080`
- **Autenticación**: JWT (header `Authorization: Bearer <token>`)
- **Formato de datos**: JSON salvo donde se indique `multipart/form-data`
- **Versionado**: Spring Boot 3.5.5 · Kotlin 1.9

### Variables de entorno mínimas
| Variable | Descripción |
| --- | --- |
| `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD` | Conexión a PostgreSQL |
| `JWT_SECRET` | Clave HMAC para emitir tokens |
| `APP_USER`, `APP_PASS` | Credenciales del administrador inicial (opcional) |
| `GCP_BUCKET_NAME` | Bucket de Google Cloud Storage |
| `GCP_KEY_JSON` | Credenciales del servicio GCS (JSON en una sola línea) |
| `GCP_TOTAL_STORAGE_BYTES` | Capacidad total opcional para métricas de almacenamiento |

## Cambios recientes
- Nuevo endpoint `GET /api/gcs/stats` con métricas reales de almacenamiento y detección de archivos huérfanos.
- `GcsUtil` exige credenciales válidas y propaga errores de Google Cloud en vez de devolver listas vacías.
- Seguridad actualizada para proteger las rutas `/api/gcs/**` y mantener compatibilidad con `/gcs/**`.

## Estado de autenticación
Todos los endpoints (salvo `/health` y `/auth/*`) requieren un JWT válido. Cuando la respuesta incluya `{ "success": false, "error": "Token inválido" }`, es necesario renovar el token con `/auth/refresh`.

## 1. Autenticación
| Método | Ruta | Descripción |
| --- | --- | --- |
| `POST` | `/auth/register` | Alta de usuario (viewer por defecto) |
| `POST` | `/auth/login` | Devuelve `token` (1h) y `refreshToken` (14d) |
| `POST` | `/auth/refresh` | Renueva ambos tokens a partir del refresh token |
| `POST` | `/auth/logout` | Revoca el access token y todos los refresh tokens del usuario |

### Ejemplo de respuesta de login
```json
{
  "success": true,
  "token": "<jwt>",
  "refreshToken": "<refresh-token>",
  "expiresIn": 3600,
  "user": {
    "username": "admin@docuflow.com",
    "name": "Administrador",
    "role": "admin",
    "permissions": ["files.read", "files.upload", "logs.view"]
  }
}
```

## 2. Archivos (`/files`)
| Método | Ruta | Descripción |
| --- | --- | --- |
| `GET` | `/files` | Lista documentos registrados |
| `GET` | `/files/{id}` | Recupera metadatos del documento |
| `POST` | `/files` | Sube un archivo (campo `file`) y lo registra en GCS + BD |
| `GET` | `/files/{id}/download` | Devuelve el binario almacenado en GCS |
| `DELETE` | `/files/{id}` | Elimina el archivo en GCS y su registro |
| `GET` | `/files/stats` | Métricas de documentos persistidos en la base |
| `GET` | `/files/count` | Total de registros |
| `GET` | `/files/total-size` | Bytes almacenados según la BD |

> Tamaño máximo de carga: 20 MB. Formatos permitidos en `/files` y `/upload`: `pdf`, `docx`, `xlsx`.

## 3. Subida rápida (`/upload`)
Alias simplificado para clientes legacy que solo necesitan subir un archivo con el campo `file`. Usa la misma lógica de validación y almacenamiento que `/files`.

## 4. Google Cloud Storage (`/api/gcs`)
| Método | Ruta | Descripción |
| --- | --- | --- |
| `GET` | `/api/gcs/files` | Lista blobs reales del bucket configurado |
| `GET` | `/api/gcs/stats` | Resumen de almacenamiento (usa `GCP_TOTAL_STORAGE_BYTES` si está definido) |
| `GET` | `/api/gcs/files/orphaned` | Placeholder: responde con lista vacía hasta integrar comparación con BD |

### Respuesta típica de `/api/gcs/stats`
```json
{
  "ready": true,
  "bucket": "docuflow-storage",
  "usedStorage": 24567489,
  "totalStorage": 5368709120,
  "storageUsagePercent": 0.46,
  "fileCount": 128,
  "orphanedFiles": 3,
  "timestamp": "2025-10-04T18:32:10.214152"
}
```

Si falta configuración (`GCP_BUCKET_NAME` o `GCP_KEY_JSON`), la respuesta indica `"ready": false` y `"message"` con la causa.

## 5. Comentarios y tareas (`/api/comments`)
| Método | Ruta | Descripción |
| --- | --- | --- |
| `GET` | `/api/comments` | Lista todos los comentarios/tareas |
| `GET` | `/api/comments/document/{documentId}` | Filtra por documento |
| `GET` | `/api/comments/count` | Total de comentarios |
| `POST` | `/api/comments` | Crea comentario o tarea (`isTask`) |
| `PUT` | `/api/comments/{id}` | Edita contenido / asignaciones |
| `PUT` | `/api/comments/{id}/assign` | Actualiza responsables |
| `PUT` | `/api/comments/{id}/complete` | Marca tarea como completada |
| `DELETE` | `/api/comments/{id}` | Elimina registro |

## 6. Dashboard (`/api/dashboard`)
| Método | Ruta | Descripción |
| --- | --- | --- |
| `GET` | `/api/dashboard/stats` | Métricas agregadas (archivos, usuarios, logs, storage)`*
| `GET` | `/api/dashboard/activity` | Últimas 20 acciones |
| `GET` | `/api/dashboard/recent-activities` | Actividades recientes parametrizables |
| `GET` | `/api/dashboard/recent-files` | Últimos archivos cargados |
| `GET` | `/api/dashboard/files` | Detalle de archivos con tamaño formateado |
| `GET` | `/api/dashboard/files/stats` | Estadísticas de archivos (tipos, más grande, etc.) |
| `GET` | `/api/dashboard/logs` | Logs con metadatos |
| `GET` | `/api/dashboard/users` | Snapshot sintetizado de usuarios* |
| `GET` | `/api/dashboard/comments` | Comentarios completos |
| `GET` | `/api/dashboard/downloads/today` | Descargas realizadas en el día |

`*` Algunos campos de usuarios y métricas se generan temporalmente hasta contar con información definitiva.

## 7. Logs (`/api/logs`)
| Método | Ruta | Descripción |
| --- | --- | --- |
| `GET` | `/api/logs` | Paginación de logs (`page`, `size`) |
| `GET` | `/api/logs/recent` | Últimos `limit` registros (10 por defecto) |
| `GET` | `/api/logs/count` | Total de logs |
| `GET` | `/api/logs/user/{username}` | Logs asociados a un usuario |

## 8. Exportaciones (`/export`)
Formatos soportados: `csv` (por defecto) o `json`.
| Método | Ruta | Parámetros |
| --- | --- | --- |
| `GET` | `/export/logs` | `format`, `username`, `startDate`, `endDate` (ISO opcional) |
| `GET` | `/export/files` | `format` |
| `GET` | `/export/stats` | `format` |

## 9. Gestión de usuarios admin (`/api/admin/users`)
Todas las rutas requieren que el llamador sea administrador.
| Método | Ruta | Descripción |
| --- | --- | --- |
| `GET` | `/api/admin/users` | Lista usuarios |
| `GET` | `/api/admin/users/{id}` | Obtiene usuario |
| `POST` | `/api/admin/users` | Crea usuario (JSON `AdminUserCreateRequest`) |
| `PUT` | `/api/admin/users/{id}` | Actualiza datos básicos |
| `PATCH` | `/api/admin/users/{id}/password` | Actualiza contraseña |
| `PATCH` | `/api/admin/users/{id}/permissions` | Ajusta permisos |
| `DELETE` | `/api/admin/users/{id}` | Elimina usuario |
| `GET` | `/api/admin/users/roles` | Catálogo de roles permitidos |

> La ruta histórica `/users` permanece disponible para compatibilidad pero se recomienda usar el prefijo `/api/admin/users`.

## 10. Permisos (`/permissions`)
| Método | Ruta | Descripción |
| --- | --- | --- |
| `GET` | `/permissions/modules` | Catálogo de módulos y acciones disponibles |
| `GET` | `/permissions/user/{userId}` | Permisos actuales del usuario (granulares) |
| `PUT` | `/permissions/user/{userId}` | Actualiza permisos a partir de lista granular |
| `POST` | `/permissions/check` | Verifica si el usuario autenticado tiene un permiso |
| `GET` | `/permissions/roles/permissions` | Permisos por rol (`admin`, `colaborador`, `viewer`) |

## 11. Notificaciones (`/notifications`)
| Método | Ruta | Descripción |
| --- | --- | --- |
| `GET` | `/notifications` | Notificaciones visibles para el usuario actual |
| `GET` | `/notifications/admin/all` | Catálogo completo (solo admin) |
| `POST` | `/notifications` | Crea notificación (admin para globales) |
| `GET` | `/notifications/types` | Tipos disponibles |
| `GET` | `/notifications/priorities` | Prioridades disponibles |
| `GET` | `/notifications/type/{type}` | Filtra por tipo |
| `GET` | `/notifications/{id}` | Detalle (valida visibilidad) |
| `PUT` | `/notifications/{id}/deactivate` | Desactiva una notificación |
| `GET` | `/notifications/stats` | Métricas (solo admin) |

## 12. Salud (`/health`)
- `GET /health` → Estado simple (sin autenticación).
- `GET /health/detailed` → Diagnóstico extendido (requiere token y rol admin).

## Respuestas de error
| Código | Motivo más frecuente |
| --- | --- |
| `400` | Datos faltantes o formato inválido |
| `401` | Token ausente o inválido |
| `403` | Usuario sin permisos suficientes |
| `404` | Recurso inexistente |
| `503` | Dependencias externas (p. ej. GCS) no disponibles |

## Ejecución local
```powershell
cd Backend
./mvnw.cmd spring-boot:run
```

Para validaciones rápidas sin pruebas automáticas:
```powershell
cd Backend
./mvnw.cmd -q -DskipTests=true verify
```

---
Última actualización: 4 de octubre de 2025.