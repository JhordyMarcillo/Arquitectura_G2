# Configuración del Servidor .NET (GR02)

El servidor ya existe: `04. SERVIDOR/WS_ConUnid_DOTNET_RESTFUL_GR02`. **No** hay que escribir ningún controlador; su contrato está en `04. SERVIDOR/CONTRATO_GR02.md`.

## Arranque
```powershell
cd "04. SERVIDOR\WS_ConUnid_DOTNET_RESTFUL_GR02\WS_ConUnid_DOTNET_RESTFUL_GR02"
dotnet run --launch-profile http
```
El perfil `http` escucha en `http://0.0.0.0:5014` (todas las interfaces), así que sirve para el celular y para el emulador.

## Red
| Cliente | IP del servidor | `BASE_URL` |
|---|---|---|
| Celular en el hotspot de Windows | `192.168.137.1` (IP que Windows asigna al PC al compartir la conexión) | `http://192.168.137.1:5014/api/conversor` |
| Emulador | `10.0.2.2` (= `127.0.0.1` del PC) | `http://10.0.2.2:5014/api/conversor` |
| Otra red | IP del PC (`ipconfig`) | `http://<ip>:5014/api/conversor` + agregar la IP a `network_security_config.xml` |

Si el celular no conecta, permite el puerto 5014 en el Firewall de Windows.

## Prueba rápida
```powershell
.\probar-servidor.ps1 localhost
```
O a mano:
```powershell
$login = Invoke-RestMethod -Uri "http://localhost:5014/api/conversor/login" -Method Post -ContentType "application/json" -Body '{"usuario":"MONSTER","contrasenia":"Monster9"}'
Invoke-RestMethod -Uri "http://localhost:5014/api/conversor/convertir" -Method Post -ContentType "application/json" -Headers @{ Authorization = "Bearer $($login.token)" } -Body '{"categoria":"TEMPERATURA","unidadOrigen":"CELSIUS","unidadDestino":"FAHRENHEIT","valor":25}'
```
Respuesta esperada: `estado = OK`, `resultado = 77`.
