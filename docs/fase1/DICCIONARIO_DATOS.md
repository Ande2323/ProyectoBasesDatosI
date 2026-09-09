# Diccionario de Datos — Modelo Relacional

**Proyecto:** Sistema de gestión de inventario, consignaciones e intercambios para una tienda de cartas coleccionables de *Magic: The Gathering*
**Universidad El Bosque** · Bases de Datos 1 · **Docente:** Ing., Esp., M.Ed. Christian Felipe Duarte
**Fecha:** 8 de septiembre de 2026
**Versión:** 1.0 — preliminar, sujeta a ajuste tras el modelo Entidad-Relación

---

## 1. Convenciones de diseño

| Aspecto | Criterio adoptado |
|---|---|
| **Clave primaria** | Natural siempre que la entidad tenga un identificador propio y confiable del mundo real. Artificial solo cuando ese identificador no existe. |
| **Claves artificiales** | Únicamente en `Artista` y `PrecioReferencia` (2 de 26 tablas). |
| **Nomenclatura** | `snake_case`, nombres de entidad en singular. |
| **Dinero** | `DECIMAL(p,2)`. Nunca `FLOAT`, por errores de redondeo en operaciones monetarias. |
| **Booleanos** | `CHAR(1)` con `CHECK IN ('S','N')`, por portabilidad entre Oracle y MySQL. |
| **Autoincremental** | Se implementa según el RDBMS elegido en Fase 2: `IDENTITY` (Oracle 12c+), `AUTO_INCREMENT` (MySQL/MariaDB) o `SERIAL` (PostgreSQL). |
| **Fechas** | `DATE` para fechas de negocio; `TIMESTAMP` cuando importa la hora de la transacción. |

### Justificación de la estrategia de claves

Se privilegian las claves naturales porque en este dominio la mayoría de entidades tienen identificadores reales, impresos o legales: el código de edición va impreso en cada carta, la rareza tiene un símbolo, el idioma tiene código ISO, las personas tienen documento de identidad, los proveedores tienen NIT y las operaciones tienen consecutivos emitidos por la tienda.

Se recurre a clave artificial en dos casos:

- **`Artista`**: no existe ninguna autoridad que asigne identificadores a ilustradores. Dos artistas pueden llamarse igual y un mismo artista puede aparecer acreditado con grafías distintas entre ediciones.
- **`PrecioReferencia`**: su clave natural sería de cinco columnas y es la tabla con mayor volumen de registros del modelo.

---

## 2. Resumen de tablas

| # | Tabla | Módulo | Tipo |
|---|---|---|---|
| 1 | `Carta` | Catálogo | Entidad fuerte |
| 2 | `Color` | Catálogo | Entidad fuerte |
| 3 | `Tipo` | Catálogo | Entidad fuerte |
| 4 | `CartaColor` | Catálogo | Relación N:M |
| 5 | `CartaTipo` | Catálogo | Relación N:M |
| 6 | `Edicion` | Catálogo | Entidad fuerte |
| 7 | `Rareza` | Catálogo | Entidad fuerte |
| 8 | `Artista` | Catálogo | Entidad fuerte |
| 9 | `Idioma` | Catálogo | Entidad fuerte |
| 10 | `Impresion` | Catálogo | Entidad débil |
| 11 | `Condicion` | Inventario | Entidad fuerte |
| 12 | `PrecioReferencia` | Inventario | Entidad fuerte |
| 13 | `Ejemplar` | Inventario | Entidad fuerte |
| 14 | `Cliente` | Personas | Entidad fuerte |
| 15 | `Rol` | Personas | Entidad fuerte |
| 16 | `Empleado` | Personas | Entidad fuerte |
| 17 | `CuentaUsuario` | Personas | Entidad fuerte (1:1) |
| 18 | `Proveedor` | Personas | Entidad fuerte |
| 19 | `Compra` | Operaciones | Entidad débil |
| 20 | `DetalleCompra` | Operaciones | Entidad débil |
| 21 | `Venta` | Operaciones | Entidad fuerte |
| 22 | `DetalleVenta` | Operaciones | Entidad débil |
| 23 | `ContratoConsignacion` | Operaciones | Entidad fuerte |
| 24 | `Liquidacion` | Operaciones | Entidad fuerte |
| 25 | `Intercambio` | Operaciones | Entidad fuerte |
| 26 | `DetalleIntercambio` | Operaciones | Relación N:M |

