# Modelo Entidad-Relación — Notación Peter-Chen

**Proyecto:** Sistema de gestión de inventario, consignaciones e intercambios para una tienda de cartas coleccionables de *Magic: The Gathering*
**Universidad El Bosque** · Bases de Datos 1
**Versión:** 1.0

Documento de apoyo al diagrama `modelos/conceptual/ER_Peter_Chen.drawio`. El diagrama presenta entidades, relaciones, cardinalidades y participación; los atributos se especifican en las tablas de la sección 3 de este documento.

---

## 1. Notación empleada

| Símbolo | Significado |
|---|---|
| Rectángulo simple | Entidad fuerte |
| Rectángulo doble | Entidad débil |
| Rombo simple | Relación |
| Rombo doble | Relación identificadora (conecta una entidad débil con su propietaria) |
| Línea simple | Participación parcial (opcional) |
| Línea doble | Participación total (obligatoria) |
| `1`, `N`, `M` | Cardinalidad en cada extremo |

### Clasificación de atributos usada en las tablas

| Tipo | Descripción | Representación Chen |
|---|---|---|
| **Identificador** | Determina unívocamente la instancia | Elipse con texto subrayado |
| **Discriminante** | Clave parcial de una entidad débil | Elipse con texto subrayado punteado |
| **Simple** | Valor atómico único | Elipse |
| **Compuesto** | Se descompone en subatributos | Elipse con elipses hijas |
| **Multivaluado** | Admite varios valores simultáneos | Elipse doble |
| **Derivado** | Se calcula a partir de otros datos, no se almacena | Elipse punteada |

---

## 2. Inventario de entidades

El modelo conceptual tiene **21 entidades**: 18 fuertes y 3 débiles.

### 2.1 Entidades fuertes

| # | Entidad | Módulo | Descripción |
|---|---|---|---|
| 1 | `Carta` | Catálogo | La carta como concepto de reglas, independiente de sus impresiones |
| 2 | `Color` | Catálogo | Los cinco colores del juego más incoloro |
| 3 | `Tipo` | Catálogo | Supertipos, tipos y subtipos de carta |
| 4 | `Edicion` | Catálogo | Colección o set en que se publicó un grupo de cartas |
| 5 | `Rareza` | Catálogo | Nivel de rareza de una impresión |
| 6 | `Artista` | Catálogo | Ilustrador de la impresión |
| 7 | `Idioma` | Catálogo | Idioma de impresión |
| 8 | `Condicion` | Inventario | Estado físico de conservación con su factor de ajuste |
| 9 | `Ejemplar` | Inventario | Copia física individual. Entidad central del modelo |
| 10 | `Cliente` | Personas | Comprador, consignante o participante en intercambios |
| 11 | `Empleado` | Personas | Quien registra las operaciones |
| 12 | `Rol` | Personas | Perfil de permisos del empleado |
| 13 | `CuentaUsuario` | Personas | Credenciales de acceso al sistema |
| 14 | `Proveedor` | Personas | Distribuidor al que la tienda compra inventario |
| 15 | `Venta` | Operaciones | Venta de ejemplares a un cliente |
| 16 | `ContratoConsignacion` | Operaciones | Acuerdo para vender cartas de un cliente a comisión |
| 17 | `Liquidacion` | Operaciones | Pago al consignante por sus cartas vendidas |
| 18 | `Intercambio` | Operaciones | Trade entre dos clientes avalado por la tienda |

### 2.2 Entidades débiles

| # | Entidad | Propietaria(s) | Discriminante | Por qué es débil |
|---|---|---|---|---|
| 19 | `Impresion` | `Edicion` e `Idioma` | `numero_coleccion`, `es_foil` | El número de colección solo identifica dentro de una edición y un idioma |
| 20 | `PrecioReferencia` | `Impresion` | `fecha` | Una fecha no identifica un precio; identifica el precio *de una impresión concreta* |
| 21 | `Compra` | `Proveedor` | `numero_factura` | Dos proveedores distintos pueden emitir su factura 001 |

---

## 3. Atributos por entidad

### 3.1 `Carta`

| Atributo | Tipo Chen | Observación |
|---|---|---|
| `nombre` | **Identificador** | Nombre oficial de la carta |
| `texto_oraculo` | Simple | Nulo en tierras básicas |
| `coste_mana` | Simple | Notación de símbolos, ej. `{1}{U}{U}` |
| `valor_mana` | **Derivado** | Se calcula sumando los símbolos de `coste_mana` |
| `poder` | Simple | Solo criaturas |
| `resistencia` | Simple | Solo criaturas |
| `lealtad` | Simple | Solo planeswalkers |

