# 🚀 Script para ejecutar DocuFlow Backend en LOCAL (PowerShell)

Write-Host "🚀 Iniciando DocuFlow Backend en modo LOCAL..." -ForegroundColor Green
Write-Host "📊 Base de datos: localhost:5432/docuflow_local" -ForegroundColor Cyan
Write-Host "☁️  Google Cloud Storage: docuflow-storage" -ForegroundColor Cyan
Write-Host "🔐 Usuario: estudiante" -ForegroundColor Cyan
Write-Host ""

# 🔐 Autenticación
$env:APP_USER = "estudiante"
$env:APP_PASS = "123456"

# 🔑 JWT Secret (Mejorado)
$env:JWT_SECRET = "ca4e5p4RMZ1G0TBsZewXwuo3iriXMYgKbfFYwm56JlxXZp9VLdlWNCg2yegEVPAPCqITM2bvBsjAcY2t5LvMtA=="

# 🗄️ Base de Datos PostgreSQL Local
$env:DATABASE_URL = "jdbc:postgresql://localhost:5432/docuflow_local"
$env:DATABASE_USER = "postgres"
$env:DATABASE_PASSWORD = "postgres"

# ☁️ Google Cloud Storage (Mismo bucket que producción)
$env:GCP_BUCKET_NAME = "docuflow-storage"
$env:GCP_KEY_JSON = @'
{"type":"service_account","project_id":"analog-fastness-472001-f1","private_key_id":"95e8c79b7cb292b5d3ca1ce75ed6dc76d555d33b","private_key":"-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCpLf3TtCBEkKgA\nUHWh9jJWSTVlUbO1QVOIg63u9BEUm+YalNyxb5JMl1pZM+UI3Y4a7Ke50xbfbTZs\nJ3BhRNUuX/yKgg+Ml1gdDbUIzaM96vVoJuZvBzDmDmH2O+5JNB25eMaW8+PtSNPP\ng0S8Gpchl6e8WM4nD5rJ97S9D0rx30BJ7DrcwLdhSeI5N1W7hBNYI0YkMUi3Bjxq\nRV9RqnBuUw1geXcmFlaUQICIBJdGUoaDY4BGv/Va4FLU2QBi93SRMLcPH08jyIsN\nDvOl1Tg3ynsxxiGgQ3nHD+McM6jyNMQ8b3Sj9exaC7neTZsS/UhMTTKNp8IG3LTs\nMLyqiX03AgMBAAECggEAJ2sl5zvjANbLrjcREYmxtNUd8duVibgM5JP55OFNUYX5\nmVvyCA29AseJL5ud0/D7eV6Gvg9nFuv1cEHIN4G4QjPvPb6MLO/egfZ9pe10CatD\nPt9BdcExLYlQXfUc0kWoX1y6uNVe2BzN7V3imAmCCTuyw3nVnthN8p0aqap0heJj\nH5XZ3xeRID9EbIkWGLEPj5OTg4oOKF6RQNbppzvLLTfIhcTivohyE9q0LKLMeM2F\n5jxRb27EgmlVlK8OKTUHhNY5O0BayUgF3+CbjqouvNhfZ9Rem2LgVCa0x5hVhDNG\nszKXzsVZLp7UiFoGovXrF1eaqkJ9i1EY17Q4XzpNIQKBgQDobtrTxggjXxCzdPN+\nZzQjx5YjsVPMuXtubHU6A+6eO00c7I2l2ypHy8QMSjVTIsy5Cj4l8kbzoYWmyLNr\npPouZlKQR2gyVTFryNrbuJWGOethcKN3HwI406nPe6bAU1xEdQlzeNHtk7Cv3GdL\nW0H5j3Y7HBjFJJ/oHvtg5n9j6QKBgQC6VU0g3I5k3nDDiqtFHBA5lneOfxxbl+9M\nvnj1wYV2YLVN8P9KLWVbuqAP7m2kcWmXoOAEzLUGqqu2tamsnGf0J+NQXarfnwgQ\njGsoq5oRbwyp1B327GQpux5OqwbN6/wGj4y28bfcxrDSkBcUp7qJF4ayNPNpsPAd\nXDqN5u7EHwKBgQCTWauac8G3As5opiyzJqQURrQ03nccoz3PzCwo4lNEtp11R7Wo\nvp84MsKPdAAIO1iDui2aCtTWIMDE8hlklYsRHUccNlilCAMzrHqqroMEO/WvqSPI\nIb1b3zuY0G/vHymwMG4UaTTUoztw0Y4eZLUXi85NPMurN0O0eOeuD5IU+QKBgFbq\nd0G+38T5ZYww4Ncp0f+qyjXz4NxwsLURnAa2sHSZg7jJk6ucJU12bjdANnCgXmrW\nJejXPBHSBqsBbhYQFwVynbvdFVmeKvrdJRchhIphTHzjbt7BQa+dkvOLH29qhnLg\n/vhPJD6dh9mQUNPXsVFFYhU6UdiZKkfnv4B1miZtAoGAOTcugcAlVZy+M1MvVkMZ\n0Cg23bezXj0DVO9omThvLek5Ar0e8Ru0PuN9cBNbQH1teqS/ZCGNlEYUS2yabfNE\nJeg0JcKKWXwCywKs8Addgj4w1HVAPykMGjTUg9HLcwbBs8PJaeVBu2Jx1pwSXsXT\nuABOQE0QkkjGP3CQCcwYB9o=\n-----END PRIVATE KEY-----\n","client_email":"docuflow-storage-access@analog-fastness-472001-f1.iam.gserviceaccount.com","client_id":"100061917821046395812","auth_uri":"https://accounts.google.com/o/oauth2/auth","token_uri":"https://oauth2.googleapis.com/token","auth_provider_x509_cert_url":"https://www.googleapis.com/oauth2/v1/certs","client_x509_cert_url":"https://www.googleapis.com/robot/v1/metadata/x509/docuflow-storage-access%40analog-fastness-472001-f1.iam.gserviceaccount.com","universe_domain":"googleapis.com"}
'@

# Ejecutar Maven
.\mvnw.cmd spring-boot:run