**24 entidades del modelo conceptual + 2 tablas de relación N:M**, ampliamente por encima del mínimo de 8 entidades exigido.

---

## 3. Módulo Catálogo

Describe las cartas como objetos de conocimiento, con independencia de que la tienda posea o no una copia.

### 3.1 `Carta`

La carta como concepto de reglas ("oráculo"). Sus atributos dependen únicamente del nombre, no de la edición en que se imprima.

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `nombre` | VARCHAR(150) | **PK** | Nombre oficial de la carta |
| `texto_oraculo` | VARCHAR(1000) | NULL | Texto de reglas vigente. Nulo en tierras básicas |
| `coste_mana` | VARCHAR(50) | NULL | Notación de símbolos, ej. `{1}{U}{U}` |
| `valor_mana` | INTEGER | NOT NULL, CHECK >= 0 | Coste convertido de maná |
| `poder` | VARCHAR(5) | NULL | Solo criaturas. VARCHAR porque existen valores `*`, `1+*`, `X` |
| `resistencia` | VARCHAR(5) | NULL | Solo criaturas |
| `lealtad` | VARCHAR(5) | NULL | Solo planeswalkers |

### 3.2 `Color`

Catálogo de los cinco colores del juego más incoloro.

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `codigo` | CHAR(1) | **PK** | `W`, `U`, `B`, `R`, `G`, `C` |
| `nombre` | VARCHAR(20) | NOT NULL, UNIQUE | Blanco, Azul, Negro, Rojo, Verde, Incoloro |
| `orden` | INTEGER | NOT NULL | Orden canónico WUBRG para presentación |

### 3.3 `Tipo`

Catálogo de supertipos, tipos y subtipos de carta.

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `nombre` | VARCHAR(50) | **PK** | Criatura, Instantáneo, Humano, Legendaria… |
| `categoria` | VARCHAR(12) | NOT NULL, CHECK IN ('SUPERTIPO','TIPO','SUBTIPO') | Nivel al que pertenece |

### 3.4 `CartaColor`

Resuelve la relación N:M entre `Carta` y `Color`. Existe porque una carta puede tener varios colores, lo que constituiría un atributo multivaluado y violaría la 1FN.

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `nombre_carta` | VARCHAR(150) | **PK**, FK → `Carta.nombre` | |
| `codigo_color` | CHAR(1) | **PK**, FK → `Color.codigo` | |

### 3.5 `CartaTipo`

Resuelve la relación N:M entre `Carta` y `Tipo`.

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `nombre_carta` | VARCHAR(150) | **PK**, FK → `Carta.nombre` | |
| `nombre_tipo` | VARCHAR(50) | **PK**, FK → `Tipo.nombre` | |

### 3.6 `Edicion`

La colección o set en que se publicó un grupo de cartas. El código de set va impreso en la carta misma.

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `codigo` | VARCHAR(10) | **PK** | `MH3`, `BLB`, `LTR` |
| `nombre` | VARCHAR(100) | NOT NULL, UNIQUE | Modern Horizons 3 |
| `fecha_lanzamiento` | DATE | NOT NULL | |
| `total_cartas` | INTEGER | NULL, CHECK > 0 | Tamaño del set |
| `tipo_edicion` | VARCHAR(30) | NOT NULL | Expansión, Núcleo, Suplementaria |

### 3.7 `Rareza`

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `codigo` | CHAR(1) | **PK** | `C`, `U`, `R`, `M`, `S` |
| `nombre` | VARCHAR(20) | NOT NULL, UNIQUE | Común, Infrecuente, Rara, Mítica, Especial |
| `orden` | INTEGER | NOT NULL | Jerarquía para ordenar consultas |

### 3.8 `Artista`

Ilustrador de la impresión. **Única entidad de catálogo con clave artificial**, porque no existe registro oficial de ilustradores: dos artistas pueden llamarse igual y un mismo artista puede aparecer acreditado con grafías distintas.

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `id_artista` | INTEGER | **PK**, autoincremental | Identificador interno |
| `nombre` | VARCHAR(120) | NOT NULL | Nombre acreditado en la carta |
| `nacionalidad` | VARCHAR(60) | NULL | |

### 3.9 `Idioma`

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `codigo` | CHAR(3) | **PK** | ISO 639-2: `eng`, `spa`, `jpn` |
| `nombre` | VARCHAR(40) | NOT NULL, UNIQUE | Inglés, Español, Japonés |

