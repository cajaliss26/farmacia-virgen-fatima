-- ===============================
-- BD FARMACIA VF (MySQL 8.0.44)
-- ===============================

CREATE DATABASE IF NOT EXISTS bd_farmaciavf
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE bd_farmaciavf;

SET FOREIGN_KEY_CHECKS = 0;

-- 1) roles
DROP TABLE IF EXISTS roles;
CREATE TABLE roles (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       nombre VARCHAR(40) NOT NULL,
                       PRIMARY KEY (id),
                       UNIQUE KEY uk_roles_nombre (nombre)
) ENGINE=InnoDB;

-- 2) usuarios
DROP TABLE IF EXISTS usuarios;
CREATE TABLE usuarios (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          nombre VARCHAR(60) NOT NULL,
                          apellido VARCHAR(60) NOT NULL,
                          email VARCHAR(100) NOT NULL,
                          password_hash VARCHAR(255) NOT NULL,
                          telefono VARCHAR(60),
                          rol_id BIGINT,
                          activo TINYINT(1) NOT NULL DEFAULT 1,
                          fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          PRIMARY KEY (id),
                          UNIQUE KEY uk_usuarios_email (email),
                          KEY idx_usuarios_rol_id (rol_id),
                          CONSTRAINT fk_usuarios_roles
                              FOREIGN KEY (rol_id) REFERENCES roles(id)
                                  ON UPDATE CASCADE
                                  ON DELETE SET NULL
) ENGINE=InnoDB;

-- 3) sucursales
DROP TABLE IF EXISTS sucursales;
CREATE TABLE sucursales (
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            nombre VARCHAR(80) NOT NULL,
                            distrito VARCHAR(60) NOT NULL,
                            direccion VARCHAR(120) NOT NULL,
                            telefono VARCHAR(20),
                            PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 4) categorias
DROP TABLE IF EXISTS categorias;
CREATE TABLE categorias (
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            nombre VARCHAR(60) NOT NULL,
                            descripcion VARCHAR(120),
                            PRIMARY KEY (id),
                            UNIQUE KEY uk_categorias_nombre (nombre)
) ENGINE=InnoDB;

-- 5) productos
DROP TABLE IF EXISTS productos;
CREATE TABLE productos (
                           id BIGINT NOT NULL AUTO_INCREMENT,
                           nombre VARCHAR(120) NOT NULL,
                           categoria_id BIGINT NOT NULL,
                           sucursal_id BIGINT NOT NULL,
                           precio DECIMAL(10,2) NOT NULL,
                           activo TINYINT(1) NOT NULL DEFAULT 1,
                           PRIMARY KEY (id),
                           KEY idx_productos_categoria_id (categoria_id),
                           KEY idx_productos_sucursal_id (sucursal_id),
                           CONSTRAINT fk_productos_categorias
                               FOREIGN KEY (categoria_id) REFERENCES categorias(id)
                                   ON UPDATE CASCADE
                                   ON DELETE RESTRICT,
                           CONSTRAINT fk_productos_sucursales
                               FOREIGN KEY (sucursal_id) REFERENCES sucursales(id)
                                   ON UPDATE CASCADE
                                   ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 6) carritos
DROP TABLE IF EXISTS carritos;
CREATE TABLE carritos (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          usuario_id BIGINT NOT NULL,
                          activo TINYINT(1) NOT NULL DEFAULT 1,
                          total DECIMAL(10,2) NOT NULL DEFAULT 0.00,

                          creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          actualizado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                              ON UPDATE CURRENT_TIMESTAMP,

                          PRIMARY KEY (id),
                          KEY idx_carritos_usuario_id (usuario_id),
                          CONSTRAINT fk_carritos_usuarios
                              FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
                                  ON UPDATE CASCADE
                                  ON DELETE CASCADE
) ENGINE=InnoDB;

-- 7) carrito_items
DROP TABLE IF EXISTS carrito_items;
CREATE TABLE carrito_items (
                               id BIGINT NOT NULL AUTO_INCREMENT,
                               carrito_id BIGINT NOT NULL,
                               producto_id BIGINT NOT NULL,
                               precio_unitario DECIMAL(10,2) NOT NULL,
                               cantidad INT NOT NULL,
                               subtotal DECIMAL(10,2) NOT NULL,
                               PRIMARY KEY (id),

                               KEY idx_carrito_items_carrito_id (carrito_id),
                               KEY idx_carrito_items_producto_id (producto_id),
                               UNIQUE KEY uk_carrito_producto (carrito_id, producto_id),

                               CONSTRAINT fk_carrito_items_carritos
                                   FOREIGN KEY (carrito_id) REFERENCES carritos(id)
                                       ON UPDATE CASCADE
                                       ON DELETE CASCADE,
                               CONSTRAINT fk_carrito_items_productos
                                   FOREIGN KEY (producto_id) REFERENCES productos(id)
                                       ON UPDATE CASCADE
                                       ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 8) pedidos