> Los colores y los tipos serían atributos **multivaluados** de `Carta`. Se modelan como entidades independientes relacionadas M:N, que es el tratamiento correcto para eliminar la multivaluación y cumplir 1FN.

### 3.2 `Color`

| Atributo | Tipo Chen | Observación |
|---|---|---|
| `codigo` | **Identificador** | `W`, `U`, `B`, `R`, `G`, `C` |
| `nombre` | Simple | Blanco, Azul, Negro, Rojo, Verde, Incoloro |
| `orden` | Simple | Orden canónico WUBRG |

### 3.3 `Tipo`

| Atributo | Tipo Chen | Observación |
|---|---|---|
| `nombre` | **Identificador** | Criatura, Instantáneo, Humano, Legendaria… |
| `categoria` | Simple | SUPERTIPO, TIPO o SUBTIPO |

### 3.4 `Edicion`

| Atributo | Tipo Chen | Observación |
|---|---|---|
| `codigo` | **Identificador** | Va impreso en la carta, ej. `MH3` |
| `nombre` | Simple | |
| `fecha_lanzamiento` | Simple | |
| `total_cartas` | Simple | |
| `tipo_edicion` | Simple | Expansión, Núcleo, Suplementaria |

### 3.5 `Rareza`

| Atributo | Tipo Chen | Observación |
|---|---|---|
| `codigo` | **Identificador** | `C`, `U`, `R`, `M`, `S` |
| `nombre` | Simple | |
| `orden` | Simple | Jerarquía para ordenar |

### 3.6 `Artista`

| Atributo | Tipo Chen | Observación |
|---|---|---|
| `id_artista` | **Identificador** | Clave artificial: no existe registro oficial de ilustradores |
| `nombre` | Simple | Nombre acreditado |
| `nacionalidad` | Simple | |

### 3.7 `Idioma`

| Atributo | Tipo Chen | Observación |
|---|---|---|
| `codigo` | **Identificador** | ISO 639-2: `eng`, `spa`, `jpn` |
| `nombre` | Simple | |

### 3.8 `Impresion` — entidad débil

| Atributo | Tipo Chen | Observación |
|---|---|---|
| `numero_coleccion` | **Discriminante** | Número impreso. Admite valores como `112a` |
| `es_foil` | **Discriminante** | Completa la identificación dentro de la edición e idioma |
| `variante_ilustracion` | Simple | Borderless, Extended Art, Showcase |
| `url_imagen` | Simple | Recurso para la aplicación web |

> Identificación completa: `Edicion.codigo` + `Idioma.codigo` + `numero_coleccion` + `es_foil`.

### 3.9 `Condicion`

| Atributo | Tipo Chen | Observación |
|---|---|---|
| `codigo` | **Identificador** | `NM`, `LP`, `MP`, `HP`, `DMG` |
| `nombre` | Simple | |
| `descripcion` | Simple | |
| `factor_ajuste` | Simple | Multiplicador sobre el precio de referencia |

### 3.10 `PrecioReferencia` — entidad débil

| Atributo | Tipo Chen | Observación |
|---|---|---|
| `fecha` | **Discriminante** | Día de la cotización |
| `valor` | Simple | Precio en pesos |
| `fuente` | Simple | Scryfall, TCGPlayer, tasación interna |

### 3.11 `Ejemplar`

| Atributo | Tipo Chen | Observación |
|---|---|---|
| `codigo_etiqueta` | **Identificador** | Código físico que la tienda adhiere a la funda |
| `estado` | Simple | DISPONIBLE, RESERVADO, VENDIDO, INTERCAMBIADO, DEVUELTO |
| `ubicacion` | Simple | Vitrina, caja, binder |
| `fecha_ingreso` | Simple | |
| `observaciones` | Simple | |
| `precio_sugerido` | **Derivado** | Precio de referencia vigente × factor de ajuste de la condición |

### 3.12 `Cliente`

| Atributo | Tipo Chen | Observación |
|---|---|---|
| `documento` | **Identificador compuesto** | Se descompone en `tipo_documento` y `numero_documento` |
| `nombre_completo` | Compuesto | Se descompone en `nombres` y `apellidos` |
| `telefono` | Simple | |
| `correo` | Simple | |
| `fecha_registro` | Simple | |

### 3.13 `Empleado`

