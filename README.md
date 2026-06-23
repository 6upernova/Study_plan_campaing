# Study Plan Campaign

Visualizador RPG del plan de estudios de la **Licenciatura en Ciencias de la Computación** (UTN - ISI). Cada materia se representa como una criatura o estructura fantástica en un mapa interactivo, donde los caminos de tierra conectan las correlatividades entre asignaturas.

## Propósito

Transformar el plan de estudios universitario en una **campaña RPG interactiva**, donde el estudiante navega su recorrido académico como si fuera un mapa de exploración. Las materias son "criaturas" cuya dificultad se calcula a partir de estadísticas reales (tasas de aprobación, promedios de notas), y las correlatividades son senderos que conectan las estructuras del mapa.

## Arquitectura MVVM

El proyecto sigue el patrón **MVVM + Clean Architecture** en el módulo `desktopApp`:

```
desktopApp/src/main/kotlin/org/edu/stones/
├── domain/                      # MODEL
│   ├── entity/                   # Entidades de dominio (Subject)
│   ├── repository/               # Interfaces de repositorios
│   └── usecase/                  # Casos de uso (Interface + Impl)
│       ├── GetAllSubjectsUseCase
│       ├── GetSubjectDetailUseCase
│       ├── GetSubjectNameUseCase
│       ├── GetSubjectLegendUseCase
│       ├── GenerateSubjectImageUseCase
│       ├── PreloadSubjectImagesUseCase
│       ├── PreloadSubjectLegendsUseCase
│       └── SubjectCreatureHelper  # Mapping materia → criatura RPG
├── data/                         # MODEL (Data Layer)
│   ├── external/                  # Fuentes externas (APIs)
│   │   ├── GoogleScriptSubjectExternalSource  # Datos de materias (Google Apps Script)
│   │   ├── LegendExternalSourceImpl           # Leyendas IA (Pollinations GET)
│   │   ├── OpenAiLegendExternalSourceImpl     # Leyendas IA (Pollinations POST)
│   │   ├── ImageGenExternalSourceImpl         # Imágenes IA (Pollinations Image)
│   │   └── LegendBroker                        # Round-robin entre fuentes de leyendas
│   ├── local/                     # Fuentes locales (caché en disco)
│   │   ├── subjects/              # Caché JSON del grafo (SHA-256, 24h validez)
│   │   ├── legend/                # Caché de leyendas (.txt)
│   │   ├── image/                 # Caché de imágenes (.png)
│   │   └── mapper/                # Mappers CachedGraph ↔ Domain
│   └── repository/                # Implementaciones de repositorios
│       ├── SubjectsRepositoryImpl # Estrategia cache-first + TransitiveReduction
│       ├── LegendRepositoryImpl   # Caché 3 niveles: memoria → disco → API
│       └── ImageRepositoryImpl   # Caché 3 niveles: memoria → disco → API
├── di/                           # Inyección de dependencias (manual, singleton)
├── presentation/                 # VIEW + VIEWMODEL
│   ├── home/
│   │   ├── HomeViewModel.kt       # VIEWMODEL: StateFlow<HomeUiState>
│   │   ├── HomeScreen.kt          # VIEW: Pantalla principal con grafo
│   │   ├── DirectedGraphCanvas.kt # Canvas interactivo del grafo dirigido
│   │   ├── GraphNode.kt          # Modelos: GraphNode, GraphEdge, StructureType
│   │   ├── GraphLayoutEngine.kt   # Algoritmo de layout Sugiyama
│   │   ├── GraphBackground.kt     # Background con imagen tileada
│   │   ├── GraphConfig.kt         # ~60 parámetros de configuración del grafo
│   │   └── GraphConfigDefaults.kt # Presets: Default, Desktop, Tablet, Compact
│   ├── detail/
│   │   ├── DetailViewModel.kt     # VIEWMODEL: StateFlow<DetailUiState>
│   │   ├── SubjectDetailScreen.kt # VIEW: Detalle de materia (ventana popup)
│   │   ├── HeaderSection.kt       # Nombre con borde decorativo
│   │   ├── GeneralInfoSection.kt  # Tabla de propiedades + imagen IA
│   │   ├── RequirementsSection.kt # Correlativas cursadas y aprobadas
│   │   ├── StatisticsSection.kt   # Arcos: aprobación, promedio, asistencia
│   │   └── LegendSection.kt       # Leyenda épica generada por IA
│   └── App.kt                    # Punto de entrada Composable
└── FontsFamily.kt                # Fuente Friz Quadrata (estilo RPG)
```