### 3.10 `Impresion`

Aparición concreta de una `Carta` dentro de una `Edicion`. Es el eje del catálogo: rareza y artista **no dependen del nombre de la carta** sino de la impresión, pues una misma carta puede ser común en un set y rara en otro.

**Entidad débil** respecto de `Edicion` e `Idioma`, con discriminante propio `numero_coleccion` + `es_foil`.

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `codigo_edicion` | VARCHAR(10) | **PK**, FK → `Edicion.codigo` | En qué set salió |
| `numero_coleccion` | VARCHAR(10) | **PK** | Número impreso. VARCHAR por valores como `112a` |
| `codigo_idioma` | CHAR(3) | **PK**, FK → `Idioma.codigo` | Idioma de impresión |
| `es_foil` | CHAR(1) | **PK**, CHECK IN ('S','N') | Acabado metalizado |
| `nombre_carta` | VARCHAR(150) | NOT NULL, FK → `Carta.nombre` | Qué carta es |
| `codigo_rareza` | CHAR(1) | NOT NULL, FK → `Rareza.codigo` | |
| `id_artista` | INTEGER | NOT NULL, FK → `Artista.id_artista` | |
| `variante_ilustracion` | VARCHAR(50) | NULL | Borderless, Extended Art, Showcase |
| `url_imagen` | VARCHAR(300) | NULL | Recurso para la aplicación web |

---

## 4. Módulo Inventario y valoración

### 4.1 `Condicion`

Estado físico de conservación según el estándar del mercado de coleccionables. El `factor_ajuste` es un dato del negocio que depende de la condición y no del ejemplar: mantenerlo aquí evita una dependencia transitiva que violaría la 3FN.

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `codigo` | VARCHAR(3) | **PK** | `NM`, `LP`, `MP`, `HP`, `DMG` |
| `nombre` | VARCHAR(40) | NOT NULL, UNIQUE | Near Mint, Lightly Played, Moderately Played, Heavily Played, Damaged |
| `descripcion` | VARCHAR(200) | NULL | Criterio de clasificación |
| `factor_ajuste` | DECIMAL(3,2) | NOT NULL, CHECK BETWEEN 0.01 AND 1.00 | Multiplicador sobre el precio de referencia |

### 4.2 `PrecioReferencia`

Historial del valor de mercado de una impresión. **Clave artificial** por dos motivos: su clave natural sería de cinco columnas y es la tabla con mayor volumen de registros del modelo.

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `id_precio` | INTEGER | **PK**, autoincremental | Identificador interno |
| `codigo_edicion` | VARCHAR(10) | NOT NULL, FK → `Impresion` | |
| `numero_coleccion` | VARCHAR(10) | NOT NULL, FK → `Impresion` | |
| `codigo_idioma` | CHAR(3) | NOT NULL, FK → `Impresion` | |
| `es_foil` | CHAR(1) | NOT NULL, FK → `Impresion` | |
| `fecha` | DATE | NOT NULL | Día de la cotización |
| `valor` | DECIMAL(12,2) | NOT NULL, CHECK > 0 | Precio de referencia en pesos |
| `fuente` | VARCHAR(50) | NULL | Scryfall, TCGPlayer, tasación interna |

**Nota de diseño:** la tabla admite deliberadamente varios registros para la misma impresión y fecha, de modo que puedan convivir distintas fuentes de cotización el mismo día. Ver el supuesto S-04 en la sección 7.

### 4.3 `Ejemplar`

La copia física individual. Es la entidad central del proyecto: si la tienda tiene tres copias de la misma carta, existen tres registros distintos.

