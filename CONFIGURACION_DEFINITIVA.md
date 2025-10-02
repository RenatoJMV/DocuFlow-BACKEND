# 🎯 Configuración Definitiva de DocuFlow - Multi-Ambiente

## ✅ **Tu Configuración Actual (Verificada)**

---

## 🌍 **PRODUCCIÓN - Render**

### **Variables de Entorno Configuradas en Render Dashboard**

```bash
# 🔐 Autenticación de Usuario (Hardcoded para demo)
APP_USER=estudiante
APP_PASS=123456

# 🗄️ Base de Datos PostgreSQL (Render)
DATABASE_URL=jdbc:postgresql://dpg-d31qmogdl3ps73fh8b6g-a.oregon-postgres.render.com:5432/docuflow_vqq1
DATABASE_USER=docuflow_user
DATABASE_PASSWORD=7F5N36bmISnkj5RVl8D8X9CpBsFPgpn6

# ☁️ Google Cloud Storage (Bucket compartido Local + Producción)
GCP_BUCKET_NAME=docuflow-storage
GCP_KEY_JSON={"type":"service_account","project_id":"analog-fastness-472001-f1",...}

# 🔑 JWT Secret
JWT_SECRET=7e12b8940b6d9e364d3a183e05937366
```

### **✅ Estado: CONFIGURACIÓN CORRECTA**
- ✅ Base de datos PostgreSQL en Render
- ✅ Google Cloud Storage configurado
- ✅ JWT configurado
- ✅ Usuario de prueba configurado
- ✅ `application.properties` lee las variables correctamente

---

## 🏠 **DESARROLLO LOCAL**

### **Configuración para tu Máquina**

#### **1. Variables de Entorno Locales**

**Opción A: Archivo `.env` en la raíz del proyecto (recomendado)**

Crea un archivo `.env` en `E:\GitHub\DocuFlow-BACKEND\`:

```bash
# 🔐 Autenticación de Usuario (mismo que producción)
APP_USER=estudiante
APP_PASS=123456

# 🗄️ Base de Datos PostgreSQL LOCAL
DATABASE_URL=jdbc:postgresql://localhost:5432/docuflow_local
DATABASE_USER=postgres
DATABASE_PASSWORD=tu_password_local

# ☁️ Google Cloud Storage (MISMO QUE PRODUCCIÓN)
GCP_BUCKET_NAME=docuflow-storage
GCP_KEY_JSON={"type":"service_account","project_id":"analog-fastness-472001-f1","private_key_id":"95e8c79b7cb292b5d3ca1ce75ed6dc76d555d33b","private_key":"-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCpLf3TtCBEkKgA\nUHWh9jJWSTVlUbO1QVOIg63u9BEUm+YalNyxb5JMl1pZM+UI3Y4a7Ke50xbfbTZs\nJ3BhRNUuX/yKgg+Ml1gdDbUIzaM96vVoJuZvBzDmDmH2O+5JNB25eMaW8+PtSNPP\ng0S8Gpchl6e8WM4nD5rJ97S9D0rx30BJ7DrcwLdhSeI5N1W7hBNYI0YkMUi3Bjxq\nRV9RqnBuUw1geXcmFlaUQICIBJdGUoaDY4BGv/Va4FLU2QBi93SRMLcPH08jyIsN\nDvOl1Tg3ynsxxiGgQ3nHD+McM6jyNMQ8b3Sj9exaC7neTZsS/UhMTTKNp8IG3LTs\nMLyqiX03AgMBAAECggEAJ2sl5zvjANbLrjcREYmxtNUd8duVibgM5JP55OFNUYX5\nmVvyCA29AseJL5ud0/D7eV6Gvg9nFuv1cEHIN4G4QjPvPb6MLO/egfZ9pe10CatD\nPt9BdcExLYlQXfUc0kWoX1y6uNVe2BzN7V3imAmCCTuyw3nVnthN8p0aqap0heJj\nH5XZ3xeRID9EbIkWGLEPj5OTg4oOKF6RQNbppzvLLTfIhcTivohyE9q0LKLMeM2F\n5jxRb27EgmlVlK8OKTUHhNY5O0BayUgF3+CbjqouvNhfZ9Rem2LgVCa0x5hVhDNG\nszKXzsVZLp7UiFoGovXrF1eaqkJ9i1EY17Q4XzpNIQKBgQDobtrTxggjXxCzdPN+\nZzQjx5YjsVPMuXtubHU6A+6eO00c7I2l2ypHy8QMSjVTIsy5Cj4l8kbzoYWmyLNr\npPouZlKQR2gyVTFryNrbuJWGOethcKN3HwI406nPe6bAU1xEdQlzeNHtk7Cv3GdL\nW0H5j3Y7HBjFJJ/oHvtg5n9j6QKBgQC6VU0g3I5k3nDDiqtFHBA5lneOfxxbl+9M\nvnj1wYV2YLVN8P9KLWVbuqAP7m2kcWmXoOAEzLUGqqu2tamsnGf0J+NQXarfnwgQ\njGsoq5oRbwyp1B327GQpux5OqwbN6/wGj4y28bfcxrDSkBcUp7qJF4ayNPNpsPAd\nXDqN5u7EHwKBgQCTWauac8G3As5opiyzJqQURrQ03nccoz3PzCwo4lNEtp11R7Wo\nvp84MsKPdAAIO1iDui2aCtTWIMDE8hlklYsRHUccNlilCAMzrHqqroMEO/WvqSPI\nIb1b3zuY0G/vHymwMG4UaTTUoztw0Y4eZLUXi85NPMurN0O0eOeuD5IU+QKBgFbq\nd0G+38T5ZYww4Ncp0f+qyjXz4NxwsLURnAa2sHSZg7jJk6ucJU12bjdANnCgXmrW\nJejXPBHSBqsBbhYQFwVynbvdFVmeKvrdJRchhIphTHzjbt7BQa+dkvOLH29qhnLg\n/vhPJD6dh9mQUNPXsVFFYhU6UdiZKkfnv4B1miZtAoGAOTcugcAlVZy+M1MvVkMZ\n0Cg23bezXj0DVO9omThvLek5Ar0e8Ru0PuN9cBNbQH1teqS/ZCGNlEYUS2yabfNE\nJeg0JcKKWXwCywKs8Addgj4w1HVAPykMGjTUg9HLcwbBs8PJaeVBu2Jx1pwSXsXT\nuABOQE0QkkjGP3CQCcwYB9o=\n-----END PRIVATE KEY-----\n","client_email":"docuflow-storage-access@analog-fastness-472001-f1.iam.gserviceaccount.com","client_id":"100061917821046395812","auth_uri":"https://accounts.google.com/o/oauth2/auth","token_uri":"https://oauth2.googleapis.com/token","auth_provider_x509_cert_url":"https://www.googleapis.com/oauth2/v1/certs","client_x509_cert_url":"https://www.googleapis.com/robot/v1/metadata/x509/docuflow-storage-access%40analog-fastness-472001-f1.iam.gserviceaccount.com","universe_domain":"googleapis.com"}

