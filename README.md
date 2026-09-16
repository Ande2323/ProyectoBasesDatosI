# Proyecto BD I

## Sistema Interno de Inventario y Reservas de Cartas Coleccionables

# Enunciado del problema

Se requiere un sistema de información que modele el inventario de una tienda especializada en cartas coleccionables de *Magic: The Gathering* — catálogo de cartas, ediciones, impresiones y ejemplares físicos — y que permita al personal conocer en todo momento qué tiene la tienda, en qué condición está cada copia, cuánto vale y si está disponible o apartada.

El dominio a cubrir incluye el catálogo de cartas con sus colores, tipos, ediciones, rarezas, artistas e idiomas; el inventario identificado ejemplar por ejemplar, con su condición física y su ubicación en el local; el historial de precios de referencia del mercado para efectos de valoración; un esquema de usuarios con tres perfiles que diferencia el acceso administrativo, el operativo y el de consulta; y un módulo de reservas mediante el cual un cliente registrado aparta ejemplares desde su perfil en la página, con un plazo máximo de treinta días para reclamarlos.

Se solicita construir el modelo entidad-relación extendido (MERE) que represente fielmente la operación interna de la tienda según los supuestos definidos en este documento, y que permita, a partir de los datos base, calcular precios sugeridos, disponibilidad real y vencimiento de reservas mediante consultas y agregaciones.

# Supuestos del modelo (reglas y delimitaciones del caso)

# A. Supuestos generales del negocio

- El sistema es de uso interno de una única tienda física y su alcance se limita a la gestión del inventario y a las reservas que los clientes realizan desde la página.
- **El sistema no maneja dinero en movimiento.** No se modelan ventas, compras, consignaciones, intercambios, pagos, facturación, cuentas por cobrar ni cuentas por pagar. Los precios que el sistema almacena son valores de referencia del mercado, no transacciones.
- Toda entidad del sistema tiene identificador único y, cuando corresponde, atributos básicos de auditoría (fecha de ingreso o de registro, estado activo/inactivo).
- Las bajas son lógicas: empleados desvinculados, clientes inactivos y reservas vencidas se marcan con su estado correspondiente pero se conservan para preservar coherencia histórica.
- No se modelan múltiples sucursales, monedas, canales de venta en línea ni integraciones en tiempo real con servicios externos de cotización; se asume una única fuente operativa y los precios de referencia se cargan manualmente.
- La unidad real del inventario no es la carta como concepto, sino el ejemplar físico concreto: cada copia de cartón que la tienda tiene en su poder es un registro independiente.

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
- Cada ejemplar registra su estado (disponible, reservado o entregado), su ubicación física dentro del local, su fecha de ingreso al inventario y observaciones sobre daños puntuales.
- El ingreso al inventario se registra únicamente mediante el atributo fecha_ingreso del ejemplar. No se modela la procedencia de la mercancía ni la entidad que la suministró.
- La condición de conservación se modela como entidad CONDICION con los valores estándar del mercado (Near Mint, Lightly Played, Moderately Played, Heavily Played y Damaged) y un factor de ajuste sobre el precio. Se modela como entidad y no como atributo porque el factor depende de la condición y no del ejemplar, lo que constituiría una dependencia transitiva.
- Un ejemplar en estado reservado no puede ser apartado por otro cliente mientras la reserva siga vigente.
- No se modelan traslados entre ubicaciones, inventarios físicos periódicos, mermas, graduaciones de terceros ni certificados de autenticidad.

# E. Valoración y precios de referencia

- Un PRECIO_REFERENCIA registra el valor de mercado de una impresión en una fecha determinada, junto con la fuente de la cotización. Es una entidad débil de IMPRESION, cuya clave parcial es la fecha.
- El historial se conserva completo: los precios no se sobrescriben, de modo que sea posible analizar la evolución del valor de una impresión en el tiempo.
- El precio sugerido de un ejemplar es un atributo derivado, obtenido al multiplicar el precio de referencia vigente de su impresión por el factor de ajuste de su condición física.
- El sistema admite varias cotizaciones para una misma impresión en una misma fecha, provenientes de fuentes distintas.
- Estos valores son informativos: permiten al cliente saber cuánto vale la carta que aparta y a la tienda conocer el valor de su inventario, pero el sistema no registra ninguna transacción sobre ellos.
- No se modelan reglas de descuento, listas de precios diferenciadas por cliente ni predicciones de valorización.

# F. Personas: clientes y empleados

- Un CLIENTE es toda persona externa que se registra en la página para consultar el catálogo y apartar ejemplares desde su perfil.
- Un EMPLEADO es quien opera el sistema dentro de la tienda, con su cargo, fecha de vinculación y estado.
- Tanto CLIENTE como EMPLEADO tienen como identificador el atributo compuesto documento, descomponible en tipo de documento y número de documento, y un atributo compuesto nombre_completo, descomponible en nombres y apellidos. Ambos se descomponen durante la normalización a 3FN.
- La supervisión entre empleados se modela como relación recursiva SUPERVISA sobre EMPLEADO, con los roles supervisor y supervisado y atributos de fecha de inicio y fecha de fin. Es N:M porque un empleado puede haber sido supervisado por distintas personas en periodos distintos, y la fecha de fin nula indica que la supervisión sigue vigente.
- La participación en SUPERVISA es parcial en ambos extremos: el administrador no tiene supervisor y no todo empleado supervisa a alguien.
- No se modelan jerarquías de personas mediante especialización, ni datos de nómina, contratos laborales, horarios o comisiones por desempeño.

# G. Usuarios, roles y permisos

