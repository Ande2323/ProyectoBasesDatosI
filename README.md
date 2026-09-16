# Proyecto BD I

## Sistema de Gestión de Inventario, Consignaciones e Intercambios de Cartas Coleccionables

# Enunciado del problema

Se requiere un sistema de información que modele la operación de una tienda especializada en cartas coleccionables de *Magic: The Gathering* — catálogo de cartas, ediciones, impresiones, ejemplares físicos, clientes, empleados y operaciones comerciales — y que permita derivar información de negocio a partir de ese modelo: trazabilidad completa de cada ejemplar, valoración por condición física, márgenes por venta, saldos pendientes con consignantes, rotación por edición y auditoría de las operaciones por empleado.

El dominio a cubrir incluye el catálogo de cartas con sus colores, tipos, ediciones, rarezas, artistas e idiomas; el inventario identificado ejemplar por ejemplar, con su condición física, su propietario y su ubicación; el historial de precios de referencia del mercado; el abastecimiento mediante compras a proveedores; la venta de ejemplares a clientes; la recepción de cartas en consignación bajo contrato con su posterior liquidación de comisiones; la intermediación de intercambios entre coleccionistas avalados por la tienda; y un esquema de empleados con roles y credenciales para diferenciar el acceso administrativo del operativo.

Se solicita construir el modelo entidad-relación extendido (MERE) que represente fielmente la operación de la tienda según los supuestos definidos en este documento, y que permita, a partir de los datos base, calcular precios sugeridos, liquidaciones, márgenes y métricas mediante consultas y agregaciones.

# Supuestos del modelo (reglas y delimitaciones del caso)

# A. Supuestos generales del negocio

- El sistema cubre la operación de una única tienda física, delimitada a las líneas de negocio de venta de inventario propio, consignación e intermediación de intercambios.
- Toda entidad del sistema tiene identificador único y, cuando corresponde, atributos básicos de auditoría (fecha de ingreso o de registro, estado activo/inactivo).
- Las bajas son lógicas: empleados desvinculados, clientes inactivos y contratos vencidos se marcan con su estado correspondiente pero se conservan para preservar coherencia histórica de las operaciones en que participaron.
- No se modelan múltiples monedas, sucursales, canales de venta en línea ni integraciones en tiempo real con servicios externos de cotización; se asume una única fuente operativa y los precios de referencia se cargan manualmente.
- La unidad real del negocio no es la carta como concepto, sino el ejemplar físico concreto: cada copia de cartón que la tienda tiene en su poder es un registro independiente.

# B. Catálogo de cartas

- Una CARTA representa el concepto de reglas del juego, identificado por su nombre oficial, con los atributos que dependen únicamente de ese nombre: texto de oráculo, coste de maná, poder, resistencia y lealtad.
- El atributo valor_mana es derivado: se calcula a partir del coste de maná y no se almacena.
- Los colores de una carta se modelan como entidad COLOR relacionada N:M con CARTA, y no como atributo multivaluado, porque cada color tiene atributos propios (código, nombre y orden canónico) y debe ser un valor controlado.
- Los tipos, supertipos y subtipos se modelan como entidad TIPO relacionada N:M con CARTA, con un atributo que indica a qué nivel de la jerarquía de tipos pertenece cada valor.
- No se modelan formatos de juego, legalidad por formato, mazos, reglas de interacción ni erratas históricas del texto de oráculo.

# C. Ediciones, impresiones e idiomas

- Una EDICION representa la colección en que se publicó un grupo de cartas, identificada por el código impreso en la carta misma, con nombre, fecha de lanzamiento, cantidad de cartas y tipo de edición.
- Una IMPRESION es la aparición concreta de una carta dentro de una edición, en un idioma y un acabado determinados. Su identificación depende parcialmente de la edición y del idioma: es una entidad débil con dos relaciones identificadoras, hacia EDICION y hacia IDIOMA.
- La clave parcial de IMPRESION está compuesta por el número de colección y el indicador de acabado foil, porque la versión foil y la no foil comparten número dentro de una misma edición e idioma.
- La rareza y el artista son atributos de la impresión y no de la carta, porque una misma carta puede ser común en una edición y rara en otra, e ilustrada por artistas distintos. Se modelan como entidades RAREZA y ARTISTA relacionadas 1:N con IMPRESION.
- ARTISTA recibe identificador interno propio porque no existe ninguna autoridad que asigne identificadores a ilustradores y un mismo artista puede aparecer acreditado con grafías distintas entre ediciones.
- No se modelan variantes de marco, promociones por evento, cartas de prueba ni la procedencia editorial de la impresión.