Su clave natural, el código de etiqueta que la tienda imprime y adhiere a la funda, permite que todas las tablas de operaciones referencien un ejemplar con **una sola columna**, conteniendo la propagación de la clave compuesta de `Impresion`.

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `codigo_etiqueta` | VARCHAR(20) | **PK** | Código físico pegado a la funda |
| `codigo_edicion` | VARCHAR(10) | NOT NULL, FK → `Impresion` | |
| `numero_coleccion` | VARCHAR(10) | NOT NULL, FK → `Impresion` | |
| `codigo_idioma` | CHAR(3) | NOT NULL, FK → `Impresion` | |
| `es_foil` | CHAR(1) | NOT NULL, FK → `Impresion` | |
| `codigo_condicion` | VARCHAR(3) | NOT NULL, FK → `Condicion.codigo` | |
| `numero_contrato` | VARCHAR(20) | **NULL**, FK → `ContratoConsignacion` | Nulo ⇒ propiedad de la tienda |
| `estado` | VARCHAR(15) | NOT NULL, CHECK IN ('DISPONIBLE','RESERVADO','VENDIDO','INTERCAMBIADO','DEVUELTO') | Situación actual |
| `ubicacion` | VARCHAR(50) | NULL | Vitrina 2, Caja B, Binder Rojo |
| `fecha_ingreso` | DATE | NOT NULL | |
| `observaciones` | VARCHAR(200) | NULL | Daños puntuales, notas del tasador |

El campo `numero_contrato` resuelve la propiedad del ejemplar con un solo atributo, sin posibilidad de contradicción: nulo significa que la carta es de la tienda; con valor, que pertenece a un consignante.

---

## 5. Módulo Personas

### 5.1 `Cliente`

Persona externa que compra, consigna o participa en intercambios. Un mismo cliente puede desempeñar los tres papeles.

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `tipo_documento` | VARCHAR(5) | **PK**, CHECK IN ('CC','CE','TI','PAS') | |
| `numero_documento` | VARCHAR(20) | **PK** | |
| `nombres` | VARCHAR(60) | NOT NULL | |
| `apellidos` | VARCHAR(60) | NOT NULL | |
| `telefono` | VARCHAR(20) | NULL | |
| `correo` | VARCHAR(120) | NULL, UNIQUE | |
| `fecha_registro` | DATE | NOT NULL | |

La clave compuesta proviene del atributo compuesto `documento` del modelo Entidad-Relación, descompuesto en sus dos componentes al pasar al modelo relacional.

### 5.2 `Rol`

Perfil del empleado dentro del sistema. Los permisos dependen del rol y no del empleado.

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `nombre` | VARCHAR(40) | **PK** | Administrador, Vendedor, Tasador |
| `descripcion` | VARCHAR(200) | NULL | |
| `nivel_permiso` | INTEGER | NOT NULL, CHECK BETWEEN 1 AND 5 | |

### 5.3 `Empleado`

Quien opera el sistema y registra los movimientos. Permite la auditoría de todas las operaciones.

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `tipo_documento` | VARCHAR(5) | **PK**, CHECK IN ('CC','CE','PAS') | |
| `numero_documento` | VARCHAR(20) | **PK** | |
| `nombres` | VARCHAR(60) | NOT NULL | |
| `apellidos` | VARCHAR(60) | NOT NULL | |
| `nombre_rol` | VARCHAR(40) | NOT NULL, FK → `Rol.nombre` | Relación 1:N |
| `cargo` | VARCHAR(50) | NOT NULL | |
| `fecha_vinculacion` | DATE | NOT NULL | |
| `estado` | CHAR(1) | NOT NULL, CHECK IN ('A','I') | Activo / Inactivo |
| `telefono` | VARCHAR(20) | NULL | |

### 5.4 `CuentaUsuario`

Credenciales de acceso al sistema. Se separa de `Empleado` para aislar los datos de autenticación de los datos laborales, y porque no todo empleado requiere cuenta.

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `nombre_usuario` | VARCHAR(30) | **PK** | Login del sistema |
| `tipo_documento_emp` | VARCHAR(5) | NOT NULL, FK → `Empleado` | |
| `numero_documento_emp` | VARCHAR(20) | NOT NULL, FK → `Empleado` | |
| `contrasena_hash` | VARCHAR(255) | NOT NULL | Nunca almacenada en texto plano |
| `ultimo_acceso` | TIMESTAMP | NULL | |
| `estado` | CHAR(1) | NOT NULL, CHECK IN ('A','B') | Activa / Bloqueada |
| | | **UNIQUE (`tipo_documento_emp`, `numero_documento_emp`)** | |

La restricción `UNIQUE` sobre la clave foránea es lo que convierte la relación en **1:1**. Sin ella sería 1:N.

### 5.5 `Proveedor`

