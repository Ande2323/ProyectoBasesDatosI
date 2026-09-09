# Gestión del Proyecto — Entregables Transversales

**Proyecto:** Sistema de gestión de inventario, consignaciones e intercambios para una tienda de cartas coleccionables de *Magic: The Gathering*
**Universidad El Bosque** · Programa de Ingeniería de Sistemas
**Asignatura:** Bases de Datos 1 · **Docente:** Ing., Esp., M.Ed. Christian Felipe Duarte
**Fecha:** 8 de septiembre de 2026

Este documento reúne los seis entregables transversales obligatorios del proyecto final, más una matriz de riesgos como elemento complementario.

---

# 1. Acta de Constitución del Proyecto

## 1.1 Información general

| Campo | Contenido |
|---|---|
| **Nombre del proyecto** | Sistema de gestión de inventario, consignaciones e intercambios para una tienda de cartas coleccionables |
| **Código** | PF-BD1-2026 |
| **Patrocinador** | Ing., Esp., M.Ed. Christian Felipe Duarte — Docente de Bases de Datos 1 |
| **Gerente del proyecto** | _(por asignar entre los integrantes)_ |
| **Equipo de trabajo** | _(por completar: 3 integrantes)_ |
| **Fecha de inicio** | _(por definir — Semana 1 del periodo académico)_ |
| **Fecha de finalización** | _(por definir — Semana 14)_ |
| **Duración** | 14 semanas |
| **Peso académico** | 16% del semestre |

## 1.2 Propósito y justificación

Una tienda especializada en cartas coleccionables administra su inventario, sus consignaciones y los intercambios entre clientes mediante hojas de cálculo y registros informales. Esto impide identificar cada ejemplar físico de forma individual, lo que genera ventas indebidas de cartas consignadas o comprometidas, cuentas poco claras con los consignantes, precios fijados sin criterio y ausencia total de trazabilidad y auditoría.

El proyecto desarrolla una base de datos relacional normalizada que resuelve esta problemática, aplicando de forma integral los conceptos de la asignatura: modelado conceptual y lógico, normalización hasta 3FN, álgebra relacional, virtualización, implementación de un RDBMS y conectividad con una aplicación web.

## 1.3 Objetivo general

Diseñar e implementar una base de datos relacional, normalizada hasta la Tercera Forma Normal, que soporte la gestión de inventario, consignaciones e intercambios de una tienda de cartas coleccionables, accesible desde una aplicación web de demostración desplegada sobre una máquina virtual con sistema operativo Linux.

## 1.4 Alcance de alto nivel

**Incluye:** catálogo de cartas con separación entre carta e impresión, inventario por ejemplar físico, valoración con historial de precios y ajuste por condición, compras a proveedores, ventas, contratos de consignación con liquidación de comisiones, intercambios entre clientes avalados por la tienda, y administración de empleados con roles y auditoría de operaciones.

**Excluye:** organización de torneos, producto sellado, reservas con abono parcial, compra directa a clientes (*buylist*), pasarelas de pago, facturación electrónica, logística de envíos e integración en tiempo real con servicios externos de precios.

## 1.5 Entregables principales

| Fase | Entregables |
|---|---|
| **Fase 1** | Documento de propuesta · Documento de supuestos · Modelo E-R Peter-Chen · Modelo E-R Extendido · Modelo E-R Crow's Foot · Modelo Relacional Crow's Foot · Diccionario de datos · 50 sentencias de álgebra relacional |
| **Fase 2** | Análisis de selección de SO · Documento de implementación de la VM · Análisis de selección de RDBMS · Documento de implementación del RDBMS · Scripts de creación, inserción y varios con README · Aplicación web conectada · Repositorio GitHub · Video de demostración |
| **Transversales** | Acta de constitución · Identificación de interesados · EDT/WBS · Cronograma · Presupuesto · Acta de cierre |

## 1.6 Hitos principales

