# Backend — Magic Cards

API en **Java puro** para el sistema de inventario y reservas. Sin frameworks ni ORM:
el servidor HTTP es el del JDK (`com.sun.net.httpserver`), el SQL se escribe a mano con
JDBC (`PreparedStatement` / `ResultSet`) y el JSON se arma con `JsonUtil`. La base es
**MySQL 8** y el modelo es el de `Scripts.zip`, `Normalizacion_3FN_Magic_BD.pdf` y el
diccionario de datos. El frontend (Angular) consume esta API.

**Dónde corre cada cosa:** el backend y el frontend en Windows; MySQL en la máquina
virtual de AWS (Amazon Linux 2023).

## Abrirlo en VS Code

1. **Archivo → Abrir carpeta…** y elige esta carpeta `backend/` (no la raíz del repo),
   para que VS Code lea `.vscode/settings.json`.
2. Necesitas Java 21 y la extensión *Extension Pack for Java*.
3. El driver de MySQL ya viene en `lib/`.
4. Copia `config/configuracion.properties.example` como `config/configuracion.properties`
   y pon la IP de la máquina y la contraseña de `app_web`. Ese archivo no se sube a git:
   la contraseña se comparte en privado entre el grupo.
5. Ejecuta con **F5** (configuración *Backend Magic*). Si la extensión de Java se queda en *Activating*, usa la terminal:

   ```powershell
   & "C:\Program Files\Java\jdk-21.0.10\bin\javac.exe" -encoding UTF-8 -d bin (Get-ChildItem -Recurse src -Filter *.java).FullName
   & "C:\Program Files\Java\jdk-21.0.10\bin\java.exe" -cp "bin;lib/*" co.edu.unbosque.Aplicacion
   ```

Prueba rápida: abre `http://localhost:8080/api/salud`. Debe responder
`{"servidor":"activo","baseDatos":"conectada"}`.

## La base de datos en AWS

Instalada en la máquina: MySQL 8.4, con la base `magic_cards` creada con `DDL.sql` de
`Scripts.zip` más `sql/restricciones.sql`, que agrega los UNIQUE que la normalización dejó
pendientes (sección 9). Los catálogos básicos se cargan con `sql/catalogos_base.sql`, y
`sql/imagenes.sql` crea la tabla de las imágenes en una base anterior a ese `DDL.sql`.

La base solo conserva las cuentas de `DML.sql`, un administrador (`lgomez`), un empleado
(`arojas`) y clientes de prueba, con sus roles y los catálogos básicos. Cartas, impresiones,
precios, ejemplares, reservas, salidas, ediciones y artistas se cargan desde el panel.

**Para conectarse desde Windows,** el grupo de seguridad de la instancia debe permitir el
puerto **3306** desde la IP de quien lo use (regla *MySQL/Aurora*, origen *My IP*), y el **22**
para administrarla. Si cambia la IP de tu casa, hay que actualizar la regla. Nunca abras el
3306 a `0.0.0.0/0`.

**La IP pública cambia** cada vez que se apaga y se prende la instancia. Hay que actualizar
`bd.url`, o asignarle una *Elastic IP* para que quede fija.

**Administrar MySQL:** entra por SSH y usa `sudo mysql`; la clave de root está en
`/root/.my.cnf`, dentro de la máquina.

```bash
ssh -i clave-aws.pem ec2-user@IP_DE_LA_MAQUINA
```

El usuario de la aplicación es `app_web`, con permisos solo de `SELECT`, `INSERT`, `UPDATE`
y `DELETE` sobre `magic_cards`, y exige conexión cifrada. No se usa `USUARIO_APP.sql`
porque su clave está publicada en el repositorio.

## Estructura

```
src/co/edu/unbosque/
├── Aplicacion.java   arranque: lee el puerto y levanta el servidor
├── model/            una clase por tabla del modelo en 3FN (getters y setters)
├── dto/              records que viajan entre capas
├── mapper/           model <-> dto
├── dao/              interfaces (crearX, actualizarX, mostrarX, buscarX)
├── dao/impl/         SQL a mano con JDBC y transacciones
├── controller/       reglas de negocio y validaciones; solo habla en DTOs
├── api/              rutas HTTP: leen el JSON, llaman al controller y responden
└── util/             ConexionBD, Configuracion, JsonUtil, HashUtil, FechaUtil, GestorSesiones...
```

El paquete `api/` cumple el papel que en los proyectos de escritorio tenía `view/`:
es la frontera con el usuario, que ahora es Angular.

## Inicio de sesión y permisos

1. `POST /api/sesion` con `{"nombreUsuario": "...", "contrasena": "..."}` devuelve un `token`.
2. Angular lo envía en cada petición: `Authorization: Bearer <token>`.
3. `DELETE /api/sesion` cierra la sesión. Las sesiones duran `sesion.horas` (8 por defecto).