Distribuidor o mayorista al que la tienda adquiere su inventario propio.

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `nit` | VARCHAR(20) | **PK** | NIT con dígito de verificación |
| `razon_social` | VARCHAR(120) | NOT NULL | |
| `nombre_contacto` | VARCHAR(80) | NULL | |
| `telefono` | VARCHAR(20) | NULL | |
| `correo` | VARCHAR(120) | NULL | |
| `ciudad` | VARCHAR(60) | NULL | |
| `direccion` | VARCHAR(150) | NULL | |

---

## 6. Módulo Operaciones

Las operaciones siguen el patrón **encabezado + detalle**: el encabezado guarda lo común a la transacción y el detalle guarda cada ejemplar involucrado.

### 6.1 `Compra`

Adquisición de cartas a un proveedor. **Entidad débil** respecto de `Proveedor`: el número de factura solo identifica dentro de cada proveedor, pues dos distribuidores pueden emitir su factura 001.

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `nit_proveedor` | VARCHAR(20) | **PK**, FK → `Proveedor.nit` | |
| `numero_factura` | VARCHAR(30) | **PK** | Factura emitida por el proveedor |
| `tipo_documento_emp` | VARCHAR(5) | NOT NULL, FK → `Empleado` | Auditoría |
| `numero_documento_emp` | VARCHAR(20) | NOT NULL, FK → `Empleado` | Auditoría |
| `fecha_compra` | DATE | NOT NULL | |
| `total` | DECIMAL(14,2) | NOT NULL, CHECK >= 0 | |

### 6.2 `DetalleCompra`

Línea de compra. No existe columna `cantidad`: cada ejemplar es una pieza física única, de modo que hay una línea por carta.

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `codigo_etiqueta` | VARCHAR(20) | **PK**, FK → `Ejemplar` | El ejemplar adquirido |
| `nit_proveedor` | VARCHAR(20) | NOT NULL, FK → `Compra` | |
| `numero_factura` | VARCHAR(30) | NOT NULL, FK → `Compra` | |
| `costo_unitario` | DECIMAL(12,2) | NOT NULL, CHECK >= 0 | Costo histórico de adquisición |

Al ser el ejemplar la clave primaria, la restricción de integridad se obtiene sin esfuerzo adicional: es imposible registrar la misma copia física en dos compras distintas.

### 6.3 `Venta`

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `numero_venta` | VARCHAR(20) | **PK** | Consecutivo emitido por la tienda |
| `tipo_documento_cli` | VARCHAR(5) | NOT NULL, FK → `Cliente` | |
| `numero_documento_cli` | VARCHAR(20) | NOT NULL, FK → `Cliente` | |
| `tipo_documento_emp` | VARCHAR(5) | NOT NULL, FK → `Empleado` | Auditoría |
| `numero_documento_emp` | VARCHAR(20) | NOT NULL, FK → `Empleado` | Auditoría |
| `fecha_venta` | TIMESTAMP | NOT NULL | |
| `medio_pago` | VARCHAR(20) | NOT NULL, CHECK IN ('EFECTIVO','TARJETA','TRANSFERENCIA') | |
| `total` | DECIMAL(14,2) | NOT NULL, CHECK >= 0 | |
| `estado` | VARCHAR(12) | NOT NULL, CHECK IN ('PAGADA','ANULADA') | |

### 6.4 `DetalleVenta`

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `codigo_etiqueta` | VARCHAR(20) | **PK**, FK → `Ejemplar` | El ejemplar vendido |
| `numero_venta` | VARCHAR(20) | NOT NULL, FK → `Venta` | |
| `precio_venta` | DECIMAL(12,2) | NOT NULL, CHECK > 0 | Valor congelado al momento de la venta |
| `descuento` | DECIMAL(12,2) | NOT NULL, DEFAULT 0, CHECK >= 0 | |

El precio se almacena en el detalle y no se consulta de `PrecioReferencia`, de modo que un cambio posterior en el mercado no altere las ventas ya registradas. Al ser el ejemplar la clave primaria, se implementa parcialmente el RF-10: una copia no puede venderse dos veces.

### 6.5 `ContratoConsignacion`