# 🔑 JWT Secret (mismo que producción)
JWT_SECRET=7e12b8940b6d9e364d3a183e05937366
```

**Opción B: Configurar en IntelliJ IDEA**

1. Ve a **Run** → **Edit Configurations**
2. Selecciona tu configuración de Spring Boot
3. En **Environment Variables**, agrega:

```
APP_USER=estudiante;APP_PASS=123456;DATABASE_URL=jdbc:postgresql://localhost:5432/docuflow_local;DATABASE_USER=postgres;DATABASE_PASSWORD=tu_password_local;GCP_BUCKET_NAME=docuflow-storage;GCP_KEY_JSON={"type":"service_account",...};JWT_SECRET=7e12b8940b6d9e364d3a183e05937366
```

---

#### **2. Base de Datos PostgreSQL Local**

**Crear la base de datos:**

```sql
-- En tu PostgreSQL local (pgAdmin o psql)
CREATE DATABASE docuflow_local;

-- Verificar conexión
\c docuflow_local
```

**O usar psql desde terminal:**

```bash
psql -U postgres
CREATE DATABASE docuflow_local;
\q
```

---

## 🔄 **Comparación de Ambientes**

| Variable | Local | Render (Producción) |
|----------|-------|---------------------|
| **APP_USER** | `estudiante` | `estudiante` |
| **APP_PASS** | `123456` | `123456` |
| **DATABASE_URL** | `jdbc:postgresql://localhost:5432/docuflow_local` | `jdbc:postgresql://dpg-d31qmogdl3ps73fh8b6g-a.oregon-postgres.render.com:5432/docuflow_vqq1` |
| **DATABASE_USER** | `postgres` (o tu usuario local) | `docuflow_user` |
| **DATABASE_PASSWORD** | Tu password local | `7F5N36bmISnkj5RVl8D8X9CpBsFPgpn6` |
| **GCP_BUCKET_NAME** | `docuflow-storage` | `docuflow-storage` ⬅️ **MISMO** |
| **GCP_KEY_JSON** | Credenciales completas | Credenciales completas ⬅️ **MISMO** |
| **JWT_SECRET** | `7e12b8940b6d9e364d3a183e05937366` | `7e12b8940b6d9e364d3a183e05937366` ⬅️ **MISMO** |

---

## 🚀 **Cómo Ejecutar**

### **En Local:**

1. **Asegúrate de que PostgreSQL esté corriendo:**
   ```bash
   # Verificar si está corriendo
   pg_ctl status
   ```

2. **Crea la base de datos `docuflow_local`** (si no existe)

3. **Configura las variables de entorno** (archivo `.env` o IntelliJ)