DROP TABLE IF EXISTS pedidos;
CREATE TABLE pedidos (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         usuario_id BIGINT NOT NULL,
                         carrito_origen_id BIGINT,
                         estado VARCHAR(20) NOT NULL,
                         total DECIMAL(10,2) NOT NULL,
                         creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         PRIMARY KEY (id),
                         KEY idx_pedidos_usuario_id (usuario_id),
                         KEY idx_pedidos_carrito_origen_id (carrito_origen_id),
                         CONSTRAINT fk_pedidos_usuarios
                             FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
                                 ON UPDATE CASCADE
                                 ON DELETE RESTRICT,
                         CONSTRAINT fk_pedidos_carritos
                             FOREIGN KEY (carrito_origen_id) REFERENCES carritos(id)
                                 ON UPDATE CASCADE
                                 ON DELETE SET NULL
) ENGINE=InnoDB;

-- 9) pedido_items
DROP TABLE IF EXISTS pedido_items;
CREATE TABLE pedido_items (
                              id BIGINT NOT NULL AUTO_INCREMENT,
                              pedido_id BIGINT NOT NULL,
                              producto_id BIGINT NOT NULL,
                              precio_unitario DECIMAL(10,2) NOT NULL,
                              cantidad INT NOT NULL,
                              subtotal DECIMAL(10,2) NOT NULL,
                              PRIMARY KEY (id),
                              KEY idx_pedido_items_pedido_id (pedido_id),
                              KEY idx_pedido_items_producto_id (producto_id),
                              CONSTRAINT fk_pedido_items_pedidos
                                  FOREIGN KEY (pedido_id) REFERENCES pedidos(id)
                                      ON UPDATE CASCADE
                                      ON DELETE CASCADE,
                              CONSTRAINT fk_pedido_items_productos
                                  FOREIGN KEY (producto_id) REFERENCES productos(id)
                                      ON UPDATE CASCADE
                                      ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 10) metodos_pago
DROP TABLE IF EXISTS metodos_pago;
CREATE TABLE metodos_pago (
                              id BIGINT NOT NULL AUTO_INCREMENT,
                              nombre VARCHAR(50) NOT NULL,
                              PRIMARY KEY (id),
                              UNIQUE KEY uk_metodos_pago_nombre (nombre)
) ENGINE=InnoDB;

-- 11) pagos
DROP TABLE IF EXISTS pagos;
CREATE TABLE pagos (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       usuario_id BIGINT NOT NULL,
                       metodo_pago_id BIGINT NOT NULL,
                       monto_total DECIMAL(10,2) NOT NULL,
                       fecha_pago DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       PRIMARY KEY (id),
                       KEY idx_pagos_usuario_id (usuario_id),
                       KEY idx_pagos_metodo_pago_id (metodo_pago_id),
                       CONSTRAINT fk_pagos_usuarios
                           FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
                               ON UPDATE CASCADE
                               ON DELETE RESTRICT,
                       CONSTRAINT fk_pagos_metodos_pago
                           FOREIGN KEY (metodo_pago_id) REFERENCES metodos_pago(id)
                               ON UPDATE CASCADE
                               ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 12) detalle_pago
DROP TABLE IF EXISTS detalle_pago;
CREATE TABLE detalle_pago (
                              id BIGINT NOT NULL AUTO_INCREMENT,
                              pago_id BIGINT NOT NULL,
                              producto_id BIGINT NOT NULL,
                              precio_unitario DECIMAL(10,2) NOT NULL,
                              subtotal DECIMAL(10,2) NOT NULL,
                              PRIMARY KEY (id),
                              KEY idx_detalle_pago_pago_id (pago_id),
                              KEY idx_detalle_pago_producto_id (producto_id),
                              CONSTRAINT fk_detalle_pago_pagos
                                  FOREIGN KEY (pago_id) REFERENCES pagos(id)
                                      ON UPDATE CASCADE
                                      ON DELETE CASCADE,
                              CONSTRAINT fk_detalle_pago_productos
                                  FOREIGN KEY (producto_id) REFERENCES productos(id)
                                      ON UPDATE CASCADE
                                      ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 13) reservas
DROP TABLE IF EXISTS reservas;
CREATE TABLE reservas (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          usuario_id BIGINT NOT NULL,
                          sucursal_id BIGINT NOT NULL,
                          fecha_reserva DATE NOT NULL,
                          hora_inicio TIME NOT NULL,
                          hora_fin TIME NOT NULL,
                          estado ENUM('PENDIENTE','CONFIRMADA','ENTREGADA','CANCELADA') NOT NULL DEFAULT 'PENDIENTE',
                          PRIMARY KEY (id),
                          KEY idx_reservas_usuario_id (usuario_id),
                          KEY idx_reservas_sucursal_id (sucursal_id),
                          CONSTRAINT fk_reservas_usuarios
                              FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
                                  ON UPDATE CASCADE
                                  ON DELETE RESTRICT,
                          CONSTRAINT fk_reservas_sucursales
                              FOREIGN KEY (sucursal_id) REFERENCES sucursales(id)
                                  ON UPDATE CASCADE
                                  ON DELETE RESTRICT
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;