| Hito | Semana |
|---|---|
| H1 — Propuesta entregada y aprobada por el docente | 2 |
| H2 — Modelo conceptual completo (Peter-Chen y extendido) | 4 |
| H3 — Modelo lógico normalizado a 3FN | 6 |
| H4 — Entrega de Fase 1 | 8 |
| H5 — Infraestructura operativa (VM y RDBMS) | 10 |
| H6 — Modelo físico implementado y poblado | 12 |
| H7 — Aplicación web conectada y funcional | 13 |
| H8 — Entrega de Fase 2 y sustentación | 14 |

## 1.7 Supuestos

1. Los integrantes disponen de equipos con capacidad suficiente para ejecutar una máquina virtual (mínimo 8 GB de RAM y 40 GB de disco libre).
2. Los datos de catálogo de *Magic: The Gathering* son de acceso público y pueden usarse para poblar la base de datos.
3. La problemática es simulada; no existe una tienda real que actúe como cliente, por lo que los requerimientos los define el equipo con base en el funcionamiento habitual del negocio.
4. El docente actúa simultáneamente como patrocinador y evaluador del proyecto.
5. Los tres integrantes mantienen disponibilidad durante las 14 semanas del periodo académico.

## 1.8 Restricciones

| Tipo | Restricción |
|---|---|
| **Técnica** | Solo se permiten distribuciones Linux o Unix como sistema operativo |
| **Técnica** | Se prohíbe el uso de SQL Server como RDBMS |
| **Técnica** | El modelo E-R debe tener mínimo 8 entidades y los tres tipos de relación |
| **Técnica** | La base de datos debe normalizarse hasta 3FN |
| **Alcance** | Mínimo 50 sentencias de álgebra relacional documentadas |
| **Tiempo** | 14 semanas improrrogables, sujetas al calendario académico |
| **Recurso** | Equipo fijo de 3 estudiantes, sin posibilidad de ampliación |
| **Entrega** | El video de demostración no puede exceder 15 minutos y debe incluir a todos los integrantes con audio y cámara |
| **Herramienta** | La gestión de código y documentación debe realizarse en GitHub |

## 1.9 Criterios de éxito

1. La propuesta es aprobada por el docente sin necesidad de replantear la problemática.
2. El modelo relacional alcanza 3FN de forma demostrable, con las dependencias funcionales documentadas.
3. Los scripts de creación e inserción se ejecutan sin errores sobre el RDBMS instalado en la máquina virtual.
4. La aplicación web consulta y muestra datos reales provenientes de la base de datos.
5. El repositorio de GitHub evidencia contribuciones significativas de los tres integrantes.
6. Todos los entregables de ambas fases se presentan dentro de los plazos establecidos.

## 1.10 Aprobación

| Rol | Nombre | Firma | Fecha |
|---|---|---|---|
| Patrocinador | Ing. Christian Felipe Duarte | | |
| Gerente del proyecto | _(por completar)_ | | |
| Integrante | _(por completar)_ | | |
| Integrante | _(por completar)_ | | |

---

# 2. Identificación de Interesados

## 2.1 Registro de interesados

| # | Interesado | Rol | Interés en el proyecto | Poder | Interés |
|---|---|---|---|---|---|
| I-01 | Docente de la asignatura | Patrocinador y evaluador | Que el proyecto demuestre dominio de los conceptos del curso | Alto | Alto |
| I-02 | Integrantes del equipo | Ejecutores | Aprobar la asignatura y adquirir experiencia práctica | Alto | Alto |
| I-03 | Programa de Ingeniería de Sistemas | Institución | Cumplimiento de los resultados de aprendizaje | Medio | Bajo |
| I-04 | Dueño de la tienda (cliente simulado) | Cliente | Resolver la pérdida de trazabilidad y las cuentas con consignantes | Alto | Alto |
| I-05 | Empleados de la tienda | Usuarios finales | Registrar operaciones sin ambigüedad y sin trabajo manual | Bajo | Alto |
| I-06 | Clientes consignantes | Usuarios indirectos | Transparencia sobre sus cartas y sus liquidaciones | Bajo | Alto |
| I-07 | Clientes compradores y participantes en trades | Usuarios indirectos | Precios justos y respaldo en los intercambios | Bajo | Medio |
| I-08 | Proveedores de la tienda | Externos | Registro correcto de sus facturas | Bajo | Bajo |
| I-09 | Compañeros de curso | Audiencia | Referencia para sus propios proyectos | Bajo | Bajo |

