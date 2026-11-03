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
(`BANQUITO` primero), usuario `admin`/`admin`. Contraseña de MySQL por variable `DB_PASSWORD` (vacía por defecto).
Cédulas de prueba: 0102030405, 0203040506, 0304050607, 0405060708.