# D. Ejemplares y condición física

- Un EJEMPLAR es la copia física individual que la tienda tiene en su poder, identificada por el código de etiqueta que la tienda imprime y adhiere a la funda al momento del ingreso. Si la tienda posee tres copias de la misma impresión, existen tres ejemplares distintos.
- Cada ejemplar registra su estado (disponible, reservado, vendido, intercambiado o devuelto), su ubicación física, su fecha de ingreso y observaciones sobre daños puntuales.
- La condición de conservación se modela como entidad CONDICION con los valores estándar del mercado (Near Mint, Lightly Played, Moderately Played, Heavily Played y Damaged) y un factor de ajuste sobre el precio. Se modela como entidad y no como atributo porque el factor depende de la condición y no del ejemplar, lo que constituiría una dependencia transitiva.
- La propiedad del ejemplar se determina por su participación en la relación AMPARA con un contrato de consignación: el ejemplar que no participa en ella es propiedad de la tienda.
- Un ejemplar comprometido —vendido, entregado en un intercambio o devuelto a su consignante— no puede volver a transarse.
- No se modelan reservas con abono parcial, ejemplares dañados en tránsito, graduaciones de terceros ni certificados de autenticidad.

# E. Valoración y precios de referencia

- Un PRECIO_REFERENCIA registra el valor de mercado de una impresión en una fecha determinada, junto con la fuente de la cotización. Es una entidad débil de IMPRESION, cuya clave parcial es la fecha.
- El historial se conserva completo: los precios no se sobrescriben, de modo que sea posible analizar la evolución del valor de una impresión en el tiempo.
- El precio sugerido de un ejemplar es un atributo derivado, obtenido al multiplicar el precio de referencia vigente de su impresión por el factor de ajuste de su condición física.
- El sistema admite varias cotizaciones para una misma impresión en una misma fecha, provenientes de fuentes distintas.
- Los valores pactados en cada operación se almacenan en la relación correspondiente y no se recalculan: una venta registrada conserva el precio al que efectivamente se transó, con independencia de cómo evolucione el mercado después.
- No se modelan reglas de descuento por volumen, listas de precios diferenciadas por cliente ni predicciones de valorización.

# F. Personas: clientes y empleados

- Un CLIENTE es toda persona externa que compra, consigna o participa en intercambios. Un mismo cliente puede desempeñar los tres papeles.
- Un EMPLEADO es quien opera el sistema y registra los movimientos, con su cargo, fecha de vinculación y estado.
- Tanto CLIENTE como EMPLEADO tienen como identificador el atributo compuesto documento, descomponible en tipo de documento y número de documento, y un atributo compuesto nombre_completo, descomponible en nombres y apellidos. Ambos se descomponen durante la normalización a 3FN.
- La supervisión entre empleados se modela como relación recursiva SUPERVISA sobre EMPLEADO, con los roles supervisor y supervisado y atributos de fecha de inicio y fecha de fin. Es N:M porque un empleado puede haber sido supervisado por distintas personas en periodos distintos, y la fecha de fin nula indica que la supervisión sigue vigente.
- La participación en SUPERVISA es parcial en ambos extremos: el administrador no tiene supervisor y no todo empleado supervisa a alguien.
- No se modelan jerarquías de personas mediante especialización, ni datos de nómina, contratos laborales, horarios o comisiones por desempeño.

# G. Usuarios y roles