## 2.2 Matriz poder / interés

|  | **Interés bajo** | **Interés alto** |
|---|---|---|
| **Poder alto** | Mantener satisfecho: I-03 | **Gestionar de cerca:** I-01, I-02, I-04 |
| **Poder bajo** | Monitorear: I-08, I-09 | Mantener informado: I-05, I-06, I-07 |

## 2.3 Estrategia de gestión

| Interesado | Estrategia |
|---|---|
| I-01 Docente | Validar la propuesta antes de avanzar, consultar dudas de modelado en clase y entregar dentro de los plazos |
| I-02 Equipo | Reuniones semanales de seguimiento, reparto explícito de responsabilidades y uso disciplinado de Git |
| I-04 Cliente simulado | Documentar los requerimientos funcionales y validar los supuestos del negocio antes de modelar |
| I-05, I-06, I-07 Usuarios | Recoger sus necesidades como requerimientos funcionales y reflejarlas en las consultas de la aplicación web |
| I-03, I-08, I-09 | Monitoreo pasivo; no requieren gestión activa |

---

# 3. Estructura de Desglose del Trabajo (EDT/WBS)

## 3.1 Descomposición jerárquica

```
PF-BD1-2026 — Sistema de gestión de cartas coleccionables
│
├── 1. GESTIÓN DEL PROYECTO
│   ├── 1.1 Acta de constitución
│   ├── 1.2 Identificación de interesados
│   ├── 1.3 EDT/WBS
│   ├── 1.4 Cronograma
│   ├── 1.5 Presupuesto
│   ├── 1.6 Seguimiento y control semanal
│   └── 1.7 Acta de cierre
│
├── 2. FASE 1 — DISEÑO Y MODELADO
│   ├── 2.1 Propuesta
│   │   ├── 2.1.1 Definición de la problemática
│   │   ├── 2.1.2 Documento de propuesta
│   │   └── 2.1.3 Documento de supuestos
│   ├── 2.2 Modelado conceptual
│   │   ├── 2.2.1 Diagrama E-R notación Peter-Chen
│   │   └── 2.2.2 Diagrama E-R Extendido
│   ├── 2.3 Modelado lógico
│   │   ├── 2.3.1 Diagrama E-R notación Crow's Foot
│   │   ├── 2.3.2 Modelo relacional Crow's Foot
│   │   └── 2.3.3 Normalización hasta 3FN
│   ├── 2.4 Diccionario de datos
│   └── 2.5 Álgebra relacional
│       ├── 2.5.1 Formulación de 50 sentencias
│       └── 2.5.2 Documento de implementación
│
├── 3. FASE 2 — IMPLEMENTACIÓN
│   ├── 3.1 Infraestructura
│   │   ├── 3.1.1 Análisis de selección de Sistema Operativo
│   │   ├── 3.1.2 Implementación de la máquina virtual
│   │   ├── 3.1.3 Análisis de selección de RDBMS
│   │   └── 3.1.4 Implementación del RDBMS
│   └── 3.2 Modelo físico
│       ├── 3.2.1 Scripts de creación de objetos
│       ├── 3.2.2 Scripts de inserción de registros
│       ├── 3.2.3 Scripts varios
│       └── 3.2.4 Documento README
│
├── 4. APLICACIÓN WEB
│   ├── 4.1 Diseño de la interfaz
│   ├── 4.2 Capa de conexión a la base de datos
│   ├── 4.3 Consultas de catálogo e inventario
│   └── 4.4 Pruebas de conectividad y funcionamiento
│
└── 5. CIERRE
    ├── 5.1 Consolidación del repositorio en GitHub
    ├── 5.2 Video de demostración
    ├── 5.3 Documento final
    └── 5.4 Sustentación
```

