# 🔧 Guía para resolver fallos de build en Render

## 1. Síntoma observado
```
[INFO] Applied plugin: 'spring'
[INFO] Applied plugin: 'jpa'
[INFO] Applied plugin: 'all-open'
error: exit status 1
```

La build se aborta porque el entorno Free de Render agota memoria/tiempo durante la compilación de Kotlin.

## 2. Ajustes aplicados
- **Dockerfile optimizado** con cache de dependencias y flags de Maven.
- **Perfil de memoria** ajustado (`JAVA_OPTS`) para ejecutarse con 512 MB.
- **render.yaml** añadido para que Render use los comandos de build/start correctos.

### Dockerfile (extracto relevante)
```dockerfile
# Reutilizar dependencias
RUN mvn dependency:go-offline -B

# Compilación silenciosa
RUN mvn clean package -DskipTests \
    -Dmaven.compiler.verbose=false \
    -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

FROM eclipse-temurin:21-jre-alpine
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/target/backend-0.0.1-SNAPSHOT.jar"]
```

### render.yaml
```yaml
build = "mvn clean package -DskipTests"
start = "java -Xmx512m -Xms256m -jar target/backend-0.0.1-SNAPSHOT.jar"
```

## 3. Checklist antes del despliegue
1. Confirmar que los archivos modificados (`Backend/Dockerfile`, `Backend/render.yaml`) están committeados.
2. Ejecutar en local (opcional) para validar:
   ```powershell
   cd Backend
   ./mvnw.cmd -q -DskipTests=true package
   ```
3. Verificar variables de entorno en Render:
   - `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD`
   - `JWT_SECRET`
   - `APP_USER`, `APP_PASS` (si se usa auto-provisión de admin)
   - `GCP_BUCKET_NAME`
   - `GCP_KEY_JSON` (contenido del JSON de servicio en una sola línea)
4. Lanzar **Manual Deploy → Deploy latest commit** desde el dashboard.

## 4. Logs esperados
```
==> Downloading dependencies...
==> Building with Maven...
[INFO] BUILD SUCCESS
==> Starting application...
Started DocuFlowBackendApplication in X.XXX seconds
```

Si aparece nuevamente `exit status 1`, escalar a un plan con más memoria o compilar localmente y subir el artefacto (`target/backend-0.0.1-SNAPSHOT.jar`).

## 5. Recomendaciones
- Mantener un `JWT_SECRET` distinto entre entornos.
- Evitar subir scripts locales (`run-local.sh`, `run-local.ps1`).
- Monitorear los logs posteriores al deploy para confirmar que las variables de entorno se cargan correctamente.

Última revisión: 4 de octubre de 2025.
