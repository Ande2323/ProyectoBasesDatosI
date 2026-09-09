# Scripts de base de datos

- `ddl/` — Creación de objetos: tablas, claves primarias y foráneas, restricciones e índices.
- `dml/` — Inserción de registros de prueba.
- `consultas/` — Consultas de negocio y scripts varios.

## Orden de ejecución

1. `ddl/01_crear_tablas.sql`
2. `ddl/02_restricciones.sql`
3. `dml/01_catalogo.sql` — catálogos base: colores, tipos, rarezas, idiomas, condiciones
4. `dml/02_cartas_impresiones.sql`
5. `dml/03_personas.sql`
6. `dml/04_operaciones.sql`

Los nombres son la convención propuesta; ajustar a medida que se creen los scripts.
