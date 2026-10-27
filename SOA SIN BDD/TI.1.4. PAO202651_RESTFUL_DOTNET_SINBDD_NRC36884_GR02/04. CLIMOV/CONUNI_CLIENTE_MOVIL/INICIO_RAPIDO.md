# 🚀 Guía Rápida de Inicio (GR02)

## 1. Inicia el servidor .NET
```powershell
cd "04. SERVIDOR\WS_ConUnid_DOTNET_RESTFUL_GR02\WS_ConUnid_DOTNET_RESTFUL_GR02"
dotnet run --launch-profile http
```
Escucha en `http://0.0.0.0:5014`.

## 2. Comprueba el servidor desde el PC
```powershell
.\probar-servidor.ps1 localhost
```
Debe mostrar el login `200`, las categorías, `Resultado: 77` y los errores `AUTH-001` y `UNIT-001`.

## 3. Ajusta `BASE_URL` en `ApiService.java`
- Celular conectado al hotspot del PC: `http://192.168.137.1:5014/api/conversor` (por defecto).
- Emulador: `http://10.0.2.2:5014/api/conversor`.

## 4. Ejecuta la app
Abre `CONUNI_CLIENTE_MOVIL` en Android Studio, sincroniza Gradle y pulsa Run.

## 5. Inicia sesión
- Usuario: `MONSTER`
- Contraseña: `Monster9`

## Problemas comunes
| Mensaje | Causa |
|---|---|
| `No se puede conectar al servidor` | Servidor apagado, IP incorrecta o el celular no está en la red del PC |
| `Credenciales incorrectas` | Contraseña distinta de `Monster9` |
| `Token invalido o sesion expirada` | El servidor se reinició o pasaron 30 min: vuelve a iniciar sesión |
