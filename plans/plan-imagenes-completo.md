# Plan consolidado — completar generación de imagen + cache

> Plan ejecutable para agente. Cubre **solo lo que falta** de los Planes A (API) y B (cache).
> Estado verificado el 2026-06-20: el proyecto **NO compila** (`BUILD FAILED`, 9 referencias sin resolver).
> El lado consumidor YA está hecho — NO recrearlo. Solo crear las clases núcleo y ajustar 1 línea del DI.

---

## Estado actual (verificado)

### ✅ Ya implementado — NO TOCAR (salvo el ajuste puntual del Paso 7)
- `presentation/detail/DetailViewModel.kt` — estado (`imageBytes`, `isImageLoading`) + `loadSubjectImage()`. Llama `generateSubjectImageUseCase(subject)`.
- `presentation/detail/SubjectDetailScreen.kt` — `SubjectImage` decodifica bytes con Skia, muestra spinner, y cae a `images/fotoBase.png` si falla.
- `di/SubjectDependencyInjector.kt` — ya referencia `PollinationsImageSource`, `ImageRepositoryImpl`, `GenerateSubjectImageUseCaseImpl`.

### ❌ Falta crear (causa del fallo de compilación)
| Archivo | Plan | Estado |
|---|---|---|
| `domain/repository/ImageRepository.kt` | A | falta |
| `domain/usecase/GenerateSubjectImageUseCase.kt` | A | falta |
| `domain/usecase/GenerateSubjectImageUseCaseImpl.kt` | A | falta |
| `data/external/ImageGenExternalSource.kt` | A | falta |
| `data/external/dto/PollinationsImageSource.kt` | A | falta |
| `data/repository/ImageRepositoryImpl.kt` | A+B | falta (crear ya con cache) |
| `data/cache/ImageDiskCache.kt` | B | falta |

### ⚠️ Ajuste necesario en DI
- Línea actual: `ImageRepositoryImpl(imageSource)` (1 arg, firma sin cache).
- Debe quedar: `ImageRepositoryImpl(imageSource, imageDiskCache)` (con cache).

**Contratos que los consumidores ya esperan (respetar exactamente):**
- `GenerateSubjectImageUseCase` con `operator fun invoke(subject: Subject): ByteArray?`
- `GenerateSubjectImageUseCaseImpl(imageRepository)`
- `PollinationsImageSource()` (sin args)
- `ImageRepositoryImpl(imageSource, imageDiskCache)`

Base path de todos los archivos: `desktopApp/src/main/kotlin/org/edu/stones/`

---

## Paso 1 — `domain/repository/ImageRepository.kt`

```kotlin
package org.edu.stones.domain.repository

interface ImageRepository {
    suspend fun getImageForPrompt(prompt: String): ByteArray?
}
```

**Aceptación:** compila; junto a `SubjectRepository.kt`.

---

## Paso 2 — `domain/usecase/GenerateSubjectImageUseCase.kt` (+ Impl)

`GenerateSubjectImageUseCase.kt`:
```kotlin
package org.edu.stones.domain.usecase

import org.edu.stones.domain.entity.Subject

interface GenerateSubjectImageUseCase {
    suspend operator fun invoke(subject: Subject): ByteArray?
}
```

`GenerateSubjectImageUseCaseImpl.kt`:
```kotlin
package org.edu.stones.domain.usecase

import org.edu.stones.domain.entity.Subject
import org.edu.stones.domain.repository.ImageRepository

class GenerateSubjectImageUseCaseImpl(
    private val imageRepository: ImageRepository
) : GenerateSubjectImageUseCase {
    override suspend fun invoke(subject: Subject): ByteArray? {
        val prompt = "ilustración educativa minimalista sobre ${subject.nombre}, estilo plano, fondo claro"
        return imageRepository.getImageForPrompt(prompt)
    }
}
```

**Aceptación:** la firma `invoke(subject)` coincide con la llamada del `DetailViewModel`.

---

## Paso 3 — `data/external/ImageGenExternalSource.kt`

```kotlin
package org.edu.stones.data.external

interface ImageGenExternalSource {
    suspend fun generate(prompt: String): ByteArray?
}
```

**Aceptación:** compila; junto a `SubjectDetailExternalSource.kt`.

---

## Paso 4 — `data/external/dto/PollinationsImageSource.kt`

Imitar el estilo de `GoogleScriptSubjectExternalSource` (provider inyectable + `runCatching`).
API: `GET https://image.pollinations.ai/prompt/{prompt}?width=512&height=512&nologo=true` → devuelve **bytes** (no JSON, NO instalar ContentNegotiation).

```kotlin
package org.edu.stones.data.external.dto

import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import io.ktor.http.URLProtocol
import io.ktor.http.path
import org.edu.stones.data.external.ImageGenExternalSource

class PollinationsImageSource(
    private val clientProvider: () -> HttpClient = { createImageHttpClient() },
    private val bytesProvider: suspend (HttpClient, String) -> ByteArray = { client, prompt ->
        client.get {
            url {
                path("prompt", prompt) // encodea cada segmento
                parameters.append("width", "512")
                parameters.append("height", "512")
                parameters.append("nologo", "true")
            }
        }.readRawBytes()
    },
) : ImageGenExternalSource {

    override suspend fun generate(prompt: String): ByteArray? =
        runCatching {
            bytesProvider(clientProvider(), prompt).takeIf { it.isNotEmpty() }
        }.getOrElse { error ->
            println("Error generando imagen: ${error.message}")
            null
        }
}

private fun createImageHttpClient(): HttpClient =
    HttpClient {
        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTPS
                host = "image.pollinations.ai"
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
        }
    }
```

