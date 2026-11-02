-- =====================================================================
-- Restricciones pendientes del modelo (sección 9 de Normalizacion_3FN_Magic_BD.pdf).
-- Ejecutar DESPUÉS de DDL.sql y DML.sql de Scripts.zip.
--
-- "Claves alternas": columnas que no deben repetirse. Sin estos UNIQUE, MySQL
-- aceptaría, por ejemplo, dos ejemplares con la misma etiqueta si dos personas
-- los registran al mismo tiempo. Con ellos el modelo también cumple BCNF.
-- =====================================================================

USE magic_cards;

ALTER TABLE rol     ADD CONSTRAINT rol_nombre_UN      UNIQUE (nombre);
ALTER TABLE idioma  ADD CONSTRAINT idioma_codigo_UN   UNIQUE (codigo);
ALTER TABLE rareza  ADD CONSTRAINT rareza_codigo_UN   UNIQUE (codigo);
ALTER TABLE edicion ADD CONSTRAINT edicion_codigo_UN  UNIQUE (codigo_impreso);
ALTER TABLE tipo    ADD CONSTRAINT tipo_codigo_UN     UNIQUE (codigo);
ALTER TABLE carta   ADD CONSTRAINT carta_nombre_UN    UNIQUE (nombre_oficial);
ALTER TABLE ejemplar ADD CONSTRAINT ejemplar_etiqueta_UN UNIQUE (codigo_etiqueta);

-- Una misma edición, idioma, número de colección y acabado es una sola impresión.
ALTER TABLE impresion ADD CONSTRAINT impresion_fisica_UN
  UNIQUE (edicion_id_edicion, idioma_id_idioma, numero_coleccion, indicador_foil);
