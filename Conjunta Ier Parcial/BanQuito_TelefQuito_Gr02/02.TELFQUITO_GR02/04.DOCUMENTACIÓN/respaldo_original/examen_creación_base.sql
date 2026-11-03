-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: core
-- ------------------------------------------------------
-- Server version	8.0.37

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `amortizacion`
--

DROP TABLE IF EXISTS `amortizacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `amortizacion` (
  `COD_AMORTIZACION` int NOT NULL AUTO_INCREMENT,
  `COD_CREDITO` int NOT NULL,
  `CUOTA` int NOT NULL,
  `VALOR_CUOTA` decimal(10,2) NOT NULL,
  `INTERES_PAGADO` decimal(10,2) NOT NULL,
  `CAPITAL_PAGADO` decimal(10,2) NOT NULL,
  `SALDO` decimal(10,2) NOT NULL,
  PRIMARY KEY (`COD_AMORTIZACION`),
  KEY `FK_AMORTIZACION_CREDITO` (`COD_CREDITO`),
  CONSTRAINT `FK_AMORTIZACION_CREDITO` FOREIGN KEY (`COD_CREDITO`) REFERENCES `credito` (`COD_CREDITO`)
) ENGINE=InnoDB AUTO_INCREMENT=131 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cliente`
--

DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `COD_CLIENTE` int NOT NULL AUTO_INCREMENT,
  `CEDULA` varchar(100) NOT NULL,
  `NOMBRE` varchar(100) NOT NULL,
  `GENERO` varchar(1) NOT NULL,
  `FECHA_NACIMIENTO` date NOT NULL,
  PRIMARY KEY (`COD_CLIENTE`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cliente_comercializadora`
--

DROP TABLE IF EXISTS `cliente_comercializadora`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente_comercializadora` (
  `codc_cliente` int NOT NULL AUTO_INCREMENT,
  `cedula` varchar(100) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `genero` varchar(1) NOT NULL,
  `fecha_nacimiento` date NOT NULL,
  PRIMARY KEY (`codc_cliente`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `compras`
--

DROP TABLE IF EXISTS `compras`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `compras` (
  `cod_compra` int NOT NULL AUTO_INCREMENT,
  `forma_pago` varchar(45) NOT NULL,
  `fecha` date NOT NULL,
  `cod_telefono` int NOT NULL,
  `codc_cliente` int NOT NULL,
  `descuento` decimal(10,2) DEFAULT NULL,
  `precio_final` decimal(10,3) NOT NULL,
  PRIMARY KEY (`cod_compra`),
  KEY `cod_telefono_idx` (`cod_telefono`),
  KEY `codc_cliente_idx` (`codc_cliente`),
  CONSTRAINT `cod_telefono` FOREIGN KEY (`cod_telefono`) REFERENCES `telefonos` (`cod_telefono`),
  CONSTRAINT `codc_cliente` FOREIGN KEY (`codc_cliente`) REFERENCES `cliente_comercializadora` (`codc_cliente`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `credito`
--

DROP TABLE IF EXISTS `credito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `credito` (
  `COD_CREDITO` int NOT NULL AUTO_INCREMENT,
  `COD_CLIENTE` int NOT NULL,
  `MONTO` decimal(10,2) NOT NULL,
  `PLAZO_MESES` int NOT NULL,
  `TASA_INTERES` decimal(5,2) NOT NULL,
  `FECHA_INICIO` date NOT NULL,
  `ACTIVO` int NOT NULL,
  PRIMARY KEY (`COD_CREDITO`),
  KEY `FK_CREDITO_CLIENTE` (`COD_CLIENTE`),
  CONSTRAINT `FK_CREDITO_CLIENTE` FOREIGN KEY (`COD_CLIENTE`) REFERENCES `cliente` (`COD_CLIENTE`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuenta`
--

DROP TABLE IF EXISTS `cuenta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuenta` (
  `NUM_CUENTA` varchar(8) NOT NULL,
  `COD_CLIENTE` int NOT NULL,
  `SALDO` decimal(10,2) NOT NULL,
  PRIMARY KEY (`NUM_CUENTA`),
  KEY `COD_CLIENTE_idx` (`COD_CLIENTE`),
  CONSTRAINT `COD_CLIENTE` FOREIGN KEY (`COD_CLIENTE`) REFERENCES `cliente` (`COD_CLIENTE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `movimiento`
--

DROP TABLE IF EXISTS `movimiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimiento` (
  `COD_MOVIMIENTO` int NOT NULL AUTO_INCREMENT,
  `NUM_CUENTA` varchar(8) NOT NULL,
  `TIPO` varchar(3) NOT NULL,
  `VALOR` decimal(10,2) NOT NULL,
  `FECHA` date NOT NULL,
  PRIMARY KEY (`COD_MOVIMIENTO`),
  KEY `NUM_CUENTA_idx` (`NUM_CUENTA`),
  CONSTRAINT `NUM_CUENTA` FOREIGN KEY (`NUM_CUENTA`) REFERENCES `cuenta` (`NUM_CUENTA`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `telefonos`
--

DROP TABLE IF EXISTS `telefonos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `telefonos` (
  `cod_telefono` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `precio` decimal(10,2) NOT NULL,
  `marca` varchar(100) NOT NULL,
  `disponible` int NOT NULL,
  PRIMARY KEY (`cod_telefono`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-12-15  1:25:58
