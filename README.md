# Proyecto BD I

## Sistema Interno de Inventario y Reservas de Cartas Coleccionables

# Enunciado del problema

Se requiere un sistema de información que modele el inventario de una tienda especializada en cartas coleccionables de *Magic: The Gathering* — catálogo de cartas, ediciones, impresiones y ejemplares físicos — y que permita al personal conocer en todo momento qué tiene la tienda, en qué condición está cada copia, cuánto vale y si está disponible o apartada.

El dominio a cubrir incluye el catálogo de cartas con sus colores, tipos, ediciones, rarezas, artistas e idiomas, junto con el linaje de reimpresiones que enlaza cada reedición con la impresión original de la carta; el inventario identificado ejemplar por ejemplar, con su condición física y su ubicación en el local; el historial de precios de referencia del mercado para efectos de valoración; un esquema de usuarios con tres perfiles que diferencia el acceso administrativo, el operativo y el de consulta; un módulo de reservas mediante el cual un cliente registrado aparta ejemplares desde su perfil en la página, con un plazo máximo de treinta días para reclamarlos; y el registro de las salidas de inventario, que deja constancia de cuándo y a qué cliente se entregó cada ejemplar, tanto si venía de una reserva como si se trató de una entrega directa en mostrador.

Se solicita construir el modelo entidad-relación extendido (MERE) que represente fielmente la operación interna de la tienda según los supuestos definidos en este documento, y que permita, a partir de los datos base, calcular precios sugeridos, disponibilidad real y vencimiento de reservas mediante consultas y agregaciones.

# Supuestos del modelo (reglas y delimitaciones del caso)

# A. Supuestos generales del negocio

- El sistema es de uso interno de una única tienda física y su alcance se limita a la gestión del inventario, a las reservas que los clientes realizan desde la página y al registro de las salidas de mercancía.
- **El sistema no maneja dinero en movimiento.** No se modelan ventas, compras, consignaciones, intercambios, pagos, facturación, cuentas por cobrar ni cuentas por pagar. Los precios que el sistema almacena son valores de referencia del mercado, no transacciones.
- **Todas las relaciones del modelo se nombran con verbos en infinitivo**, terminados en -ar, -er o -ir, y se leen desde la entidad de origen hacia la de destino.
- Toda entidad del sistema tiene identificador único y, cuando corresponde, atributos básicos de auditoría (fecha de ingreso o de registro, estado activo/inactivo).
- Las bajas son lógicas: empleados desvinculados, clientes inactivos y reservas vencidas se marcan con su estado correspondiente pero se conservan para preservar coherencia histórica.
- No se modelan múltiples sucursales, monedas, canales de venta en línea ni integraciones en tiempo real con servicios externos de cotización; se asume una única fuente operativa y los precios de referencia se cargan manualmente.
- La unidad real del inventario no es la carta como concepto, sino el ejemplar físico concreto: cada copia de cartón que la tienda tiene en su poder es un registro independiente.

# B. Catálogo de cartas

- Una CARTA representa el concepto de reglas del juego, identificado por su nombre oficial, con los atributos que dependen únicamente de ese nombre: texto de oráculo, coste de maná, poder, resistencia y lealtad.
- El atributo valor_mana es derivado: se calcula a partir del coste de maná y no se almacena.
- Los colores de una carta se modelan como entidad COLOR, vinculada mediante la relación POSEER (N:M), y no como atributo multivaluado, porque cada color tiene atributos propios (código, nombre y orden canónico) y debe ser un valor controlado.
- Los tipos, supertipos y subtipos se modelan como entidad TIPO, vinculada mediante la relación PERTENECER (N:M), con un atributo que indica a qué nivel de la jerarquía de tipos pertenece cada valor.
- No se modelan formatos de juego, legalidad por formato, mazos, reglas de interacción ni erratas históricas del texto de oráculo.

# C. Ediciones, impresiones e idiomas