Las sesiones viven en memoria y no en la base, porque el documento de supuestos excluye
las sesiones activas del modelo (sección G). Si el backend se reinicia, todos vuelven a
iniciar sesión. Sin token válido la API responde `401`; con un rol sin permiso, `403`.
Las contraseñas se guardan con SHA-256, igual que `SHA2(texto, 256)` en el DML.

| Recurso | Consultar | Crear / modificar | Eliminar |
|---|---|---|---|
| Catálogos (colores, tipos, ediciones, idiomas, rarezas, artistas, condiciones) | con sesión | ADM | Colores, tipos, idiomas, rarezas y condiciones: nunca. Ediciones y artistas: ADM, si nada lo usa |
| Roles | con sesión | — | — |
| Cartas e impresiones | con sesión | EMP, ADM | ADM, si nada las usa |
| Precios | con sesión | EMP, ADM (solo agregar) | — |
| Ejemplares | EMP, ADM; los clientes solo `/disponibles` | EMP, ADM | — (baja lógica) |
| Reservas | el cliente las suyas; EMP y ADM todas | el cliente crea y cancela; vencen solas | — |
| Salidas y estadísticas | EMP, ADM | EMP, ADM | — |
| Clientes | EMP, ADM; el cliente solo a sí mismo | registro público; editar: el propio cliente o ADM | — |
| Empleados y cuentas | ADM | ADM | baja lógica |

Nadie se da de baja ni cambia su propio rol, y siempre queda al menos un administrador
activo. Cambiar el estado o el rol de una cuenta cierra sus sesiones de inmediato.

Una cuenta **bloqueada** entra y puede consultar, pero no reservar ni modificar nada, y se
puede desbloquear. Una cuenta **dada de baja** ya no entra y la baja es definitiva.

## Rutas

Cuando se rechaza algo, el error explica el motivo: `{"error": "..."}`. Códigos: `400` datos
inválidos, `401` sin sesión, `403` sin permiso, `404` no existe, `405` método no permitido,
`409` en uso o conflicto, `500` error interno. Las fechas viajan como `AAAA-MM-DD`.

### Cuentas y personas

| Método | Ruta | Qué hace |
|---|---|---|
| POST / GET / DELETE | `/api/sesion` | Inicia sesión / quién está conectado / cierra sesión |
| PUT | `/api/sesion/contrasena` | Cambia la propia contraseña: `{"actual": "...", "nueva": "..."}` |
| POST | `/api/clientes` | Registro de cliente desde la página (crea su cuenta) |
| GET / PUT | `/api/clientes` · `/api/clientes/{tipo}/{numero}` | Lista, consulta o edita nombres |
| GET / POST | `/api/empleados` | Lista o registra empleado con su cuenta y rol (`EMP` o `ADM`) |
| GET / PUT / DELETE | `/api/empleados/{tipo}/{numero}` | Consulta, edita o da de baja (queda RETIRADO) |
| GET | `/api/cuentas` · `/api/cuentas/{id}` | Cuentas, sin contraseña |
| PUT | `/api/cuentas/{id}/estado` · `/api/cuentas/{id}/rol` | ACTIVA, BLOQUEADA o INACTIVA (definitiva) · EMP o ADM |

### Catálogos

`GET` (lista y uno), `POST` y `PUT` en `/api/colores`, `/api/tipos`, `/api/ediciones`,
`/api/idiomas`, `/api/rarezas`, `/api/artistas` y `/api/condiciones`. `/api/roles` solo se consulta.
`DELETE` solo existe en `/api/ediciones` y `/api/artistas`: los demás son catálogos básicos y
responden `405`.
Las llaves numéricas las genera MySQL: en el POST no se envían, y el `201` trae el registro con su id.

### Cartas e impresiones

| Método | Ruta | Qué hace |
|---|---|---|
| GET | `/api/cartas` · `/api/cartas?nombre=bolt` · `/api/cartas/{id}` | Lista, busca por parte del nombre o trae una, con `valorMana` calculado |
| POST / PUT | `/api/cartas` · `/api/cartas/{id}` | Guarda la carta con sus `colores` y `tipos` (listas de id) en una transacción |
| DELETE | `/api/cartas/{id}` | Solo si no tiene impresiones |
| GET | `/api/impresiones` · `?idCarta=1` · `/api/impresiones/{idCarta}/{numero}` | Consulta; `primeraEdicion` es derivado |
| GET | `/api/impresiones/{idCarta}/{numero}/reimpresiones` | Reimpresiones de una original |
| POST | `/api/impresiones` | Crea; el `numeroImpresion` lo asigna el backend |
| PUT / DELETE | `/api/impresiones/{idCarta}/{numero}` | Actualiza o elimina |

Una reimpresión apunta a la original de la misma carta, nunca a otra reimpresión.

