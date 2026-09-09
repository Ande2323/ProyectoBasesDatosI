# Contexto — Proyecto Final Bases de Datos 1

> Resumen de referencia extraído de `Proyecto_Final_DB1_20261.pdf`.
> Universidad El Bosque · Ingeniería de Sistemas · Bases de Datos 1
> Docente: Ing., Esp., M.Ed. Christian Felipe Duarte.

## Datos generales

| Ítem | Valor |
|---|---|
| Tema | Proyecto Final — Asignatura Bases de Datos 1 |
| Equipos | Grupos de 3 estudiantes |
| Peso | 16% del semestre |
| Duración | 14 semanas |
| Primera fase | Propuesta de Proyecto (problemática compleja y robusta, aprobada por el docente) |

## Objetivos de aprendizaje

### 1. Diseño y modelado relacional
- Modelar con el **modelo Entidad-Relación**: entidades, atributos y relaciones precisas.
- **Normalizar hasta 3FN** — sin redundancia ni anomalías de inserción/actualización/eliminación.
- Diseñar el sistema completo: de la conceptualización a la implementación, sobre una problemática real o simulada.

### 2. Implementación y manipulación de datos
- **Implementar un RDBMS** en entorno virtualizado (p. ej. máquina virtual con Oracle Linux).
- Usar **álgebra relacional** para manipular y consultar datos.
- **Conectar una aplicación web sencilla** a la base de datos (al menos funcionalidad de lectura).

### 3. Colaboración y documentación
- **Git y GitHub** para código y documentación, con contribuciones significativas de todos.
- **Documento final + presentación** explicando diseño, implementación y decisiones técnicas.

## Requisitos que debe cumplir la problemática elegida

- **Modelado conceptual:** diagrama E-R complejo con **mínimo 8 entidades** y múltiples tipos de relación (1:1, 1:N, N:M).
- **Normalización:** el esquema debe requerir normalización hasta **3FN**.
- **Consultas complejas:** uniones, proyecciones y selecciones (álgebra relacional).
- **Virtualización y conectividad:** implementación en VM + BD accesible desde una app web de demostración.
- **Colaboración:** gestión vía repositorio en GitHub.

La propuesta escrita debe detallar: **problemática, objetivos, alcance y requerimientos funcionales** principales.

## Entregables — Fase 1

- [ ] Documento de la propuesta del proyecto.
- [ ] Documento de Supuestos.
- [ ] Modelo Entidad-Relación — notación **Peter-Chen**.
- [ ] Modelo Entidad-Relación Extendido (si aplica).
- [ ] Modelo Entidad-Relación — notación **Barker / Crow's Foot** (Data Modeler o Workbench).
- [ ] Modelo Relacional — notación **Barker / Crow's Foot** (Data Modeler o Workbench).
- [ ] Diccionario de Datos.
- [ ] Documento de implementación — sentencias de álgebra relacional, **mínimo 50 ejercicios**.

## Entregables — Fase 2

- [ ] Documento de análisis de selección de Sistema Operativo — **solo distribuciones Linux o Unix**.
- [ ] Documento de implementación de la VM.
- [ ] Documento de análisis de selección de RDBMS — **prohibido SQL Server**; sugeridos: Oracle, MySQL, PostgreSQL, MariaDB, etc.
- [ ] Documento de implementación del RDBMS.
- [ ] Scripts del modelo lógico al físico, con **README**:
  - [ ] Scripts de creación de objetos.
  - [ ] Scripts de inserción de registros.
  - [ ] Scripts varios.
- [ ] Conexión de la aplicación web a la base de datos relacional.
- [ ] Repositorio Git con la app web y los scripts de BD.
- [ ] Video de demostración (**máx. 15 min**) del desarrollo, manipulación de la BD y app final — **todos** los integrantes, con audio y cámara.

## Entregables transversales (obligatorios)

1. Acta de constitución del proyecto.
2. Identificación de interesados (stakeholders).
3. EDT / WBS.
4. Cronograma.
5. Presupuesto.
6. Acta de cierre.

> Además, se puede adjuntar cualquier otro ítem de gestión de proyecto que el grupo considere necesario.

## Restricciones clave a no olvidar

- SO: **solo Linux/Unix**.
- RDBMS: **cualquiera menos SQL Server**.
- E-R: **≥ 8 entidades**.
- Álgebra relacional: **≥ 50 ejercicios**.
- Video: **≤ 15 minutos**, todos en cámara y audio.
- Todo el trabajo versionado en **GitHub**.
