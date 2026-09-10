# Proyecto Final — Bases de Datos I

Sistema de gestión de inventario, consignaciones e intercambios para una tienda de cartas coleccionables de *Magic: The Gathering*.

**Universidad El Bosque** · Programa de Ingeniería de Sistemas
**Asignatura:** Bases de Datos 1 · **Docente:** Ing., Esp., M.Ed. Christian Felipe Duarte
**Periodo:** 2026-2

## Integrantes

| Nombre | Documento | Rol en el proyecto |
|---|---|---|
| _(por completar)_ | | |
| _(por completar)_ | | |
| _(por completar)_ | | |

## El problema

Una tienda especializada en cartas coleccionables administra su inventario, sus consignaciones y los intercambios entre clientes mediante hojas de cálculo y registros informales.

La unidad real del negocio no es "una carta", sino **un ejemplar físico concreto**. Cada ejemplar arrastra información de tres niveles distintos que la hoja de cálculo aplana en una sola fila:

| Nivel | De qué depende | Ejemplos |
|---|---|---|
| **Carta** | del nombre | texto de oráculo, coste de maná, colores, tipos |
| **Impresión** | del nombre y la edición | rareza, artista, número de colección, idioma, foil |
| **Ejemplar** | de esa copia en particular | condición, propietario, precio, estado, ubicación |

De esa mezcla nacen la redundancia, las anomalías de actualización y la pérdida de identidad del ejemplar: la tienda no puede distinguir cuál de sus tres copias de una misma carta es propia, cuál es de un consignante y cuál está comprometida en un intercambio.

## Estructura del repositorio

```
.
├── docs/
│   ├── CONTEXTO_PROYECTO.md        Resumen de los requisitos de la asignatura
│   ├── enunciado/                  Documento original del proyecto
│   ├── gestion/                    Entregables transversales de gestión
│   ├── fase1/                      Diseño y modelado
│   └── fase2/                      Documentos de implementación
├── modelos/
│   ├── conceptual/                 E-R Peter-Chen y E-R Extendido
│   └── logico/                     E-R y modelo relacional en Crow's Foot
├── scripts/
│   ├── ddl/                        Creación de objetos
│   ├── dml/                        Inserción de registros
│   └── consultas/                  Consultas y scripts varios
└── app/                            Aplicación web de demostración
```

## Estado de los entregables

### Fase 1 — Diseño y modelado

| Entregable | Estado | Ubicación |
|---|---|---|
| Documento de la propuesta | Completado | [docs/fase1/PROPUESTA_PROYECTO.md](docs/fase1/PROPUESTA_PROYECTO.md) |
| Documento de supuestos | Pendiente | — |
| Modelo E-R Peter-Chen | Pendiente | `modelos/conceptual/` |
| Modelo E-R Extendido | Pendiente | `modelos/conceptual/` |
| Modelo E-R Crow's Foot | Pendiente | `modelos/logico/` |
| Modelo Relacional Crow's Foot | Pendiente | `modelos/logico/` |
| Diccionario de datos | Borrador | [docs/fase1/DICCIONARIO_DATOS.md](docs/fase1/DICCIONARIO_DATOS.md) |
| Álgebra relacional (50 sentencias) | Pendiente | — |

### Fase 2 — Implementación

| Entregable | Estado |
|---|---|
| Análisis de selección de Sistema Operativo | Pendiente |
| Implementación de la máquina virtual | Pendiente |
| Análisis de selección de RDBMS | Pendiente |
| Implementación del RDBMS | Pendiente |
| Scripts de creación, inserción y varios | Pendiente |
| Aplicación web conectada | Pendiente |
| Video de demostración | Pendiente |

### Transversales

| Entregable | Estado | Ubicación |
|---|---|---|
| Acta de constitución | Completado | [docs/gestion/GESTION_PROYECTO.md](docs/gestion/GESTION_PROYECTO.md) |
| Identificación de interesados | Completado | ídem |
| EDT/WBS | Completado | ídem |
| Cronograma | Completado | ídem |
| Presupuesto | Completado | ídem |
| Acta de cierre | Plantilla lista | ídem |

## Modelo de datos

24 entidades distribuidas en cuatro módulos, más 2 tablas derivadas de las relaciones muchos a muchos:

- **Catálogo:** `Carta`, `Color`, `Tipo`, `Edicion`, `Impresion`, `Rareza`, `Artista`, `Idioma`
- **Inventario:** `Ejemplar`, `Condicion`, `PrecioReferencia`
- **Personas:** `Cliente`, `Empleado`, `Rol`, `CuentaUsuario`, `Proveedor`
- **Operaciones:** `Compra`/`DetalleCompra`, `Venta`/`DetalleVenta`, `ContratoConsignacion`, `Liquidacion`, `Intercambio`/`DetalleIntercambio`
- **Relaciones N:M:** `CartaColor`, `CartaTipo`

El modelo privilegia claves naturales; solo `Artista` y `PrecioReferencia` usan clave artificial. La especificación completa está en el [diccionario de datos](docs/fase1/DICCIONARIO_DATOS.md), y hay un [recorrido de ejemplo](docs/fase1/EJEMPLO_REGISTRO.md) que sigue una carta desde el catálogo hasta su venta.

## Restricciones técnicas de la asignatura

- Sistema operativo: **solo distribuciones Linux o Unix**
- RDBMS: **cualquiera excepto SQL Server**
- Modelo E-R: **mínimo 8 entidades** con relaciones 1:1, 1:N y N:M
- Normalización hasta **3FN**
- Mínimo **50 sentencias de álgebra relacional**
- Video de demostración de **máximo 15 minutos**, con todos los integrantes en cámara y audio

## Convenciones de trabajo

- Rama principal: `main`. El trabajo se hace en ramas por funcionalidad y se integra mediante Pull Request.
- Mensajes de commit descriptivos en español, indicando qué entregable se avanza.
- Nunca subir credenciales, archivos de máquina virtual ni volcados de base de datos (ver [.gitignore](.gitignore)).
