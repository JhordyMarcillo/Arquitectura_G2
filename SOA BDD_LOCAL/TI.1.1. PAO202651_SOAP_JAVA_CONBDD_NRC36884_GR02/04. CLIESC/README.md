# 04. CLIESC: CLIESC_EurekaBank_JAVA_SOAP_GR02

Cliente de **escritorio** EurekaBank (SOAP Java) en **Swing con FlatLaf**: login con panel en degradado, barra lateral, tarjetas redondeadas con sombra, iconos vectoriales dibujados con Java2D, tabla estilizada, comprobante y notificaciones. Consume el servicio `WS_EurekaBank_JAVA_SOAP_GR02` con **JAX-WS (Metro)**. Las llamadas al servicio van en segundo plano (`SwingWorker`) para no congelar la interfaz.

## Ejecutar
```bash
mvn package
java -jar target/CLIESC_EurekaBank_JAVA_SOAP_GR02.jar
```
También con **Run** en NetBeans. Servicio por defecto: `http://localhost:8080/WS_EurekaBank_JAVA_SOAP_GR02/WSEurekaBank`. La URL se cambia en el inicio de sesión y se recuerda con `java.util.prefs` (nodo propio `cliesc-java-soap`, separado del cliente REST del TI.1.3). También se puede pasar como argumento: `java -jar target/CLIESC_EurekaBank_JAVA_SOAP_GR02.jar http://IP:8080/WS_EurekaBank_JAVA_SOAP_GR02/WSEurekaBank` (así la abre `LEVANTAR_TI_1.1.bat`).

## Funcionalidad
- Inicio de sesión con usuario de empleado (prueba: **MONSTER / Monster9**) y **URL del servicio editable**, que se recuerda.
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
| `ec.edu.monster.servicios` | `IServicioEurekaBank`, `ContratoWSEurekaBank` (interfaz `@WebService` del contrato) y `ServicioEurekaBankSoap` (proxy JAX-WS creado desde el WSDL) |
| `ec.edu.monster.modelos` / `utilidades` | Respuestas del contrato (JAXB), `ExcepcionServicio` y `Formato` |

La capa `servicios` y `modelos` es la misma del cliente de consola (`04. CLICON`).
