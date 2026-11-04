# TI.1.1 SOAP Java con BDD: EurekaBank

| Dato | Valor |
|---|---|
| Período / NRC / Grupo | PAO202651 / 36884 / GR02 |
| Tecnología | SOAP (JAX-WS), Java, Payara 7 |
| Base de datos | MySQL, local (ver `03. BDD`) |
| Responsable | David Cepeda |
| Estado | Completo: servidor, 4 clientes, pruebas SoapUI, capturas, MER, diagramas UML, ERS, ECUD, manual y presentación de EurekaBank |

## Contenido
| Carpeta | Qué contiene |
|---|---|
| `01. UML` | ERS, ECUD y diagramas UML |
| `02. MER` | Modelo entidad-relación: conceptual, lógico y físico |
| `03. BDD` | Scripts de la base EurekaBank (MySQL 8/9) y contenedor Docker opcional |
| `04. SERVIDOR` | Servicio web `WS_EurekaBank_JAVA_SOAP_GR02` |
| `04. CLICON` | Cliente de consola |
| `04. CLIESC` | Cliente de escritorio (Swing) |
| `04. CLIMOV` | Cliente móvil (Android, SOAP escrito a mano) |
| `04. CLIWEB` | Cliente web (JSP + Servlets) |
| `05. DOCUMENTACION` | Manual, presentación, proyecto SoapUI y capturas |
| `presentacion/` | Script que usan los `.bat` de esta carpeta |

## Puesta en marcha
1. Crear la base: `Crea_BD.sql` y luego `Carga_Datos.sql` (`03. BDD`).
2. Desplegar el servidor en Payara: `http://localhost:8080/WS_EurekaBank_JAVA_SOAP_GR02/WSEurekaBank` (WSDL en `?wsdl`).
3. Ejecutar cualquier cliente e ingresar con **MONSTER / Monster9**.

## Lanzadores para la presentación
| Archivo | Qué hace |
|---|---|
| `LEVANTAR_TI_1.1.bat` | Enciende MySQL (si está apagado) y Payara, compila y despliega el servidor y el cliente web, abre la consola y el escritorio ya apuntando a la IP de la PC, y genera el APK; si hay un celular o emulador conectado por USB, lo instala y lo abre con la URL puesta |
| `DETENER_TI_1.1.bat` | Cierra la consola y el escritorio abiertos por el lanzador (Payara y MySQL siguen encendidos) |
| `RESTAURAR_BD_TI_1.1.bat` | Vuelve la base a los datos iniciales (`Crea_BD.sql` + `Carga_Datos.sql`); pide la clave de root de MySQL |
| `CONFIGURACION_PRESENTACION.txt` | Único archivo a editar: IP (`AUTO` o fija), celular por `RED` o `USB` y carpeta de Payara |

`LEVANTAR_TI_1.1.bat` pide permisos de administrador (para el firewall del puerto 8080 y el servicio de MySQL). Necesita Payara 7, Maven (el de NetBeans sirve), Java 17 o superior y, para el móvil, el SDK de Android (`adb`). El APK queda también en `presentacion/APKs/` para instalarlo a mano.

El contrato que cumple el servicio está en `../CONTRATO_EUREKABANK_GR02.md`, sección 4.
