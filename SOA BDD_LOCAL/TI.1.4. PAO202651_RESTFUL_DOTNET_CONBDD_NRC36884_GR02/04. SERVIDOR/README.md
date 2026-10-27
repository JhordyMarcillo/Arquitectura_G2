# 04. SERVIDOR: WS_EurekaBank_DOTNET_RESTFUL_GR02

Servicio REST de EurekaBank en **ASP.NET Core (.NET 10)** sobre **SQL Server**. Cumple el contrato común del grupo: [`CONTRATO_EUREKABANK_GR02.md`](../../CONTRATO_EUREKABANK_GR02.md).

## Ejecutar
```bash
# 1. Base de datos (una sola vez)
cd "../03. BDD" && docker compose up -d

# 2. Servidor: escucha en http://0.0.0.0:5080 (accesible desde la red local)
dotnet run --project WS_EurekaBank_DOTNET_RESTFUL_GR02/WS_EurekaBank_DOTNET_RESTFUL_GR02
```
URL base: `http://localhost:5080/api/eurekabank`.

**Cadena de conexión.** Se busca en este orden:
1. La variable de entorno `DB_CONNECTION_STRING`.
2. El `.env` de `03. BDD`.
3. `appsettings.json`.

Para usar la base de otra máquina, cambia `Server=` en el `.env`.

## Arquitectura
| Carpeta (namespace) | Contenido |
|---|---|
| `ec.edu.monster.controladores.api` | `ControladorEurekaBank`: rutas REST |
| `ec.edu.monster.controladores.manejadores` | `ManejadorAutenticacion` (token Bearer), `ManejadorErrores` (respuesta de error uniforme) y `PermitirAnonimo` |
| `ec.edu.monster.datos` | `AccesoDB`, `ConfiguracionBaseDatos` y repositorios de `Usuario`, `Cuenta` y `Movimiento` (ADO.NET con parámetros) |
| `ec.edu.monster.modelos.utilidades` | Solicitudes, respuestas, `DatosCuenta`, `DatosMovimiento`, `CodigosRespuesta`, `ExcepcionNegocio` e `IServicioOperaciones` |
| `ec.edu.monster.modelos.utilidades.enums` | `EstadoCuenta`, `EstadoToken` y `TipoMovimiento` |
| `ec.edu.monster.modelos.utilidades.mapeadores` | Conversión de filas a objetos y de tipo de movimiento a código (`003`, `004`, `008`, `009`, `010`) |
| `ec.edu.monster.pruebas` | `EurekaBank.http`: peticiones de prueba manual |
| `ec.edu.monster.seguridad` | `AdministradorTokens`, `CifradoClave` (SHA1, igual que en MySQL) y `ConfiguracionCors` |
| `ec.edu.monster.servicios` | `ServicioAutenticacion`, `ServicioCuentas` y `ServicioOperaciones` (depósito, retiro y transferencia, cada uno en una transacción con bloqueo de fila) |

Respecto al TI.1.4 SIN BDD se agrega la carpeta `datos`, que es la capa de acceso a la base.

## Pruebas
- `dotnet test`: 10 pruebas unitarias (cifrado, tokens y reglas de importe, clave y saldo).
- `python "../../pruebas/test_contrato_rest.py" http://localhost:5080/api/eurekabank`: prueba de contrato completa contra el servidor en ejecución. Resultado: **32/32 verificaciones correctas**.
