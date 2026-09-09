# Propuesta de Proyecto — Bases de Datos 1

**Universidad El Bosque** · Programa de Ingeniería de Sistemas
**Asignatura:** Bases de Datos 1 · **Docente:** Ing., Esp., M.Ed. Christian Felipe Duarte
**Fecha:** 8 de septiembre de 2026

**Título del proyecto:** Sistema de gestión de inventario, consignaciones e intercambios para una tienda de cartas coleccionables de *Magic: The Gathering*.

**Integrantes:** _(por completar)_

---

## 1. Contexto

Una tienda especializada en cartas coleccionables de *Magic: The Gathering* en Bogotá opera tres líneas de negocio de forma simultánea:

1. **Venta de inventario propio**, adquirido a proveedores y distribuidores.
2. **Consignación**: recibe cartas de clientes para venderlas a cambio de una comisión, bajo un contrato con plazo y condiciones.
3. **Intermediación de intercambios**: actúa como garante y valorador en los trades entre coleccionistas, cobrando una comisión por el servicio.

Actualmente toda la operación se registra en hojas de cálculo, cuadernos de intercambios y el conocimiento no documentado de los empleados.

## 2. Planteamiento del problema

La unidad real del negocio no es "una carta", sino **un ejemplar físico concreto**. Cada ejemplar arrastra información que pertenece a tres niveles distintos, y la hoja de cálculo los aplana en una sola fila:

| Nivel | De qué depende la información | Ejemplos de atributos |
|---|---|---|
| **Carta** (concepto de reglas) | del nombre de la carta | texto de oráculo, coste de maná, colores, tipos |
| **Impresión** (edición concreta) | del nombre **y** la edición | rareza, artista, número de colección, idioma, foil |
| **Ejemplar** (copia física) | de esa copia en particular | condición, propietario, precio, estado, ubicación |

Esta mezcla de niveles produce **redundancia** (el texto de una carta se repite en cientos de filas), **anomalías de actualización** (corregir una errata obliga a editar todas esas filas) y, sobre todo, **pérdida de identidad del ejemplar**: la tienda no puede distinguir cuál de las tres copias de una misma carta en vitrina es de su propiedad, cuál pertenece a un consignante y cuál ya está comprometida en un intercambio.

### Consecuencias observadas

1. **Se vende lo que no debía venderse.** Una copia consignada se vende por debajo del precio pactado con su dueño, o se vende una copia ya reservada para un intercambio.
2. **No hay cuentas claras con los consignantes.** No se sabe con precisión qué copias de quién se vendieron, cuánta comisión corresponde a la tienda ni cuánto se le adeuda a cada consignante.
3. **Los precios se fijan sin criterio.** El valor de mercado cambia semanalmente y depende fuertemente de la condición física del ejemplar, pero no existe historial de precios ni factor de ajuste documentado.
4. **Los intercambios no dejan rastro.** No queda registro de qué cartas entregó cada parte, si el intercambio fue equilibrado en valor, ni qué comisión cobró la tienda. Ante un reclamo no hay evidencia.
5. **No hay responsabilidad sobre las operaciones.** No se puede determinar qué empleado registró cada venta, compra, consignación o intercambio.

### Pregunta que guía el proyecto

> ¿Cómo dar identidad y trazabilidad completa a cada ejemplar físico —desde que entra a la tienda hasta que sale— de modo que la valoración, la liquidación de comisiones y la auditoría de las operaciones sean confiables y verificables?

## 3. Justificación

La problemática exige separar tres niveles de información que hoy están mezclados, lo que la convierte en un caso de normalización natural y no forzado: las dependencias transitivas están claramente presentes en el esquema original y su descomposición hasta **Tercera Forma Normal (3FN)** se justifica por sí misma. Adicionalmente:

- Requiere un modelo Entidad-Relación **complejo**, con más de 8 entidades y los tres tipos de relación (1:1, 1:N y N:M).
- Involucra transacciones con valor económico, por lo que las consultas complejas (uniones, proyecciones y selecciones) responden preguntas de negocio reales y no ejercicios artificiales.
- Es un dominio acotado y verificable: los datos de catálogo de *Magic: The Gathering* son públicos y permiten poblar la base de datos con información realista.

## 4. Objetivos

### Objetivo general

Diseñar e implementar una base de datos relacional, normalizada hasta 3FN, que soporte la gestión de inventario, consignaciones e intercambios de una tienda de cartas coleccionables, accesible desde una aplicación web de demostración desplegada sobre una máquina virtual Linux.

### Objetivos específicos

1. Construir el modelo conceptual del dominio mediante un diagrama Entidad-Relación en notación Peter-Chen, incluyendo su versión extendida cuando aplique.
2. Derivar el modelo lógico en notación Crow's Foot (Barker) y normalizarlo hasta la Tercera Forma Normal, documentando las dependencias funcionales eliminadas.
3. Elaborar el diccionario de datos completo del modelo.
4. Formular y documentar un mínimo de 50 sentencias de álgebra relacional que resuelvan consultas propias del negocio.
5. Configurar una máquina virtual con una distribución Linux e instalar sobre ella un RDBMS, justificando ambas decisiones técnicas.
6. Implementar el modelo físico mediante scripts de creación de objetos y de inserción de registros.
7. Desarrollar una aplicación web de demostración conectada a la base de datos.
8. Gestionar el desarrollo del proyecto en un repositorio de GitHub, con contribuciones de todos los integrantes.