### Capas

| Capa | Responsabilidad |
|------|----------------|
| **Model** (`domain/` + `data/`) | Entidades, repositorios, casos de uso, fuentes de datos externas y locales, mappers, caché |
| **ViewModel** (`presentation/*/ViewModel.kt`) | Estado de UI reactivo via `StateFlow`, orquestación de casos de uso, lógica de presentación |
| **View** (`presentation/*/Screen.kt` + componentes) | Composables de Jetpack Compose, renderizado del grafo, pantallas de detalle |

### Flujo de datos

```
View (Compose) → ViewModel (StateFlow) → UseCase → Repository → [Cache → External API]
```

## Estructura del Proyecto

```
study_plan_campaing/
├── shared/            # Código compartido KMP (common, JVM, JS, Wasm)
├── desktopApp/        # Aplicación principal (JVM/Compose Desktop)
├── webApp/            # Aplicación web (JS/Wasm, básica)
├── docs/              # Documentación (plan de optimización del grafo)
└── gradle/            # Version catalog y wrapper
```

## Funcionalidades

- **Grafo interactivo del plan de estudios** — Visualización del plan completo como grafo dirigido con sprites RPG
- **Layout Sugiyama** — Asignación de capas por año/cuatrimestre, minimización de cruces por barycenter, coordenadas auto-escalables
- **Reducción transitiva** — Eliminación de aristas redundantes (JGraphT `TransitiveReduction`)
- **Mapeo criatura-materia** — Goblins/kobolds (1er año) atédragones/liches (5to año), según estadísticas
- **Imágenes generadas por IA** — Retratos fantásticos de cada materia via Pollinations.ai (turbo, 512×512)
- **Leyendas épicas por IA** — Texto narrativo en español de cada materia como criatura (Pollinations.ai + round-robin broker)
- **Caché de 3 niveles** — Memoria (Mutex) → Disco (SHA-256) → API externa
- **Ventanas de detalle** — Clic en nodo → popup con estadísticas, correlativas, imagen y leyenda
- **Exploración interactiva** — Hover resalta aristas conectadas en dorado, scroll horizontal, layout responsivo
- **Estética RPG** — Fuente Friz Quadrata, fondos pergamino, caminos de tierra con profundidad, sprites de estructuras

## Tecnologías

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Kotlin | 2.4.0 | Lenguaje principal |
| Compose Multiplatform | 1.11.1 | Framework UI multiplataforma |
| AndroidX Lifecycle | 2.11.0-beta01 | ViewModel + StateFlow |
| Ktor Client | 3.5.0 | HTTP client (CIO, ContentNegotiation) |
| JGraphT | 1.5.2 | Grafo dirigido + TransitiveReduction |
| kotlinx.serialization | — | Serialización JSON (DTOs, caché) |
| kotlinx-coroutines | 1.11.0 | Asincronía y concurrencia |

### APIs externas

- **Google Apps Script** — Datos de materias de la carrera ISI
- **image.pollinations.ai** — Generación de imágenes (modelo turbo, 512×512)
- **text.pollinations.ai** — Generación de texto/leyendas (GET y POST/OpenAI-compat)

## Cómo ejecutar

**Desktop (aplicación principal):**
```bash
./gradlew :desktopApp:run
```

**Hot reload:**
```bash
./gradlew :desktopApp:hotRun --auto
```

**Web (Wasm):**
```bash
./gradlew :webApp:wasmJsBrowserDevelopmentRun
```

**Tests:**
```bash
./gradlew :shared:jvmTest
./gradlew :desktopApp:test
```
