# Frontend — Card Magic

Tienda y panel del sistema de inventario y reservas de cartas Magic, en **Angular 21**, con
el diseño "Cómic de Batalla" (contorno grueso, sombra dura y colores planos). Corre en
Windows y consume la API del backend Java.

## Arrancarlo

1. Arranca primero el backend (ver `backend/README.md`): debe responder en el puerto 8080.
2. La primera vez, instala las dependencias:

   ```bash
   npm install
   ```

3. Arranca el frontend y abre <http://localhost:4200>:

   ```bash
   npm start
   ```

Las peticiones a `/api` llegan al backend a través de `proxy.conf.json`.

Cuentas de prueba: `lgomez` (administrador), `arojas` (empleado) y `ctorres`, `jperez` y
`smartinez` (clientes). Sus claves están en `DML.sql`.

## Estructura

```
src/app/
├── pages/         una carpeta por pantalla: entrar, registro, catalogo, carta, mis-reservas, perfil
│   └── panel/     lo del personal: inventario, reservas, salidas, precios, cartas, estadísticas,
│                  y solo para administradores: catálogos y usuarios
├── components/    encabezado, estallido (la explosión de cómic), carta-visual (la carta dibujada)
├── services/      api.service (llamadas al backend), sesion.service (token y usuario)
├── interceptors/  agrega el token a cada petición y maneja la sesión vencida
├── guards/        protege las rutas que piden sesión
├── models/        interfaces de los datos de la API
└── utils/         formato de precios, fechas y símbolos de maná
```

Los estilos comunes (`.vineta`, `.boton`, `.campo`, `.sello`, colores y tipografías) están en
`src/styles.css`.

Cada copia física tiene su imagen, obligatoria al registrarla desde el panel. Los ejemplares
precargados que aún no la tengan se dibujan con CSS en `carta-visual`, según el color de la carta.