## 3.2 Diccionario de la EDT — paquetes de trabajo

| Código | Paquete de trabajo | Entregable verificable | Responsable |
|---|---|---|---|
| 1.1–1.7 | Gestión del proyecto | Documentos de gestión | Gerente del proyecto |
| 2.1 | Propuesta | `PROPUESTA_PROYECTO.md` aprobado por el docente | Todo el equipo |
| 2.2 | Modelado conceptual | Diagramas Peter-Chen y extendido | _(por asignar)_ |
| 2.3 | Modelado lógico | Diagramas Crow's Foot en Data Modeler o Workbench | _(por asignar)_ |
| 2.4 | Diccionario de datos | `DICCIONARIO_DATOS.md` | _(por asignar)_ |
| 2.5 | Álgebra relacional | Documento con 50 sentencias resueltas | Todo el equipo |
| 3.1 | Infraestructura | VM Linux operativa con RDBMS instalado | _(por asignar)_ |
| 3.2 | Modelo físico | Scripts ejecutables con README | _(por asignar)_ |
| 4.1–4.4 | Aplicación web | Aplicación conectada a la base de datos | _(por asignar)_ |
| 5.1–5.4 | Cierre | Repositorio, video, documento final y sustentación | Todo el equipo |

---

# 4. Cronograma

> Las fechas se anclan al calendario académico del periodo. Ajustar la columna de fechas una vez se confirme la Semana 1.

| Semana | Actividad principal | Entregable / Hito | Responsable |
|---|---|---|---|
| 1 | Conformación del equipo, definición de la problemática, acta de constitución | Documentos de gestión iniciales | Todo el equipo |
| 2 | Redacción de la propuesta y del documento de supuestos | **H1** — Entrega de la propuesta | Todo el equipo |
| 3 | Elaboración del diagrama E-R en notación Peter-Chen | Diagrama Peter-Chen | _(por asignar)_ |
| 4 | Diagrama E-R Extendido y revisión del modelo conceptual | **H2** — Modelo conceptual completo | _(por asignar)_ |
| 5 | Diagrama E-R en notación Crow's Foot con Data Modeler o Workbench | Diagrama Crow's Foot | _(por asignar)_ |
| 6 | Modelo relacional y normalización hasta 3FN | **H3** — Modelo lógico normalizado | _(por asignar)_ |
| 7 | Elaboración del diccionario de datos | Diccionario de datos | _(por asignar)_ |
| 8 | Formulación y documentación de las 50 sentencias de álgebra relacional | **H4** — Entrega de Fase 1 | Todo el equipo |
| 9 | Análisis de selección de SO e implementación de la máquina virtual | Documentos de SO y VM | _(por asignar)_ |
| 10 | Análisis de selección de RDBMS e instalación | **H5** — Infraestructura operativa | _(por asignar)_ |
| 11 | Scripts de creación de objetos (DDL) | Scripts DDL | _(por asignar)_ |
| 12 | Scripts de inserción y scripts varios, con README | **H6** — Modelo físico poblado | _(por asignar)_ |
| 13 | Desarrollo de la aplicación web y conexión a la base de datos | **H7** — Aplicación funcional | _(por asignar)_ |
| 14 | Grabación del video, acta de cierre y sustentación | **H8** — Entrega de Fase 2 | Todo el equipo |

## 4.1 Dependencias críticas