> Si `path("prompt", prompt)` da problemas de encoding en runtime, alternativa:
> `encodedPath = "/prompt/" + prompt.encodeURLPath()` (import `io.ktor.http.encodeURLPath`, `io.ktor.http.encodedPath`).

**Aceptación:** compila; host/path/timeout correctos; `bytesProvider` inyectable para test.

---

## Paso 5 — `data/cache/ImageDiskCache.kt` (Plan B)

```kotlin
package org.edu.stones.data.cache

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest

class ImageDiskCache(
    private val cacheDir: Path =
        Paths.get(System.getProperty("user.home"), ".study_plan", "cache"),
) {
    fun cacheKey(prompt: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(prompt.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun get(key: String): ByteArray? = runCatching {
        val file = cacheDir.resolve("$key.png")
        if (Files.exists(file)) Files.readAllBytes(file) else null
    }.getOrNull()

    fun put(key: String, bytes: ByteArray) {
        runCatching {
            Files.createDirectories(cacheDir)
            Files.write(cacheDir.resolve("$key.png"), bytes)
        }
    }
}
```

**Aceptación:** `cacheKey` determinístico; fallos de disco no rompen (todo en `runCatching`).

---

## Paso 6 — `data/repository/ImageRepositoryImpl.kt` (Plan A + cache de B, cache-aside)

Crear **directamente con cache** (no la versión intermedia sin cache, porque el DI ya va a pasar 2 args):

```kotlin
package org.edu.stones.data.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.edu.stones.data.cache.ImageDiskCache
import org.edu.stones.data.external.ImageGenExternalSource
import org.edu.stones.domain.repository.ImageRepository

class ImageRepositoryImpl(
    private val source: ImageGenExternalSource,
    private val diskCache: ImageDiskCache,
) : ImageRepository {

    private val memory = mutableMapOf<String, ByteArray>()
    private val mutex = Mutex()

    override suspend fun getImageForPrompt(prompt: String): ByteArray? {
        val key = diskCache.cacheKey(prompt)

        // 1. memoria
        mutex.withLock { memory[key] }?.let { return it }

        // 2. disco -> calienta memoria
        diskCache.get(key)?.let { fromDisk ->
            mutex.withLock { memory[key] = fromDisk }
            return fromDisk
        }

        // 3. red -> escribe disco + memoria (solo si exitoso)
        val generated = source.generate(prompt) ?: return null
        diskCache.put(key, generated)
        mutex.withLock { memory[key] = generated }
        return generated
    }
}
```

**Aceptación:** compila; nunca cachea `null`.

---

## Paso 7 — Ajustar DI (`di/SubjectDependencyInjector.kt`)

Cambiar el bloque de imagen (líneas ~37-39) para inyectar el cache:

```kotlin
private val imageSource = PollinationsImageSource()
private val imageDiskCache = ImageDiskCache()
private val imageRepository = ImageRepositoryImpl(imageSource, imageDiskCache)
private val generateSubjectImageUseCase = GenerateSubjectImageUseCaseImpl(imageRepository)
```

Agregar import: `import org.edu.stones.data.cache.ImageDiskCache`.

**Aceptación:** `ImageRepositoryImpl` se construye con 2 args; resto del DI intacto.

---

## Paso 8 — Compilar

```
./gradlew :desktopApp:compileKotlin
```
**Aceptación:** `BUILD SUCCESSFUL`, 0 referencias sin resolver.

---

## Paso 9 — Tests

### 9a — `PollinationsImageSource` (sin red, `bytesProvider` mockeado)
- ok: devuelve `ByteArray` no vacío → `generate()` lo retorna.
- error: `bytesProvider` lanza → `generate()` == null.
- vacío: `ByteArray(0)` → `generate()` == null.

### 9b — `ImageRepositoryImpl` (cache) con source que cuenta llamadas
```kotlin
class CountingSource(private val bytes: ByteArray?) : ImageGenExternalSource {
    var calls = 0
    override suspend fun generate(prompt: String): ByteArray? { calls++; return bytes }
}
```
Usar `cacheDir` temporal (`Files.createTempDirectory`):
1. Hit memoria: mismo prompt 2x → `calls == 1`.
2. Hit disco: repo A escribe; repo B nuevo (mismo dir, memoria vacía) → no llama al source.
3. No cachear fallo: source devuelve `null` → no se escribe archivo; segundo pedido vuelve a llamar.
4. `cacheKey` determinístico y distinto para prompts distintos.

**Aceptación:** todos verdes.

---

## Paso 10 — Verificación manual

- [ ] Abrir una materia → spinner, luego imagen generada.
- [ ] Cerrar y reabrir la MISMA materia → imagen **instantánea** (disco).
- [ ] Existe `~/.study_plan/cache/<hash>.png`.
- [ ] Sin red → cae a `fotoBase.png`, no crashea.
- [ ] Borrar la carpeta de cache → vuelve a generar sin crashear.

---

## Resumen de ejecución
7 archivos nuevos + 1 edición (DI). Sin dependencias nuevas en `build.gradle.kts`
(`ktor-client-cio`, coroutines y Skia ya están). Orden: Pasos 1→7 para que compile, luego 8→10 para validar.
