# Proyecto Final — Bases de Datos I

Sistema interno de inventario y reservas para una tienda de cartas coleccionables de *Magic: The Gathering*.

**Universidad El Bosque** · Programa de Ingeniería de Sistemas
**Asignatura:** Bases de Datos 1 · **Docente:** Ing., Esp., M.Ed. Christian Felipe Duarte
**Periodo:** 2026-2

## Integrantes

- Andres Camilo Pedraza Reyes
- Daniel Felipe Gomez Veru
- Angélica María Gomez Calderon

## Descripción

El sistema modela el inventario de la tienda ejemplar por ejemplar: qué cartas tiene, en qué condición está cada copia, cuánto vale según su precio de referencia y si está disponible o apartada. Los clientes registrados pueden reservar ejemplares desde su perfil en la página, con un plazo máximo de 30 días para reclamarlos; vencido el plazo, la reserva se libera automáticamente. Cada entrega de ejemplares a un cliente queda registrada como salida del inventario.

El sistema no maneja ventas, compras, pagos ni facturación. Tiene tres perfiles de usuario: cliente, empleado y administrador.

El enunciado completo y los supuestos del modelo están en [Propuesta_Proyecto_BD1.pdf](Propuesta_Proyecto_BD1.pdf).

## Entregables

### Fase 1 — Diseño y modelado

- [x] Documento de la propuesta del proyecto — [Propuesta_Proyecto_BD1.pdf](Propuesta_Proyecto_BD1.pdf)
- [x] Documento de supuestos — incluido en la propuesta
- [ ] Modelo Entidad-Relación con notación Peter-Chen
- [ ] Modelo Entidad-Relación Extendido
- [ ] Modelo Entidad-Relación con notación Crow's Foot
- [ ] Modelo Relacional con notación Crow's Foot
- [ ] Diccionario de datos
- [ ] Documento de álgebra relacional (mínimo 50 sentencias)

### Fase 2 — Implementación

- [ ] Análisis de selección de Sistema Operativo
- [ ] Documento de implementación de la máquina virtual
- [ ] Análisis de selección de RDBMS
- [ ] Documento de implementación del RDBMS
- [ ] Scripts de creación de objetos
- [ ] Scripts de inserción de registros
- [ ] Scripts varios y README
- [ ] Conexión de la aplicación web a la base de datos
- [ ] Video de demostración (máximo 15 minutos)

### Transversales

- [ ] Acta de constitución del proyecto
- [ ] Identificación de interesados
- [ ] EDT / WBS
- [ ] Cronograma
- [ ] Presupuesto
- [ ] Acta de cierre

## Restricciones técnicas

- Sistema operativo: solo distribuciones Linux o Unix
- RDBMS: cualquiera excepto SQL Server
- Modelo E-R: mínimo 8 entidades con relaciones 1:1, 1:N y N:M
- Normalización hasta 3FN
- Mínimo 50 sentencias de álgebra relacional
- Video de máximo 15 minutos, con todos los integrantes en cámara y audio

## Convenciones de trabajo

- Hacer `git pull` antes de empezar a trabajar y antes de cada `git push`.
- Mensajes de commit descriptivos en español, indicando qué entregable se avanza.
- Nunca subir credenciales, archivos de máquina virtual ni volcados de base de datos (ver [.gitignore](.gitignore)).
