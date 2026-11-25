-- Script de la base de datos (la aplicacion tambien lo ejecuta sola si no existe)
CREATE DATABASE IF NOT EXISTS api_paises;
USE api_paises;

CREATE TABLE IF NOT EXISTS pais(
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    capital VARCHAR(100),
    region VARCHAR(100),
    poblacion BIGINT,
    moneda VARCHAR(100),
    UNIQUE KEY uk_pais_nombre (nombre)  -- evita duplicados al importar varias veces
);
