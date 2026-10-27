# 04. CLIMOV: CLIMOV_EurekaBank_DOTNET_RESTFUL_GR02

Cliente **móvil** EurekaBank (RESTful .NET) en **.NET MAUI** para Android (y Windows): login de marca, pestañas Cuentas / Movimientos / Operar, tarjetas, historial con colores, selector segmentado y comprobante.

## Ejecutar
- **Visual Studio:** abrir el proyecto, elegir el emulador Android y ejecutar.
- **Línea de comandos:** `dotnet build -t:Run -f net10.0-android` (con un emulador o celular conectado).
- **URL del servidor:** en el emulador `localhost` se cambia por `10.0.2.2` (la PC); en un celular real se escribe la IP de la máquina del servidor, por ejemplo `http://192.168.1.50:5080/api/eurekabank`. También funciona con el servidor REST Java del TI.1.3 (`http://localhost:8080/WS_EurekaBank_JAVA_RESTFUL_GR02/api/eurekabank`): basta cambiar la URL en el inicio de sesión.

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
| `ec.edu.monster.servicios` | `IServicioEurekaBank` y `ServicioEurekaBankRest` (HttpClient + System.Text.Json) |