- Un empleado accede al sistema mediante una CUENTA_USUARIO identificada por su nombre de usuario, con contraseña cifrada, fecha de último acceso y estado.
- La relación entre EMPLEADO y CUENTA_USUARIO es 1:1 y su participación es parcial del lado del empleado, porque no todo empleado requiere acceso al sistema.
- Los roles se manejan en un catálogo ROL con nombre, descripción y nivel de permiso. Cada empleado tiene un único rol asignado.
- Toda venta, compra, consignación, liquidación e intercambio queda atribuida al empleado que la registró, lo que permite auditar la responsabilidad sobre cada operación.
- No se modelan permisos granulares por recurso, registros de acceso, sesiones activas ni analítica de uso por usuario.

# H. Proveedores y compras

- Un PROVEEDOR es el distribuidor o mayorista al que la tienda adquiere su inventario propio, identificado por su NIT.
- Una COMPRA es la adquisición de ejemplares a un proveedor. Su identificación depende parcialmente del proveedor, porque dos distribuidores distintos pueden emitir su factura con el mismo número: es una entidad débil de PROVEEDOR cuya clave parcial es el número de factura.
- El total de la compra es un atributo derivado, obtenido de la suma de los costos unitarios de los ejemplares adquiridos.
- La relación ADQUIERE entre COMPRA y EJEMPLAR tiene como atributo propio el costo unitario, que es el valor histórico contra el cual se calcula el margen de la venta posterior.
- La participación del ejemplar en ADQUIERE es parcial, porque las cartas recibidas en consignación nunca fueron compradas.
- No se modelan órdenes de compra, cuentas por pagar, plazos de crédito con el proveedor ni devoluciones al distribuidor.

# I. Ventas

- Una VENTA registra la entrega de uno o varios ejemplares a un cliente, con su consecutivo interno, fecha, medio de pago y estado (pagada o anulada).
- El total de la venta es un atributo derivado, obtenido de la suma de los precios de venta menos los descuentos aplicados.
- La relación INCLUYE entre VENTA y EJEMPLAR tiene como atributos propios el precio de venta y el descuento, que congelan el valor de la transacción en el momento en que ocurrió.
- La participación del ejemplar en INCLUYE es parcial, porque un ejemplar disponible aún no ha sido vendido.
- Un ejemplar se vende una sola vez: al estar excluida la compra directa de cartas a clientes, una copia que sale del inventario no regresa a él.
- No se modelan pasarelas de pago, facturación electrónica, pagos parciales, devoluciones del cliente ni logística de envíos.

# J. Consignaciones y liquidaciones

- Un CONTRATO_CONSIGNACION es el acuerdo por el cual un cliente deja cartas para que la tienda las venda a cambio de una comisión. Registra su consecutivo, fecha de inicio, fecha límite, porcentaje de comisión y estado (vigente, liquidado o vencido).
- El contrato es una entidad y no un atributo del ejemplar porque agrupa muchos ejemplares bajo unas mismas condiciones: si el porcentaje de comisión viviera en cada ejemplar, se repetiría en cada carta del mismo acuerdo.
- La relación AMPARA entre CONTRATO_CONSIGNACION y EJEMPLAR es la que determina la propiedad de la carta durante su permanencia en la tienda.
- Una LIQUIDACION es el comprobante del pago que la tienda hace al consignante por las cartas suyas que se vendieron, con su monto bruto, su comisión retenida y su estado.
- La relación entre CONTRATO_CONSIGNACION y LIQUIDACION es 1:N, porque un contrato puede liquidarse por partes a medida que se venden sus ejemplares.
- El monto neto a pagar es un atributo derivado, obtenido de restar la comisión al monto bruto.
- Al vencer el plazo del contrato, los ejemplares no vendidos se devuelven a su propietario y quedan en estado devuelto.
- No se modelan penalizaciones por retiro anticipado, renovaciones automáticas del contrato ni comisiones diferenciadas por tipo de carta.

# K. Intercambios entre clientes

