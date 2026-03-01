# SmartCloud — Servicio de optimización y almacenamiento inteligente de archivos

---------------
SmartCloud es un backend tipo "nube" que permite subir archivos (imágenes, PDFs, ZIPs y más), optimizarlos sin pérdida perceptible cuando conviene, versionarlos y almacenarlos de forma eficiente. El objetivo principal es ayudar a empresas a reducir costos de almacenamiento y ancho de banda, aportando además trazabilidad y métricas de ahorro.

De qué trata el proyecto
-----------------------
- Ingesta de archivos (multipart / Base64 / URLs externas).
- Optimización inteligente (core): convierte/reencoda o recomprime sólo si hay ganancia real.
- Versionado: mantiene original + versiones optimizadas y su historial.
- Almacenamiento eficiente en objeto (S3-compatible) y metadatos en BD relacional.
- Métricas y reportes de ahorro por usuario/empresa.
- Seguridad con roles (ADMIN / CLIENT) y JWT.

Por qué usamos MinIO
---------------------
MinIO es la opción de almacenamiento S3-compatible elegida en este proyecto por las siguientes razones prácticas:

- Compatibilidad S3: Permite desarrollar con la API estándar S3 (futuro porteo a AWS S3 u otros proveedores).
- Ligero y fácil de levantar en desarrollo (Docker). Ideal para tests locales y CI.
- Buen rendimiento en carga/descarga de objetos y soporte para pre-signed URLs.
- Control y costes locales: puedes simular cargas/ahorros sin costes cloud durante la fase de desarrollo.
- Soporta las operaciones que necesitamos: buckets, objetos grandes (streaming), metadatos, políticas de acceso.

En resumen: MinIO nos da la API, la flexibilidad y la sencillez para implementar y probar las funciones de almacenamiento/serving sin atarnos a un proveedor en etapas tempranas.

Arquitectura y flujo visual 
-------------------------------------


Client
  |
  | (1) Subida (multipart / Base64 / URL)
  v
API (Spring Boot controllers)
  |
  | (2) Autenticación (JWT) / Validaciones
  v
Service Layer
  |-- StorageService ---> MinIO (objetos: original + optimizados)
  |-- DB (Postgres)  ---> Metadatos: File, FileVersion, AuditLog, StorageUsage
  |-- CompressionService ---> (sync o encolar job)
                     |
                     v
              Compression Worker
                     |
                     +--> Guarda versiones optimizadas en MinIO
                     +--> Actualiza CompressionJob, StorageUsage, AuditLog en DB



Flujo numerado (típico)
1. Cliente sube archivo al endpoint `/api/files` (autenticado).
2. Se crea registro `File` con versión original y un `CompressionJob` en estado PENDING.
3. Si el sistema está en modo síncrono, se intenta optimizar inmediatamente; si está en modo asíncrono, se publica el job en un broker.
4. Worker procesa el archivo (streaming), decide si optimizar (comparación de tamaño/beneficio) y, si corresponde, genera una versión optimizada.
5. Original y optimizados se guardan en MinIO; la BD registra `FileVersion`, hashes y auditoría.
6. Se actualizan métricas por usuario/empresa (peso original vs optimizado, % ahorro).

Componentes clave y responsabilidades
------------------------------------
- API (Spring Boot): endpoints REST, validación, seguridad JWT.
- Auth: login con `JwtTokenProvider`, protección de endpoints.
- StorageService: interacción con MinIO (put/get, presigned URLs, ensure bucket).
- CompressionService / Worker: lógica de optimización (imágenes, PDFs, ZIP).
- Persistence: JPA + Postgres para metadata, versiones, jobs y auditoría.

Modelo de datos (conceptual)
- User — cuentas y roles
- File — entidad principal que agrupa versiones
- FileVersion — cada versión de un archivo (original/optimized), con referencia a objeto en MinIO
- CompressionJob — registro del intento/proceso de optimización
- StorageUsage — métricas y acumulados por usuario/empresa
- AuditLog — trazabilidad (quién, cuándo, IP, hash, acción)

Por qué este diseño es útil para empresas
- Reduce costos: ahorros medibles en almacenamiento y ancho de banda.
- Trazabilidad y cumplimiento: auditoría y versionado.
- Escalable: separación de ingesta, procesamiento y almacenamiento permite escalar independiente.


## ¿Por qué es interesante?
Este proyecto combina:

- Procesamiento y optimización real de archivos (no fake).
- Versionado y trazabilidad de cada artefacto.
- Cálculo de métricas y ahorro de almacenamiento/ancho de banda.
- Seguridad basada en roles y tokens (JWT).
- Arquitectura orientada a servicios con almacenamiento S3-compatible (MinIO).


Uso rápido: Swagger y endpoints
--------------------------------

- Swagger UI (documentación interactiva): http://localhost:8080/swagger-ui/index.html
  - Abre esa URL, autoriza con Bearer <JWT> (Authorize) y prueba los endpoints.

Endpoints principales (qué hacen)
---------------------------------
- POST /api/users/register
  - Registro público de usuarios. Body: `UserCreateDto` { username, email, password }.

- POST /api/auth/login
  - Login con username/password. Responde `{ "token": "<JWT>" }`.

- POST /api/files/upload
  - Subida de archivos (multipart/form-data). Parámetro `file` (archivo). Requiere Authorization: Bearer <token>.
  - Respuesta: metadata creada (id, originalFilename, storedFilename, contentType, size, downloadUrl).

- GET /api/files
  - Lista archivos del usuario autenticado.

- GET /api/files/{id}/download
  - Descarga el archivo si eres el propietario. Devuelve stream con header `Content-Disposition`.

- DELETE /api/files/{id}
  - Borra el objeto en MinIO y la metadata en la base de datos (solo propietario).

Cómo verificar en MinIO
------------------------
1. MinIO Console (web): abre la UI que levanta `docker-compose` (puerto según tu `docker-compose.yml`, por defecto puede ser http://localhost:9001 o la que esté configurada). Inicia sesión con las credenciales de `application.yaml` (`minio.access-key` / `minio.secret-key`).
   - Ve al bucket configurado (`minio.bucket`, p. ej. `smartcloud-files`) y navega las keys bajo `users/{userId}/...`.

2. Usando la herramienta `mc` (MinIO client):

```bash
# configura un alias (ejemplo)
mc alias set local http://localhost:9100 smartcloud smartcloud123
# lista objetos en el bucket
mc ls local/smartcloud-files
# lista objetos de un usuario concreto (ejemplo userId=1)
mc ls local/smartcloud-files/users/1
```

3. Verificación rápida tras subir un archivo:
   - Subes con `POST /api/files/upload` (multipart). Recibirás `id` y `downloadUrl`.
   - En MinIO UI o con `mc` verifica que el object key devuelto (en `storedFilename` o `objectKey`) existe.

