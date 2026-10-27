# 03. BDD: EurekaBank en SQL Server

Modelo EurekaBank de 17 tablas traducido a T-SQL desde el script MySQL del Ing. Eric Coronel (versión del 20-jun-2017). Es **idéntico** al de los TI Java, que usan MySQL: mismas tablas, mismos datos y mismas claves.

> El script SQL Server original del docente es una versión anterior: tiene 13 tablas y le falta el módulo de seguridad (`Usuario`, `Modulo`, `Permiso`, `LogSession`). Además, sus procedimientos están escritos en sintaxis MySQL. Por eso el grupo usa esta traducción.

| Archivo | Para qué sirve |
|---|---|
| `Crea_BD.sql` | Crea la base `EUREKABANK`, las 17 tablas y el login de la aplicación `eureka` |
| `Carga_Datos.sql` | Carga los datos de prueba |
| `docker-compose.yml` | Levanta SQL Server 2022 y crea y carga la base si no existe |
| `.env.example` | Plantilla del `.env`, que contiene la clave de `sa` y no se sube al repositorio |

## Cómo levantarla
**Opción A: Docker (recomendada)**
```bash
cp .env.example .env        # editar MSSQL_SA_PASSWORD
docker compose up -d
```
El contenedor `init` ejecuta los scripts solo si la base no existe. Para empezar de cero: `docker compose down -v`.

**Opción B: SQL Server instalado.** Ejecuta `Crea_BD.sql` y luego `Carga_Datos.sql` en SSMS, o con `sqlcmd -S localhost -U sa -C -i archivo.sql`.

## Conexión desde el servidor
| Dato | Valor |
|---|---|
| Host / puerto | `localhost,1433` |
| Base de datos | `EUREKABANK` |
| Usuario / clave | `eureka` / `admin` (login SQL, rol `db_owner`) |
| Cadena .NET | `Server=localhost,1433;Database=EUREKABANK;User Id=eureka;Password=admin;TrustServerCertificate=True;` |

## Datos de prueba
- **Usuarios** (tabla `Usuario`, clave guardada con `SHA1` en hexadecimal, igual que en MySQL): `cromero / chicho`, `lcastro / flaca`, `aramos / china`, `internet / internet`. `creyes` y `ediaz` están `ANULADO`.
- **Cuentas:** `00100001`, `00100002`, `00200001`, `00200002`, `00200003` (activas) y `00300001` (cancelada). Clave de cuenta: `123456`.
- **Tipos de movimiento:** `003` depósito, `004` retiro, `008` / `009` transferencia (ingreso / salida), `010` cargo por movimiento.