Acuerdo por el cual un cliente deja cartas para que la tienda las venda a cambio de una comisión.

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `numero_contrato` | VARCHAR(20) | **PK** | Consecutivo del contrato |
| `tipo_documento_cli` | VARCHAR(5) | NOT NULL, FK → `Cliente` | El consignante |
| `numero_documento_cli` | VARCHAR(20) | NOT NULL, FK → `Cliente` | |
| `tipo_documento_emp` | VARCHAR(5) | NOT NULL, FK → `Empleado` | |
| `numero_documento_emp` | VARCHAR(20) | NOT NULL, FK → `Empleado` | |
| `fecha_inicio` | DATE | NOT NULL | |
| `fecha_limite` | DATE | NOT NULL, **CHECK > `fecha_inicio`** | Vencimiento del plazo |
| `porcentaje_comision` | DECIMAL(5,2) | NOT NULL, CHECK BETWEEN 0 AND 100 | |
| `estado` | VARCHAR(12) | NOT NULL, CHECK IN ('VIGENTE','LIQUIDADO','VENCIDO') | |

### 6.6 `Liquidacion`

Pago que la tienda hace al consignante por las cartas suyas que se vendieron.

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `numero_liquidacion` | VARCHAR(20) | **PK** | Comprobante emitido por la tienda |
| `numero_contrato` | VARCHAR(20) | NOT NULL, FK → `ContratoConsignacion` | |
| `tipo_documento_emp` | VARCHAR(5) | NOT NULL, FK → `Empleado` | |
| `numero_documento_emp` | VARCHAR(20) | NOT NULL, FK → `Empleado` | |
| `fecha_liquidacion` | DATE | NOT NULL | |
| `monto_bruto` | DECIMAL(14,2) | NOT NULL, CHECK >= 0 | Total vendido del contrato |
| `monto_comision` | DECIMAL(14,2) | NOT NULL, CHECK >= 0 | Retención de la tienda |
| `estado` | VARCHAR(12) | NOT NULL, CHECK IN ('PENDIENTE','PAGADA') | |

**No se almacena `monto_neto`**, por ser un atributo derivado (`monto_bruto − monto_comision`). Se calcula en consulta o mediante una vista.

### 6.7 `Intercambio`

Trade entre dos clientes, avalado y valorado por la tienda.

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `numero_intercambio` | VARCHAR(20) | **PK** | Consecutivo del trade |
| `tipo_doc_cli_a` | VARCHAR(5) | NOT NULL, FK → `Cliente` | Parte A |
| `numero_doc_cli_a` | VARCHAR(20) | NOT NULL, FK → `Cliente` | |
| `tipo_doc_cli_b` | VARCHAR(5) | NOT NULL, FK → `Cliente` | Parte B |
| `numero_doc_cli_b` | VARCHAR(20) | NOT NULL, FK → `Cliente` | |
| `tipo_documento_emp` | VARCHAR(5) | NOT NULL, FK → `Empleado` | Quien avaló |
| `numero_documento_emp` | VARCHAR(20) | NOT NULL, FK → `Empleado` | |
| `fecha` | TIMESTAMP | NOT NULL | |
| `valor_lado_a` | DECIMAL(14,2) | NOT NULL, CHECK >= 0 | Tasación de lo aportado por A |
| `valor_lado_b` | DECIMAL(14,2) | NOT NULL, CHECK >= 0 | Tasación de lo aportado por B |
| `comision` | DECIMAL(12,2) | NOT NULL, CHECK >= 0 | Cobro de la tienda |
| `estado` | VARCHAR(12) | NOT NULL, CHECK IN ('CERRADO','ANULADO') | |
| | | **CHECK (`numero_doc_cli_a` <> `numero_doc_cli_b`)** | Nadie intercambia consigo mismo |

Las dos claves foráneas hacia `Cliente` representan una **relación con roles diferenciados** (`aporta_A` / `aporta_B`), que en notación Peter-Chen debe etiquetarse explícitamente.

### 6.8 `DetalleIntercambio`

Resuelve la relación N:M entre `Intercambio` y `Ejemplar`, con el lado aportante como atributo de la relación.

| Atributo | Tipo | Restricción | Descripción |
|---|---|---|---|
| `numero_intercambio` | VARCHAR(20) | **PK**, FK → `Intercambio` | |
| `codigo_etiqueta` | VARCHAR(20) | **PK**, FK → `Ejemplar` | Carta puesta en la mesa |
| `lado` | CHAR(1) | NOT NULL, CHECK IN ('A','B') | Quién la aporta |
| `valor_tasado` | DECIMAL(12,2) | NOT NULL, CHECK > 0 | Valor asignado en el trade |

A diferencia de la venta, aquí la clave primaria debe ser compuesta: un ejemplar puede participar en varios intercambios a lo largo de su vida.

---

## 7. Resumen de claves primarias

