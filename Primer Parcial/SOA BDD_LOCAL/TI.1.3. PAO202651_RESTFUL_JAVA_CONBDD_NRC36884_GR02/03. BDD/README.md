# 03. BDD: EurekaBank en MySQL 8

Modelo EurekaBank de 17 tablas, basado en el script MySQL del Ing. Eric Coronel (versión del 20-jun-2017) y adaptado a MySQL 8. Es **idéntico** al de los TI .NET, que usan SQL Server: mismas tablas, mismos datos y mismas claves.

| Archivo | Para qué sirve |
|---|---|
| `Crea_BD.sql` | Crea la base `eurekabank`, las 17 tablas y el usuario de la aplicación `eureka` |
| `Carga_Datos.sql` | Carga los datos de prueba |
| `docker-compose.yml` | Levanta MySQL 8.4 con la base ya creada y cargada |
| `.env` | Claves compartidas del grupo (root del contenedor) y datos de conexión para los servidores (`DB_HOST`, `DB_USER`, `DB_URL`...) |

> El `.env` se versiona a propósito para que todos los TI usen la misma configuración. Son claves **solo de práctica**: el repositorio es público, así que no las reutilices en otro sistema.

## Cómo levantarla
**Opción A: Docker (recomendada)**
```bash
docker compose up -d
```
Los scripts se ejecutan solo la primera vez. Para empezar de cero: `docker compose down -v`.

**Opción B: MySQL instalado.** Ejecuta `Crea_BD.sql` y luego `Carga_Datos.sql` en Workbench o con `mysql -u root -p < archivo.sql`.

## Conexión desde el servidor
| Dato | Valor |
|---|---|
| Host / puerto | `localhost:3306` |
| Base de datos | `eurekabank` |
| Usuario / clave | `eureka` / `admin` |
| JDBC | `jdbc:mysql://localhost:3306/eurekabank` |

> En Linux (Docker) MySQL distingue mayúsculas en los nombres de tabla. Usa el nombre exacto: `Cuenta`, `Movimiento`, `Usuario`, etc.

## Datos de prueba
- **Usuarios** (tabla `Usuario`, clave guardada con `SHA1` en hexadecimal): `MONSTER / Monster9` (usuario del GR02), `cromero / chicho`, `lcastro / flaca`, `aramos / china`, `internet / internet`. `creyes` y `ediaz` están `ANULADO`.
- **Cuentas:** `00100001`, `00100002`, `00200001`, `00200002`, `00200003` (activas) y `00300001` (cancelada). Clave de cuenta: `123456`.
- **Tipos de movimiento:** `003` depósito, `004` retiro, `008` / `009` transferencia (ingreso / salida), `010` cargo por movimiento.
