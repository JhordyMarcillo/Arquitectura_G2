/*
Empresa        :  EurekaBank
Software       :  Sistema de Cuentas de Ahorro
DBMS           :  SQL Server 2019+
Base de Datos  :  EUREKABANK
Script         :  Crea la Base de Datos
Modelo original:  Ing. Eric Gustavo Coronel Castillo (version MySQL 20-Jun-2017)
Adaptacion     :  GR02 - NRC 36884 - PAO202651

Notas:
  - Es la traduccion a T-SQL del modelo MySQL de 17 tablas. El script SQL Server
    original del docente es una version anterior (13 tablas, sin el modulo de
    seguridad), por eso no se usa directamente.
  - Mismas tablas, columnas, claves y restricciones que la version MySQL del grupo.
*/


-- =============================================
-- Creacion de la Base de Datos
-- =============================================

USE master;
GO

IF DB_ID('EUREKABANK') IS NOT NULL
BEGIN
	ALTER DATABASE EUREKABANK SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
	DROP DATABASE EUREKABANK;
END;
GO

CREATE DATABASE EUREKABANK;
GO


-- =============================================
-- Seleccionar la Base de Datos
-- =============================================

USE EUREKABANK;
GO


-- =============================================
-- Creacion de los Objetos de la Base de Datos
-- =============================================

CREATE TABLE TipoMovimiento (
	chr_tipocodigo       CHAR(3) NOT NULL,
	vch_tipodescripcion  VARCHAR(40) NOT NULL,
	vch_tipoaccion       VARCHAR(10) NOT NULL,
	vch_tipoestado       VARCHAR(15) DEFAULT 'ACTIVO' NOT NULL,
	CONSTRAINT PK_TipoMovimiento
		PRIMARY KEY (chr_tipocodigo),
	CONSTRAINT chk_tipomovimiento_vch_tipoaccion
		CHECK (vch_tipoaccion IN ('INGRESO', 'SALIDA')),
	CONSTRAINT chk_tipomovimiento_vch_tipoestado
		CHECK (vch_tipoestado IN ('ACTIVO', 'ANULADO', 'CANCELADO'))
);
GO

CREATE TABLE Sucursal (
	chr_sucucodigo       CHAR(3) NOT NULL,
	vch_sucunombre       VARCHAR(50) NOT NULL,
	vch_sucuciudad       VARCHAR(30) NOT NULL,
	vch_sucudireccion    VARCHAR(50) NULL,
	int_sucucontcuenta   INTEGER NOT NULL,
	CONSTRAINT PK_Sucursal
		PRIMARY KEY (chr_sucucodigo)
);
GO

CREATE TABLE Empleado (
	chr_emplcodigo       CHAR(4) NOT NULL,
	vch_emplpaterno      VARCHAR(25) NOT NULL,
	vch_emplmaterno      VARCHAR(25) NOT NULL,
	vch_emplnombre       VARCHAR(30) NOT NULL,
	vch_emplciudad       VARCHAR(30) NOT NULL,
	vch_empldireccion    VARCHAR(50) NULL,
	CONSTRAINT PK_Empleado PRIMARY KEY (chr_emplcodigo)
);
GO

CREATE TABLE Modulo (
	int_moducodigo       INTEGER NOT NULL,
	vch_modunombre       VARCHAR(50) NULL,
	vch_moduestado       VARCHAR(15) NOT NULL DEFAULT 'ACTIVO',
	CONSTRAINT PK_Modulo PRIMARY KEY (int_moducodigo),
	CONSTRAINT chk_modulo_vch_moduestado
		CHECK (vch_moduestado IN ('ACTIVO', 'ANULADO', 'CANCELADO'))
);
GO

CREATE TABLE Usuario (
	chr_emplcodigo       CHAR(4) NOT NULL,
	vch_emplusuario      VARCHAR(20) NOT NULL,
	vch_emplclave        VARCHAR(50) NOT NULL,
	vch_emplestado       VARCHAR(15) NULL DEFAULT 'ACTIVO',
	CONSTRAINT PK_Usuario PRIMARY KEY (chr_emplcodigo),
	CONSTRAINT U_Usuario_vch_emplusuario UNIQUE (vch_emplusuario),
	CONSTRAINT chk_usuario_vch_emplestado
		CHECK (vch_emplestado IN ('ACTIVO', 'ANULADO', 'CANCELADO')),
	CONSTRAINT fk_usuario_empleado
		FOREIGN KEY (chr_emplcodigo)
		REFERENCES Empleado (chr_emplcodigo)
);
GO