- Un INTERCAMBIO representa el trade entre dos clientes, avalado y valorado por la tienda, con su consecutivo, fecha, comisión cobrada y estado (cerrado o anulado).
- Cada una de las dos partes del intercambio se modela mediante una relación independiente entre CLIENTE e INTERCAMBIO, diferenciadas por el rol que desempeña el cliente: lado A y lado B. Ambas son necesarias porque un trade tiene exactamente dos partes que deben poder distinguirse.
- La relación INVOLUCRA entre INTERCAMBIO y EJEMPLAR es N:M y tiene como atributos propios el lado que aporta la carta y el valor tasado que se le asigna en ese trade concreto.
- Los valores totales de cada lado son atributos derivados, obtenidos de la suma de los valores tasados de los ejemplares que aporta cada parte. Permiten verificar si el intercambio fue equilibrado, que es el servicio que la tienda avala.
- En un intercambio las cartas cambian de propietario pero no hay pago entre los clientes: lo único que se cobra es la comisión de la tienda, que es atributo del intercambio.
- No se modelan contraofertas, negociaciones previas, historial de reputación de los participantes ni intercambios entre más de dos partes.

# L. Estadísticas y métricas derivadas

- El valor del inventario, los márgenes por venta y los saldos pendientes con cada consignante son datos derivados, calculados a partir de los registros de las operaciones. No se almacenan duplicados en otras tablas.
- La trazabilidad completa de un ejemplar —cómo ingresó, quién lo poseyó y cómo salió— se obtiene mediante consultas sobre las relaciones ADQUIERE, AMPARA, INCLUYE e INVOLUCRA, y no como entidad materializada.
- Las métricas analíticas (rotación por edición, evolución del precio de una impresión, comparación entre el inventario propio y el consignado, operaciones registradas por empleado, rentabilidad por proveedor) se resuelven por consulta sobre el modelo base y no requieren entidades adicionales.
- No se modelan recálculos programados, marcas de fecha de último cálculo, tableros de indicadores ni auditoría de correcciones posteriores.

# M. Resumen estructural del modelo

Resultado del modelo conceptual final:

- Entidades regulares (18): CARTA, COLOR, TIPO, EDICION, RAREZA, ARTISTA, IDIOMA, CONDICION, EJEMPLAR, CLIENTE, EMPLEADO, ROL, CUENTA_USUARIO, PROVEEDOR, VENTA, CONTRATO_CONSIGNACION, LIQUIDACION, INTERCAMBIO.
- Entidades débiles (3): IMPRESION (depende de EDICION y de IDIOMA), PRECIO_REFERENCIA (depende de IMPRESION), COMPRA (depende de PROVEEDOR).
- Total de entidades del MERE: 21.
- Jerarquías ISA: ninguna. Las personas se modelan como entidades independientes CLIENTE y EMPLEADO, sin supertipo común.
- Relaciones identificadoras (4): CONTIENE e IMPRESA_EN (hacia IMPRESION), SE_COTIZA (hacia PRECIO_REFERENCIA) y EMITE (hacia COMPRA).
- Total de relaciones: 28, distribuidas en 1 de tipo 1:1 (ACCEDE_CON), 23 de tipo 1:N y 4 de tipo N:M.
- Relaciones recursivas (1): SUPERVISA sobre EMPLEADO, con roles supervisor y supervisado.
- Relaciones con roles diferenciados (2): PARTICIPA_COMO_A y PARTICIPA_COMO_B entre CLIENTE e INTERCAMBIO.
- Relaciones con atributos propios (4): ADQUIERE (costo_unitario), INCLUYE (precio_venta, descuento), INVOLUCRA (lado, valor_tasado) y SUPERVISA (fecha_inicio, fecha_fin).
- Atributos derivados: valor_mana (CARTA), precio_sugerido (EJEMPLAR), total (COMPRA y VENTA), monto_neto (LIQUIDACION), valor_lado_a y valor_lado_b (INTERCAMBIO).
- Atributos compuestos: documento y nombre_completo (CLIENTE y EMPLEADO).
- Atributos multivaluados: ninguno. Los colores y los tipos, que serían atributos multivaluados de CARTA, se modelan como entidades relacionadas N:M.
