# 04. CLIESC: CLIESC_EurekaBank_DOTNET_RESTFUL_GR02

Cliente de **escritorio** EurekaBank (RESTful .NET) en **WPF (.NET 10)** con estilos propios: login con panel institucional, barra lateral de navegación, tarjetas con sombra, tablas estilizadas, comprobante de operaciones y notificaciones flotantes.

## Ejecutar
```bash
dotnet run --project CLIESC_EurekaBank_DOTNET_RESTFUL_GR02
```
También se abre desde Visual Studio. Servidor por defecto: `http://localhost:5080/api/eurekabank`. También funciona con el servidor REST Java del TI.1.3 (`http://localhost:8080/WS_EurekaBank_JAVA_RESTFUL_GR02/api/eurekabank`): basta cambiar la URL en el inicio de sesión.

## Funcionalidad
- Inicio de sesión con usuario de empleado (prueba: **MONSTER / Monster9**) y **URL del servidor editable**, que se recuerda.
- **Cuentas:** indicadores (cuentas activas y saldo total por moneda) y listado con estado.
- **Movimientos:** saldo, total de ingresos y salidas, e historial con colores (verde ingreso, rojo salida).
- **Operaciones:** depósito, retiro y transferencia con comprobante (nuevo saldo) o mensaje de rechazo con su código.
- Si la sesión expira (`AUTH-002`) vuelve al inicio de sesión.

## Estructura
| Carpeta | Contenido |
|---|---|
| `Recursos/Estilos.xaml` | Paleta, logo vectorial, estilos de campos, botones, menu y tablas |
| `ec.edu.monster.vistas` | `VentanaLogin`, `VentanaPrincipal` y páginas `PaginaCuentas`, `PaginaMovimientos`, `PaginaOperacion` |
| `ec.edu.monster.utilidades` | `Formato` (dinero, importes) y `Preferencias` (URL recordada) |
| `ec.edu.monster.servicios` | `IServicioEurekaBank` y `ServicioEurekaBankRest` (HttpClient + System.Text.Json) |
| `ec.edu.monster.modelos` | Respuestas del contrato y `ExcepcionServicio` |
