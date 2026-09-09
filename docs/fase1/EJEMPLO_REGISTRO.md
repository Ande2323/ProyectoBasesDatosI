# Ejemplo de Registro — Recorrido completo de una carta

**Proyecto:** Sistema de gestión de inventario, consignaciones e intercambios para una tienda de cartas coleccionables de *Magic: The Gathering*
**Universidad El Bosque** · Bases de Datos 1
**Fecha:** 8 de septiembre de 2026

Este documento sigue una carta —**Lightning Bolt**— desde su registro en el catálogo hasta su venta, mostrando cada fila que se crea en cada tabla del modelo. Sirve como guía para escribir los scripts de inserción de la Fase 2 y como material de apoyo para la sustentación.

> **Nota:** los valores de número de colección, rareza y artista son **ilustrativos**. Al poblar la base de datos deben verificarse contra una fuente confiable como Scryfall.

---

## Paso 1 — El concepto de la carta

### `Carta`

| `nombre` (PK) | `texto_oraculo` | `coste_mana` | `valor_mana` | `poder` | `resistencia` | `lealtad` |
|---|---|---|---|---|---|---|
| Lightning Bolt | Lightning Bolt deals 3 damage to any target. | `{R}` | 1 | NULL | NULL | NULL |

**Una sola fila**, aunque la carta se haya reimpreso decenas de veces. Aquí vive el texto de reglas, escrito una única vez. Si Wizards of the Coast publica una errata, se corrige en un solo lugar.

---

## Paso 2 — Sus colores y tipos

Estos catálogos ya existen en la base; no se crean por esta carta.

### `Color` *(6 filas en total)*

| `codigo` (PK) | `nombre` | `orden` |
|---|---|---|
| R | Rojo | 4 |

### `Tipo`

| `nombre` (PK) | `categoria` |
|---|---|
| Instantáneo | TIPO |

Y se crean los enlaces de las relaciones N:M:

### `CartaColor`

| `nombre_carta` (PK) | `codigo_color` (PK) |
|---|---|
| Lightning Bolt | R |

### `CartaTipo`

| `nombre_carta` (PK) | `nombre_tipo` (PK) |
|---|---|
| Lightning Bolt | Instantáneo |

> **Comparación útil:** una carta multicolor como *Niv-Mizzet, Parun* generaría **dos** filas en `CartaColor` (U y R) y **cuatro** en `CartaTipo` (Legendaria, Criatura, Dragón, Mago). Ahí se ve por qué el color no puede ser una columna dentro de `Carta`: sería un atributo multivaluado y violaría la 1FN.

---

## Paso 3 — Las ediciones donde se imprimió

### `Edicion`

| `codigo` (PK) | `nombre` | `fecha_lanzamiento` | `total_cartas` | `tipo_edicion` |
|---|---|---|---|---|
| M11 | Magic 2011 | 2010-07-16 | 249 | Núcleo |
| 2XM | Double Masters | 2020-08-07 | 384 | Suplementaria |

### `Artista` *(única tabla de catálogo con clave artificial)*

| `id_artista` (PK) | `nombre` | `nacionalidad` |
|---|---|---|
| 1 | Christopher Moeller | Estadounidense |
| 2 | Christopher Rush | Estadounidense |

---

## Paso 4 — Las impresiones

Aquí está el corazón del modelo.

### `Impresion`

| `codigo_edicion` | `numero_coleccion` | `codigo_idioma` | `es_foil` | `nombre_carta` | `codigo_rareza` | `id_artista` |
|---|---|---|---|---|---|---|
| M11 | 149 | eng | N | Lightning Bolt | C | 1 |
| M11 | 149 | spa | N | Lightning Bolt | C | 1 |
| 2XM | 129 | eng | N | Lightning Bolt | R | 2 |
| 2XM | 129 | eng | S | Lightning Bolt | R | 2 |

*(las primeras cuatro columnas forman la clave primaria)*

**Cuatro filas para una sola carta.** Tres observaciones importantes:

1. Las filas 1 y 2 comparten el mismo número de colección `149`. **No hay conflicto** porque `codigo_idioma` forma parte de la clave primaria: son la versión en inglés y la versión en español.
2. Las filas 3 y 4 comparten edición, número **e** idioma, y se distinguen únicamente por `es_foil`. La versión foil y la no-foil son productos distintos con precios muy diferentes.
3. La rareza cambia entre ediciones: **común** en M11, **rara** en 2XM. Esta es la razón por la cual la rareza vive en `Impresion` y no en `Carta`.

