# 04. CLIWEB: CLIWEB_EurekaBank_DOTNET_SOAP_GR02

Cliente **web** EurekaBank (SOAP .NET) en **ASP.NET Core Razor Pages (.NET 10)**. Mismo diseño que el escritorio (login dividido, barra lateral, tarjetas, historial y comprobante) con CSS e iconos SVG propios, sin CDN: funciona en una red local sin internet.

## Ejecutar
```bash
dotnet run --project CLIWEB_EurekaBank_DOTNET_SOAP_GR02
```
Abrir `http://localhost:5190` (escucha en `0.0.0.0`, accesible desde la red local). Servidor por defecto (`appsettings.json`): `http://localhost:5090/WSEurekaBank.svc`. También funciona con el servidor SOAP Java del TI.1.1 cuando cumpla el contrato: basta cambiar la URL en el inicio de sesión.

## Funcionalidad
- Inicio de sesión con usuario de empleado (prueba: **MONSTER / Monster9**) y **URL del servidor editable**, que se recuerda.
- **Cuentas:** indicadores (cuentas activas y saldo total por moneda) y listado con estado.
- **Movimientos:** saldo, total de ingresos y salidas, e historial con colores (verde ingreso, rojo salida).
- **Operaciones:** depósito, retiro y transferencia con comprobante (nuevo saldo) o mensaje de rechazo con su código.
- Si la sesión expira (`AUTH-002`) vuelve al inicio de sesión.

## Estructura
| Carpeta | Contenido |
|---|---|
| `ec.edu.monster.vistas` | Páginas `Login`, `Index` (cuentas), `Movimientos` y `Operacion/{tipo}`, plantilla e `Iconos` |
| `ec.edu.monster.controladores` | `PaginaProtegida` (exige sesión) y `SesionWeb` (token y URL en la sesión del navegador) |
| `wwwroot` | `css/eurekabank.css` y `img/logo.svg` |
| `ec.edu.monster.servicios` | `IServicioEurekaBank` y `ServicioEurekaBankSoap` (ChannelFactory de WCF con el contrato `IWSEurekaBank`) |

Prueba funcional: `python "../../../pruebas/test_cliente_web.py" http://localhost:5190` (11/11).
