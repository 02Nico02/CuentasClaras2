-- Categorías de Grupos
INSERT INTO Categoria (nombre, icono, grupo, fecha_creacion, fecha_actualizacion)
VALUES ('Casa', 'icono_casa.png', true, NOW(), NOW());

INSERT INTO Categoria (nombre, icono, grupo, fecha_creacion, fecha_actualizacion)
VALUES ('Trabajo', 'icono_trabajo.png', true, NOW(), NOW());

INSERT INTO Categoria (nombre, icono, grupo, fecha_creacion, fecha_actualizacion)
VALUES ('Familia', 'icono_familia.png', true, NOW(), NOW());

-- Categorías de Gastos
INSERT INTO Categoria (nombre, icono, grupo, fecha_creacion, fecha_actualizacion)
VALUES ('Comida', 'icono_comida.png', false, NOW(), NOW());

INSERT INTO Categoria (nombre, icono, grupo, fecha_creacion, fecha_actualizacion)
VALUES ('Transporte', 'icono_transporte.png', false, NOW(), NOW());

INSERT INTO Categoria (nombre, icono, grupo, fecha_creacion, fecha_actualizacion)
VALUES ('Entretenimiento', 'icono_entretenimiento.png', false, NOW(), NOW());