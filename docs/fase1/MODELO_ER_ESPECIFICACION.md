# Especificación del Modelo Entidad-Relación

**Proyecto:** Sistema de gestión de inventario, consignaciones e intercambios para una tienda de cartas coleccionables de *Magic: The Gathering*
**Universidad El Bosque** · Programa de Ingeniería de Sistemas
**Asignatura:** Bases de Datos 1 · **Docente:** Ing., Esp., M.Ed. Christian Felipe Duarte

Especificación textual del modelo, redactada para ser trasladada al diagrama en notación Peter-Chen.

**Convención:** los atributos <u>subrayados</u> son identificadores. Los atributos compuestos, multivaluados y derivados se indican debajo de cada entidad.

---

# Entidades y atributos

**CARTA** (<u>nombre</u>, texto_oraculo, coste_mana, poder, resistencia, lealtad)
· `valor_mana` es derivado de `coste_mana`

**COLOR** (<u>codigo</u>, nombre, orden)

**TIPO** (<u>nombre</u>, categoria)

**EDICION** (<u>codigo</u>, nombre, fecha_lanzamiento, total_cartas, tipo_edicion)

**RAREZA** (<u>codigo</u>, nombre, orden)

**ARTISTA** (<u>id_artista</u>, nombre, nacionalidad)

**IDIOMA** (<u>codigo</u>, nombre)

**IMPRESION** (numero_coleccion, es_foil, variante_ilustracion, url_imagen)
· entidad débil dependiente en identificación de EDICION **y** de IDIOMA
· `numero_coleccion` + `es_foil` es su clave parcial

**CONDICION** (<u>codigo</u>, nombre, descripcion, factor_ajuste)

**PRECIO_REFERENCIA** (fecha, valor, fuente)
· entidad débil dependiente en identificación de IMPRESION; `fecha` es su clave parcial

**EJEMPLAR** (<u>codigo_etiqueta</u>, estado, ubicacion, fecha_ingreso, observaciones)
· `precio_sugerido` es derivado del precio de referencia vigente y del factor de ajuste de la condición

**CLIENTE** (documento, nombre_completo, telefono, correo, fecha_registro)
· `documento` es compuesto e identificador: tipo_documento, numero_documento
· `nombre_completo` es compuesto: nombres, apellidos

**EMPLEADO** (documento, nombre_completo, cargo, fecha_vinculacion, estado, telefono)
· `documento` es compuesto e identificador: tipo_documento, numero_documento
· `nombre_completo` es compuesto: nombres, apellidos

**ROL** (<u>nombre</u>, descripcion, nivel_permiso)

**CUENTA_USUARIO** (<u>nombre_usuario</u>, contrasena_hash, ultimo_acceso, estado)

**PROVEEDOR** (<u>nit</u>, razon_social, nombre_contacto, telefono, correo, ciudad, direccion)

**COMPRA** (numero_factura, fecha_compra)
· entidad débil dependiente en identificación de PROVEEDOR; `numero_factura` es su clave parcial
· `total` es derivado de la suma de los costos unitarios

**VENTA** (<u>numero_venta</u>, fecha_venta, medio_pago, estado)
· `total` es derivado de la suma de precios de venta menos descuentos

**CONTRATO_CONSIGNACION** (<u>numero_contrato</u>, fecha_inicio, fecha_limite, porcentaje_comision, estado)

**LIQUIDACION** (<u>numero_liquidacion</u>, fecha_liquidacion, monto_bruto, monto_comision, estado)
· `monto_neto` es derivado de `monto_bruto` − `monto_comision`

**INTERCAMBIO** (<u>numero_intercambio</u>, fecha, comision, estado)
· `valor_lado_a` y `valor_lado_b` son derivados de la suma de `valor_tasado` de cada lado

---

## Qué representa cada entidad