| Atributo | Tipo Chen | Observación |
|---|---|---|
| `documento` | **Identificador compuesto** | `tipo_documento` + `numero_documento` |
| `nombre_completo` | Compuesto | `nombres` + `apellidos` |
| `cargo` | Simple | |
| `fecha_vinculacion` | Simple | |
| `estado` | Simple | Activo / Inactivo |
| `telefono` | Simple | |

### 3.14 `Rol`

| Atributo | Tipo Chen | Observación |
|---|---|---|
| `nombre` | **Identificador** | Administrador, Vendedor, Tasador |
| `descripcion` | Simple | |
| `nivel_permiso` | Simple | |

### 3.15 `CuentaUsuario`

| Atributo | Tipo Chen | Observación |
|---|---|---|
| `nombre_usuario` | **Identificador** | Login del sistema |
| `contrasena_hash` | Simple | Nunca en texto plano |
| `ultimo_acceso` | Simple | |
| `estado` | Simple | Activa / Bloqueada |

### 3.16 `Proveedor`

| Atributo | Tipo Chen | Observación |
|---|---|---|
| `nit` | **Identificador** | Documento legal |
| `razon_social` | Simple | |
| `nombre_contacto` | Simple | |
| `telefono` | Simple | |
| `correo` | Simple | |
| `ciudad` | Simple | |
| `direccion` | Simple | |

### 3.17 `Compra` — entidad débil

| Atributo | Tipo Chen | Observación |
|---|---|---|
| `numero_factura` | **Discriminante** | Factura emitida por el proveedor |
| `fecha_compra` | Simple | |
| `total` | **Derivado** | Suma de los costos unitarios de los ejemplares adquiridos |

### 3.18 `Venta`

| Atributo | Tipo Chen | Observación |
|---|---|---|
| `numero_venta` | **Identificador** | Consecutivo de la tienda |
| `fecha_venta` | Simple | |
| `medio_pago` | Simple | Efectivo, tarjeta, transferencia |
| `total` | **Derivado** | Suma de precios de venta menos descuentos |
| `estado` | Simple | Pagada / Anulada |

### 3.19 `ContratoConsignacion`

| Atributo | Tipo Chen | Observación |
|---|---|---|
| `numero_contrato` | **Identificador** | Consecutivo del contrato |
| `fecha_inicio` | Simple | |
| `fecha_limite` | Simple | Vencimiento del plazo |
| `porcentaje_comision` | Simple | |
| `estado` | Simple | Vigente / Liquidado / Vencido |

### 3.20 `Liquidacion`

| Atributo | Tipo Chen | Observación |
|---|---|---|
| `numero_liquidacion` | **Identificador** | Comprobante emitido por la tienda |
| `fecha_liquidacion` | Simple | |
| `monto_bruto` | Simple | Total vendido del contrato |
| `monto_comision` | Simple | Retención de la tienda |
| `monto_neto` | **Derivado** | `monto_bruto − monto_comision` |
| `estado` | Simple | Pendiente / Pagada |

### 3.21 `Intercambio`

| Atributo | Tipo Chen | Observación |
|---|---|---|
| `numero_intercambio` | **Identificador** | Consecutivo del trade |
| `fecha` | Simple | |
| `valor_lado_a` | **Derivado** | Suma de `valor_tasado` de los ejemplares del lado A |
| `valor_lado_b` | **Derivado** | Suma de `valor_tasado` de los ejemplares del lado B |
| `comision` | Simple | Cobro de la tienda |
| `estado` | Simple | Cerrado / Anulado |

---

## 4. Relaciones

28 relaciones en total. La columna **Participación** indica si cada extremo es total (obligatorio) o parcial (opcional).

### 4.1 Módulo Catálogo

| # | Relación | Entidades | Card. | Participación | Atributos |
|---|---|---|---|---|---|
| R-01 | **posee** | `Carta` — `Color` | M:N | Carta total, Color parcial | — |
| R-02 | **es de tipo** | `Carta` — `Tipo` | M:N | Carta total, Tipo parcial | — |
| R-03 | **se imprime en** | `Carta` — `Impresion` | 1:N | Carta total, Impresión total | — |
| R-04 | **contiene** *(identificadora)* | `Edicion` — `Impresion` | 1:N | Edición total, Impresión total | — |
| R-05 | **impresa en** *(identificadora)* | `Idioma` — `Impresion` | 1:N | Idioma parcial, Impresión total | — |
| R-06 | **clasifica** | `Rareza` — `Impresion` | 1:N | Rareza parcial, Impresión total | — |
| R-07 | **ilustra** | `Artista` — `Impresion` | 1:N | Artista total, Impresión total | — |

