# 05. DOCUMENTACION

| Archivo / carpeta | Contenido |
|---|---|
| `WS_EUREKABANK_SOAP_JAVA_CONBDD_GR02.docx` | Manual (Word): arquitectura, base de datos, código del servidor y de los 4 clientes, mensajes SOAP reales y capturas |
| `WS_EUREKABANK_SOAP_JAVA_CONBDD_GR02.pptx` | Presentación (23 diapositivas, mismo diseño que las del grupo) |
| `SOAPUI/WS_EurekaBank_JAVA_SOAP_GR02-soapui-project.xml` | Proyecto SoapUI con las pruebas del contrato |
| `CAPTURAS/` | Capturas de funcionamiento del servidor y de los clientes |

## Proyecto SoapUI
En SoapUI: **File → Import Project** y elegir el `.xml`. El servidor debe estar desplegado en Payara (`http://localhost:8080/WS_EurekaBank_JAVA_SOAP_GR02/WSEurekaBank`). El WSDL ya va guardado dentro del proyecto.

- **Interfaz `WSEurekaBankPortBinding`:** una petición de ejemplo por cada operación (`iniciarSesion`, `listarCuentas`, `consultarMovimientos`, `registrarDeposito`, `registrarRetiro`, `registrarTransferencia`).
- **TestSuite `Contrato EurekaBank GR02`:** 3 casos y 18 peticiones. Cada caso inicia sesión con `MONSTER / Monster9` y un paso **Property Transfer** copia el `token` a la propiedad `token` del proyecto, que usan las demás peticiones (`${#Project#token}`).

| Caso | Qué comprueba |
|---|---|
| 1. Autenticacion | Login correcto (empleado 0015), clave incorrecta y usuario anulado (`AUTH-003`), token falso (`AUTH-001`) |
| 2. Consultas | 6 cuentas, movimientos de `00200001` en Soles, cuenta inexistente (`CTA-001`) |
| 3. Operaciones | Depósito, retiro y transferencia correctos; `DATA-001`, `CTA-002`, `CTA-003`, `CTA-004`, `CTA-005`, `CTA-006` |

Las aserciones son *SOAP Response*, *Not SOAP Fault* y *XPath Match* sobre `estado` y `codigo`, porque los errores de negocio llegan con `estado = ERROR`, no como fault.

> El caso 3 registra movimientos reales. Para volver a los datos iniciales, ejecute de nuevo `Crea_BD.sql` y `Carga_Datos.sql` (`03. BDD`).

## Capturas
Las del servidor, la consola, la web y el móvil se tomaron con la base recién cargada (cifras del estado inicial). Las de escritorio y las de base de datos se tomaron después, así que sus saldos incluyen esas operaciones.

| Archivo | Pantalla |
|---|---|
| `SERVIDOR_01_endpoint.png` | Página del servicio en Payara (servicio, puerto y clase) |
| `SERVIDOR_02_wsdl.png` | WSDL publicado |
| `SERVIDOR_03_pruebas_mvn_test.png` | Salida real de `mvn test`: 6 pruebas JUnit en verde |
| `SERVIDOR_04_respuesta_iniciarSesion.png` | Respuesta SOAP real de `iniciarSesion` |
| `BD_01_tablas.png`, `BD_02_cuentas.png` | Las 17 tablas y los saldos de `Cuenta`, consultados con el usuario `eureka`. Los saldos incluyen las operaciones de las capturas |
| `CONSOLA_sesion_completa.txt` | Salida real del cliente de consola: login fallido (`AUTH-003`), login, cuentas, movimientos, depósito, retiro rechazado (`CTA-003`) y transferencia. Las entradas se dieron por tubería, por eso no aparecen escritas |
| `CONSOLA_01` … `CONSOLA_07` | Pantallas de esa misma salida dibujadas como imagen de terminal (texto real, sin editar) |
| `WEB_01` … `WEB_08` | Cliente web: login, login incorrecto, cuentas, movimientos, depósito y su comprobante, retiro rechazado (`CTA-003`) y transferencia |
| `MOVIL_01` … `MOVIL_08` | Cliente móvil en el emulador de Android: las mismas pantallas que el cliente web |
| `ESC_01` … `ESC_08` | Cliente de escritorio (Swing + FlatLaf): las mismas pantallas que el cliente web. Se tomaron después de las anteriores, por eso los saldos ya incluyen esas operaciones |
