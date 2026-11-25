# 04. CLIWEB: CLIWEB_EurekaBank_JAVA_SOAP_GR02

Cliente **web** EurekaBank (SOAP Java) con **JSP + Servlets (Jakarta EE 11)** para Payara. Usa el mismo CSS y logo que los demás clientes web del grupo: login dividido, barra lateral, tarjetas, historial y comprobante. Consume el servicio `WS_EurekaBank_JAVA_SOAP_GR02` con **JAX-WS**; Payara ya trae Metro y JAXB, así que el WAR no los empaqueta.

## Ejecutar
`mvn package` y desplegar `target/CLIWEB_EurekaBank_JAVA_SOAP_GR02.war` en Payara (o **Run** en NetBeans):
```bash
asadmin deploy --force=true target/CLIWEB_EurekaBank_JAVA_SOAP_GR02.war
```
Abrir `http://localhost:8080/CLIWEB_EurekaBank_JAVA_SOAP_GR02`.

Servicio por defecto (`web.xml`, parámetro `urlServidor`): `http://localhost:8080/WS_EurekaBank_JAVA_SOAP_GR02/WSEurekaBank`. Se puede cambiar en el inicio de sesión.

## Funcionalidad
- Inicio de sesión con usuario de empleado (prueba: **MONSTER / Monster9**) y **URL del servicio editable**.
- **Cuentas:** indicadores (cuentas activas y saldo total por moneda) y listado con estado.
- **Movimientos:** saldo, total de ingresos y salidas, e historial con colores (verde ingreso, rojo salida).
- **Operaciones:** depósito, retiro y transferencia con comprobante (nuevo saldo) o mensaje de rechazo con su código.
- Si la sesión expira (`AUTH-002`) vuelve al inicio de sesión.

## Estructura
| Paquete / carpeta | Contenido |
|---|---|
| `ec.edu.monster.controladores` | `FiltroSesion` (exige sesión), `ControladorLogin`, `ControladorPaginas` y `SesionWeb` (un proxy SOAP por sesión del navegador) |
| `ec.edu.monster.servicios` | `IServicioEurekaBank`, `ContratoWSEurekaBank` (interfaz `@WebService` del contrato) y `ServicioEurekaBankSoap` (proxy JAX-WS creado desde el WSDL) |
| `ec.edu.monster.modelos` | Respuestas del contrato (JAXB) y `ExcepcionServicio` |
| `ec.edu.monster.vistas` | `FilaCuenta`, `FilaMovimiento` y `LineaComprobante` (datos ya formateados para las JSP) |
| `WEB-INF/vistas` | `login.jsp`, `cuentas.jsp`, `movimientos.jsp`, `operacion.jsp` y fragmentos comunes |
| `recursos` | `css/eurekabank.css` e imágenes |

Prueba funcional: `python "../../../pruebas/test_cliente_web.py" http://localhost:8080/CLIWEB_EurekaBank_JAVA_SOAP_GR02 java` (**11/11**).
