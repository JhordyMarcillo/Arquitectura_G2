# 04. CLIMOV: CLIMOV_EurekaBank_DOTNET_SOAP_GR02

Cliente **móvil** EurekaBank (SOAP .NET) en **.NET MAUI** para Android (y Windows): login de marca, pestañas Cuentas / Movimientos / Operar, tarjetas, historial con colores, selector segmentado y comprobante.

## Ejecutar
- **Visual Studio:** abrir el proyecto, elegir el emulador Android y ejecutar.
- **Línea de comandos:** `dotnet build -t:Run -f net10.0-android` (con un emulador o celular conectado).
- **URL del servidor:** en el emulador `localhost` se cambia por `10.0.2.2` (la PC); en un celular real se escribe la IP de la máquina del servidor, por ejemplo `http://192.168.1.50:5090/WSEurekaBank.svc`. También funciona con el servidor SOAP Java del TI.1.1 cuando cumpla el contrato: basta cambiar la URL en el inicio de sesión.

> `Directory.Build.props` envia `obj` y `bin` a `%LOCALAPPDATA%\EurekaBankGR02` porque la herramienta de Android (aapt2) no soporta las rutas largas de esta carpeta. HTTP sin cifrar está habilitado para la red local.

## Funcionalidad
- Inicio de sesión con usuario de empleado (prueba: **MONSTER / Monster9**) y **URL del servidor editable**, que se recuerda.
- **Cuentas:** indicadores (cuentas activas y saldo total por moneda) y listado con estado.
- **Movimientos:** saldo, total de ingresos y salidas, e historial con colores (verde ingreso, rojo salida).
- **Operaciones:** depósito, retiro y transferencia con comprobante (nuevo saldo) o mensaje de rechazo con su código.
- Si la sesión expira (`AUTH-002`) vuelve al inicio de sesión.

## Estructura
| Carpeta | Contenido |
|---|---|
| `ec.edu.monster.vistas` | `PaginaLogin`, `PaginaCuentas`, `PaginaMovimientos`, `PaginaOperaciones` y filas de presentación |
| `Resources` | Estilos EurekaBank, logo e iconos SVG, icono de la app y pantalla de inicio |
| `ec.edu.monster.servicios` | `IServicioEurekaBank` y `ServicioEurekaBankSoap` (ChannelFactory de WCF con el contrato `IWSEurekaBank`) |