### 4.2 Módulo Inventario

| # | Relación | Entidades | Card. | Participación | Atributos |
|---|---|---|---|---|---|
| R-08 | **se cotiza** *(identificadora)* | `Impresion` — `PrecioReferencia` | 1:N | Impresión parcial, Precio total | — |
| R-09 | **corresponde a** | `Impresion` — `Ejemplar` | 1:N | Impresión parcial, Ejemplar total | — |
| R-10 | **califica** | `Condicion` — `Ejemplar` | 1:N | Condición parcial, Ejemplar total | — |

### 4.3 Módulo Personas

| # | Relación | Entidades | Card. | Participación | Atributos |
|---|---|---|---|---|---|
| R-11 | **desempeña** | `Rol` — `Empleado` | 1:N | Rol parcial, Empleado total | — |
| R-12 | **accede con** | `Empleado` — `CuentaUsuario` | 1:1 | Empleado parcial, Cuenta total | — |
| R-28 | **supervisa** *(recursiva)* | `Empleado` — `Empleado` | M:N | Parcial en ambos roles | `fecha_inicio`, `fecha_fin` |

> R-12 es la única relación 1:1 del modelo. La participación parcial del lado `Empleado` refleja que no todo empleado necesita cuenta de acceso.

> R-28 es la única relación **recursiva** del modelo. Conecta `Empleado` consigo misma con los roles `supervisor` y `supervisado`, que deben etiquetarse sobre las líneas del diagrama. Es M:N porque un empleado puede haber sido supervisado por distintas personas en periodos distintos, y un supervisor tiene varios supervisados a la vez; los atributos de periodo son los que conservan esa historia. Un `fecha_fin` nula indica que la supervisión sigue vigente. La participación es parcial en ambos extremos porque el administrador no tiene supervisor y no todo empleado supervisa a alguien.

### 4.4 Módulo Operaciones — Compras

| # | Relación | Entidades | Card. | Participación | Atributos |
|---|---|---|---|---|---|
| R-13 | **emite** *(identificadora)* | `Proveedor` — `Compra` | 1:N | Proveedor parcial, Compra total | — |
| R-14 | **registra compra** | `Empleado` — `Compra` | 1:N | Empleado parcial, Compra total | — |
| R-15 | **adquiere** | `Compra` — `Ejemplar` | 1:N | Compra total, Ejemplar parcial | `costo_unitario` |

> En R-15 el ejemplar participa parcialmente porque las cartas en consignación no se compran: ingresan por contrato.

### 4.5 Módulo Operaciones — Ventas

| # | Relación | Entidades | Card. | Participación | Atributos |
|---|---|---|---|---|---|
| R-16 | **realiza** | `Cliente` — `Venta` | 1:N | Cliente parcial, Venta total | — |
| R-17 | **atiende** | `Empleado` — `Venta` | 1:N | Empleado parcial, Venta total | — |
| R-18 | **incluye** | `Venta` — `Ejemplar` | 1:N | Venta total, Ejemplar parcial | `precio_venta`, `descuento` |

### 4.6 Módulo Operaciones — Consignación

| # | Relación | Entidades | Card. | Participación | Atributos |
|---|---|---|---|---|---|
| R-19 | **consigna** | `Cliente` — `ContratoConsignacion` | 1:N | Cliente parcial, Contrato total | — |
| R-20 | **recibe contrato** | `Empleado` — `ContratoConsignacion` | 1:N | Empleado parcial, Contrato total | — |
| R-21 | **ampara** | `ContratoConsignacion` — `Ejemplar` | 1:N | Contrato total, Ejemplar parcial | — |
| R-22 | **genera** | `ContratoConsignacion` — `Liquidacion` | 1:N | Contrato parcial, Liquidación total | — |
| R-23 | **elabora** | `Empleado` — `Liquidacion` | 1:N | Empleado parcial, Liquidación total | — |

> R-21 es la relación que da propiedad al ejemplar: si un ejemplar no participa en ella, pertenece a la tienda.

### 4.7 Módulo Operaciones — Intercambios

| # | Relación | Entidades | Card. | Participación | Atributos |
|---|---|---|---|---|---|
| R-24 | **participa como A** | `Cliente` — `Intercambio` | 1:N | Cliente parcial, Intercambio total | — |
| R-25 | **participa como B** | `Cliente` — `Intercambio` | 1:N | Cliente parcial, Intercambio total | — |
| R-26 | **avala** | `Empleado` — `Intercambio` | 1:N | Empleado parcial, Intercambio total | — |
| R-27 | **involucra** | `Intercambio` — `Ejemplar` | M:N | Intercambio total, Ejemplar parcial | `lado`, `valor_tasado` |

