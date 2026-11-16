-- =====================================================================
-- TELEFQUITO - Sistema de facturacion de la Comercializadora de Telefonos
-- Celulares (esquema: comercializadora).  Sin inventario: solo el catalogo
-- de telefonos disponibles para la venta.  Examen Complexivo | Grupo GR02
-- =====================================================================
DROP DATABASE IF EXISTS comercializadora;
CREATE DATABASE comercializadora CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE comercializadora;

CREATE TABLE cliente_comercializadora (
  codc_cliente     INT NOT NULL AUTO_INCREMENT,
  cedula           VARCHAR(100) NOT NULL,
  nombre           VARCHAR(100) NOT NULL,
  genero           VARCHAR(1) NOT NULL,
  fecha_nacimiento DATE NOT NULL,
  PRIMARY KEY (codc_cliente)
) ENGINE=InnoDB;

CREATE TABLE telefonos (
  cod_telefono INT NOT NULL AUTO_INCREMENT,
  nombre       VARCHAR(100) NOT NULL,
  precio       DECIMAL(10,2) NOT NULL,
  marca        VARCHAR(100) NOT NULL,
  disponible   INT NOT NULL COMMENT '1 = a la venta, 0 = no disponible',
  PRIMARY KEY (cod_telefono)
) ENGINE=InnoDB;

CREATE TABLE compras (
  cod_compra   INT NOT NULL AUTO_INCREMENT COMMENT 'Numero de factura',
  forma_pago   VARCHAR(45) NOT NULL COMMENT 'Efectivo | Credito Directo',
  fecha        DATE NOT NULL,
  codc_cliente INT NOT NULL,
  subtotal     DECIMAL(10,2) NOT NULL,
  descuento    DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '42% solo en efectivo',
  precio_final DECIMAL(10,2) NOT NULL COMMENT 'Total de la factura',
  PRIMARY KEY (cod_compra),
  CONSTRAINT codc_cliente FOREIGN KEY (codc_cliente) REFERENCES cliente_comercializadora (codc_cliente)
) ENGINE=InnoDB;

CREATE TABLE detalle_compra (
  cod_detalle  INT NOT NULL AUTO_INCREMENT,
  cod_compra   INT NOT NULL,
  cod_telefono INT NOT NULL,
  precio       DECIMAL(10,2) NOT NULL,
  descuento    DECIMAL(10,2) NOT NULL DEFAULT 0,
  precio_final DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (cod_detalle),
  CONSTRAINT fk_detalle_compra   FOREIGN KEY (cod_compra)   REFERENCES compras (cod_compra),
  CONSTRAINT fk_detalle_telefono FOREIGN KEY (cod_telefono) REFERENCES telefonos (cod_telefono)
) ENGINE=InnoDB COMMENT 'Lineas de la factura (maximo 3 telefonos por factura)';

INSERT INTO cliente_comercializadora (codc_cliente, cedula, nombre, genero, fecha_nacimiento) VALUES
(1,'0102030405','Juan Carlos Pérez','M','1990-05-14'),
(2,'0203040506','María López','F','1985-08-22'),
(3,'0304050607','Carlos Sánchez','M','1978-12-05'),
(4,'0405060708','Ana Gómez','F','1992-03-19'),
(5,'0506070809','Lucía Rodríguez','F','2000-07-30'),
(6,'0607080910','Luis Martínez','M',DATE_SUB(CURDATE(), INTERVAL 22 YEAR));

INSERT INTO telefonos (nombre, precio, marca, disponible) VALUES
('Samsung Galaxy S23',699.00,'Samsung',1),
('iPhone 13',649.00,'Apple',1),
('Xiaomi Redmi Note 12',229.00,'Xiaomi',1),
('Honor Magic 5',459.00,'Honor',1),
('OnePlus Nord 3',399.00,'OnePlus',1),
('Samsung Galaxy A34',319.00,'Samsung',1),
('iPhone 15 Pro Max',1399.00,'Apple',1);
