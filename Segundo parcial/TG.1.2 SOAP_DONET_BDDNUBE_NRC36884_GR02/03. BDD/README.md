# 03. BDD: EurekaBank en SQL Server (Amazon RDS)

Modelo EurekaBank de 17 tablas traducido a T-SQL desde el script MySQL del Ing. Eric Coronel (versión del 20-jun-2017). Es **idéntico** al de los TI Java, que usan MySQL: mismas tablas, mismos datos y mismas claves. En esta versión la base **corre en la nube**, en una instancia de **Amazon RDS for SQL Server** (AWS).

> El script SQL Server original del docente es una versión anterior: tiene 13 tablas y le falta el módulo de seguridad (`Usuario`, `Modulo`, `Permiso`, `LogSession`). Además, sus procedimientos están escritos en sintaxis MySQL. Por eso el grupo usa esta traducción.

| Archivo | Para qué sirve |
|---|---|
| `Crea_BD.sql` | Crea la base `EUREKABANK`, las 17 tablas y el login de la aplicación `eureka` (la clave llega con `sqlcmd -v EUREKA_PASSWORD=...`, no está en el archivo) |
| `Carga_Datos.sql` | Carga los datos de prueba |
| `Aplicar_BDD_RDS.ps1` | Ejecuta los dos scripts contra la instancia de RDS. Solo actúa si la base no existe; `-Recrear` la borra y la vuelve a crear |
| `.env.example` | Plantilla sin claves de la conexión |
| `.env` | Conexión real (endpoint, clave del login `eureka`). **No se versiona**: está en `.gitignore` |

> En el TI local el `.env` se versionaba a propósito porque eran claves de práctica en un contenedor. Aquí la base es accesible desde Internet (limitada por el security group), por eso las claves **no** van al repositorio.

## Instancia en AWS
| Dato | Valor |
|---|---|
| Servicio | Amazon RDS for SQL Server (edición Express) |
| Identificador | `arqg2-sqlserver` (región us-east-1) |
| Endpoint | `arqg2-sqlserver.c03ae0mmmpir.us-east-1.rds.amazonaws.com`, puerto `1433` |
| Administrador | `admin` (clave en `%USERPROFILE%\.aws\rds-arqg2-credenciales.txt`; solo la usa el script de despliegue) |
| Red | Security group `arqg2-rds-sg`: acceso por IP autorizada al puerto 1433 |

Encender y apagar (la instancia se factura mientras está encendida; AWS la reinicia sola a los 7 días de estar apagada):
```bash
aws rds start-db-instance --region us-east-1 --db-instance-identifier arqg2-sqlserver
aws rds wait db-instance-available --region us-east-1 --db-instance-identifier arqg2-sqlserver
aws rds stop-db-instance  --region us-east-1 --db-instance-identifier arqg2-sqlserver
```

## Cómo crear la base
```powershell
powershell -ExecutionPolicy Bypass -File .\Aplicar_BDD_RDS.ps1            # primera vez
powershell -ExecutionPolicy Bypass -File .\Aplicar_BDD_RDS.ps1 -Recrear   # empezar de cero
```
Requiere `sqlcmd` y que tu IP esté permitida en el security group. Desde SSMS: conéctate al endpoint con el usuario `admin`, activa *Query > SQLCMD Mode*, agrega `:setvar EUREKA_PASSWORD "<clave>"` al inicio de `Crea_BD.sql` y ejecútalo; luego `Carga_Datos.sql`.

## Conexión desde el servidor
| Dato | Valor |
|---|---|
| Host / puerto | endpoint de RDS, `1433` |
| Base de datos | `EUREKABANK` |
| Usuario | `eureka` (login SQL, rol `db_owner` solo en `EUREKABANK`; la clave está en `.env`) |
| Cadena .NET | `Server=<endpoint>,1433;Database=EUREKABANK;User Id=eureka;Password=<clave>;Encrypt=True;TrustServerCertificate=True;` |

El servidor busca la cadena en este orden: variable de entorno `DB_CONNECTION_STRING`, este `.env` y, por último, `appsettings.json` (sin clave: solo plantilla).

## Datos de prueba
- **Usuarios** (tabla `Usuario`, clave guardada con `SHA1` en hexadecimal, igual que en MySQL): `MONSTER / Monster9` (usuario del GR02), `cromero / chicho`, `lcastro / flaca`, `aramos / china`, `internet / internet`. `creyes` y `ediaz` están `ANULADO`.
- **Cuentas:** `00100001`, `00100002`, `00200001`, `00200002`, `00200003` (activas) y `00300001` (cancelada). Clave de cuenta: `123456`.
- **Tipos de movimiento:** `003` depósito, `004` retiro, `008` / `009` transferencia (ingreso / salida), `010` cargo por movimiento.
