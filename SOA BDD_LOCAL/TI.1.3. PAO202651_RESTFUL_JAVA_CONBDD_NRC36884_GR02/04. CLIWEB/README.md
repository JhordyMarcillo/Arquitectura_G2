# 04. CLIWEB: CLIWEB_EurekaBank_JAVA_RESTFUL_GR02

Cliente **web** EurekaBank (RESTful Java) con **JSP + Servlets (Jakarta EE 10)** para Payara. Usa el mismo CSS y logo que los clientes web .NET, asi que se ve igual: login dividido, barra lateral, tarjetas, historial y comprobante.

## Ejecutar
`mvn package` y desplegar `target/CLIWEB_EurekaBank_JAVA_RESTFUL_GR02.war` en Payara (o **Run** en NetBeans). Abrir `http://localhost:8080/CLIWEB_EurekaBank_JAVA_RESTFUL_GR02`.

Servidor por defecto (`web.xml`, parámetro `urlServidor`): `http://localhost:8080/WS_EurekaBank_JAVA_RESTFUL_GR02/api/eurekabank`. También funciona con el servidor REST .NET del TI.1.4 (`http://localhost:5080/api/eurekabank`): basta cambiar la URL en el inicio de sesión.

## Funcionalidad
- Inicio de sesión con usuario de empleado (prueba: **MONSTER / Monster9**) y **URL del servidor editable**, que se recuerda.
- **Cuentas:** indicadores (cuentas activas y saldo total por moneda) y listado con estado.
- **Movimientos:** saldo, total de ingresos y salidas, e historial con colores (verde ingreso, rojo salida).
- **Operaciones:** depósito, retiro y transferencia con comprobante (nuevo saldo) o mensaje de rechazo con su código.
- Si la sesión expira (`AUTH-002`) vuelve al inicio de sesión.

## Estructura
| Paquete / carpeta | Contenido |
|---|---|
| `ec.edu.monster.controladores` | `FiltroSesion` (exige sesión), `ControladorLogin`, `ControladorPaginas` y `SesionWeb` |
| `ec.edu.monster.vistas` | `FilaCuenta`, `FilaMovimiento` y `LineaComprobante` (datos ya formateados para las JSP) |
| `WEB-INF/vistas` | `login.jsp`, `cuentas.jsp`, `movimientos.jsp`, `operacion.jsp` y fragmentos comunes |
| `recursos` | `css/eurekabank.css` y `img/logo.svg` |

Prueba funcional: `python "../../../pruebas/test_cliente_web.py" http://localhost:8080/CLIWEB_EurekaBank_JAVA_RESTFUL_GR02 java` (11/11).