| Actividad | Depende de | Riesgo si se retrasa |
|---|---|---|
| Modelado conceptual (S3) | Aprobación de la propuesta (S2) | Modelar sobre una problemática no aprobada obliga a rehacer el trabajo |
| Modelo lógico (S5–S6) | Modelo conceptual (S3–S4) | Retrasa toda la Fase 1 en cascada |
| Scripts DDL (S11) | RDBMS instalado (S10) y modelo lógico (S6) | Bloquea la inserción de datos y la aplicación web |
| Aplicación web (S13) | Base de datos poblada (S12) | No hay datos que mostrar en la demostración |
| Video (S14) | Aplicación funcional (S13) | Sin margen de maniobra: es la última semana |

---

# 5. Presupuesto

> Valores estimados en pesos colombianos (COP). El proyecto es de naturaleza académica, por lo que el costo predominante es el talento humano valorado a precio de referencia de mercado.

## 5.1 Talento humano

| Concepto | Cantidad | Valor unitario | Subtotal |
|---|---|---|---|
| Horas de trabajo del equipo (3 integrantes × 8 h/semana × 14 semanas) | 336 h | $15.000 | **$5.040.000** |

## 5.2 Infraestructura y recursos

| Concepto | Cantidad | Valor unitario | Subtotal |
|---|---|---|---|
| Depreciación de equipos de cómputo (3 portátiles, 3,5 meses) | 3 | $100.000 | $300.000 |
| Servicio de internet (prorrateado por el periodo) | 3 | $100.000 | $300.000 |
| Consumo de energía eléctrica | — | $150.000 | $150.000 |
| Papelería, impresiones y material de sustentación | — | $80.000 | $80.000 |
| **Subtotal infraestructura** | | | **$830.000** |

## 5.3 Software

| Concepto | Licenciamiento | Costo |
|---|---|---|
| Sistema operativo (distribución Linux) | Código abierto | $0 |
| RDBMS (MySQL, MariaDB o PostgreSQL) | Código abierto | $0 |
| Software de virtualización (VirtualBox) | Código abierto | $0 |
| Herramienta de modelado (SQL Data Modeler o Workbench) | Gratuita | $0 |
| Entorno de desarrollo y control de versiones | Gratuito | $0 |
| **Subtotal software** | | **$0** |

## 5.4 Resumen

| Categoría | Valor |
|---|---|
| Talento humano | $5.040.000 |
| Infraestructura y recursos | $830.000 |
| Software | $0 |
| **Subtotal** | **$5.870.000** |
| Reserva de contingencia (10%) | $587.000 |
| **TOTAL ESTIMADO** | **$6.457.000** |

La reserva de contingencia cubre imprevistos como la necesidad de repetir la grabación del video, reprocesos de modelado tras la retroalimentación del docente o fallas de hardware que obliguen a reinstalar la máquina virtual.

---

# 6. Matriz de Riesgos

| # | Riesgo | Probabilidad | Impacto | Respuesta |
|---|---|---|---|---|
| R-01 | La propuesta no es aprobada y debe replantearse la problemática | Baja | Alto | Validar la problemática con el docente antes de la entrega formal de la Semana 2 |
| R-02 | El modelo de 24 entidades excede el tiempo disponible para el modelado | Media | Alto | Modelar por módulos (catálogo, inventario, personas, operaciones) y avanzar en paralelo |
| R-03 | Los equipos no soportan la máquina virtual con fluidez | Media | Medio | Verificar requisitos en la Semana 1; usar una distribución Linux ligera sin entorno gráfico pesado |
| R-04 | Dificultad para poblar la base con datos realistas y en volumen suficiente | Media | Medio | Cargar datos de catálogo público desde archivos CSV mediante scripts de inserción masiva |
| R-05 | Reparto desigual de la carga entre los integrantes | Media | Alto | Asignar paquetes de trabajo nominales en la EDT y verificar contribuciones en el historial de GitHub |
| R-06 | Pérdida de trabajo por versionado deficiente | Baja | Alto | Uso obligatorio de ramas por funcionalidad y commits frecuentes desde la Semana 1 |
| R-07 | El RDBMS elegido no soporta alguna restricción de integridad del modelo | Media | Medio | Evaluar el soporte de `ON UPDATE CASCADE` y de restricciones `CHECK` durante el análisis de selección de la Semana 10 |
| R-08 | Dificultad para coordinar la grabación del video con los tres integrantes | Media | Alto | Agendar la grabación en la Semana 13, dejando la Semana 14 como margen |
| R-09 | La aplicación web no logra conectarse a la base de datos en la VM | Media | Alto | Probar la conectividad en la Semana 12, apenas el modelo físico esté poblado |

