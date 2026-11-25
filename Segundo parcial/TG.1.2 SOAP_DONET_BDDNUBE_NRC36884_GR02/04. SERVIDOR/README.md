# 04. SERVIDOR: WS_EurekaBank_DOTNET_SOAP_GR02

Servicio **SOAP** de EurekaBank en **.NET 10 con CoreWCF**, la versión moderna de WCF, sobre **SQL Server en Amazon RDS (nube de AWS)**. Cumple el contrato común del grupo: [`CONTRATO_EUREKABANK_GR02.md`](../../CONTRATO_EUREKABANK_GR02.md), sección 4.

## Ejecutar
```bash
# 1. Base de datos en la nube (una sola vez): RDS encendida y scripts aplicados
cd "../03. BDD" && powershell -ExecutionPolicy Bypass -File .\Aplicar_BDD_RDS.ps1

# 2. Servidor: escucha en http://0.0.0.0:5090 (accesible desde la red local)
dotnet run --project WS_EurekaBank_DOTNET_SOAP_GR02/WS_EurekaBank_DOTNET_SOAP_GR02
```
- **Servicio:** `http://localhost:5090/WSEurekaBank.svc`
- **WSDL:** `http://localhost:5090/WSEurekaBank.svc?wsdl` (en un solo archivo: `?singleWsdl`). Se puede importar en SoapUI o usarlo para generar un proxy.
- **Dirección:** el WSDL publica la dirección con la que se lo pidió, sea `localhost` o la IP de la red.

## Conexión a la base en la nube
La cadena de conexión se resuelve en este orden: variable de entorno `DB_CONNECTION_STRING`, archivo `03. BDD/.env` (con la clave real, no versionado) y `appsettings.json` (sin clave, solo plantilla). Usa `Encrypt=True`. Si el servidor responde `No se pudo acceder a la base de datos`, revisa que la RDS esté disponible y que tu IP esté permitida en el security group (puerto 1433); el motivo se imprime en la consola del servidor (`[BD] ...`).

> `Directory.Build.props` envía `bin` y `obj` a `%LOCALAPPDATA%\EurekaBankGR02`. La ruta de esta carpeta es tan larga que, con ella, la DLL nativa del driver (`Microsoft.Data.SqlClient.SNI.dll`) no carga (`The filename or extension is too long`).

## Contrato SOAP
- **Espacio de nombres:** `http://ws.monster.edu.ec/`
- **Servicio:** `WSEurekaBank`
- **Estilo:** document/literal wrapped
- **Operaciones:** `iniciarSesion`, `listarCuentas`, `consultarMovimientos`, `registrarDeposito`, `registrarRetiro` y `registrarTransferencia`.
- **Errores de negocio:** no generan fault. Se devuelven en la respuesta con `estado = ERROR` y el `codigo` del contrato.

## Arquitectura
| Carpeta (namespace) | Contenido |
|---|---|
| `ec.edu.monster.controladores.ws` | `IWSEurekaBank` (contrato `[ServiceContract]`) y `WSEurekaBank` (implementación) |
| `ec.edu.monster.controladores.manejadoresws` | `ManejadorSoap`: valida el token y convierte excepciones en respuestas con código |
| `ec.edu.monster.datos` | `AccesoDB`, `ConfiguracionBaseDatos` y repositorios (ADO.NET con parámetros) |
| `ec.edu.monster.modelos.utilidades` | Respuestas `[DataContract]` con nombres en camelCase, solicitudes, códigos y `ExcepcionNegocio` |
| `ec.edu.monster.modelos.utilidades.enums` y `.mapeadores` | Estados, tipos de movimiento y conversiones |
| `ec.edu.monster.pruebas` | `SolicitudesSoap.http`: sobres SOAP de prueba manual |
| `ec.edu.monster.seguridad` | `AdministradorTokens` y `CifradoClave` (SHA1, igual que en MySQL) |
| `ec.edu.monster.servicios` | `ServicioAutenticacion`, `ServicioCuentas` y `ServicioOperaciones` (las mismas reglas transaccionales del TI.1.4) |

## Pruebas
- `dotnet test`: 12 pruebas unitarias.
- `python "../../pruebas/test_contrato_soap.py" http://localhost:5090/WSEurekaBank.svc`: resultado **20/20 verificaciones correctas**.