- Una EDICION representa la colección en que se publicó un grupo de cartas, identificada por el código impreso en la carta misma, con nombre, fecha de lanzamiento, cantidad de cartas y tipo de edición.
- Una IMPRESION es la aparición concreta de una carta dentro de una edición, en un idioma y un acabado determinados. Su identificación depende parcialmente de la edición y del idioma: es una entidad débil con dos relaciones identificadoras, CONTENER hacia EDICION y EMPLEAR hacia IDIOMA.
- La clave parcial de IMPRESION está compuesta por el número de colección y el indicador de acabado foil, porque la versión foil y la no foil comparten número dentro de una misma edición e idioma.
- La rareza y el artista son atributos de la impresión y no de la carta, porque una misma carta puede ser común en una edición y rara en otra, e ilustrada por artistas distintos. Se modelan como entidades RAREZA y ARTISTA, vinculadas mediante las relaciones CLASIFICAR e ILUSTRAR (ambas 1:N).
- ARTISTA recibe identificador interno propio porque no existe ninguna autoridad que asigne identificadores a ilustradores y un mismo artista puede aparecer acreditado con grafías distintas entre ediciones.
- El linaje de reediciones se modela mediante la relación recursiva REIMPRIMIR sobre IMPRESION, con los roles impresion_original y reimpresion. Es 1:N porque una impresión original puede tener muchas reimpresiones, pero cada reimpresión procede de una sola original.
- REIMPRIMIR lleva como atributo propio tipo_reimpresion, que distingue si la reedición es funcional, con ilustración nueva o con marco nuevo. Ese atributo es lo que hace que la relación aporte información y no sea un simple atajo de consulta.
- La participación en REIMPRIMIR es parcial en ambos extremos: la impresión original no procede de ninguna otra, y una impresión nunca reeditada no tiene reimpresiones asociadas. Una impresión sin vínculo hacia una original es, por definición, una primera edición.
- Toda reimpresión apunta a la impresión original de la carta y no a la inmediatamente anterior, de modo que el linaje se resuelve en un solo paso y sin recorridos recursivos.
- El vínculo de REIMPRIMIR no exige coincidencia de idioma: una impresión en español puede proceder de una original en inglés, porque las ediciones antiguas se publicaron únicamente en ese idioma.
- No se modelan variantes de marco, promociones por evento, cartas de prueba ni la procedencia editorial de la impresión.

# D. Ejemplares y condición física

- Un EJEMPLAR es la copia física individual que la tienda tiene en su poder, identificada por el código de etiqueta que la tienda imprime y adhiere a la funda al momento del ingreso. Si la tienda posee tres copias de la misma impresión, existen tres ejemplares distintos.
- Cada ejemplar registra su estado, su ubicación física dentro del local, su fecha de ingreso al inventario y observaciones sobre daños puntuales.
- El estado de un ejemplar es disponible, reservado, entregado o dado de baja. Los ejemplares dañados, perdidos o extraviados se marcan como dados de baja y no generan salida, porque no se entregan a nadie.
- El ingreso al inventario se registra únicamente mediante el atributo fecha_ingreso del ejemplar. No se modela la procedencia de la mercancía ni la entidad que la suministró.
- La impresión a la que corresponde cada ejemplar se establece mediante la relación CORRESPONDER (1:N).
- La condición de conservación se modela como entidad CONDICION, vinculada mediante la relación CALIFICAR (1:N), con los valores estándar del mercado (Near Mint, Lightly Played, Moderately Played, Heavily Played y Damaged) y un factor de ajuste sobre el precio. Se modela como entidad y no como atributo porque el factor depende de la condición y no del ejemplar, lo que constituiría una dependencia transitiva.
- Un ejemplar en estado reservado no puede ser apartado por otro cliente mientras la reserva siga vigente.
- No se modelan traslados entre ubicaciones, inventarios físicos periódicos, mermas, graduaciones de terceros ni certificados de autenticidad.

# E. Valoración y precios de referencia

- Un PRECIO_REFERENCIA registra el valor de mercado de una impresión en una fecha determinada, junto con la fuente de la cotización. Es una entidad débil de IMPRESION, vinculada mediante la relación identificadora COTIZAR, y su clave parcial es la fecha.
- El historial se conserva completo: los precios no se sobrescriben, de modo que sea posible analizar la evolución del valor de una impresión en el tiempo.
- El precio sugerido de un ejemplar es un atributo derivado, obtenido al multiplicar el precio de referencia vigente de su impresión por el factor de ajuste de su condición física.
- El sistema admite varias cotizaciones para una misma impresión en una misma fecha, provenientes de fuentes distintas.
- Estos valores son informativos: permiten al cliente saber cuánto vale la carta que aparta y a la tienda conocer el valor de su inventario, pero el sistema no registra ninguna transacción sobre ellos.
- No se modelan reglas de descuento, listas de precios diferenciadas por cliente ni predicciones de valorización.

