# 04. CLICON: CLICON_EurekaBank_JAVA_RESTFUL_GR02

Cliente de **consola** EurekaBank (RESTful Java) con Maven: banner, menu numerado, tablas con bordes redondeados y colores ANSI para ingresos y salidas.

## Ejecutar
```bash
mvn package
java -jar target/CLICON_EurekaBank_JAVA_RESTFUL_GR02.jar
# o indicando el servidor:
java -jar target/CLICON_EurekaBank_JAVA_RESTFUL_GR02.jar http://localhost:8080/WS_EurekaBank_JAVA_RESTFUL_GR02/api/eurekabank
```
También con **Run** en NetBeans. Servidor por defecto: `http://localhost:8080/WS_EurekaBank_JAVA_RESTFUL_GR02/api/eurekabank`. También funciona con el servidor REST .NET del TI.1.4 (`http://localhost:5080/api/eurekabank`): basta cambiar la URL en el inicio de sesión.

## Funcionalidad
- Inicio de sesión con usuario de empleado (prueba: **MONSTER / Monster9**) y **URL del servidor editable**, que se recuerda.
- **Cuentas:** indicadores (cuentas activas y saldo total por moneda) y listado con estado.
- **Movimientos:** saldo, total de ingresos y salidas, e historial con colores (verde ingreso, rojo salida).
- **Operaciones:** depósito, retiro y transferencia con comprobante (nuevo saldo) o mensaje de rechazo con su código.
- Si la sesión expira (`AUTH-002`) vuelve al inicio de sesión.

## Estructura (paquetes)
| Paquete | Contenido |
|---|---|
| `ec.edu.monster` | `Principal` (punto de entrada) |
| `ec.edu.monster.controladores` | `ControladorConsola`: inicio de sesión, menu y operaciones |
| `ec.edu.monster.vistas` | `VistaConsola`: encabezado, tablas, tarjetas y paneles |
| `ec.edu.monster.servicios` | `IServicioEurekaBank` y `ServicioEurekaBankRest` (HttpURLConnection + Gson) |
| `ec.edu.monster.modelos` / `utilidades` | Respuestas del contrato, `ExcepcionServicio` y `Formato` |
