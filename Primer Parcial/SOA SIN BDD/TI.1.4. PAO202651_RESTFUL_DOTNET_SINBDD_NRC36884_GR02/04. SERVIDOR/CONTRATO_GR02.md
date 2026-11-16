# Contrato REST — WS_ConUnid_DOTNET_RESTFUL_GR02

> **Única fuente de verdad** para los cuatro clientes (`04. CLICON`, `04. CLIESC`, `04. CLIWEB`, `04. CLIMOV`).
> Sale del código de `04. SERVIDOR/WS_ConUnid_DOTNET_RESTFUL_GR02` y cada respuesta de este documento se obtuvo
> con el servidor en ejecución (perfil `http`) el 2026-10-25. Si el código cambia, se actualiza este archivo.

---

## 1. URL base y puerto

| Dato | Valor | De dónde sale |
|---|---|---|
| Perfil para clientes | `http` → `http://0.0.0.0:5014` (escucha en todas las interfaces) | `Properties/launchSettings.json` |
| Perfil `https` | `https://localhost:7102;http://localhost:5014` (solo la misma PC) | `Properties/launchSettings.json` |
| Prefijo de la API | `api/conversor` | `[Route("api/conversor")]` en `ControladorConversorUnidades.cs` |
| URL base, red compartida | `http://192.168.137.1:5014/api/conversor` | IP del equipo servidor en la red del hotspot |
| URL base, misma PC | `http://localhost:5014/api/conversor` | — |
| Emulador Android | `http://10.0.2.2:5014/api/conversor` | `10.0.2.2` = localhost del PC desde el emulador |
| HTTPS | Desactivado (`UseHttpsRedirection` comentado). Usar `http://` | `Program.cs` |
| Raíz `/` | `404` sin cuerpo (no sirve para verificar conexión) | Verificado |

Arranque: `dotnet run --launch-profile http` dentro de `WS_ConUnid_DOTNET_RESTFUL_GR02/WS_ConUnid_DOTNET_RESTFUL_GR02`.

---

## 2. Reglas generales

- Todas las respuestas son JSON en **camelCase** y empiezan con `estado`, `codigo`, `mensaje` (clase `Respuesta`).
- Éxito: `estado:"OK"`, `codigo:"200"` (texto, no número), HTTP 200.
- Error: `estado:"ERROR"`, `codigo` de la tabla 4 y `mensaje`. El cuerpo JSON viene también en 400, 401 y 500.
- Token: `Authorization: Bearer <token>` en todas las rutas **excepto** `POST /login`. El prefijo `Bearer ` no distingue mayúsculas.
- El token es un GUID guardado en memoria del servidor; dura **30 minutos** (`AdministradorTokens.MINUTOS_EXPIRACION`). Reiniciar el servidor invalida todos los tokens (→ `AUTH-001`).
- No hay endpoint de logout: cerrar sesión es borrar el token en el cliente.
- Categorías y unidades se aceptan en mayúsculas o minúsculas y con espacios alrededor (`Trim().ToUpperInvariant()`); el servidor responde siempre en MAYÚSCULAS.
- Body de las peticiones: `Content-Type: application/json`. Nombres de campos sin ñ: `contrasenia`.

---

## 3. Rutas

| CU | Método | Ruta (sobre la URL base) | Token |
|---|---|---|---|
| CU-01 Autenticarse | `POST` | `/login` | No |
| CU-02 Consultar catálogo | `GET` | `/categorias` | Sí |
| CU-03 Consultar unidades | `GET` | `/categorias/{categoria}/unidades` | Sí |
| CU-04 Realizar conversión | `POST` | `/convertir` | Sí |

### 3.1 `POST /login` (CU-01)

Petición:
```json
{"usuario":"MONSTER","contrasenia":"Monster9"}
```

Respuesta 200:
```json
{"estado":"OK","codigo":"200","mensaje":"Autenticacion realizada correctamente","token":"352b40df-6822-4122-8124-ace01f671fea","expiracion":"2026-10-25T20:50:31.060353-05:00"}
```

| Caso | HTTP | Cuerpo |
|---|---|---|
| Credenciales incorrectas | 401 | `{"estado":"ERROR","codigo":"AUTH-003","mensaje":"Credenciales incorrectas"}` |
| Usuario o contraseña vacíos | 400 | `{"estado":"ERROR","codigo":"DATA-001","mensaje":"El usuario y la contrasenia son obligatorios"}` |

Credenciales válidas: `MONSTER` / `Monster9` (distingue mayúsculas; `AdministradorCredenciales.cs`). `expiracion` es fecha ISO 8601 con zona horaria local del servidor.

### 3.2 `GET /categorias` (CU-02)

Respuesta 200:
```json
{"estado":"OK","codigo":"200","mensaje":"Categorias consultadas correctamente","categorias":["LONGITUD","MASA","TEMPERATURA"]}
```

### 3.3 `GET /categorias/{categoria}/unidades` (CU-03)

Respuesta 200 (`/categorias/MASA/unidades`):
```json
{"estado":"OK","codigo":"200","mensaje":"Unidades consultadas correctamente","categoria":"MASA","unidades":["MILIGRAMO","GRAMO","KILOGRAMO","LIBRA"]}
```

| Caso | HTTP | Cuerpo |
|---|---|---|
| Categoría inexistente (`XYZ`) | 400 | `{"estado":"ERROR","codigo":"DATA-001","mensaje":"Categoria inexistente: XYZ"}` |