**CARTA**: la carta como concepto de reglas, con su texto de oráculo, independiente de en cuántas ediciones se haya impreso. **COLOR** y **TIPO**: catálogos del juego; una carta puede tener varios de cada uno. **EDICION**: la colección en que se publicó un grupo de cartas. **IMPRESION**: la aparición concreta de una carta dentro de una edición, en un idioma y un acabado determinados. **RAREZA**, **ARTISTA** e **IDIOMA**: atributos de esa impresión que no dependen del nombre de la carta. **PRECIO_REFERENCIA**: el valor de mercado de una impresión en una fecha dada. **CONDICION**: el estado físico de conservación con su factor de ajuste sobre el precio. **EJEMPLAR**: la copia física individual que la tienda tiene en la mano; es la unidad real del negocio. **CLIENTE**: quien compra, consigna o intercambia. **EMPLEADO**, **ROL** y **CUENTA_USUARIO**: quién opera el sistema, con qué permisos y con qué credenciales. **PROVEEDOR**: el distribuidor al que la tienda compra inventario propio. **COMPRA**: la adquisición de cartas a un proveedor. **VENTA**: la venta de ejemplares a un cliente. **CONTRATO_CONSIGNACION**: el acuerdo por el cual un cliente deja cartas para que la tienda las venda a comisión. **LIQUIDACION**: el pago al consignante por sus cartas vendidas. **INTERCAMBIO**: el trade entre dos clientes avalado y valorado por la tienda.

---

# Relaciones

## Módulo Catálogo

**1. POSEE** — CARTA (M) —— (N) COLOR · CARTA: total · COLOR: parcial

**2. ES_DE_TIPO** — CARTA (M) —— (N) TIPO · CARTA: total · TIPO: parcial

**3. SE_IMPRIME_EN** — CARTA (1) —— (N) IMPRESION · ambos: total

**4. CONTIENE** — EDICION (1) —— (N) IMPRESION · identificadora · IMPRESION: total

**5. IMPRESA_EN** — IDIOMA (1) —— (N) IMPRESION · identificadora · IMPRESION: total · IDIOMA: parcial
· segunda relación identificadora: la clave de IMPRESION necesita edición **e** idioma

**6. CLASIFICA** — RAREZA (1) —— (N) IMPRESION · IMPRESION: total · RAREZA: parcial

**7. ILUSTRA** — ARTISTA (1) —— (N) IMPRESION · ambos: total

## Módulo Inventario

**8. SE_COTIZA** — IMPRESION (1) —— (N) PRECIO_REFERENCIA · identificadora · PRECIO_REFERENCIA: total · IMPRESION: parcial

**9. CORRESPONDE_A** — IMPRESION (1) —— (N) EJEMPLAR · EJEMPLAR: total · IMPRESION: parcial

**10. CALIFICA** — CONDICION (1) —— (N) EJEMPLAR · EJEMPLAR: total · CONDICION: parcial

## Módulo Personas

**11. DESEMPENA** — ROL (1) —— (N) EMPLEADO · EMPLEADO: total · ROL: parcial

**12. ACCEDE_CON** — EMPLEADO (1) —— (1) CUENTA_USUARIO · CUENTA_USUARIO: total · EMPLEADO: parcial
· única relación 1:1 del modelo; parcial porque no todo empleado necesita cuenta de acceso

**13. SUPERVISA** — EMPLEADO (M) —— (N) EMPLEADO · **recursiva**
roles: `supervisor` / `supervisado` · participación parcial en ambos extremos
atributos de la relación: `fecha_inicio`, `fecha_fin`
· un empleado puede haber sido supervisado por distintas personas en periodos distintos, y un supervisor tiene varios supervisados a la vez
· `fecha_fin` nula significa que la supervisión sigue vigente
· es parcial en ambos extremos porque el administrador no tiene supervisor y no todo empleado supervisa a alguien

## Módulo Operaciones — Compras

**14. EMITE** — PROVEEDOR (1) —— (N) COMPRA · identificadora · COMPRA: total · PROVEEDOR: parcial

**15. REGISTRA_COMPRA** — EMPLEADO (1) —— (N) COMPRA · COMPRA: total · EMPLEADO: parcial

**16. ADQUIERE** — COMPRA (1) —— (N) EJEMPLAR · COMPRA: total · EJEMPLAR: parcial
atributos de la relación: `costo_unitario`
· el ejemplar es parcial porque las cartas consignadas nunca se compraron

## Módulo Operaciones — Ventas

**17. REALIZA** — CLIENTE (1) —— (N) VENTA · VENTA: total · CLIENTE: parcial

**18. ATIENDE** — EMPLEADO (1) —— (N) VENTA · VENTA: total · EMPLEADO: parcial

**19. INCLUYE** — VENTA (1) —— (N) EJEMPLAR · VENTA: total · EJEMPLAR: parcial
atributos de la relación: `precio_venta`, `descuento`

## Módulo Operaciones — Consignación

**20. CONSIGNA** — CLIENTE (1) —— (N) CONTRATO_CONSIGNACION · CONTRATO: total · CLIENTE: parcial