CREATE TABLE Permiso (
	chr_emplcodigo       CHAR(4) NOT NULL,
	int_moducodigo       INTEGER NOT NULL,
	vch_permestado       VARCHAR(15) NOT NULL DEFAULT 'ACTIVO',
	CONSTRAINT PK_Permiso PRIMARY KEY (chr_emplcodigo, int_moducodigo),
	CONSTRAINT chk_permiso_vch_permestado
		CHECK (vch_permestado IN ('ACTIVO', 'ANULADO', 'CANCELADO')),
	CONSTRAINT fk_permiso_modulo
		FOREIGN KEY (int_moducodigo)
		REFERENCES Modulo (int_moducodigo),
	CONSTRAINT fk_permiso_usuario
		FOREIGN KEY (chr_emplcodigo)
		REFERENCES Usuario (chr_emplcodigo)
);
GO

CREATE TABLE LogSession (
	ID                 INT IDENTITY(1,1) NOT NULL,
	chr_emplcodigo     CHAR(4) NOT NULL,
	fec_ingreso        DATETIME NOT NULL,
	fec_salida         DATETIME NULL,
	ip                 VARCHAR(20) NOT NULL DEFAULT 'NONE',
	hostname           VARCHAR(100) NOT NULL DEFAULT 'NONE',
	CONSTRAINT PK_LogSession PRIMARY KEY (ID),
	CONSTRAINT fk_logsession_empleado
		FOREIGN KEY (chr_emplcodigo)
		REFERENCES Empleado (chr_emplcodigo)
);
GO

CREATE TABLE Asignado (
	chr_asigcodigo       CHAR(6) NOT NULL,
	chr_sucucodigo       CHAR(3) NOT NULL,
	chr_emplcodigo       CHAR(4) NOT NULL,
	dtt_asigfechaalta    DATE NOT NULL,
	dtt_asigfechabaja    DATE NULL,
	CONSTRAINT PK_Asignado
		PRIMARY KEY (chr_asigcodigo),
	CONSTRAINT fk_asignado_empleado
		FOREIGN KEY (chr_emplcodigo)
		REFERENCES Empleado (chr_emplcodigo),
	CONSTRAINT fk_asignado_sucursal
		FOREIGN KEY (chr_sucucodigo)
		REFERENCES Sucursal (chr_sucucodigo)
);
GO

CREATE INDEX idx_asignado01 ON Asignado (chr_emplcodigo);
CREATE INDEX idx_asignado02 ON Asignado (chr_sucucodigo);
GO

CREATE TABLE Cliente (
	chr_cliecodigo       CHAR(5) NOT NULL,
	vch_cliepaterno      VARCHAR(25) NOT NULL,
	vch_cliematerno      VARCHAR(25) NOT NULL,
	vch_clienombre       VARCHAR(30) NOT NULL,
	chr_cliedni          CHAR(8) NOT NULL,
	vch_clieciudad       VARCHAR(30) NOT NULL,
	vch_cliedireccion    VARCHAR(50) NOT NULL,
	vch_clietelefono     VARCHAR(20) NULL,
	vch_clieemail        VARCHAR(50) NULL,
	CONSTRAINT PK_Cliente
		PRIMARY KEY (chr_cliecodigo)
);
GO

CREATE TABLE Moneda (
	chr_monecodigo       CHAR(2) NOT NULL,
	vch_monedescripcion  VARCHAR(20) NOT NULL,
	CONSTRAINT PK_Moneda
		PRIMARY KEY (chr_monecodigo)
);
GO

CREATE TABLE Cuenta (
	chr_cuencodigo       CHAR(8) NOT NULL,
	chr_monecodigo       CHAR(2) NOT NULL,
	chr_sucucodigo       CHAR(3) NOT NULL,
	chr_emplcreacuenta   CHAR(4) NOT NULL,
	chr_cliecodigo       CHAR(5) NOT NULL,
	dec_cuensaldo        DECIMAL(12,2) NOT NULL,
	dtt_cuenfechacreacion DATE NOT NULL,
	vch_cuenestado       VARCHAR(15) DEFAULT 'ACTIVO' NOT NULL,
	int_cuencontmov      INTEGER NOT NULL,
	chr_cuenclave        CHAR(6) NOT NULL,
	CONSTRAINT chk_cuenta_chr_cuenestado
		CHECK (vch_cuenestado IN ('ACTIVO', 'ANULADO', 'CANCELADO')),
	CONSTRAINT PK_Cuenta
		PRIMARY KEY (chr_cuencodigo),
	CONSTRAINT fk_cuenta_cliente
		FOREIGN KEY (chr_cliecodigo)
		REFERENCES Cliente (chr_cliecodigo),
	CONSTRAINT fk_cuenta_empleado
		FOREIGN KEY (chr_emplcreacuenta)
		REFERENCES Empleado (chr_emplcodigo),
	CONSTRAINT fk_cuenta_sucursal
		FOREIGN KEY (chr_sucucodigo)
		REFERENCES Sucursal (chr_sucucodigo),
	CONSTRAINT fk_cuenta_moneda
		FOREIGN KEY (chr_monecodigo)
		REFERENCES Moneda (chr_monecodigo)
);
GO

