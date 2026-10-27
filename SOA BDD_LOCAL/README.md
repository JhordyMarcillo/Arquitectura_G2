# SOA con BDD local: EurekaBank (GR02, NRC 36884, PAO202651)

Segunda fase de los talleres SOA. El caso de estudio es **EurekaBank**, un sistema de cuentas de ahorro. Los servicios trabajan contra una **base de datos local**.

| TI | TecnologÃ­a | Motor de BDD | Responsable | Carpeta |
|---|---|---|---|---|
| TI.1.1 | SOAP Java | MySQL 8 | David Cepeda | `TI.1.1. PAO202651_SOAP_JAVA_CONBDD_NRC36884_GR02` |
| TI.1.2 | SOAP .NET | SQL Server 2022 | German Caceres | `TI.1.2. PAO202651_SOAP_DOTNET_CONBDD_NRC36884_GR02` |
| TI.1.3 | RESTful Java | MySQL 8 | German Caceres | `TI.1.3. PAO202651_RESTFUL_JAVA_CONBDD_NRC36884_GR02` |
| TI.1.4 | RESTful .NET | SQL Server 2022 | German Caceres | `TI.1.4. PAO202651_RESTFUL_DOTNET_CONBDD_NRC36884_GR02` |

## Una sola base de datos, dos motores
Los cuatro TI usan **el mismo modelo EurekaBank (17 tablas)** con **los mismos datos de prueba**:

- Los proyectos **Java** usan **MySQL**: `03. BDD` de TI.1.1 y TI.1.3.
- Los proyectos **.NET** usan **SQL Server**: `03. BDD` de TI.1.2 y TI.1.4.

Los scripts de un mismo motor son idÃ©nticos en todos los TI. AsÃ­, un mismo cliente (por ejemplo, el usuario `cromero` o la cuenta `00100001`) obtiene el mismo resultado en cualquiera de los cuatro servidores.

## Estructura de cada TI
```
01. UML/            ERS, ECUD y diagramas (casos de uso, actividad, secuencia, clases, arquitectura)
02. MER/            modelo conceptual, lÃ³gico y fÃ­sico
03. BDD/            Crea_BD.sql, Carga_Datos.sql y docker-compose.yml
04. SERVIDOR/       servicio web (SOAP o REST)
04. CLICON/         cliente de consola
04. CLIESC/         cliente de escritorio
04. CLIMOV/         cliente mÃ³vil
04. CLIWEB/         cliente web
05. DOCUMENTACION/  manuales, capturas y pruebas (SoapUI o Postman)
```
