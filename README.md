# Proyecto Final — Bases de Datos I

Sistema de gestión de inventario, consignaciones e intercambios para una tienda de cartas coleccionables de *Magic: The Gathering*.

**Universidad El Bosque** · Programa de Ingeniería de Sistemas
**Asignatura:** Bases de Datos 1 · **Docente:** Ing., Esp., M.Ed. Christian Felipe Duarte
**Periodo:** 2026-2

## Integrantes

| Nombre | Documento |
|---|---|
| _(por completar)_ | |
| _(por completar)_ | |
| _(por completar)_ | |

## El problema

Una tienda especializada en cartas coleccionables administra su inventario, sus consignaciones y los intercambios entre clientes mediante hojas de cálculo y registros informales.

La unidad real del negocio no es "una carta", sino **un ejemplar físico concreto**. Cada ejemplar arrastra información de tres niveles distintos que la hoja de cálculo aplana en una sola fila:

| Nivel | De qué depende | Ejemplos |
|---|---|---|
| **Carta** | del nombre | texto de oráculo, coste de maná, colores, tipos |
| **Impresión** | del nombre y la edición | rareza, artista, número de colección, idioma, foil |
| **Ejemplar** | de esa copia en particular | condición, propietario, precio, estado, ubicación |

De esa mezcla nacen la redundancia, las anomalías de actualización y la pérdida de identidad del ejemplar: la tienda no puede distinguir cuál de sus tres copias de una misma carta es propia, cuál es de un consignante y cuál está comprometida en un intercambio.

## Entregables

### Fase 1 — Diseño y modelado

- [ ] Documento de la propuesta del proyecto
- [ ] Documento de supuestos
- [ ] Modelo Entidad-Relación con notación Peter-Chen
- [ ] Modelo Entidad-Relación Extendido
- [ ] Modelo Entidad-Relación con notación Crow's Foot
- [ ] Modelo Relacional con notación Crow's Foot
- [ ] Diccionario de datos
- [ ] Documento de álgebra relacional (mínimo 50 sentencias)

### Fase 2 — Implementación

- [ ] Análisis de selección de Sistema Operativo
- [ ] Documento de implementación de la máquina virtual
- [ ] Análisis de selección de RDBMS
- [ ] Documento de implementación del RDBMS
- [ ] Scripts de creación de objetos
- [ ] Scripts de inserción de registros
- [ ] Scripts varios y README
- [ ] Conexión de la aplicación web a la base de datos
- [ ] Video de demostración (máximo 15 minutos)

### Transversales

- [ ] Acta de constitución del proyecto
- [ ] Identificación de interesados
- [ ] EDT / WBS
- [ ] Cronograma
- [ ] Presupuesto
- [ ] Acta de cierre

## Restricciones técnicas

- Sistema operativo: **solo distribuciones Linux o Unix**
- RDBMS: **cualquiera excepto SQL Server**
- Modelo E-R: **mínimo 8 entidades** con relaciones 1:1, 1:N y N:M
- Normalización hasta **3FN**
- Mínimo **50 sentencias de álgebra relacional**
- Video de **máximo 15 minutos**, con todos los integrantes en cámara y audio

## Convenciones de trabajo

- Rama principal: `main`. El trabajo se hace en ramas por funcionalidad y se integra mediante Pull Request.
- Mensajes de commit descriptivos en español, indicando qué entregable se avanza.
- Nunca subir credenciales, archivos de máquina virtual ni volcados de base de datos (ver [.gitignore](.gitignore)).
