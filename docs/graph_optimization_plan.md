# Plan de Optimización del Grafo de Correlativas

## Objetivo
Transformar el mapa de materias (RPG-style) con un layout Sugiyama de capas,
sprites de estructuras por año, caminos de tierra procedurales, y eliminación
de transitividad para una visualización clara del plan de estudios.

## Decisiones de diseño
- **Sprites de nodos**: PNGs estáticos locales (`icono1.png` a `icono18.png`)
- **Caminos**: Dibujados proceduralmente con Canvas (sin assets externos)
- **Asignación de estructura**: Por año (`icono{1-5}.png` = 1er año, etc.
  o mapeo por código hash a los 18 iconos disponibles)
- **Eliminación de transitividad**: `TransitiveReduction` de JGraphT

## Cambios por archivo

### 1. GraphNode.kt — Modelos de datos
- Crear `StructureType` enum (asignación por año vs disponibilidad de iconos)
- Reemplazar `gridX/gridY/noiseX/noiseY` → `x: Float`, `y: Float` directos
- Agregar `isDummy: Boolean = false`
- Agregar `waypoints: List<Offset>` en `GraphEdge`
- Agregar `structureType: StructureType` en `GraphNode`

### 2. GraphLayoutEngine.kt — Sugiyama Layout (4 fases)
- **Fase 1: Layer Assignment** — Topological sort + restricción de dominio (cuatrimestre)
- **Fase 2: Dummy Vertices** — Nodos virtuales para aristas que cruzan múltiples layers
- **Fase 3: Vertex Ordering** — Barycenter heuristic para minimizar cruces
- **Fase 4: Coordinate Assignment** — Posiciones X/Y finales + waypoints para aristas
- Eliminar: ruido hash, `sortByPrerequisites()`

### 3. DirectedGraphCanvas.kt — Render RPG
- Nodos: `Image(painterResource)` con sprite de estructura + `Text` label debajo
- Caminos: dibujados proceduralmente con DrawScope (múltiples capas de color marrón)
- Flechas: dirección indicada en el camino (estilo RPG)
- Nodos dummy: no se renderizan

### 4. GraphLayoutEngineTest.kt — Tests
- Adaptar tests a nuevos campos (layer, positionInLayer, x/y directos)
- Agregar tests: layer assignment, dummy vertices, barycenter, waypoints, sprites

### 5. Resources
- Sprites: `desktopApp/src/main/resources/images/homeScreen/icono*.png` (1-18)
- Background: `desktopApp/src/main/resources/images/background.png`