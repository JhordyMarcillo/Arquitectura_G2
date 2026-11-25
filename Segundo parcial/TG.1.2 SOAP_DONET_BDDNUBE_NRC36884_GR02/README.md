# TG.1.2 SOAP .NET con BDD en la nube: EurekaBank

Versión del TI.1.2 (SOAP .NET con BDD local) en la que **la base de datos SQL Server corre en la nube**, en una instancia de **Amazon RDS** (AWS). El servidor SOAP y los cuatro clientes son los mismos; solo cambia dónde vive la base.

| Dato | Valor |
|---|---|
| Período / NRC / Grupo | PAO202651 / 36884 / GR02 |
| Tecnología | SOAP, C# (.NET 10, CoreWCF) |
| Base de datos | **SQL Server en Amazon RDS** (instancia `arqg2-sqlserver`, us-east-1). Ver `03. BDD` |
| Origen | `TI.1.2. PAO202651_SOAP_DOTNET_CONBDD_NRC36884_GR02` (SOA BDD_LOCAL, primer parcial) |
| Estado | Servidor probado contra la base en la nube: 12 pruebas unitarias y **20/20** verificaciones del contrato SOAP. Los clientes de consola, escritorio y web compilan; el móvil no se recompiló |

## Qué cambió respecto al TI.1.2 local
| Aspecto | TI.1.2 (BDD local) | TG.1.2 (BDD en la nube) |
|---|---|---|
| Dónde corre SQL Server | Contenedor Docker en la PC | Amazon RDS for SQL Server (instancia administrada) |
| Cómo se crea la base | `docker compose up -d` | `03. BDD/Aplicar_BDD_RDS.ps1` (sqlcmd contra RDS) |
| Conexión del servidor | `localhost,1433`, usuario `eureka` / `admin` | Endpoint de RDS, usuario `eureka` con clave propia, `Encrypt=True` |
| Claves | `.env` versionado (solo práctica) | `.env` **no versionado** (`.gitignore`) y `.env.example` como plantilla |
| Acceso de red | Solo la PC | Security group de AWS: solo las IP autorizadas, puerto 1433 |
| Compilación del servidor | `bin`/`obj` en la carpeta del proyecto | `bin`/`obj` en `%LOCALAPPDATA%\EurekaBankGR02` (`Directory.Build.props`): la ruta de esta carpeta es demasiado larga para la DLL nativa del driver |

El modelo de datos (17 tablas), los datos de prueba, el contrato SOAP y los clientes **no cambian**.

## Contenido
| Carpeta | Qué contiene |
|---|---|
| `01. UML` | ERS, ECUD y diagramas UML (la arquitectura muestra la base en Amazon RDS) |
| `02. MER` | Modelo entidad-relación: conceptual, lógico y físico |
| `03. BDD` | Scripts de EurekaBank para RDS, `.env` y script de despliegue |
| `04. SERVIDOR` | Servicio web `WS_EurekaBank_DOTNET_SOAP_GR02` |
| `04. CLICON` | Cliente de consola |
| `04. CLIESC` | Cliente de escritorio |
| `04. CLIMOV` | Cliente móvil |
| `04. CLIWEB` | Cliente web |
| `05. DOCUMENTACION` | Manual y presentación adaptados a la base en la nube |

## Puesta en marcha
1. Encender la instancia de RDS (tarda 10 a 20 minutos) y confirmar que tu IP está permitida en el security group (puerto 1433).
2. `03. BDD`: `powershell -ExecutionPolicy Bypass -File .\Aplicar_BDD_RDS.ps1` (solo la primera vez).
3. `04. SERVIDOR`: `dotnet run --project WS_EurekaBank_DOTNET_SOAP_GR02/WS_EurekaBank_DOTNET_SOAP_GR02`.
4. Abrir cualquiera de los clientes. Usuario de prueba: `MONSTER / Monster9`.

> **Costo:** la instancia de RDS se factura mientras esté encendida. Al terminar: `aws rds stop-db-instance --region us-east-1 --db-instance-identifier arqg2-sqlserver`. AWS la vuelve a encender sola a los 7 días.
