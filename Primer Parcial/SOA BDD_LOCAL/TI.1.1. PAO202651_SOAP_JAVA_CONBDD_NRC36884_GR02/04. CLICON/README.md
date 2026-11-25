# 04. CLICON: CLICON_EurekaBank_JAVA_SOAP_GR02

Cliente de **consola** EurekaBank (SOAP Java) con Maven: banner, menú numerado, tablas con bordes redondeados y colores ANSI para ingresos y salidas. Consume el servicio `WS_EurekaBank_JAVA_SOAP_GR02` con **JAX-WS (Metro)**.

## Ejecutar
```bash
mvn package
java -jar target/CLICON_EurekaBank_JAVA_SOAP_GR02.jar
# o indicando el servicio:
java -jar target/CLICON_EurekaBank_JAVA_SOAP_GR02.jar http://192.168.1.50:8080/WS_EurekaBank_JAVA_SOAP_GR02/WSEurekaBank
```
También con **Run** en NetBeans. Servicio por defecto: `http://localhost:8080/WS_EurekaBank_JAVA_SOAP_GR02/WSEurekaBank`. La URL se puede cambiar en el inicio de sesión.

## Funcionalidad
- Inicio de sesión con usuario de empleado (prueba: **MONSTER / Monster9**) y **URL del servicio editable**.
- **Cuentas:** listado con moneda, saldo y estado.
- **Movimientos:** saldo, número de ingresos e historial con colores (verde ingreso, rojo salida).
- **Operaciones:** depósito, retiro y transferencia con comprobante (nuevo saldo) o mensaje de rechazo con su código (`CTA-003`, `CTA-004`...).
- Si la sesión expira (`AUTH-002`) vuelve al inicio de sesión.

## Cómo consume el servicio SOAP
- `ContratoWSEurekaBank`: interfaz `@WebService` escrita a mano, espejo del contrato (espacio de nombres `http://ws.monster.edu.ec/`, operaciones y parámetros iguales a los del servidor).
- `ServicioEurekaBankSoap`: crea el proxy con `Service.create(URL?wsdl)` y `getPort(...)`, lo reutiliza mientras la URL no cambie y convierte las respuestas con `estado = ERROR` o los fallos de red en `ExcepcionServicio`.
- Los modelos (`RespuestaSesion`, `DatosCuenta`, `DatosMovimiento`...) llevan anotaciones JAXB con los mismos nombres del WSDL.

## Estructura (paquetes)
| Paquete | Contenido |
|---|---|
| `ec.edu.monster` | `Principal` (punto de entrada) |
| `ec.edu.monster.controladores` | `ControladorConsola`: inicio de sesión, menú y operaciones |
| `ec.edu.monster.vistas` | `VistaConsola`: encabezado, tablas, tarjetas y paneles |
| `ec.edu.monster.servicios` | `IServicioEurekaBank`, `ContratoWSEurekaBank` y `ServicioEurekaBankSoap` |
| `ec.edu.monster.modelos` / `utilidades` | Respuestas del contrato (JAXB), `ExcepcionServicio` y `Formato` |
