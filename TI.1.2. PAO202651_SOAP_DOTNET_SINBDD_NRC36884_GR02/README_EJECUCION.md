# TI.1.2 SOAP .NET – Conversión de unidades (GR02)

Credenciales: `MONSTER` / `MONSTER9` (se validan solo en el servidor).

## 1. Servidor (WCF, puerto 8085)

```powershell
.\TI.1.2. PAO202650_SOAP_DOTNET_SINBDD_NRC30742_GR5\04. SERVIDOR\CONUNI_SOAP_DOTNET_GR02\iniciar_servidor.ps1
```

- **Como Administrador**: abre el puerto 8085 (reserva de URL + firewall) y queda accesible desde otros equipos y celulares.
- Sin Administrador: solo `http://localhost:8085/Service1.svc`.
- WSDL: `http://<IP>:8085/Service1.svc?wsdl`

## 2. Clientes – una sola variable cada uno

Para conectarse desde otra red, cambia **únicamente** esa variable por la IP del equipo del servidor.

| Cliente | Dónde | Variable |
|---|---|---|
| Consola (`04. CLICON`) | `CONUNI_SOAP_DOTNET_CLICON_G02/App.config` | `UrlServicioSOAP` |
| Escritorio (`04. CLIESC`) | `CONUNI_SOAP_DOTNET_CLIESC_G02/App.config` | `UrlServicioSOAP` |
| Web (`04. CLIWEB`) | `CONUNI_SOAP_DOTNET_CLIWEB_G02/appsettings.json` | `SoapService:Endpoint` |
| Móvil (`04. CLIMOV`) | `app/src/main/java/ec/edu/monster/servidor/ConfiguracionServidor.kt` | `SERVER_IP` |

Ejemplo: `http://192.168.0.90:8085/Service1.svc` (en el emulador Android usar `10.0.2.2`).

## 3. Ejecutar clientes

- Consola / Escritorio: compilar con Visual Studio o MSBuild; la salida va a `C:\ConUni\SOAP_DOTNET\<CLICON|CLIESC>\Debug\`
  (ruta corta a propósito: la ruta del repositorio supera los 260 caracteres y .NET Framework no carga el `.exe.config` desde ahí).
- Web: `dotnet run --launch-profile http` → `http://localhost:5015` (escucha en `0.0.0.0`, accesible por IP desde la red).
- Móvil: abrir en Android Studio y ejecutar (celular real en la misma red Wi-Fi que el servidor).