### 3.4 `POST /convertir` (CU-04)

Petición (`valor` es número JSON, no texto):
```json
{"categoria":"LONGITUD","unidadOrigen":"KILOMETRO","unidadDestino":"METRO","valor":5}
```

Respuesta 200:
```json
{"estado":"OK","codigo":"200","mensaje":"Conversion realizada correctamente","resultado":5000,"unidadOrigen":"KILOMETRO","unidadDestino":"METRO"}
```

Otros resultados verificados: `25 CELSIUS → FAHRENHEIT = 77`, `1 KILOGRAMO → LIBRA = 2.2046226218487757` (sin redondear: el cliente decide cuántos decimales mostrar).

Los errores de negocio de esta ruta traen además `resultado:null`, `unidadOrigen` y `unidadDestino` tal como se enviaron:

| Caso | HTTP | Cuerpo |
|---|---|---|
| Unidad inexistente | 400 | `{"estado":"ERROR","codigo":"UNIT-001","mensaje":"Unidad de origen no soportada: XYZ","resultado":null,"unidadOrigen":"XYZ","unidadDestino":"METRO"}` |
| Unidad de otra categoría | 400 | `{"estado":"ERROR","codigo":"DATA-001","mensaje":"La unidad de destino CELSIUS no pertenece a la categoria LONGITUD","resultado":null,"unidadOrigen":"METRO","unidadDestino":"CELSIUS"}` |
| Falta `valor` | 400 | `{"estado":"ERROR","codigo":"DATA-001","mensaje":"El valor es obligatorio y debe ser numerico","resultado":null,"unidadOrigen":"METRO","unidadDestino":"KILOMETRO"}` |
| Categoría inexistente | 400 | `{"estado":"ERROR","codigo":"DATA-001","mensaje":"Categoria inexistente: XYZ","resultado":null,...}` |
| Falta categoría / unidad | 400 | `DATA-001` con `La categoria es obligatoria`, `La unidad de origen es obligatoria` o `La unidad de destino es obligatoria` |
| `valor` no numérico (`"ABC"`) o JSON mal formado | 400 | `{"estado":"ERROR","codigo":"DATA-001","mensaje":"Datos inconsistentes: la solicitud no tiene el formato esperado"}` (sin `resultado`) |

---

## 4. Códigos de error

| HTTP | `codigo` | CU | `mensaje` exacto | Origen |
|---|---|---|---|---|
| 401 | `AUTH-001` | CU-08 | `Token invalido o sesion expirada` | Sin header, token mal formado, desconocido o servidor reiniciado |
| 401 | `AUTH-002` | CU-09 | `Sesion expirada, vuelva a autenticarse` | Token con más de 30 min |
| 401 | `AUTH-003` | CU-01 | `Credenciales incorrectas` | Login fallido |
| 400 | `UNIT-001` | CU-10 | `Unidad de origen no soportada: X` / `Unidad de destino no soportada: X` | Unidad fuera del catálogo |
| 400 | `DATA-001` | CU-11 | Ver tablas 3.1, 3.3 y 3.4 | Datos faltantes, incompatibles o mal formados |
| 500 | `SRV-001` | — | `Error interno del servidor` | Excepción no controlada (`ManejadorErrores`) |

Los 401 se generan en `ManejadorAutenticacion` (filtro) antes de llegar al controlador; su cuerpo es solo `estado`, `codigo`, `mensaje`.

`AUTH-002` no se verificó en vivo (requiere esperar 30 min); el código y el texto salen de `ManejadorAutenticacion.cs`.

---

## 5. Catálogo de unidades

| Categoría | Unidades (en este orden) |
|---|---|
| `LONGITUD` | `MILIMETRO`, `CENTIMETRO`, `METRO`, `KILOMETRO`, `MILLA`, `PIE` |
| `MASA` | `MILIGRAMO`, `GRAMO`, `KILOGRAMO`, `LIBRA` |
| `TEMPERATURA` | `CELSIUS`, `FAHRENHEIT`, `KELVIN` |

Sale de los `enum` de `ec.edu.monster.modelos.utilidades.enums`. No hay toneladas, onzas, pulgadas ni yardas.

---

## 6. CORS (afecta al cliente web)

| Dato | Valor (`ConfiguracionCors.cs`) |
|---|---|
| Orígenes permitidos | `http://localhost:4200`, `http://localhost:5173`, `http://localhost:3000`, `http://localhost:5141` |
| Métodos | `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS` |
| Headers | `Origin`, `Content-Type`, `Accept`, `Authorization`, `X-Requested-With` |
| Preflight | `Access-Control-Max-Age: 3600` |

Verificado: un preflight desde `http://localhost:5173` recibe `Access-Control-Allow-Origin`; desde `http://localhost:8080` no, y el navegador bloquea la petición. Un cliente web debe servirse en uno de esos orígenes (o hay que agregar el suyo en el servidor). Consola, escritorio y móvil no pasan por CORS.

---

## 7. Cómo verificar conexión desde un cliente

`GET /categorias` sin token → `401` con JSON `AUTH-001`. Cualquier respuesta HTTP significa que el servidor está vivo; solo un error de red indica que está apagado o inalcanzable.

Peticiones de prueba listas: `WS_ConUnid_DOTNET_RESTFUL_GR02/ec.edu.monster.pruebas/ConUnid.http`.