> R-24 y R-25 son dos relaciones distintas entre las **mismas** entidades, diferenciadas por el **rol** que desempeña el cliente. En el diagrama deben etiquetarse los roles sobre las líneas.

### 4.8 Resumen de cardinalidades

| Cardinalidad | Cantidad | Ejemplos |
|---|---|---|
| **1:1** | 1 | R-12 `Empleado` — `CuentaUsuario` |
| **1:N** | 23 | R-04 `Edicion` — `Impresion`, R-18 `Venta` — `Ejemplar` |
| **M:N** | 4 | R-01 `Carta` — `Color`, R-02 `Carta` — `Tipo`, R-27 `Intercambio` — `Ejemplar`, R-28 `Empleado` — `Empleado` |

Los tres tipos exigidos por la asignatura están presentes.

---

## 5. Derivación al modelo relacional

El modelo conceptual tiene **21 entidades** y el modelo relacional tiene **27 tablas**. La diferencia se explica así:

| Origen | Tablas generadas |
|---|---|
| Las 21 entidades | 21 tablas |
| R-01 `posee` (M:N) | `CartaColor` |
| R-02 `es de tipo` (M:N) | `CartaTipo` |
| R-27 `involucra` (M:N) | `DetalleIntercambio` |
| R-28 `supervisa` (M:N recursiva) | `Supervision`, con dos claves foráneas hacia `Empleado` |
| R-15 `adquiere` (1:N con atributo y participación parcial) | `DetalleCompra` |
| R-18 `incluye` (1:N con atributos y participación parcial) | `DetalleVenta` |
| | **Total: 27 tablas** |

### Por qué R-15 y R-18 generan tabla

Una relación 1:N normalmente se resuelve poniendo la clave foránea en el lado N, sin crear tabla nueva. Aquí se creó tabla por dos razones:

1. **La participación del ejemplar es parcial.** Un ejemplar consignado nunca se compró, y uno aún no vendido no tiene precio de venta. Poner esas columnas dentro de `Ejemplar` llenaría la tabla de nulos.
2. **La relación tiene atributos propios** (`costo_unitario`, `precio_venta`, `descuento`) que no describen al ejemplar sino a la transacción.

---

## 6. Decisiones de modelado

| # | Decisión | Justificación |
|---|---|---|
| D-01 | `Impresion` tiene **dos** relaciones identificadoras (`Edicion` e `Idioma`) | Su clave requiere ambas: el número de colección se repite entre idiomas de la misma edición |
| D-02 | `PrecioReferencia` se modela como entidad débil pero en el modelo relacional recibe clave artificial `id_precio` | Su clave natural sería de cinco columnas y es la tabla de mayor volumen. Es una desviación deliberada del conceptual al lógico, documentada en el diccionario de datos |
| D-03 | `Artista` recibe clave artificial ya en el conceptual | No existe autoridad que asigne identificadores a ilustradores; el nombre puede repetirse o variar de grafía entre ediciones |
| D-04 | Los colores y tipos se modelan como entidades y no como atributos multivaluados | Tienen atributos propios (`orden`, `categoria`) y deben ser valores controlados |
| D-05 | `valor_mana`, `total`, `monto_neto`, `valor_lado_a`, `valor_lado_b` y `precio_sugerido` son derivados | No se almacenan; se calculan en consulta o mediante vistas, evitando redundancia |
| D-06 | El cliente participa dos veces en `Intercambio` con roles distintos | Un trade tiene exactamente dos partes, que deben poder distinguirse |
| D-07 | La supervisión entre empleados se modela como relación recursiva M:N con periodo | Permite responder quién supervisaba a un empleado en la fecha de una operación concreta, que es el complemento natural del módulo de auditoría |

---

## 7. Pendientes

| # | Pendiente | Efecto |
|---|---|---|
| P-01 | Definir la regla de desempate cuando existan varias fuentes de precio para la misma impresión y fecha | Necesaria para calcular `precio_sugerido` (RF-06) |
| P-02 | Decidir si se agrega `DetalleLiquidacion` para registrar qué ejemplares cubre cada liquidación | Hoy la liquidación indica cuánto se pagó, pero no por cuáles cartas |
| P-03 | Evaluar si `telefono` de `Cliente` debe ser multivaluado | Afectaría el diagrama con una elipse doble y una tabla adicional al derivar |
