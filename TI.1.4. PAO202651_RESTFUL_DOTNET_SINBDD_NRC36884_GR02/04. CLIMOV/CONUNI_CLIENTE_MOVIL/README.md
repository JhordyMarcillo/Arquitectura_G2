# Cliente Móvil Android — Conversor de Unidades (GR02)

App Android (Java) del grupo GR02 que consume la API REST .NET `WS_ConUnid_DOTNET_RESTFUL_GR02` (`04. SERVIDOR`).
La app **no** calcula conversiones: autentica contra el servidor, pide el catálogo y envía cada conversión.

## Credenciales
- **Usuario:** MONSTER
- **Contraseña:** Monster9 (sin espacio; las valida el servidor)

## Servidor
Arranque: `dotnet run --launch-profile http` en `04. SERVIDOR/WS_ConUnid_DOTNET_RESTFUL_GR02/WS_ConUnid_DOTNET_RESTFUL_GR02` (escucha en `0.0.0.0:5014`).

`BASE_URL` en `app/src/main/java/ec/edu/monster/controlador/ApiService.java`:

| Dónde corre la app | `BASE_URL` |
|---|---|
| Celular en la red compartida del PC (hotspot de Windows) | `http://192.168.137.1:5014/api/conversor` (por defecto) |
| Emulador de Android Studio | `http://10.0.2.2:5014/api/conversor` |

Si usas otra IP, agrégala también a `app/src/main/res/xml/network_security_config.xml`.

## Formato de la API REST (contrato completo en `04. SERVIDOR/CONTRATO_GR02.md`)
| CU | Método y ruta | Token | Cuerpo |
|---|---|---|---|
| CU-01 | `POST /login` | No | `{"usuario":"MONSTER","contrasenia":"Monster9"}` |
| CU-02 | `GET /categorias` | Sí | — |
| CU-03 | `GET /categorias/{categoria}/unidades` | Sí | — |
| CU-04 | `POST /convertir` | Sí | `{"categoria":"TEMPERATURA","unidadOrigen":"CELSIUS","unidadDestino":"FAHRENHEIT","valor":25}` |

Header: `Authorization: Bearer <token>` (el token dura 30 minutos).
Toda respuesta trae `estado`, `codigo` y `mensaje`; la conversión además `resultado`, `unidadOrigen` y `unidadDestino`.
Errores: `AUTH-001`/`AUTH-002` (vuelve al login), `AUTH-003`, `UNIT-001`, `DATA-001`, `SRV-001`.

## Catálogo (lo envía el servidor)
| Categoría | Unidades |
|---|---|
| LONGITUD | MILIMETRO, CENTIMETRO, METRO, KILOMETRO, MILLA, PIE |
| MASA | MILIGRAMO, GRAMO, KILOGRAMO, LIBRA |
| TEMPERATURA | CELSIUS, FAHRENHEIT, KELVIN |

## Estructura
```
app/src/main/java/ec/edu/monster/
├── controlador/ApiService.java             # único punto que hace HTTP
├── modelo/ConversionRequest.java, ConversionResponse.java, SesionUsuario.java
└── vista/LoginActivity.java, MainActivity.java, TransformacionesActivity.java
```

## Comprobar el servidor desde el PC
`.\probar-servidor.ps1` (o `.\probar-servidor.ps1 localhost`).

## Más información
- `INICIO_RAPIDO.md`: pasos para ejecutar.
- `CONFIGURACION_SERVIDOR.md`: red y servidor.
- `ANDROID_STUDIO.md`, `GENERAR_APK.md`: Android Studio y APK.
- `../GUIA_CLIENTE_MOVIL.md`: guía completa del cliente.
