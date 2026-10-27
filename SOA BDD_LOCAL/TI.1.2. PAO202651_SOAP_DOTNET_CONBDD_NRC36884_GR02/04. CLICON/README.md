# 04. CLICON: CLICON_EurekaBank_DOTNET_SOAP_GR02

Cliente de **consola** EurekaBank (SOAP .NET) en .NET 10 con **Spectre.Console**: banner, menús navegables con flechas, tablas con bordes redondeados, paneles de éxito y error, y colores para ingresos y salidas.

## Ejecutar
```bash
dotnet run --project CLICON_EurekaBank_DOTNET_SOAP_GR02
# o indicando el servidor:
dotnet run --project CLICON_EurekaBank_DOTNET_SOAP_GR02 -- http://localhost:5090/WSEurekaBank.svc
```
Servidor por defecto: `http://localhost:5090/WSEurekaBank.svc`. También funciona con el servidor SOAP Java del TI.1.1 cuando cumpla el contrato: basta cambiar la URL en el inicio de sesión.

## Funcionalidad
- Inicio de sesión con usuario de empleado (prueba: **MONSTER / Monster9**) y **URL del servidor editable**, que se recuerda.
- **Cuentas:** indicadores (cuentas activas y saldo total por moneda) y listado con estado.
- **Movimientos:** saldo, total de ingresos y salidas, e historial con colores (verde ingreso, rojo salida).
- **Operaciones:** depósito, retiro y transferencia con comprobante (nuevo saldo) o mensaje de rechazo con su código.
- Si la sesión expira (`AUTH-002`) vuelve al inicio de sesión.

## Estructura
| Carpeta | Contenido |
|---|---|
| `ec.edu.monster.controladores` | `ControladorConsola`: flujo de inicio de sesión, menu y operaciones |
| `ec.edu.monster.vistas` | `VistaConsola`: encabezado, tablas, tarjetas y paneles |
| `ec.edu.monster.servicios` | `IServicioEurekaBank` y `ServicioEurekaBankSoap` (ChannelFactory de WCF con el contrato `IWSEurekaBank`) |
| `ec.edu.monster.modelos` | Respuestas del contrato y `ExcepcionServicio` |