---

# 7. Acta de Cierre del Proyecto

> **Documento a diligenciar al finalizar la Semana 14.** Se incluye la estructura para su posterior completado.

## 7.1 Información general

| Campo | Contenido |
|---|---|
| Nombre del proyecto | Sistema de gestión de inventario, consignaciones e intercambios para una tienda de cartas coleccionables |
| Fecha de cierre | _(por completar)_ |
| Gerente del proyecto | _(por completar)_ |

## 7.2 Verificación de entregables

| Entregable | Estado | Observaciones |
|---|---|---|
| Documento de propuesta | ☐ Recibido | |
| Documento de supuestos | ☐ Recibido | |
| Modelo E-R Peter-Chen | ☐ Recibido | |
| Modelo E-R Extendido | ☐ Recibido | |
| Modelo E-R Crow's Foot | ☐ Recibido | |
| Modelo Relacional Crow's Foot | ☐ Recibido | |
| Diccionario de datos | ☐ Recibido | |
| Documento de álgebra relacional (50 sentencias) | ☐ Recibido | |
| Análisis de selección de Sistema Operativo | ☐ Recibido | |
| Documento de implementación de la VM | ☐ Recibido | |
| Análisis de selección de RDBMS | ☐ Recibido | |
| Documento de implementación del RDBMS | ☐ Recibido | |
| Scripts de creación, inserción y varios con README | ☐ Recibido | |
| Aplicación web conectada a la base de datos | ☐ Recibido | |
| Repositorio en GitHub | ☐ Recibido | |
| Video de demostración | ☐ Recibido | |
| Documentos de gestión del proyecto | ☐ Recibido | |

## 7.3 Cumplimiento de objetivos

| Objetivo específico | Cumplido | Evidencia |
|---|---|---|
| 1. Modelo conceptual Peter-Chen | ☐ | |
| 2. Modelo lógico normalizado a 3FN | ☐ | |
| 3. Diccionario de datos | ☐ | |
| 4. Mínimo 50 sentencias de álgebra relacional | ☐ | |
| 5. VM Linux con RDBMS instalado | ☐ | |
| 6. Modelo físico implementado | ☐ | |
| 7. Aplicación web conectada | ☐ | |
| 8. Gestión en GitHub con aporte de los tres integrantes | ☐ | |

## 7.4 Balance final

| Indicador | Planificado | Real | Desviación |
|---|---|---|---|
| Duración | 14 semanas | _(por completar)_ | |
| Horas de trabajo | 336 h | _(por completar)_ | |
| Presupuesto | $6.457.000 | _(por completar)_ | |

## 7.5 Lecciones aprendidas

| # | Lección | Categoría |
|---|---|---|
| 1 | _(por completar)_ | Técnica / Gestión / Equipo |
| 2 | _(por completar)_ | |
| 3 | _(por completar)_ | |

## 7.6 Aceptación formal

| Rol | Nombre | Firma | Fecha |
|---|---|---|---|
| Patrocinador | Ing. Christian Felipe Duarte | | |
| Gerente del proyecto | _(por completar)_ | | |
| Integrante | _(por completar)_ | | |
| Integrante | _(por completar)_ | | |
