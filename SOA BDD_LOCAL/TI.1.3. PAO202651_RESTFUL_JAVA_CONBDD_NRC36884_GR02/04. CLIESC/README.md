# 04. CLIESC: CLIESC_EurekaBank_JAVA_RESTFUL_GR02

Cliente de **escritorio** EurekaBank (RESTful Java) en **Swing con FlatLaf**: login con panel en degradado, barra lateral, tarjetas redondeadas con sombra, iconos vectoriales dibujados con Java2D, tabla estilizada, comprobante y notificaciones. Las llamadas al servicio van en segundo plano (`SwingWorker`) para no congelar la interfaz.

## Ejecutar
```bash
mvn package
java -jar target/CLIESC_EurekaBank_JAVA_RESTFUL_GR02.jar
```
También con **Run** en NetBeans. Servidor por defecto: `http://localhost:8080/WS_EurekaBank_JAVA_RESTFUL_GR02/api/eurekabank` (se recuerda con `java.util.prefs`). También funciona con el servidor REST .NET del TI.1.4 (`http://localhost:5080/api/eurekabank`): basta cambiar la URL en el inicio de sesión.

## Funcionalidad
- Inicio de sesión con usuario de empleado (prueba: **MONSTER / Monster9**) y **URL del servidor editable**, que se recuerda.
- **Cuentas:** indicadores (cuentas activas y saldo total por moneda) y listado con estado.
- **Movimientos:** saldo, total de ingresos y salidas, e historial con colores (verde ingreso, rojo salida).
- **Operaciones:** depósito, retiro y transferencia con comprobante (nuevo saldo) o mensaje de rechazo con su código.
- Si la sesión expira (`AUTH-002`) vuelve al inicio de sesión.

## Estructura (paquetes)
| Paquete | Contenido |
|---|---|
| `ec.edu.monster` | `Principal`: configura FlatLaf y abre el login |
| `ec.edu.monster.vistas` | `Tema` (paleta, componentes, logo e iconos), `VentanaLogin`, `VentanaPrincipal`, `PanelCuentas`, `PanelMovimientos`, `PanelOperacion` y `TablaEureka` |
| `ec.edu.monster.controladores` | `TareaServicio`: llamadas en segundo plano |
| `ec.edu.monster.servicios` | `IServicioEurekaBank` y `ServicioEurekaBankRest` (HttpURLConnection + Gson) |
