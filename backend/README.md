# Backend — Magic Cards

API en **Java puro** para el sistema de inventario y reservas. Sin frameworks ni ORM:
el servidor HTTP es el del JDK (`com.sun.net.httpserver`), el SQL se escribe a mano con
JDBC (`PreparedStatement` / `ResultSet`) y el JSON se arma con `JsonUtil`. La base es
**MySQL 8** y el modelo es el de `Scripts.zip`, `Normalizacion_3FN_Magic_BD.pdf` y el
diccionario de datos. El frontend (Angular) consume esta API.

## Abrirlo en VS Code

1. **Archivo → Abrir carpeta…** y elige esta carpeta `backend/` (no la raíz del repo),
   para que VS Code lea `.vscode/settings.json`.
2. Necesitas Java 21 y la extensión *Extension Pack for Java*.
3. Copia **`mysql-connector-j-<versión>.jar`** en `lib/` (ver `lib/LEEME.txt`).
4. Copia `config/configuracion.properties.example` como `config/configuracion.properties`
   y pon la URL, el usuario y la contraseña de MySQL. Ese archivo no se sube a git.
5. Ejecuta con **F5** (configuración *Backend Magic*).

Prueba rápida: abre `http://localhost:8080/api/salud`. Debe responder
`{"servidor":"activo","baseDatos":"conectada"}`.

## Preparar la base

En este orden, con un usuario administrador de MySQL:

1. `DDL.sql` y `DML.sql` de `Scripts.zip`: crean la base `magic_cards` y los datos de prueba.
2. `sql/restricciones.sql` de esta carpeta: agrega los UNIQUE que la normalización dejó
   pendientes (sección 9). **Es necesario:** sin ellos los códigos de los catálogos y el
   nombre de la carta podrían repetirse.
3. `USUARIO_APP.sql` de `Scripts.zip`: crea el usuario `app_web`. Cambien su clave antes.

`DML.sql` trae un administrador (`lgomez`), un empleado (`arojas`) y clientes de prueba.

## Subirlo a la máquina de AWS

En la máquina (ejemplo para Ubuntu), con el repo clonado en `/home/ubuntu/ProyectoBasesDatosI`:

```bash
sudo apt update && sudo apt install -y openjdk-21-jdk-headless mysql-server
cd ~/ProyectoBasesDatosI/backend
cp config/configuracion.properties.example config/configuracion.properties
nano config/configuracion.properties
chmod 600 config/configuracion.properties
chmod +x compilar.sh ejecutar.sh
./compilar.sh
./ejecutar.sh
```

El driver debe estar en `lib/` (o copiarse con `scp`). En `configuracion.properties`,
`cors.origen` debe incluir el origen desde donde se abre Angular, por ejemplo
`http://localhost:4200,http://IP_PUBLICA:4200`.

Para que arranque solo con la máquina, ajusta usuario y ruta en `magic-backend.service` y:

```bash
sudo cp magic-backend.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable --now magic-backend
sudo systemctl status magic-backend
journalctl -u magic-backend -f
```

**En el grupo de seguridad de AWS:**

- Abre el puerto **8080** (API) y el **22** (SSH), este último solo desde tu IP.
- **No abras el 3306.** MySQL y el backend están en la misma máquina: se hablan por
  `localhost`. Por eso conviene crear el usuario como `'app_web'@'localhost'` en lugar del
  `'%'` que trae `USUARIO_APP.sql`.
- La API va por HTTP sin cifrar, así que contraseñas y tokens viajan en claro. Sirve para
  la demostración; para uso real iría detrás de HTTPS.

Si cambias el código en VS Code mientras el backend corre por terminal: ambos usan `bin/`.
Detén uno antes de compilar con el otro.

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
| Catálogos (colores, tipos, ediciones, idiomas, rarezas, artistas, condiciones) | con sesión | ADM | ADM, si nada lo usa |
| Roles | con sesión | — | — |
| Cartas e impresiones | con sesión | EMP, ADM | ADM, si nada las usa |
| Precios | con sesión | EMP, ADM (solo agregar) | — |
| Ejemplares | EMP, ADM; los clientes solo `/disponibles` | EMP, ADM | — (baja lógica) |
| Reservas | el cliente las suyas; EMP y ADM todas | el cliente crea y cancela; EMP y ADM marcan vencida | — |
| Salidas y estadísticas | EMP, ADM | EMP, ADM | — |
| Clientes | EMP, ADM; el cliente solo a sí mismo | registro público; editar: el propio cliente o ADM | — |
| Empleados y cuentas | ADM | ADM | baja lógica |

Nadie se da de baja ni cambia su propio rol, y siempre queda al menos un administrador
activo. Dar de baja o cambiar el rol de una cuenta cierra sus sesiones de inmediato.

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
| PUT | `/api/cuentas/{id}/estado` · `/api/cuentas/{id}/rol` | ACTIVA, INACTIVA o BLOQUEADA · EMP o ADM |

### Catálogos

`GET` (lista y uno), `POST`, `PUT` y `DELETE` en `/api/colores`, `/api/tipos`, `/api/ediciones`,
`/api/idiomas`, `/api/rarezas`, `/api/artistas` y `/api/condiciones`. `/api/roles` solo se consulta.
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
| POST | `/api/precios` | Agrega: `{"idCarta", "numeroImpresion", "valor", "fecha"?, "fuenteCotizacion"?}`. Uno por día; no se modifica |
| GET | `/api/ejemplares/disponibles?idCarta=&numeroImpresion=` | Para clientes: sin ubicación ni observaciones |
| GET | `/api/ejemplares?estado=&idCarta=&numeroImpresion=` · `/api/ejemplares/{id}` | Inventario completo |
| POST | `/api/ejemplares` | Ingreso: `{"codigoEtiqueta", "idCarta", "numeroImpresion", "codigoCondicion", "ubicacion"?, "observacionesDanos"?}` |
| PUT | `/api/ejemplares/{id}` | Cambia ubicación, condición y observaciones |
| PUT | `/api/ejemplares/{id}/baja` | Baja por daño o pérdida (solo si está DISPONIBLE); no genera salida |

`precioSugerido` se calcula una sola vez al ingresar (precio vigente × factor de la condición)
y después no cambia (sección 7.1 de la normalización). Estados del ejemplar: `DISPONIBLE`,
`RESERVADO`, `VENDIDO` y `BAJA`; los cambian las reservas, las salidas y la baja.

### Reservas y salidas

| Método | Ruta | Qué hace |
|---|---|---|
| POST | `/api/reservas` | El cliente aparta: `{"ejemplares": [3, 4]}`. Vence en `reserva.dias` (30) |
| GET | `/api/reservas?estado=` · `/api/reservas/{n}` | El cliente ve las suyas; el personal, todas |
| PUT | `/api/reservas/{n}/cancelar` | El cliente cancela la suya y libera los ejemplares |
| PUT | `/api/reservas/{n}/vencer` | El personal la marca vencida |
| POST | `/api/salidas` | Entrega directa: `{"clienteTipoDocumento", "clienteNumeroDocumento", "ejemplares": [1]}` |
| POST | `/api/salidas` | Reclamo: `{"consecutivoReserva": 1, "ejemplares": [3]}`; sin `ejemplares`, entrega todos |
| GET | `/api/salidas` · `/api/salidas/{n}` | Consulta |

- **Vencimiento automático:** cada consulta u operación libera primero las reservas cuyo
  plazo ya pasó. No hace falta un proceso programado.
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