| Tabla | Clave primaria | Columnas | Origen |
|---|---|---|---|
| `Carta` | `nombre` | 1 | Natural |
| `Color` | `codigo` | 1 | Natural |
| `Tipo` | `nombre` | 1 | Natural |
| `CartaColor` | `nombre_carta` + `codigo_color` | 2 | Relación N:M |
| `CartaTipo` | `nombre_carta` + `nombre_tipo` | 2 | Relación N:M |
| `Edicion` | `codigo` | 1 | Natural |
| `Rareza` | `codigo` | 1 | Natural |
| `Artista` | `id_artista` | 1 | **Artificial** |
| `Idioma` | `codigo` | 1 | Natural |
| `Impresion` | `codigo_edicion` + `numero_coleccion` + `codigo_idioma` + `es_foil` | 4 | Entidad débil |
| `Condicion` | `codigo` | 1 | Natural |
| `PrecioReferencia` | `id_precio` | 1 | **Artificial** |
| `Ejemplar` | `codigo_etiqueta` | 1 | Natural |
| `Cliente` | `tipo_documento` + `numero_documento` | 2 | Atributo compuesto |
| `Rol` | `nombre` | 1 | Natural |
| `Empleado` | `tipo_documento` + `numero_documento` | 2 | Atributo compuesto |
| `CuentaUsuario` | `nombre_usuario` | 1 | Natural |
| `Proveedor` | `nit` | 1 | Natural |
| `Compra` | `nit_proveedor` + `numero_factura` | 2 | Entidad débil |
| `DetalleCompra` | `codigo_etiqueta` | 1 | Entidad débil |
| `Venta` | `numero_venta` | 1 | Natural |
| `DetalleVenta` | `codigo_etiqueta` | 1 | Entidad débil |
| `ContratoConsignacion` | `numero_contrato` | 1 | Natural |
| `Liquidacion` | `numero_liquidacion` | 1 | Natural |
| `Intercambio` | `numero_intercambio` | 1 | Natural |
| `DetalleIntercambio` | `numero_intercambio` + `codigo_etiqueta` | 2 | Relación N:M |

**24 claves naturales y 2 artificiales.** 19 de las 26 tablas tienen clave primaria de una sola columna.

---

## 8. Supuestos y notas de implementación

| # | Supuesto / nota |
|---|---|
| **S-01** | La tienda etiqueta físicamente cada ejemplar al ingresarlo, lo que hace de `codigo_etiqueta` un identificador natural confiable. |
| **S-02** | Al estar excluida la compra directa a clientes (*buylist*), un ejemplar se adquiere una sola vez y se vende una sola vez. Esto justifica que `codigo_etiqueta` sea clave primaria de `DetalleCompra` y `DetalleVenta`. |
| **S-03** | `Carta.nombre` como clave primaria de tipo VARCHAR(150) se replica en `Impresion`, `CartaColor` y `CartaTipo`. A la escala del proyecto el impacto es nulo; en producción con millones de filas incidiría en el tamaño de los índices. |
| **S-04** | `PrecioReferencia` admite varios registros para la misma impresión y fecha, previendo distintas fuentes de cotización. **Pendiente de definir:** la regla de desempate que usará el RF-06 para elegir qué fuente manda al calcular el precio sugerido (por ejemplo, prioridad por fuente o el registro más reciente). |
| **S-05** | Las claves foráneas compuestas requieren `ON UPDATE CASCADE` si un valor de clave natural llegara a cambiar. **Oracle no soporta `ON UPDATE CASCADE`**; MySQL, MariaDB y PostgreSQL sí. Debe tenerse en cuenta en el documento de selección de RDBMS de la Fase 2. |
| **S-06** | El RF-10 —impedir que un ejemplar comprometido vuelva a transarse— queda parcialmente cubierto por las claves primarias de `DetalleVenta` y `DetalleCompra`, pero la validación completa cruza `Ejemplar.estado` con `DetalleVenta` y `DetalleIntercambio`, por lo que requiere un disparador o validación en la aplicación. |
| **S-07** | Los precios se expresan en pesos colombianos (COP) sin decimales de centavo relevantes, aunque el tipo `DECIMAL(p,2)` los soporta. |

---

_Documento preliminar. Se ajustará tras la elaboración del modelo Entidad-Relación en notación Peter-Chen y su normalización formal hasta 3FN._