CREATE INDEX idx_cuenta01 ON Cuenta (chr_cliecodigo);
CREATE INDEX idx_cuenta02 ON Cuenta (chr_emplcreacuenta);
CREATE INDEX idx_cuenta03 ON Cuenta (chr_sucucodigo);
CREATE INDEX idx_cuenta04 ON Cuenta (chr_monecodigo);
GO

CREATE TABLE Movimiento (
	chr_cuencodigo       CHAR(8) NOT NULL,
	int_movinumero       INTEGER NOT NULL,
	dtt_movifecha        DATE NOT NULL,
	chr_emplcodigo       CHAR(4) NOT NULL,
	chr_tipocodigo       CHAR(3) NOT NULL,
	dec_moviimporte      DECIMAL(12,2) NOT NULL,
	chr_cuenreferencia   CHAR(8) NULL,
	CONSTRAINT chk_movimiento_importe
		CHECK (dec_moviimporte >= 0.0),
	CONSTRAINT PK_Movimiento
		PRIMARY KEY (chr_cuencodigo, int_movinumero),
	CONSTRAINT fk_movimiento_tipomovimiento
		FOREIGN KEY (chr_tipocodigo)
		REFERENCES TipoMovimiento (chr_tipocodigo),
	CONSTRAINT fk_movimiento_empleado
		FOREIGN KEY (chr_emplcodigo)
		REFERENCES Empleado (chr_emplcodigo),
	CONSTRAINT fk_movimiento_cuenta
		FOREIGN KEY (chr_cuencodigo)
		REFERENCES Cuenta (chr_cuencodigo)
);
GO

CREATE INDEX idx_movimiento01 ON Movimiento (chr_tipocodigo);
CREATE INDEX idx_movimiento02 ON Movimiento (chr_emplcodigo);
GO

CREATE TABLE Parametro (
	chr_paracodigo       CHAR(3) NOT NULL,
	vch_paradescripcion  VARCHAR(50) NOT NULL,
	vch_paravalor        VARCHAR(70) NOT NULL,
	vch_paraestado       VARCHAR(15) DEFAULT 'ACTIVO' NOT NULL,
	CONSTRAINT chk_parametro_vch_paraestado
		CHECK (vch_paraestado IN ('ACTIVO', 'ANULADO', 'CANCELADO')),
	CONSTRAINT PK_Parametro
		PRIMARY KEY (chr_paracodigo)
);
GO

CREATE TABLE InteresMensual (
	chr_monecodigo       CHAR(2) NOT NULL,
	dec_inteimporte      DECIMAL(12,2) NOT NULL,
	CONSTRAINT PK_InteresMensual
		PRIMARY KEY (chr_monecodigo),
	CONSTRAINT fk_interesmensual_moneda
		FOREIGN KEY (chr_monecodigo)
		REFERENCES Moneda (chr_monecodigo)
);
GO

CREATE TABLE CostoMovimiento (
	chr_monecodigo       CHAR(2) NOT NULL,
	dec_costimporte      DECIMAL(12,2) NOT NULL,
	CONSTRAINT PK_CostoMovimiento
		PRIMARY KEY (chr_monecodigo),
	CONSTRAINT fk_costomovimiento_moneda
		FOREIGN KEY (chr_monecodigo)
		REFERENCES Moneda (chr_monecodigo)
);
GO

CREATE TABLE CargoMantenimiento (
	chr_monecodigo       CHAR(2) NOT NULL,
	dec_cargMontoMaximo  DECIMAL(12,2) NOT NULL,
	dec_cargImporte      DECIMAL(12,2) NOT NULL,
	CONSTRAINT PK_CargoMantenimiento
		PRIMARY KEY (chr_monecodigo),
	CONSTRAINT fk_cargomantenimiento_moneda
		FOREIGN KEY (chr_monecodigo)
		REFERENCES Moneda (chr_monecodigo)
);
GO

CREATE TABLE Contador (
	vch_conttabla        VARCHAR(30) NOT NULL,
	int_contitem         INTEGER NOT NULL,
	int_contlongitud     INTEGER NOT NULL,
	CONSTRAINT PK_Contador
		PRIMARY KEY (vch_conttabla)
);
GO


-- =============================================
-- Usuario de la aplicacion (servidor SOAP/REST)
-- =============================================

USE master;
GO

IF SUSER_ID('eureka') IS NULL
	CREATE LOGIN eureka WITH PASSWORD = 'admin', CHECK_POLICY = OFF, DEFAULT_DATABASE = EUREKABANK;
GO

USE EUREKABANK;
GO

CREATE USER eureka FOR LOGIN eureka;
ALTER ROLE db_owner ADD MEMBER eureka;
GO