**21. RECIBE_CONTRATO** — EMPLEADO (1) —— (N) CONTRATO_CONSIGNACION · CONTRATO: total · EMPLEADO: parcial

**22. AMPARA** — CONTRATO_CONSIGNACION (1) —— (N) EJEMPLAR · CONTRATO: total · EJEMPLAR: parcial
· es la relación que da propiedad: el ejemplar que no participa en ella es de la tienda

**23. GENERA** — CONTRATO_CONSIGNACION (1) —— (N) LIQUIDACION · LIQUIDACION: total · CONTRATO: parcial
· es 1:N porque un contrato de 30 cartas puede liquidarse por partes a medida que se venden

**24. ELABORA** — EMPLEADO (1) —— (N) LIQUIDACION · LIQUIDACION: total · EMPLEADO: parcial

## Módulo Operaciones — Intercambios

**25. PARTICIPA_COMO_A** — CLIENTE (1) —— (N) INTERCAMBIO · rol: `lado_a` · INTERCAMBIO: total · CLIENTE: parcial

**26. PARTICIPA_COMO_B** — CLIENTE (1) —— (N) INTERCAMBIO · rol: `lado_b` · INTERCAMBIO: total · CLIENTE: parcial
· 25 y 26 son dos relaciones distintas entre las mismas entidades, diferenciadas por el rol del cliente

**27. AVALA** — EMPLEADO (1) —— (N) INTERCAMBIO · INTERCAMBIO: total · EMPLEADO: parcial

**28. INVOLUCRA** — INTERCAMBIO (M) —— (N) EJEMPLAR · INTERCAMBIO: total · EJEMPLAR: parcial
atributos de la relación: `lado`, `valor_tasado`

---

# Resumen

| Concepto | Cantidad |
|---|---|
| Entidades fuertes | 18 |
| Entidades débiles | 3 (`IMPRESION`, `PRECIO_REFERENCIA`, `COMPRA`) |
| **Total de entidades** | **21** |
| Relaciones 1:1 | 1 |
| Relaciones 1:N | 23 |
| Relaciones M:N | 4 |
| **Total de relaciones** | **28** |
| Relaciones identificadoras | 4 |
| Relaciones recursivas | 1 |
| Relaciones con atributos propios | 4 |

## Construcciones presentes

| Construcción | Dónde aparece |
|---|---|
| Entidad débil y relación identificadora | `IMPRESION`, `PRECIO_REFERENCIA`, `COMPRA` |
| Atributo compuesto | `documento` y `nombre_completo` en `CLIENTE` y `EMPLEADO` |
| Atributo derivado | `valor_mana`, `precio_sugerido`, `total`, `monto_neto`, `valor_lado_a`, `valor_lado_b` |
| Relación 1:1 | **ACCEDE_CON** |
| Relación M:N | **POSEE**, **ES_DE_TIPO**, **SUPERVISA**, **INVOLUCRA** |
| Relación recursiva | **SUPERVISA** |
| Relación con roles diferenciados | **PARTICIPA_COMO_A** / **PARTICIPA_COMO_B**, **SUPERVISA** |
| Atributos de relación | **SUPERVISA**, **ADQUIERE**, **INCLUYE**, **INVOLUCRA** |

## Construcciones ausentes

El dominio no produce de forma natural relaciones **ternarias**, **agregación** ni **especialización**. Si el docente exige mostrarlas, la candidata más honesta sería especializar `EJEMPLAR` en `EJEMPLAR_PROPIO` y `EJEMPLAR_CONSIGNADO` — jerarquía **total y disyunta** — que hoy se resuelve con la participación o no del ejemplar en la relación **AMPARA**.

---

# Derivación al modelo relacional

Las 21 entidades generan 21 tablas. A ellas se suman las tablas que nacen de relaciones:

| Relación | Tabla generada | Motivo |
|---|---|---|
| **POSEE** (M:N) | `CartaColor` | Toda relación M:N genera tabla |
| **ES_DE_TIPO** (M:N) | `CartaTipo` | ídem |
| **SUPERVISA** (M:N recursiva) | `Supervision` | ídem; lleva dos claves foráneas hacia `Empleado` con roles distintos |
| **INVOLUCRA** (M:N) | `DetalleIntercambio` | ídem |
| **ADQUIERE** (1:N) | `DetalleCompra` | Tiene atributo propio y participación parcial del ejemplar: incluirlo en `Ejemplar` generaría nulos |
| **INCLUYE** (1:N) | `DetalleVenta` | ídem |

**Total: 27 tablas.**