# F. Personas: clientes y empleados

- Un CLIENTE es toda persona externa que se registra en la página para consultar el catálogo y apartar ejemplares desde su perfil.
- Un EMPLEADO es quien opera el sistema dentro de la tienda, con su cargo, fecha de vinculación y estado.
- Tanto CLIENTE como EMPLEADO tienen como identificador el atributo compuesto documento, descomponible en tipo de documento y número de documento, y un atributo compuesto nombre_completo, descomponible en nombres y apellidos. Ambos se descomponen durante la normalización a 3FN.
- No se modelan jerarquías de personas mediante especialización, ni relaciones de supervisión entre empleados, ni datos de nómina, contratos laborales, horarios o comisiones por desempeño. La responsabilidad sobre cada acción se resuelve atribuyendo las salidas al empleado que las registró, no mediante una estructura jerárquica.

# G. Usuarios, roles y permisos

- El acceso al sistema se realiza mediante una CUENTA_USUARIO identificada por su nombre de usuario, con contraseña cifrada, fecha de último acceso y estado.
- Los roles se manejan en un catálogo ROL, vinculado mediante la relación DESEMPEÑAR (1:N), con tres valores:
- **Cliente**: consulta el catálogo y la disponibilidad, y crea y cancela sus propias reservas desde su perfil. No ve información interna del inventario ni datos de otros clientes.
- **Empleado**: consulta el inventario completo, registra el ingreso de ejemplares, actualiza la condición y la ubicación, carga precios de referencia, registra las salidas y marca reservas como reclamadas o vencidas.
- **Administrador**: además de todo lo del empleado, administra los catálogos del sistema, crea y da de baja usuarios, asigna roles y registra empleados.
- El vínculo de la cuenta con su titular se establece mediante dos relaciones distintas: ACCEDER, entre CLIENTE y CUENTA_USUARIO, y OPERAR, entre EMPLEADO y CUENTA_USUARIO. Ambas son 1:1.
- Una cuenta pertenece a un cliente o a un empleado, nunca a ambos: las dos relaciones son de participación parcial del lado de la cuenta, y la regla que exige exactamente una de las dos es una restricción de negocio que no se representa en el diagrama.
- No se modelan permisos granulares por recurso, registros de acceso, sesiones activas, recuperación de contraseña ni analítica de uso por usuario.

# H. Reservas de ejemplares

- Una RESERVA es el apartado que un cliente realiza desde su perfil sobre uno o varios ejemplares disponibles. Se identifica por un consecutivo y registra la fecha en que se creó y su estado. El cliente que la crea se vincula mediante la relación SOLICITAR (1:N).
- La fecha de vencimiento es un atributo derivado: corresponde a la fecha de la reserva más treinta días, que es el plazo máximo que la política de la tienda concede para reclamar los ejemplares apartados.
- El estado de una reserva es vigente, reclamada, vencida o cancelada. Al vencerse o cancelarse, los ejemplares regresan a disponible y quedan liberados para otros clientes.
- La liberación por vencimiento es automática: una vez superado el plazo, la reserva deja de tener efecto sobre el inventario sin necesidad de intervención del personal.
- La relación APARTAR entre RESERVA y EJEMPLAR es N:M: una reserva puede cubrir varios ejemplares, y un mismo ejemplar puede haber sido apartado en distintas reservas, siempre que las anteriores hayan vencido o sido canceladas.
- Un ejemplar puede pertenecer a una sola reserva vigente a la vez. Es una restricción de negocio que no se representa en el diagrama.
- No se modelan listas de espera, notificaciones al cliente, prórrogas del plazo, penalizaciones por incumplimiento ni límites en la cantidad de ejemplares que un cliente puede apartar.

# I. Salidas del inventario