4. **Ejecuta el backend:**
   ```bash
   cd Backend
   ./mvnw spring-boot:run
   ```
   O desde IntelliJ: **Run** → **DocuFlowBackendApplication**

5. **Verifica que esté corriendo:**
   ```
   http://localhost:8080
   ```

### **En Render (Producción):**

1. ✅ Variables de entorno ya configuradas en Render Dashboard
2. ✅ Base de datos PostgreSQL de Render conectada
3. ✅ Despliegue automático desde GitHub
4. ✅ URL: `https://tu-backend.onrender.com`

---

## 🌐 **URLs de tu API**

### **Local (Desarrollo):**
```
Base URL: http://localhost:8080

Endpoints:
- POST   http://localhost:8080/login
- GET    http://localhost:8080/files
- POST   http://localhost:8080/files
- GET    http://localhost:8080/files/{id}
- GET    http://localhost:8080/files/{id}/download
- DELETE http://localhost:8080/files/{id}
```

### **Render (Producción):**
```
Base URL: https://tu-backend.onrender.com

Endpoints:
- POST   https://tu-backend.onrender.com/login
- GET    https://tu-backend.onrender.com/files
- POST   https://tu-backend.onrender.com/files
- GET    https://tu-backend.onrender.com/files/{id}
- GET    https://tu-backend.onrender.com/files/{id}/download
- DELETE https://tu-backend.onrender.com/files/{id}
```

---

## 📝 **Frontend - Configuración de URLs**

En tu frontend (JavaScript/React/Vue), usa detección automática:

```javascript
// Detectar ambiente automáticamente
const API_URL = window.location.hostname === 'localhost'
    ? 'http://localhost:8080'
    : 'https://tu-backend.onrender.com';

// O usar variable de entorno
const API_URL = import.meta.env.VITE_API_URL || 'https://tu-backend.onrender.com';

// Luego en todas las peticiones:
fetch(`${API_URL}/files`, {
    headers: {
        'Authorization': `Bearer ${token}`
    }
})
```

---

## ✅ **Checklist de Verificación**

### **Local (Desarrollo):**
- [ ] PostgreSQL corriendo en `localhost:5432`
- [ ] Base de datos `docuflow_local` creada
- [ ] Variables de entorno configuradas (`.env` o IntelliJ)
- [ ] GCS credentials configuradas (mismo JSON que producción)
- [ ] Backend corriendo en `http://localhost:8080`
- [ ] Login funciona: `POST /login` con `estudiante/123456`
- [ ] Puedes subir archivos a GCS bucket `docuflow-storage`

### **Render (Producción):**
- [x] Variables de entorno configuradas en Dashboard
- [x] Base de datos PostgreSQL de Render conectada
- [x] GCS configurado
- [x] JWT configurado
- [ ] Backend desplegado y corriendo
- [ ] Login funciona desde frontend en Render
- [ ] Archivos se suben al mismo bucket GCS

---

## 🎯 **Ventajas de tu Configuración**

✅ **Mismo bucket GCS en ambos ambientes**
- Los archivos se guardan en el mismo lugar
- Puedes probar en local y ver los archivos en producción
- No necesitas bucket separado de desarrollo

✅ **Mismo JWT_SECRET**
- Los tokens generados en local funcionan en producción (si es necesario)
- Facilita pruebas cross-environment

✅ **Base de datos separadas**
- Local: `docuflow_local` (tu máquina)
- Producción: `docuflow_vqq1` (Render)
- No mezclas datos de prueba con producción

✅ **Mismo usuario de prueba**
- Usuario: `estudiante`
- Password: `123456`
- Funciona igual en ambos ambientes

---

## 🔥 **Próximos Pasos**

1. **Crear la base de datos local:**
   ```sql
   CREATE DATABASE docuflow_local;
   ```

2. **Configurar las variables de entorno locales** (opción A o B)

3. **Ejecutar el backend localmente:**
   ```bash
   cd Backend
   ./mvnw spring-boot:run
   ```

4. **Probar el login:**
   ```bash
   curl -X POST http://localhost:8080/login \
     -H "Content-Type: application/json" \
     -d '{"username":"estudiante","password":"123456"}'
   ```

5. **Probar subida de archivos con el token obtenido**

---

## 💡 **Notas Importantes**

- ⚠️ **Seguridad:** Cambia `APP_PASS` y `JWT_SECRET` en producción para valores más seguros
- 💰 **GCS:** Tienes 1050 soles en créditos, así que está bien usar el mismo bucket
- 🔄 **Sincronización:** Los archivos subidos en local aparecerán en producción y viceversa (mismo bucket)
- 📊 **Monitoreo:** Puedes ver los archivos en GCS Console: https://console.cloud.google.com/storage/browser/docuflow-storage

---

**✅ Tu backend está listo para funcionar en ambos ambientes con Google Cloud Storage compartido.**
