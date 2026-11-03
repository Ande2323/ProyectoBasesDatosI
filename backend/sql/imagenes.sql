-- =====================================================================
-- Imagen de cada ejemplar. Va en la base porque es lo único que comparten
-- todos los equipos: en una carpeta solo la vería quien la subió.
-- Ejecutar DESPUÉS de DDL.sql. Solo crea la tabla, no guarda datos.
-- =====================================================================

USE magic_cards;

CREATE TABLE IF NOT EXISTS imagen_ejemplar (
  id_ejemplar INT NOT NULL,
  contenido   MEDIUMBLOB NOT NULL,
  CONSTRAINT imagen_ejemplar_PK PRIMARY KEY (id_ejemplar),
  CONSTRAINT imagen_ejemplar_ejemplar_FK FOREIGN KEY (id_ejemplar)
    REFERENCES ejemplar (id_ejemplar) ON DELETE CASCADE
);
