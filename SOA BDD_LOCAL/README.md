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

## Configuración compartida (`.env`)
Cada `03. BDD` incluye un `.env` **versionado a propósito**, con los mismos valores para todo el grupo:

| Motor | Administrador del contenedor | Usuario de la aplicación | Puerto |
|---|---|---|---|
| MySQL | `root` / `Eureka.Gr02.Root2026` | `eureka` / `admin` | 3306 |
| SQL Server | `sa` / `Eureka.Gr02.Sa2026` | `eureka` / `admin` | 1433 |

Los servidores se conectan **siempre con `eureka` / `admin`**. El `.env` también trae `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` y la URL o cadena de conexión lista para usar.

> El repositorio es público: son claves **solo de práctica**. No las reutilices en otro sistema.

## Conectar cualquier cliente con cualquier servidor
1. Quien tenga la base ejecuta `docker compose up -d` en `03. BDD`. Docker publica el puerto en todas las interfaces, así que otras máquinas de la misma red pueden llegar a `IP_DE_ESA_MAQUINA:3306` o `IP_DE_ESA_MAQUINA:1433`.
2. Cada servidor lee `DB_HOST` del `.env`: `localhost` si la base corre en la misma máquina, o la IP de la máquina que la tiene.
3. Los clientes apuntan a la IP y al puerto del servidor, que se configuran en cada cliente. Como los cuatro servidores usan los mismos datos, cualquier cliente puede probarse contra cualquier servidor de su mismo estilo (SOAP o REST).
4. Si la conexión no llega desde otra máquina, abre el puerto en el firewall de Windows de quien tiene la base (3306 o 1433).

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