---

## Paso 5 — Los precios de referencia

### `PrecioReferencia` *(la otra tabla con clave artificial)*

| `id_precio` (PK) | `codigo_edicion` | `numero_coleccion` | `codigo_idioma` | `es_foil` | `fecha` | `valor` | `fuente` |
|---|---|---|---|---|---|---|---|
| 1 | 2XM | 129 | eng | S | 2026-09-01 | 78000.00 | Scryfall |
| 2 | 2XM | 129 | eng | S | 2026-09-08 | 82000.00 | Scryfall |
| 3 | 2XM | 129 | eng | N | 2026-09-08 | 45000.00 | Scryfall |
| 4 | M11 | 149 | eng | N | 2026-09-08 | 12000.00 | Scryfall |

La versión foil vale casi el doble que la no-foil, y su precio subió de 78.000 a 82.000 en una semana. Ese historial es exactamente lo que hoy se pierde al sobrescribir el precio en la hoja de cálculo.

---

## Paso 6 — Los ejemplares físicos

### `Ejemplar`

| `codigo_etiqueta` (PK) | `codigo_edicion` | `numero_coleccion` | `codigo_idioma` | `es_foil` | `codigo_condicion` | `numero_contrato` | `estado` | `ubicacion` | `fecha_ingreso` |
|---|---|---|---|---|---|---|---|---|---|
| ETQ-00417 | 2XM | 129 | eng | S | NM | **NULL** | DISPONIBLE | Vitrina 1 | 2026-08-12 |
| ETQ-00418 | 2XM | 129 | eng | S | LP | **CON-2026-014** | DISPONIBLE | Vitrina 1 | 2026-09-02 |
| ETQ-00419 | M11 | 149 | eng | N | LP | NULL | VENDIDO | — | 2026-07-30 |

**Este es el paso que la hoja de cálculo no puede dar.** Las dos primeras copias son *la misma impresión exacta* —2XM, número 129, inglés, foil— y aun así son objetos distintos:

- **ETQ-00417** es propiedad de la tienda (`numero_contrato` nulo) y está en condición Near Mint.
- **ETQ-00418** pertenece a un consignante (contrato CON-2026-014) y está Lightly Played.

Sin identidad por ejemplar, el vendedor no puede saber cuál de las dos está tomando de la vitrina, que es precisamente la consecuencia #1 del planteamiento del problema.

### Cálculo del precio sugerido (RF-06)

Con el precio de referencia vigente (82.000) y el factor de ajuste de cada condición:

| Ejemplar | Precio referencia | Condición | Factor | **Precio sugerido** |
|---|---|---|---|---|
| ETQ-00417 | 82.000 | NM | 1.00 | **82.000** |
| ETQ-00418 | 82.000 | LP | 0.85 | **69.700** |

Misma impresión, 12.300 pesos de diferencia. Esa es la decisión que hoy se toma "a ojo".

---

## Paso 7 — Las operaciones

### Cómo entró ETQ-00419

#### `Proveedor`

| `nit` (PK) | `razon_social` | `ciudad` |
|---|---|---|
| 900123456-7 | Distribuidora Cartas Andinas SAS | Bogotá |

#### `Compra`

| `nit_proveedor` (PK) | `numero_factura` (PK) | `tipo_documento_emp` | `numero_documento_emp` | `fecha_compra` | `total` |
|---|---|---|---|---|---|
| 900123456-7 | FAC-8891 | CC | 79654321 | 2026-07-30 | 340000.00 |

#### `DetalleCompra`

| `codigo_etiqueta` (PK) | `nit_proveedor` | `numero_factura` | `costo_unitario` |
|---|---|---|---|
| ETQ-00419 | 900123456-7 | FAC-8891 | 7500.00 |

### Cómo salió ETQ-00419

#### `Venta`

| `numero_venta` (PK) | `tipo_documento_cli` | `numero_documento_cli` | `tipo_documento_emp` | `numero_documento_emp` | `fecha_venta` | `medio_pago` | `total` | `estado` |
|---|---|---|---|---|---|---|---|---|
| V-2026-0331 | CC | 1032456789 | CC | 79654321 | 2026-08-20 09:14:00 | EFECTIVO | 10200.00 | PAGADA |