### Inventario

| Método | Ruta | Qué hace |
|---|---|---|
| GET | `/api/precios?idCarta=1&numeroImpresion=2` | Historial, del más reciente al más antiguo |
| GET | `/api/precios/vigente?idCarta=1&numeroImpresion=2` | Precio de la fecha más reciente |
| POST | `/api/precios` | Cambia el precio: `{"idCarta", "numeroImpresion", "valor", "fuenteCotizacion"?}`. Máximo una vez al día; la fecha siempre es la de hoy y el historial no se edita |
| GET | `/api/ejemplares/disponibles?idCarta=&numeroImpresion=` | Para clientes: sin ubicación ni observaciones |
| GET | `/api/ejemplares/copias?idCarta=` | Para la ficha de una carta: libres y apartadas, sin datos internos |
| GET | `/api/ejemplares?estado=&idCarta=&numeroImpresion=` · `/api/ejemplares/{id}` | Inventario completo |
| POST | `/api/ejemplares` | Ingreso: `{"codigoEtiqueta", "idCarta", "numeroImpresion", "codigoCondicion", "imagen", "ubicacion"?, "observacionesDanos"?}` |
| PUT | `/api/ejemplares/{id}` | Cambia ubicación, condición y observaciones |
| PUT | `/api/ejemplares/{id}/baja` | Baja por daño o pérdida (solo si está DISPONIBLE); no genera salida |

**La imagen es obligatoria** al registrar un ejemplar: va en el campo `imagen` (JPG, PNG o WEBP
en Base64, hasta 5 MB) y sin ella no se crea nada. Se guarda en la tabla `imagen_ejemplar` de la base, para que todos
los equipos vean las mismas, y se consulta con `GET /api/ejemplares/{id}/imagen`, que es público para poder usarla en un `<img>`.
Con `PUT /api/ejemplares/{id}/imagen`, cuyo cuerpo es el archivo, se reemplaza; no se puede quitar.
Los ejemplares precargados por `DML.sql` no tienen imagen: se suben desde el panel.

`precioSugerido` es el precio vigente × el factor de la condición. Se recalcula al cargar un
precio, al cambiar la condición de la copia y al cambiar el factor en el catálogo. Las copias
`VENDIDO` y `BAJA` conservan el último y ya no se pueden editar. Estados del ejemplar:
`DISPONIBLE`, `RESERVADO`, `VENDIDO` y `BAJA`; los cambian las reservas, las salidas y la baja.

### Reservas y salidas

| Método | Ruta | Qué hace |
|---|---|---|
| POST | `/api/reservas` | El cliente aparta: `{"ejemplares": [3, 4]}`. Vence en `reserva.dias` (30) |
| GET | `/api/reservas?estado=` · `/api/reservas/{n}` | El cliente ve las suyas; el personal, todas |
| PUT | `/api/reservas/{n}/cancelar` | El cliente cancela la suya y libera los ejemplares |
| POST | `/api/salidas` | Entrega directa: `{"clienteTipoDocumento", "clienteNumeroDocumento", "ejemplares": [1]}` |
| POST | `/api/salidas` | Reclamo: `{"consecutivoReserva": 1, "ejemplares": [3]}`; sin `ejemplares`, entrega todos |
| GET | `/api/salidas` · `/api/salidas/{n}` | Consulta |

- **Vencimiento automático:** cada consulta u operación libera primero las reservas cuyo
  plazo ya pasó. Nadie las marca a mano y no hace falta un proceso programado.
- **Dos personas no pueden apartar la misma copia:** reservar y entregar bloquean los
  ejemplares (`SELECT ... FOR UPDATE`) dentro de una transacción.
- **Reclamo parcial:** lo que no se recoja vuelve a DISPONIBLE y la reserva queda ENTREGADA,
  porque una reserva origina una sola salida.
- La salida queda a nombre del empleado de la sesión, y en un reclamo el cliente siempre
  es el de la reserva.

### Estadísticas

`GET /api/estadisticas/{metrica}`: `valor-inventario`, `ejemplares-por-estado`, `disponibilidad`,
`rotacion-edicion`, `reservas-vencidas`, `salidas-empleado`, `reediciones`. Se calculan al consultar.

## Agregar un módulo nuevo

1. `model/` con los mismos atributos de la tabla, y `dto/` (record) con su `mapper/`.
2. `dao/` (interfaz) y `dao/impl/` con el SQL. Tablas y columnas en minúscula: en Linux,
   MySQL distingue mayúsculas en los nombres de tabla.
3. `controller/` con las reglas: retorna `null` si todo está bien o el motivo del rechazo.
4. `api/` extendiendo `ManejadorBase`, con `rolesPermitidos` si no es el permiso por defecto.
5. Registrar la ruta en `api/Servidor.java`.
