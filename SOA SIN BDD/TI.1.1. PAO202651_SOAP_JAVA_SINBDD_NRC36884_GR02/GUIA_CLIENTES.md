# Guía para ejecutar los clientes – TI.1.1 SOAP Java (sin BDD)

Todos los clientes (Consola, Escritorio, Web y Móvil) usan el **mismo servidor SOAP Java** (`04. SERVIDOR`).

- **Servidor:** `http://<IP-DEL-SERVIDOR>:8080/WS_ConUni_Java_GR02/WSConversorUnidades`
- **Usuario / contraseña:** `MONSTER9` / `MONSTER9`
- **Sesión:** el token dura 5 minutos; luego hay que iniciar sesión otra vez.
- **Unidades:**
  - Longitud: MILIMETRO, CENTIMETRO, METRO, KILOMETRO, MILLA, PIE
  - Masa: MILIGRAMO, GRAMO, KILOGRAMO, LIBRA
  - Temperatura: CELSIUS, FAHRENHEIT, KELVIN

---

## Paso 0 – Requisitos

| Para | Necesitas |
|---|---|
| Servidor | JDK 17+, Maven, Payara (el proyecto usa Payara 7) |
| Cliente Consola | JDK 17+ y Maven |
| Clientes Web / Escritorio / Móvil | Visual Studio 2022+ con **.NET 10 SDK** y el workload **.NET MAUI** (solo para móvil/escritorio MAUI) |

---

## Paso 1 – Levantar el servidor (lo hace UNA persona del grupo)

1. Abrir una terminal en `04. SERVIDOR` y compilar:
   ```bash
   mvn clean package -DskipTests
   ```
2. Iniciar Payara y desplegar el WAR (cambia la ruta de Payara por la tuya):
   ```bash
   asadmin start-domain
   asadmin deploy --force target/WS_ConUni_Java_GR02-1.0-SNAPSHOT.war
   ```
   (También se puede desplegar desde NetBeans con "Run").
3. **Verificar:** abrir en el navegador
   `http://localhost:8080/WS_ConUni_Java_GR02/WSConversorUnidades?wsdl`
   Debe mostrar un XML (el WSDL).
4. **Para que los demás se conecten:**
   - Averiguar la IP de esa PC: `ipconfig` → "Dirección IPv4" (ej. `192.168.0.90`).
   - Todos deben estar en **la misma red Wi-Fi/LAN**.
   - Permitir el puerto 8080 en el firewall de Windows (PowerShell como administrador):
     ```powershell
     New-NetFirewallRule -DisplayName "Payara 8080" -Direction Inbound -Protocol TCP -LocalPort 8080 -Action Allow
     ```
   - Los compañeros prueban desde su navegador:
     `http://<IP-DEL-SERVIDOR>:8080/WS_ConUni_Java_GR02/WSConversorUnidades?wsdl`

> Quien corre el servidor usa `localhost`; los demás usan la IP de esa PC.

---

## Paso 2 – Configurar la IP en los clientes

### Clientes C# (Escritorio, Web y Móvil)
Los tres comparten un solo archivo. Editar:

`04. CLIESC/SistemaConversorUI/SistemaConversorUI.Shared/Modelos/ConstantesRed.cs`

```csharp
public const string IP_WEB_SERVICES = "localhost";   // o la IP del servidor, ej. "192.168.0.90"
```

### Cliente Consola (Java)
No hay que editar código; se pasa la URL base al ejecutar (ver Paso 3A).

---

## Paso 3 – Ejecutar cada cliente

### A) Cliente Consola (`04. CLICON`)

**Opción 1 – NetBeans:** abrir el proyecto `04. CLICON` y pulsar *Run*.

**Opción 2 – Terminal** (desde `04. CLICON`):
```bash
mvn compile exec:java -Dexec.mainClass=ec.edu.monster.Main
```
Para apuntar a otra PC:
```bash
mvn compile exec:java -Dexec.mainClass=ec.edu.monster.Main -Dws.base.url=http://192.168.0.90:8080/WS_ConUni_Java_GR02
```
(También sirve la variable de entorno `WS_BASE_URL`.)

**Uso:** al iniciar debe decir `Servidor SOAP detectado: ...` → escribir usuario y contraseña `MONSTER9` → elegir 1/2/3 (longitud/masa/temperatura) → ingresar valor y elegir las unidades por número → `0` para salir.

### B) Cliente Web (`04. CLIWEB`)

1. Abrir `04. CLIESC/SistemaConversorUI/SistemaConversorUI.slnx` en Visual Studio.
2. Poner como proyecto de inicio **SistemaConversorUI.Web** y ejecutar (F5).
   O por terminal, desde `04. CLIWEB/SistemaConversorUI.Web/SistemaConversorUI.Web`:
   ```bash
   dotnet run --urls http://localhost:5240
   ```
3. Abrir `http://localhost:5240`.
4. Dejar el entorno **JavaSoap**, escribir `MONSTER9` en usuario y contraseña (¡el formulario trae `Monster` por defecto, no sirve!) → *Iniciar sesión*.
5. Elegir tipo, valor y unidades → *Convertir*. El resultado y el historial aparecen a la derecha.

### C) Cliente Escritorio / Móvil (`04. CLIMOV`, MAUI)

1. Abrir el mismo `SistemaConversorUI.slnx` en Visual Studio.
2. Poner como proyecto de inicio **SistemaConversorUI.Maui**.
3. Elegir el destino de ejecución:
   - **Windows Machine** → corre como app de escritorio (`IP_WEB_SERVICES` puede ser `localhost` si el servidor está en la misma PC).
   - **Android Emulator / dispositivo físico** → `IP_WEB_SERVICES` **debe ser la IP del servidor** (no `localhost`; en el celular `localhost` es el propio celular). El celular debe estar en la misma Wi-Fi.
4. Ejecutar (F5), entorno **JavaSoap**, login `MONSTER9` / `MONSTER9` y convertir.

---

## Problemas frecuentes

| Síntoma | Causa / solución |
|---|---|
| Consola: "No se encontro el WSDL en ninguna URL candidata" | El servidor no está desplegado o la URL es distinta. Revisar el Paso 1.3 y usar `-Dws.base.url=...` |
| Web/MAUI: "No se puede establecer una conexión" | IP incorrecta en `ConstantesRed.cs`, firewall bloqueando el 8080, o distinta red |
| "Credenciales incorrectas" | Usuario y contraseña deben ser exactamente `MONSTER9` (mayúsculas) |
| "Token invalido o sesion expirada" | El token dura 5 min: cerrar sesión e iniciar de nuevo |
| "Unidad ... no soportada" | Usar solo las unidades listadas arriba (ya no existen YARDA, ONZA, TONELADA, RANKINE) |
| Web: error de rutas al abrir la solución | Verificar que las carpetas se llamen `04. CLIESC`, `04. CLIWEB`, `04. CLIMOV` (con espacio) |
| Pantalla "Cambiar contraseña" da error | Es normal: este servidor no tiene esa operación |