## 5. Alcance

### Dentro del alcance

- **Catálogo de cartas** con separación entre la carta conceptual y cada una de sus impresiones (edición, número, rareza, artista, idioma, acabado foil). Colores y tipos se modelan como entidades independientes.
- **Inventario por ejemplar físico**, con condición, propietario, estado y ubicación de cada copia.
- **Valoración**: historial de precios de referencia por impresión y fecha, con factor de ajuste según la condición física del ejemplar.
- **Compras a proveedores** para abastecer el inventario propio de la tienda.
- **Ventas** a clientes, registrando el ejemplar concreto vendido y su valor al momento de la transacción.
- **Consignaciones**: contrato con un cliente consignante, porcentaje de comisión, plazo, ejemplares incluidos y liquidación del pago correspondiente cuando se venden.
- **Intercambios entre clientes** avalados por la tienda, registrando los ejemplares aportados por cada lado, la valoración de cada lado y la comisión cobrada.
- **Empleados, roles y auditoría**: cada venta, compra, consignación, liquidación e intercambio queda atribuida al empleado que la registró.

### Fuera del alcance

- Organización de torneos, mazos y estadísticas competitivas.
- Venta de producto sellado (sobres, cajas de edición) y reservas o apartados con abono parcial.
- Compra directa de cartas a clientes por parte de la tienda (*buylist*) y crédito de tienda.
- Pasarelas de pago, facturación electrónica y logística de envíos.
- Integración en tiempo real con servicios externos de precios de mercado; los precios de referencia se cargan manualmente.

## 6. Requerimientos funcionales principales

| # | Requerimiento |
|---|---|
| RF-01 | Registrar y consultar el catálogo de cartas, con sus impresiones, colores, tipos, ediciones, rarezas, artistas e idiomas. |
| RF-02 | Registrar cada ejemplar físico que ingresa al inventario, indicando su impresión, condición, propietario y estado. |
| RF-03 | Registrar compras a proveedores y dar de alta los ejemplares adquiridos. |
| RF-04 | Registrar ventas de ejemplares a clientes, dejando constancia del valor de la transacción y del empleado responsable. |
| RF-05 | Mantener el historial de precios de referencia por impresión y fecha. |
| RF-06 | Calcular el precio sugerido de un ejemplar a partir del precio de referencia vigente y el factor de ajuste de su condición. |
| RF-07 | Registrar contratos de consignación con su comisión, plazo y ejemplares asociados. |
| RF-08 | Generar la liquidación a pagar al consignante por los ejemplares suyos que se hayan vendido. |
| RF-09 | Registrar intercambios entre dos clientes, con los ejemplares aportados por cada lado, la valoración de cada lado y la comisión de la tienda. |
| RF-10 | Impedir que un ejemplar comprometido (vendido, intercambiado o devuelto) vuelva a transarse. |
| RF-11 | Administrar clientes, proveedores, empleados y roles. |
| RF-12 | Consultar la trazabilidad completa de un ejemplar: cómo ingresó, quién lo poseyó y cómo salió. |
| RF-13 | Generar consultas de negocio: rotación por edición, márgenes por venta, valor del inventario propio frente al consignado, saldos pendientes con consignantes y operaciones por empleado. |
| RF-14 | Exponer la consulta del catálogo y la disponibilidad del inventario en una aplicación web de demostración. |

## 7. Modelo preliminar de entidades

El modelo comprende 24 entidades distribuidas en cuatro módulos, más dos tablas derivadas de las relaciones muchos a muchos:

- **Catálogo:** `Carta`, `Color`, `Tipo`, `Edicion`, `Impresion`, `Rareza`, `Artista`, `Idioma`
- **Inventario:** `Ejemplar`, `Condicion`, `PrecioReferencia`
- **Personas:** `Cliente`, `Empleado`, `Rol`, `CuentaUsuario`, `Proveedor`
- **Operaciones:** `Compra` / `DetalleCompra`, `Venta` / `DetalleVenta`, `ContratoConsignacion`, `Liquidacion`, `Intercambio` / `DetalleIntercambio`
- **Tablas de relación N:M:** `CartaColor`, `CartaTipo`

### Tipos de relación presentes

| Tipo | Relación |
|---|---|
| **1:1** | `Empleado` ↔ `CuentaUsuario` |
| **1:N** | `Edicion` → `Impresion`; `Cliente` → `ContratoConsignacion`; `Impresion` → `Ejemplar` |
| **N:M** | `Carta` ↔ `Color`; `Carta` ↔ `Tipo`; `Intercambio` ↔ `Ejemplar` |

La especificación de atributos, tipos de dato, claves primarias y restricciones de integridad se encuentra en el documento `DICCIONARIO_DATOS.md`.

## 8. Viabilidad

- **Datos:** el catálogo de *Magic: The Gathering* es información pública, lo que permite poblar la base con datos realistas sin depender de terceros.
- **Herramientas:** el modelado se realiza con Oracle SQL Data Modeler o MySQL Workbench; el despliegue, sobre una distribución Linux en máquina virtual con un RDBMS de código abierto o gratuito.
- **Equipo:** el alcance está dividido en módulos (catálogo, inventario y valoración, operaciones comerciales) que permiten repartir el trabajo entre los tres integrantes con contribuciones verificables en GitHub.

---

_Documento sujeto a aprobación del docente. Una vez aprobado, sirve como guía principal para el desarrollo de las Fases 1 y 2 del proyecto._
