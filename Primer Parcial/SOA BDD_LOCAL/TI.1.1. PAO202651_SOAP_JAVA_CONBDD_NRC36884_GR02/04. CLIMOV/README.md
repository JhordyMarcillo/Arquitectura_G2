# 04. CLIMOV: CLIMOV_EurekaBank_JAVA_SOAP_GR02

Cliente **móvil Android nativo** EurekaBank (SOAP Java) con **Material 3**: login de marca, pestañas Cuentas / Movimientos / Operar, tarjetas, historial con colores, selector segmentado y comprobante. Consume el servicio `WS_EurekaBank_JAVA_SOAP_GR02`. Las llamadas al servicio van en hilos de fondo.

## Por qué el SOAP va "a mano"
Android no trae JAX-WS ni JAXB (las librerías que usan la consola, el escritorio y la web), así que el cliente SOAP se escribió directamente en `ServicioEurekaBankSoap`, sin dependencias externas:
1. **Arma el sobre SOAP 1.1** con el mismo XML del contrato:
   ```xml
   <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/"><s:Body>
     <iniciarSesion xmlns="http://ws.monster.edu.ec/"><usuario>MONSTER</usuario><clave>Monster9</clave></iniciarSesion>
   </s:Body></s:Envelope>
   ```
2. **Lo envía** por `HttpURLConnection` (`POST`, `Content-Type: text/xml`, cabecera `SOAPAction`).
3. **Lee la respuesta** con `XmlPullParser` (incluido en Android): busca `<operacionResult>`, lo convierte en un árbol simple y lo pasa a los modelos (`RespuestaAutenticacion`, `DatosCuenta`, `DatosMovimiento`...). Respeta `xsi:nil` (por ejemplo, `cuentaReferencia` vacía) y detecta los `Fault` de SOAP.

Funciona con el servidor Java (TI.1.1) y con el .NET (TI.1.2), porque ambos publican el mismo XML.

## Ejecutar
- **Android Studio:** abrir esta carpeta y ejecutar en un emulador o celular.
- **Línea de comandos:** `gradlew assembleDebug` (requiere `ANDROID_HOME` y JDK 17 o superior). El APK queda en `%LOCALAPPDATA%\EurekaBankGR02\CLIMOV_EurekaBank_JAVA_SOAP_GR02\app\outputs\apk\debug`.
- **URL del servicio:** en el emulador se usa `10.0.2.2` (la PC): `http://10.0.2.2:8080/WS_EurekaBank_JAVA_SOAP_GR02/WSEurekaBank`. En un celular real, la IP de la máquina del servidor. También se puede enviar al abrir la app: `adb shell am start -n ec.edu.monster.eurekabank.soapjava/ec.edu.monster.vistas.ActividadLogin --es urlServidor URL` (así lo hace `LEVANTAR_TI_1.1.bat`).

> La carpeta `build` se genera en `%LOCALAPPDATA%\EurekaBankGR02` porque aapt2 no soporta rutas largas. HTTP sin cifrar está habilitado para la red local. El `applicationId` es `ec.edu.monster.eurekabank.soapjava`, así que se puede instalar junto a la app REST del TI.1.3.

## Funcionalidad
- Inicio de sesión con usuario de empleado (prueba: **MONSTER / Monster9**) y **URL del servicio editable**, que se recuerda.
- **Cuentas:** indicadores (cuentas activas y saldo total por moneda) y listado con estado.
- **Movimientos:** saldo, total de ingresos y salidas, e historial con colores (verde ingreso, rojo salida).
- **Operaciones:** depósito, retiro y transferencia con comprobante (nuevo saldo) o mensaje de rechazo con su código.
- Si la sesión expira (`AUTH-002`) vuelve al inicio de sesión.

## Estructura
| Paquete / carpeta | Contenido |
|---|---|
| `ec.edu.monster.vistas` | `ActividadLogin`, `ActividadPrincipal`, fragmentos `FragmentoCuentas`, `FragmentoMovimientos`, `FragmentoOperaciones` y adaptadores |
| `ec.edu.monster.controladores` | `SesionApp`: servicio compartido, URL recordada y tareas en segundo plano |
| `ec.edu.monster.servicios` | `IServicioEurekaBank` y `ServicioEurekaBankSoap` (SOAP con `HttpURLConnection` + `XmlPullParser`) |
| `ec.edu.monster.modelos` / `utilidades` | Respuestas del contrato, `ExcepcionServicio` y `Formato` |
| `res` | Colores, tema, fondos, iconos vectoriales y layouts |