- El acceso al sistema se realiza mediante una CUENTA_USUARIO identificada por su nombre de usuario, con contraseña cifrada, fecha de último acceso y estado.
- Los roles se manejan en un catálogo ROL con tres valores:
- **Cliente**: consulta el catálogo y la disponibilidad, y crea y cancela sus propias reservas desde su perfil. No ve información interna del inventario ni datos de otros clientes.
- **Empleado**: consulta el inventario completo, registra el ingreso de ejemplares, actualiza la condición y la ubicación, carga precios de referencia y marca reservas como reclamadas o vencidas.
- **Administrador**: además de todo lo del empleado, administra los catálogos del sistema, crea y da de baja usuarios, asigna roles, registra empleados y define la relación de supervisión entre ellos.
- Una cuenta pertenece a un cliente o a un empleado, nunca a ambos: las relaciones con CLIENTE y con EMPLEADO son ambas 1:1 y de participación parcial del lado de la cuenta, y la regla que exige exactamente una de las dos es una restricción de negocio que no se representa en el diagrama.
- Toda operación sobre el inventario y toda atención de una reserva quedan atribuidas al usuario que las registró, lo que permite auditar la responsabilidad sobre cada acción.
- No se modelan permisos granulares por recurso, registros de acceso, sesiones activas, recuperación de contraseña ni analítica de uso por usuario.

# H. Reservas de ejemplares

- Una RESERVA es el apartado que un cliente realiza desde su perfil sobre uno o varios ejemplares disponibles. Se identifica por un consecutivo y registra la fecha en que se creó y su estado.
- La fecha de vencimiento es un atributo derivado: corresponde a la fecha de la reserva más treinta días, que es el plazo máximo que la política de la tienda concede para reclamar los ejemplares apartados.
- El estado de una reserva es vigente, reclamada, vencida o cancelada. Al reclamarse, los ejemplares pasan a estado entregado; al vencerse o cancelarse, regresan a disponible y quedan liberados para otros clientes.
- La liberación por vencimiento es automática: una vez superado el plazo, la reserva deja de tener efecto sobre el inventario sin necesidad de intervención del personal.
- La relación APARTA entre RESERVA y EJEMPLAR es N:M: una reserva puede cubrir varios ejemplares, y un mismo ejemplar puede haber sido apartado en distintas reservas a lo largo del tiempo, siempre que las anteriores hayan vencido o sido canceladas.
- Un ejemplar puede pertenecer a una sola reserva vigente a la vez. Es una restricción de negocio que no se representa en el diagrama.
- El empleado que atiende la entrega queda registrado en la reserva. Su participación es parcial, porque una reserva vencida o cancelada nunca fue atendida.
- El sistema no registra ninguna transacción al momento de reclamar los ejemplares: la entrega y su eventual pago ocurren fuera del alcance del sistema.
- No se modelan listas de espera, notificaciones al cliente, prórrogas del plazo, penalizaciones por incumplimiento ni límites en la cantidad de ejemplares que un cliente puede apartar.

# I. Estadísticas y métricas derivadas

- El valor total del inventario, la disponibilidad real por impresión y el vencimiento de las reservas son datos derivados, calculados a partir de los registros base. No se almacenan duplicados en otras tablas.
- La trazabilidad de un ejemplar —cuándo ingresó, en qué reservas estuvo apartado y cuál fue su destino— se obtiene mediante consultas sobre las relaciones CORRESPONDE_A y APARTA, y no como entidad materializada.
- Las métricas analíticas (rotación por edición, evolución del precio de una impresión, ejemplares apartados frente a disponibles, reservas vencidas por cliente, actividad por usuario) se resuelven por consulta sobre el modelo base y no requieren entidades adicionales.
- No se modelan recálculos programados, marcas de fecha de último cálculo, tableros de indicadores ni auditoría de correcciones posteriores.

# J. Resumen estructural del modelo

Resultado del modelo conceptual final:

- Entidades regulares (14): CARTA, COLOR, TIPO, EDICION, RAREZA, ARTISTA, IDIOMA, CONDICION, EJEMPLAR, CLIENTE, EMPLEADO, ROL, CUENTA_USUARIO, RESERVA.
- Entidades débiles (2): IMPRESION (depende de EDICION y de IDIOMA), PRECIO_REFERENCIA (depende de IMPRESION).
- Total de entidades del MERE: 16.
- Jerarquías ISA: ninguna. Las personas se modelan como entidades independientes CLIENTE y EMPLEADO, sin supertipo común.
- Relaciones identificadoras (3): CONTIENE e IMPRESA_EN (hacia IMPRESION) y SE_COTIZA (hacia PRECIO_REFERENCIA).
- Total de relaciones: 17, distribuidas en 2 de tipo 1:1, 11 de tipo 1:N y 4 de tipo N:M.
- Relaciones 1:1 (2): ACCEDE_CON entre CLIENTE y CUENTA_USUARIO, y ACCEDE_CON entre EMPLEADO y CUENTA_USUARIO.
- Relaciones N:M (4): POSEE (CARTA–COLOR), ES_DE_TIPO (CARTA–TIPO), SUPERVISA (recursiva sobre EMPLEADO) y APARTA (RESERVA–EJEMPLAR).
- Relaciones recursivas (1): SUPERVISA sobre EMPLEADO, con roles supervisor y supervisado.
- Relaciones con atributos propios (1): SUPERVISA (fecha_inicio, fecha_fin).
- Atributos derivados: valor_mana (CARTA), precio_sugerido (EJEMPLAR) y fecha_vencimiento (RESERVA).
- Atributos compuestos: documento y nombre_completo (CLIENTE y EMPLEADO).
- Atributos multivaluados: ninguno. Los colores y los tipos, que serían atributos multivaluados de CARTA, se modelan como entidades relacionadas N:M.