#### `DetalleVenta`

| `codigo_etiqueta` (PK) | `numero_venta` | `precio_venta` | `descuento` |
|---|---|---|---|
| ETQ-00419 | V-2026-0331 | 10200.00 | 0.00 |

> **Margen calculable:** costó 7.500 y se vendió en 10.200, lo que deja un margen de 2.700 pesos (36%). Este dato es imposible de obtener con el registro actual.

### El contrato de consignación de ETQ-00418

#### `ContratoConsignacion`

| `numero_contrato` (PK) | `tipo_documento_cli` | `numero_documento_cli` | `fecha_inicio` | `fecha_limite` | `porcentaje_comision` | `estado` |
|---|---|---|---|---|---|---|
| CON-2026-014 | CC | 1032456789 | 2026-09-02 | 2026-12-02 | 20.00 | VIGENTE |

Si ETQ-00418 se vende en 69.700, la liquidación al consignante sería: comisión de 13.940 (20%) y monto neto a pagar de 55.760.

---

## La consulta que lo reconstruye todo

El temor razonable al dividir la información en tantas tablas es "¿y ahora cómo veo la carta completa?". La respuesta es un `JOIN`:

```sql
SELECT  c.nombre,
        ed.nombre        AS edicion,
        i.numero_coleccion,
        r.nombre         AS rareza,
        a.nombre         AS artista,
        i.es_foil,
        cd.nombre        AS condicion,
        e.codigo_etiqueta,
        e.estado
FROM       Ejemplar  e
JOIN       Impresion i  ON  e.codigo_edicion    = i.codigo_edicion
                        AND e.numero_coleccion  = i.numero_coleccion
                        AND e.codigo_idioma     = i.codigo_idioma
                        AND e.es_foil           = i.es_foil
JOIN       Carta     c  ON  i.nombre_carta      = c.nombre
JOIN       Edicion   ed ON  i.codigo_edicion    = ed.codigo
JOIN       Rareza    r  ON  i.codigo_rareza     = r.codigo
JOIN       Artista   a  ON  i.id_artista        = a.id_artista
JOIN       Condicion cd ON  e.codigo_condicion  = cd.codigo
WHERE      e.codigo_etiqueta = 'ETQ-00417';
```

**Resultado:**

| nombre | edicion | numero | rareza | artista | foil | condicion | etiqueta | estado |
|---|---|---|---|---|---|---|---|---|
| Lightning Bolt | Double Masters | 129 | Rara | Christopher Rush | S | Near Mint | ETQ-00417 | DISPONIBLE |

Nada se perdió al normalizar: la información se reconstruye completa. Obsérvese que el `JOIN` con `Impresion` requiere **cuatro columnas**, que es el costo práctico de la clave primaria compuesta.

---

## El antes y el después

### Como está hoy en la hoja de cálculo

| Carta | Set | Rareza | Cond | Precio | Dueño |
|---|---|---|---|---|---|
| Lightning Bolt | 2XM | Rara | NM | 82000 | ? |
| Lightning Bolt | 2XM | Rara | LP | 69700 | ? |
| Lightning Bolt | M11 | Común | LP | 10200 | ? |

El texto de la carta se repetiría en las tres filas, no hay forma de distinguir cuál copia es cuál, y el dueño es una casilla que alguien recuerda o no.

### Con el modelo relacional

| Tabla | Filas creadas |
|---|---|
| `Carta` | 1 |
| `CartaColor` | 1 |
| `CartaTipo` | 1 |
| `Edicion` | 2 |
| `Artista` | 2 |
| `Impresion` | 4 |
| `PrecioReferencia` | 4 |
| `Ejemplar` | 3 |

Cada copia física con identidad, dueño, condición, valoración y trazabilidad propia.

---

## Resumen del recorrido

```
Carta (1)
  └── Impresion (4)          ← misma carta, distintas ediciones/idiomas/acabados
        ├── PrecioReferencia (4)   ← historial de valor por impresión
        └── Ejemplar (3)           ← las copias físicas en la tienda
              ├── ETQ-00417  propia,     NM,  DISPONIBLE
              ├── ETQ-00418  consignada, LP,  DISPONIBLE  → ContratoConsignacion
              └── ETQ-00419  propia,     LP,  VENDIDO     → Compra y Venta
```