- Una SALIDA registra el momento en que uno o varios ejemplares dejan el inventario al ser entregados a un cliente. Se identifica por un consecutivo y registra la fecha, el motivo y observaciones.
- El motivo de la salida es entrega directa en mostrador o reclamo de una reserva. Las bajas por daño o pérdida no generan salida: se resuelven con el estado del ejemplar, porque no hay entrega a nadie.
- Toda salida tiene un cliente identificado. La relación RECIBIR entre CLIENTE y SALIDA es de participación total del lado de la salida: no se permiten entregas anónimas, de modo que la tienda siempre sabe a quién entregó cada ejemplar.
- Toda salida queda atribuida al empleado que la despachó mediante la relación REGISTRAR (1:N), lo que permite auditar la responsabilidad sobre cada entrega.
- La relación RETIRAR entre SALIDA y EJEMPLAR es 1:N: una misma salida puede llevarse varios ejemplares, pero un ejemplar abandona el inventario una sola vez.
- La relación ORIGINAR entre RESERVA y SALIDA es 1:1 y de participación parcial en ambos extremos: una salida en mostrador no proviene de ninguna reserva, y una reserva vencida o cancelada nunca generó salida.
- El sistema no registra ninguna transacción económica en la salida: la entrega deja constancia del movimiento de inventario, mientras que el pago ocurre fuera del alcance del sistema.
- No se modelan devoluciones posteriores a la entrega, garantías, comprobantes impresos ni despachos a domicilio.

# J. Estadísticas y métricas derivadas

- El valor total del inventario, la disponibilidad real por impresión y el vencimiento de las reservas son datos derivados, calculados a partir de los registros base. No se almacenan duplicados en otras tablas.
- Las métricas analíticas (rotación por edición, evolución del precio de una impresión, ejemplares apartados frente a disponibles, reservas vencidas por cliente, salidas registradas por empleado, cantidad de reediciones por carta) se resuelven por consulta sobre el modelo base y no requieren entidades adicionales.
- No se modelan recálculos programados, marcas de fecha de último cálculo, tableros de indicadores ni auditoría de correcciones posteriores.

# K. Resumen estructural del modelo

Resultado del modelo conceptual final:

- Entidades regulares (15): CARTA, COLOR, TIPO, EDICION, RAREZA, ARTISTA, IDIOMA, CONDICION, EJEMPLAR, CLIENTE, EMPLEADO, ROL, CUENTA_USUARIO, RESERVA, SALIDA.
- Entidades débiles (2): IMPRESION (depende de EDICION y de IDIOMA), PRECIO_REFERENCIA (depende de IMPRESION).
- Total de entidades del MERE: 17.
- Jerarquías ISA: ninguna. Las personas se modelan como entidades independientes CLIENTE y EMPLEADO, sin supertipo común.
- Relaciones identificadoras (3): CONTENER y EMPLEAR (hacia IMPRESION) y COTIZAR (hacia PRECIO_REFERENCIA).
- Total de relaciones: 20, distribuidas en 3 de tipo 1:1, 14 de tipo 1:N y 3 de tipo N:M.
- Relaciones 1:1 (3): ACCEDER (CLIENTE–CUENTA_USUARIO), OPERAR (EMPLEADO–CUENTA_USUARIO) y ORIGINAR (RESERVA–SALIDA).
- Relaciones N:M (3): POSEER (CARTA–COLOR), PERTENECER (CARTA–TIPO) y APARTAR (RESERVA–EJEMPLAR).
- Relaciones 1:N (14): IMPRIMIR, CONTENER, EMPLEAR, CLASIFICAR, ILUSTRAR, REIMPRIMIR, COTIZAR, CORRESPONDER, CALIFICAR, DESEMPEÑAR, SOLICITAR, RECIBIR, REGISTRAR y RETIRAR.
- Relaciones recursivas (1): REIMPRIMIR sobre IMPRESION, con roles impresion_original y reimpresion.
- Relaciones con atributos propios (1): REIMPRIMIR (tipo_reimpresion).
- Atributos derivados: valor_mana (CARTA), precio_sugerido (EJEMPLAR) y fecha_vencimiento (RESERVA).
- Atributos compuestos: documento y nombre_completo (CLIENTE y EMPLEADO).
- Atributos multivaluados: ninguno. Los colores y los tipos, que serían atributos multivaluados de CARTA, se modelan como entidades vinculadas mediante relaciones N:M.
