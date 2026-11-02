-- =====================================================================
-- Catálogos básicos: siempre existen y el sistema no permite eliminarlos.
-- Valores de DML.sql de Scripts.zip, más Tierra, Conjuro y Encantamiento.
-- Ejecutar DESPUÉS de DDL.sql. No guarda cartas ni datos de personas.
-- =====================================================================

USE magic_cards;

INSERT IGNORE INTO color (id_color, nombre_color, orden_canonico) VALUES
  (1, 'Blanco', 1),
  (2, 'Azul',   2),
  (3, 'Negro',  3),
  (4, 'Rojo',   4),
  (5, 'Verde',  5);

INSERT IGNORE INTO tipo (id_tipo, nivel, codigo, nombre) VALUES
  (1,  2, 'INS', 'Instantáneo'),
  (2,  2, 'CRE', 'Criatura'),
  (3,  2, 'ART', 'Artefacto'),
  (4,  2, 'PLN', 'Planeswalker'),
  (5,  3, 'ELF', 'Elfo'),
  (6,  3, 'ANG', 'Ángel'),
  (7,  3, 'AVE', 'Ave'),
  (8,  1, 'LEG', 'Legendario'),
  (9,  2, 'TIE', 'Tierra'),
  (10, 2, 'CON', 'Conjuro'),
  (11, 2, 'ENC', 'Encantamiento');

INSERT IGNORE INTO condicion (codigo_condicion, nombre, factor_ajuste) VALUES
  ('NM',  'Near Mint',         1.000),
  ('LP',  'Lightly Played',    0.900),
  ('MP',  'Moderately Played', 0.750),
  ('HP',  'Heavily Played',    0.500),
  ('DMG', 'Damaged',           0.250);

INSERT IGNORE INTO rareza (id_rareza, nombre, codigo) VALUES
  (1, 'Común',       'C'),
  (2, 'Infrecuente', 'U'),
  (3, 'Rara',        'R'),
  (4, 'Mítica',      'M');

INSERT IGNORE INTO idioma (id_idioma, nombre, codigo) VALUES
  (1, 'Inglés',  'EN'),
  (2, 'Español', 'ES'),
  (3, 'Japonés', 'JA');
