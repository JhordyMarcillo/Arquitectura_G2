# BanQuito + TelefQuito — Examen Complexivo (GR02)

Dos aplicaciones web SOAP independientes (Java, Payara) + MySQL:

| Carpeta | Contenido |
|---|---|
| `01.SERVIDOR_BANQUITO_GR02/BANQUITO_SOAP_JAVA` | Core BanQuito: WSSujetoCredito, WSMontoMaximo, WSGenerarTabla (contexto `/BANQUITO_SOAP_JAVA`) |
| `02.TELFQUITO_GR02/01.SERVIDOR` | Comercializadora: WSLogin, WSTelefonos, WSCompra (contexto `/TELFQUITO_SOAP_JAVA`) |
| `02.TELFQUITO_GR02/02.CLICON, 02.CLIESC, 02.CLIWEB, 02.CLIMOV` | Clientes consola, escritorio, web (Node, :3000) y móvil |
| `02.TELFQUITO_GR02/03.BDD` | `01_Core_BanQuito.sql` (esquema `core`) y `02_Comercializadora.sql` (esquema `comercializadora`) |
| `02.TELFQUITO_GR02/04.DOCUMENTACIÓN` | Casos de uso, descripción del CU, MER, arquitectura (HTML/PDF) |

Ejecución: cargar los dos `.sql` (el día de la defensa, para refrescar fechas), desplegar los dos WAR en Payara
(`BANQUITO` primero), usuario `Monster` / contraseña `Monster9`. Contraseña de MySQL por variable `DB_PASSWORD` (vacía por defecto).
Cédulas de prueba: 0102030405, 0203040506, 0304050607, 0405060708.

## Menú para levantar todo (`../MENU_CONJUNTA.bat`)

Doble clic en `Conjunta Ier Parcial/MENU_CONJUNTA.bat`:

1. **Servidor**: compila y despliega BanQuito y luego TelefQuito en Payara (necesita MySQL encendido) y al final
   muestra la **IP de este equipo**, que es la que deben escribir los clientes de otras máquinas.
2–6. **Clientes** (consola, escritorio, web, móvil o todos): antes de abrirlos piden la **IP del servidor**
   (`localhost` si está en el mismo equipo; Enter repite la última usada) y comprueban que responda.
7. **Servidor y todos los clientes** en el mismo equipo.
8. **Cargar la base de datos** (los dos `.sql` de `03.BDD`; si root tiene clave: `set DB_PASSWORD=...`).

También se puede usar directo: `MENU_CONJUNTA.bat consola 192.168.1.20` (acciones: `servidor consola escritorio web
movil clientes todo bdd`). La IP llega a cada cliente así: consola/escritorio `-Dservidor.ip=...`, web la variable
`SERVIDOR_IP`, móvil `gradlew installDebug -PservidorIp=...` (con `localhost` el menú usa la IP del PC, o `10.0.2.2` en
el emulador). Payara se busca en `PAYARA_HOME` o en las carpetas habituales.
