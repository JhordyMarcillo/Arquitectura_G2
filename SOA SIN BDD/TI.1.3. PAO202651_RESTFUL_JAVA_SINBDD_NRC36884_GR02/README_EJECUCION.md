# TI.1.3 RESTful Java – Conversión de unidades (GR02)

Credenciales: `MONSTER` / `MONSTER9` (se validan solo en el servidor).

## 1. Servidor (Payara, puerto 8080)

```powershell
.\04. SERVIDOR\iniciar_servidor.ps1
```

Compila y despliega el servidor REST **y** el cliente web en Payara (`Documents\payara7` por defecto; otra ruta con `-Payara <ruta>`).

- API: `http://<IP>:8080/CONUNI_RESTFUL_JAVA_GR02/api/conversion/info`
- **Como Administrador** abre el puerto 8080 en el firewall para otros equipos y celulares.

## 2. Clientes – una sola variable cada uno

Para conectarse desde otra red, cambia **únicamente** esa variable por la IP del equipo del servidor.

| Cliente | Dónde | Variable |
|---|---|---|
| Consola (`04. CLICON`) | `src/main/resources/config.properties` | `api.base.url` |
| Escritorio (`04. CLIESC`) | `src/main/resources/config.properties` | `api.base.url` |
| Web (`04. CLIWEB`) | `src/main/resources/config.properties` | `api.base.url` |
| Móvil (`04. CLIMOV`) | `app/src/main/java/ec/edu/monster/api/RetrofitClient.kt` | `SERVER_IP` |

Formato de `api.base.url`: `http://192.168.0.90:8080/CONUNI_RESTFUL_JAVA_GR02/api` (en el emulador Android `SERVER_IP = "10.0.2.2"`).

## 3. Ejecutar clientes

- Consola: `mvn -q package` y luego `java -jar target/CONUNI_RESTFUL_JAVA_CLICON_G09-1.0-SNAPSHOT.jar`
- Escritorio: `mvn -q compile exec:java`
- Web: la despliega el script del servidor → `http://localhost:8080/CONUNI_RESTFUL_JAVA_CLIWEB_G02`
- Móvil: abrir en Android Studio y ejecutar (celular en la misma red Wi-Fi que el servidor).
