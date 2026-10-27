# Guía del cliente móvil (Android, Java) — Conversión de Unidades REST

> Cliente: `04. CLIMOV/CONUNI_CLIENTE_MOVIL` · Lenguaje: Java 8 · Android (minSdk 24, targetSdk 34, AGP 8.1.1, Gradle 8.5) · Servidor: `04. SERVIDOR` (ASP.NET Core, `WS_ConUnid_DOTNET_RESTFUL_GR02`)
>
> Esta guía se lee junto con:
> - `04. SERVIDOR/CONTRATO_GR02.md` (el contrato REST que este cliente consume; sale del código del servidor y manda sobre esta guía).
> - `04. SERVIDOR/TALLER CASOS DE USO CONVERSION UNIDADES.docx` (los casos de uso que el cliente debe cumplir).

---

## 0. Instrucciones para quien aplique esta guía (persona o IA)

1. **Lee primero la sección 2** (estado actual). La guía **modifica** un proyecto Android Studio existente (layouts, drawables, Gradle, manifest): de los layouts solo trae los cambios. En `04. CLIMOV` de este proyecto ese proyecto base **todavía no está**; hay que copiarlo a `04. CLIMOV/CONUNI_CLIENTE_MOVIL` antes de aplicar la sección 5.
2. **Conserva** el paquete `ec.edu.monster` con sus 3 subpaquetes `controlador`, `modelo`, `vista` (la misma división MVC que el servidor), los layouts, colores, drawables y textos de la sección 2.3.
3. Este cliente ya pide la conversión al servidor (no calcula localmente), pero con un contrato que **no existe**: `GET` con parámetros en la URL, sin token, sin categoría, y solo lee la respuesta si es 200. Hay que pasarlo al contrato de la sección 3.
4. No pongas ñ ni tildes en identificadores de código. Los textos visibles sí pueden llevarlas.
5. Si algo choca con el `.docx`, manda el `.docx` en lo funcional y esta guía en la forma. Si choca con el servidor, **manda el servidor** (`CONTRATO_GR02.md`).

| Fuente | Manda en |
|---|---|
| `.docx` del taller | Qué hace el sistema: CU-01…CU-11, flujo, mensajes |
| `04. SERVIDOR/CONTRATO_GR02.md` | Rutas, JSON, códigos, token |
| Esta guía | Cómo queda **este** cliente: clases, layouts, estilo |

> **Verificación.** El código Java de la sección 5 se compiló originalmente con `javac --release 8` contra stubs de Android, y `ApiService` se ejecutó contra el servidor de la versión anterior de esta guía. Para GR02 se corrigieron la URL (`/api/conversor`) y la ruta de conversión (`/convertir`), y el contrato se comprobó contra `04. SERVIDOR` el 2026-10-25 con `probar-servidor.ps1` (sección 5.9). El código Android **no se recompiló** (la PC no tiene Android SDK). Falta probarlo en Android Studio con un dispositivo (sección 8).

---

## 1. Variables del cliente

| Variable | Valor | Dónde |
|---|---|---|
| `BASE_URL` (red compartida, por defecto) | `http://192.168.137.1:5014/api/conversor` | `controlador/ApiService.java` |
| `BASE_URL` (emulador) | `http://10.0.2.2:5014/api/conversor` | Ídem (comentario ya existente) |
| `TIMEOUT` | 5000 ms | `ApiService` |
| Dominios en texto plano | `10.40.17.85`, `192.168.10.104`, `192.168.137.1`, `localhost`, `10.0.2.2` | `res/xml/network_security_config.xml` (sin cambios) |
| Usuario de prueba | `MONSTER` / `Monster9` | Lo valida **el servidor** |

> **Red.** `192.168.137.1` es la IP que Windows da al PC cuando comparte su conexión ("zona con cobertura inalámbrica móvil"); el celular debe estar conectado a esa red. El perfil `http` del servidor GR02 escucha en `http://0.0.0.0:5014` (todas las interfaces, `launchSettings.json`), así que sirve tanto para el celular como para el emulador (`10.0.2.2` = `127.0.0.1` del PC) sin parámetros extra. Si usas otra IP, agrégala también a `network_security_config.xml`.

---

## 2. Análisis del cliente actual

### 2.1 Archivos

| Archivo | Qué hace hoy | Acción |
|---|---|---|
| `controlador/ApiService.java` | `realizarConversion(request, callback)`: `GET BASE_URL?valor=&unidadOrigen=&unidadDestino=`; lee `resultado`, `mensaje`, `exito` solo si HTTP 200; `ExecutorService` + `Handler` del hilo principal; mensajes claros para timeout, conexión rechazada y host desconocido | **Reescribir** (sección 5.1), conservando el patrón executor/handler, `TIMEOUT`, los comentarios de URLs y los mensajes de red |
| `modelo/ConversionRequest.java` | `tipoConversion`, `unidadOrigen`, `unidadDestino`, `valor` | **Ajustar**: `tipoConversion` → `categoria` (sección 5.2) |
| `modelo/ConversionResponse.java` | `resultado` (double), `mensaje`, `exito` | **Ajustar** a `estado`, `codigo`, `mensaje`, `resultado` (Double), `unidadOrigen`, `unidadDestino` (sección 5.3) |
| — | No existe | **Crear** `modelo/SesionUsuario.java`: guarda el token en memoria (sección 5.4) |
| `vista/LoginActivity.java` | Valida `MONSTER`/`Monster9` **en el cliente** | **Reescribir** validación (sección 5.5) |
| `vista/MainActivity.java` | 3 tarjetas fijas (Temperatura, Masa, Longitud), FAB salir con diálogo, `onBackPressed` pide confirmación | **Ajustar** (sección 5.6): consulta `/categorias`, borra el token al salir |
| `vista/TransformacionesActivity.java` | Un `Spinner` con 3 pares fijos ("Celsius a Fahrenheit"…); traduce cada par a unidades `"Celsius"`, `"Kilogramo"`, `"Tonelada"`… | **Reescribir** (sección 5.7): dos spinners con las unidades del servidor |
| `res/layout/activity_login.xml` | Logo circular, "🎓 MONSTER UNIVERSITY 🎓", tarjeta de login, pista `Contraseña: Monster 9` (con espacio, incorrecta); la línea 153 tiene un `android:alpha="0.7" />` suelto | **Ajustar** (sección 5.8) |
| `res/layout/activity_main.xml` | Barra `#26A69A`, tarjetas con subtítulos "Kilogramos, Gramos, Toneladas", "Kilómetros, Metros, Centímetros" | **Ajustar** subtítulos (sección 5.8) |
| `res/layout/activity_transformaciones.xml` | Tarjeta "Tipo de Conversión" con `spinnerConversion` | **Ajustar**: `spinnerOrigen` + `spinnerDestino` (sección 5.8) |
| `res/drawable/*` | Gradientes `gradient_login` (`#4DD0E1 → #00ACC1`), `fondo_menu` (`#E0F7FA → #80DEEA`), iconos `ic_temperature`, `ic_weight`, `ic_ruler`, `ic_result`, `logo_login.png`, `icono.jpg` | Sin cambios |
| `res/xml/network_security_config.xml` | Texto plano permitido para las IPs del proyecto | Sin cambios |
| `AndroidManifest.xml` | Permiso `INTERNET`, `LoginActivity` como launcher | Sin cambios |
| `app/build.gradle.kts`, `build.gradle.kts`, `settings.gradle.kts` | appcompat, material, constraintlayout, cardview; repositorio `ksoap2` (sobrante de un proyecto SOAP) | Sin cambios obligatorios; el repositorio `ksoap2` puede quitarse |
| `SERVIDOR_DOTNET_CODIGO.cs` | Borrador de controlador .NET con `TipoConversion`, `Exito` | **Eliminar**: contradice el servidor real |
| `probar-servidor.ps1` | Prueba `GET` y `POST` con `tipoConversion` | **Reescribir** (sección 5.9) |
| `README.md`, `INICIO_RAPIDO.md`, `CONFIGURACION_SERVIDOR.md`, `PROBLEMA_IDENTIFICADO.md`, `SOLUCION_ERROR_CONEXION.md`, `RESUMEN_FINAL.md`, `ANDROID_STUDIO.md`, `GENERAR_APK.md` | Describen el contrato viejo, `Monster 9`, rutas de otro equipo (`karly`, `GR09`) | **Actualizar** (sección 8.3) |

### 2.2 Problemas frente al taller

| Requisito del `.docx` | Hoy | Debe quedar |
|---|---|---|
| El cliente no implementa conversión | Cumple (pide al servidor) | Igual |
| CU-01: el servidor autentica y emite token | Credenciales en `LoginActivity` | `POST /login` y guardar el token en `SesionUsuario` |
| CU-02: catálogo | 3 tarjetas fijas | `GET /categorias`; se ocultan las tarjetas que el servidor no envía |
| CU-03: unidades por categoría | 3 pares fijos por categoría; incluye `Tonelada`, que el servidor no tiene | `GET /categorias/{c}/unidades` en dos spinners (origen y destino) |
| CU-04: conversión | `GET` con query, sin `categoria` | `POST BASE_URL/convertir` con `{categoria, unidadOrigen, unidadDestino, valor}` |
| CU-05: token en cada petición | No | `Authorization: Bearer <token>` |
| CU-08/CU-09 | Solo "Servidor no responde correctamente (código: 401)" | Mensaje del servidor y regreso al login |
| CU-10/CU-11 | Igual: el cuerpo del 400 no se lee | `Toast` con `[UNIT-001] ...` / `[DATA-001] ...` |

### 2.3 Diseño a conservar

| Pantalla | Elementos |
|---|---|
| Login | Fondo `@drawable/gradient_login`, logo circular 130 dp, "🎓 MONSTER UNIVERSITY 🎓" `#006064`, "Sistema de Conversión de Unidades" `#00695C`, tarjeta `#F5F5F5`, campos con borde `#4DD0E1`, botón "INGRESAR" |
| Menú | Fondo `@drawable/fondo_menu`, barra `#26A69A` con "👤 MONSTER" y FAB salir, "Conversor de Unidades" `#2C3E50`, 3 tarjetas blancas con icono en círculo (`#FFEBEE`, `#E8F5E9`, `#E3F2FD`) y "›" |
| Conversión | Fondo `gradient_login`, botón atrás circular, título "🔄 Conversión de …", botón "📋 Menú" `#26A69A`, tarjetas de selección y valor, botones "🔄 Convertir" `#26A69A` y "🧹 Limpiar" `#FF9800`, tarjeta "✨ Resultado de la Conversión ✨" `#E8F5E9`, `ProgressBar` `#26A69A` |
| Mensajes | `Toast` para avisos y errores; `AlertDialog` "¿Desea cerrar sesión?" |

---

## 3. Contrato del servidor (resumen para este cliente)

Resumen de `04. SERVIDOR/CONTRATO_GR02.md`; ante cualquier duda manda ese archivo.

| Dato | Valor |
|---|---|
| `BASE_URL` (celular en la red compartida) | `http://192.168.137.1:5014/api/conversor` |
| `BASE_URL` (emulador) | `http://10.0.2.2:5014/api/conversor` |
| Perfil del servidor | `http` (`http://0.0.0.0:5014`, `launchSettings.json`) |
| Prefijo | `api/conversor` (`[Route("api/conversor")]`) |

| CU | Método y ruta | Token | Cuerpo | Respuesta OK |
|---|---|---|---|---|
| CU-01 | `POST /login` | No | `{"usuario":"MONSTER","contrasenia":"Monster9"}` | `{"estado":"OK","codigo":"200","mensaje":"Autenticacion realizada correctamente","token":"<guid>","expiracion":"2026-10-25T20:50:31.060353-05:00"}` |
| CU-02 | `GET /categorias` | Sí | — | `{"estado":"OK","codigo":"200","mensaje":"Categorias consultadas correctamente","categorias":["LONGITUD","MASA","TEMPERATURA"]}` |
| CU-03 | `GET /categorias/{categoria}/unidades` | Sí | — | `{"estado":"OK","codigo":"200","mensaje":"Unidades consultadas correctamente","categoria":"MASA","unidades":["MILIGRAMO","GRAMO","KILOGRAMO","LIBRA"]}` |
| CU-04 | `POST /convertir` | Sí | `{"categoria":"TEMPERATURA","unidadOrigen":"CELSIUS","unidadDestino":"FAHRENHEIT","valor":25}` | `{"estado":"OK","codigo":"200","mensaje":"Conversion realizada correctamente","resultado":77,"unidadOrigen":"CELSIUS","unidadDestino":"FAHRENHEIT"}` |

Header del token: `Authorization: Bearer <token>`. El token dura 30 minutos y vive en memoria del servidor: si el servidor se reinicia, el token deja de valer (`AUTH-001`). No hay endpoint de logout.

| Categoría | Unidades |
|---|---|
| `LONGITUD` | `MILIMETRO`, `CENTIMETRO`, `METRO`, `KILOMETRO`, `MILLA`, `PIE` |
| `MASA` | `MILIGRAMO`, `GRAMO`, `KILOGRAMO`, `LIBRA` |
| `TEMPERATURA` | `CELSIUS`, `FAHRENHEIT`, `KELVIN` |

Las tarjetas del menú pasan `"Temperatura"`, `"Masa"`, `"Longitud"`; `TransformacionesActivity` los convierte a `TEMPERATURA`, `MASA`, `LONGITUD` con `toUpperCase(Locale.ROOT)`. Las unidades se muestran como `Kilometro` y se envían como `KILOMETRO`.

| HTTP | `codigo` | Caso de uso | `mensaje` del servidor | Qué hace este cliente |
|---|---|---|---|---|
| 401 | `AUTH-001` | CU-08 token inválido | `Token invalido o sesion expirada` | `LoginActivity.volverAlLogin(...)` con el mensaje del servidor |
| 401 | `AUTH-002` | CU-09 sesión expirada | `Sesion expirada, vuelva a autenticarse` | Igual |
| 401 | `AUTH-003` | CU-01 credenciales incorrectas | `Credenciales incorrectas` | `Toast` "Credenciales incorrectas" y borra la contraseña |
| 400 | `UNIT-001` | CU-10 unidad no soportada | `Unidad de origen no soportada: TONELADA` | `Toast` `Error: [UNIT-001] ...` y `⚠️ ...` en el resultado |
| 400 | `DATA-001` | CU-11 datos inconsistentes | p. ej. `La unidad de destino CELSIUS no pertenece a la categoria LONGITUD` | Igual |
| 500 | `SRV-001` | Error interno | `Error interno del servidor` | Igual |
| — | `RED` (propio del cliente) | Timeout, conexión rechazada, host desconocido | — | `Toast` con los mensajes de red actuales |

> El servidor envía el JSON de error también en 400 y 401. `HttpURLConnection` lo entrega por `getErrorStream()`, no por `getInputStream()` (que lanza excepción). El `ApiService` nuevo lee el que corresponda.

---

## 4. Estructura objetivo

```
app/src/main/java/ec/edu/monster/
├── controlador/
│   └── ApiService.java            # login, obtenerCategorias, obtenerUnidades, realizarConversion
├── modelo/
│   ├── ConversionRequest.java     # categoria, unidadOrigen, unidadDestino, valor
│   ├── ConversionResponse.java    # estado, codigo, mensaje, resultado, unidadOrigen, unidadDestino
│   └── SesionUsuario.java         # NUEVO: usuario y token en memoria
└── vista/
    ├── LoginActivity.java         # CU-01 + volverAlLogin() para CU-08/CU-09
    ├── MainActivity.java          # CU-02
    └── TransformacionesActivity.java  # CU-03, CU-04, CU-10, CU-11
```

| Clase | Capa | Responsabilidad |
|---|---|---|
| `ApiService` | controlador | Único punto que hace HTTP (en un hilo aparte); pone el token; devuelve `onSuccess(T)` u `onError(codigo, mensaje)` en el hilo principal |
| `SesionUsuario` | modelo | "Almacenar el token temporalmente" (taller): estático, en memoria, se pierde al cerrar la app |
| `ConversionRequest` / `ConversionResponse` | modelo | Datos de CU-04 |
| Activities | vista | Pantallas; nunca calculan conversiones |

---

## 5. Plantillas de código

### 5.1 `controlador/ApiService.java` (completo)

```java
package ec.edu.monster.controlador;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ec.edu.monster.modelo.ConversionRequest;
import ec.edu.monster.modelo.ConversionResponse;
import ec.edu.monster.modelo.SesionUsuario;

public class ApiService {
    // URLs disponibles - puedes cambiar según tu ubicación
    // Para emulador (servidor local): http://10.0.2.2:5014/api/conversor
    // Para WiFi casa: http://192.168.10.104:5014/api/conversor
    // Para Universidad: http://10.40.17.85:5014/api/conversor
    // Para red compartida: http://192.168.137.1:5014/api/conversor

    private static final String BASE_URL = "http://192.168.137.1:5014/api/conversor"; // Red compartida
    private static final int TIMEOUT = 5000; // 5 segundos

    // Codigos que devuelve el servidor (tabla 4 de 04. SERVIDOR/CONTRATO_GR02.md)
    public static final String TOKEN_INVALIDO = "AUTH-001";
    public static final String SESION_EXPIRADA = "AUTH-002";
    public static final String CREDENCIALES_INCORRECTAS = "AUTH-003";
    public static final String UNIDAD_NO_SOPORTADA = "UNIT-001";
    public static final String DATOS_INCONSISTENTES = "DATA-001";
    public static final String ERROR_RED = "RED";

    private final ExecutorService executorService;
    private final Handler mainHandler;

    public ApiService() {
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public interface ApiCallback<T> {
        void onSuccess(T respuesta);
        void onError(String codigo, String mensaje);
    }

    // CU-01 Autenticarse: POST /login, devuelve el token
    public void login(String usuario, String contrasenia, ApiCallback<String> callback) {
        executorService.execute(() -> {
            try {
                JSONObject cuerpo = new JSONObject();
                cuerpo.put("usuario", usuario);
                cuerpo.put("contrasenia", contrasenia);

                JSONObject json = enviar("POST", "/login", cuerpo, false);
                if (esOk(json)) {
                    String token = json.optString("token", "");
                    mainHandler.post(() -> callback.onSuccess(token));
                } else {
                    notificarError(callback, json);
                }
            } catch (Exception e) {
                notificarExcepcion(callback, e);
            }
        });
    }

    // CU-02 Consultar catalogo: GET /categorias
    public void obtenerCategorias(ApiCallback<List<String>> callback) {
        executorService.execute(() -> {
            try {
                JSONObject json = enviar("GET", "/categorias", null, true);
                if (esOk(json)) {
                    List<String> categorias = leerLista(json.optJSONArray("categorias"));
                    mainHandler.post(() -> callback.onSuccess(categorias));
                } else {
                    notificarError(callback, json);
                }
            } catch (Exception e) {
                notificarExcepcion(callback, e);
            }
        });
    }

    // CU-03 Consultar unidades por categoria: GET /categorias/{categoria}/unidades
    public void obtenerUnidades(String categoria, ApiCallback<List<String>> callback) {
        executorService.execute(() -> {
            try {
                String ruta = "/categorias/" + URLEncoder.encode(categoria, "UTF-8") + "/unidades";
                JSONObject json = enviar("GET", ruta, null, true);
                if (esOk(json)) {
                    List<String> unidades = leerLista(json.optJSONArray("unidades"));
                    mainHandler.post(() -> callback.onSuccess(unidades));
                } else {
                    notificarError(callback, json);
                }
            } catch (Exception e) {
                notificarExcepcion(callback, e);
            }
        });
    }

    // CU-04 Realizar conversion: POST /convertir con cuerpo JSON
    public void realizarConversion(ConversionRequest request, ApiCallback<ConversionResponse> callback) {
        executorService.execute(() -> {
            try {
                JSONObject cuerpo = new JSONObject();
                cuerpo.put("categoria", request.getCategoria());
                cuerpo.put("unidadOrigen", request.getUnidadOrigen());
                cuerpo.put("unidadDestino", request.getUnidadDestino());
                cuerpo.put("valor", request.getValor());

                JSONObject json = enviar("POST", "/convertir", cuerpo, true);
                if (esOk(json)) {
                    ConversionResponse respuesta = new ConversionResponse();
                    respuesta.setEstado(json.optString("estado", ""));
                    respuesta.setCodigo(json.optString("codigo", ""));
                    respuesta.setMensaje(json.optString("mensaje", ""));
                    respuesta.setResultado(json.isNull("resultado") ? null : json.getDouble("resultado"));
                    respuesta.setUnidadOrigen(json.optString("unidadOrigen", request.getUnidadOrigen()));
                    respuesta.setUnidadDestino(json.optString("unidadDestino", request.getUnidadDestino()));
                    mainHandler.post(() -> callback.onSuccess(respuesta));
                } else {
                    notificarError(callback, json);
                }
            } catch (Exception e) {
                notificarExcepcion(callback, e);
            }
        });
    }

    // Envia la peticion y devuelve el JSON del cuerpo, tambien en 400/401/500
    private JSONObject enviar(String metodo, String ruta, JSONObject cuerpo, boolean conToken) throws Exception {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(BASE_URL + ruta);
            Log.d("ApiService", metodo + " " + url);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(metodo);
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);

            if (conToken && SesionUsuario.estaActiva()) {
                connection.setRequestProperty("Authorization", "Bearer " + SesionUsuario.getToken());
            }

            if (cuerpo != null) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                OutputStream os = connection.getOutputStream();
                os.write(cuerpo.toString().getBytes("UTF-8"));
                os.close();
            }

            int responseCode = connection.getResponseCode();
            InputStream stream = responseCode >= 400 ? connection.getErrorStream() : connection.getInputStream();
            String texto = leerTexto(stream);
            Log.d("ApiService", "HTTP " + responseCode + " " + texto);

            if (texto.isEmpty()) {
                JSONObject vacio = new JSONObject();
                vacio.put("estado", "ERROR");
                vacio.put("codigo", String.valueOf(responseCode));
                vacio.put("mensaje", "Servidor no responde correctamente (código: " + responseCode + ")");
                return vacio;
            }
            return new JSONObject(texto);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String leerTexto(InputStream stream) throws Exception {
        if (stream == null) {
            return "";
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuilder texto = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            texto.append(line);
        }
        br.close();
        return texto.toString();
    }

    private boolean esOk(JSONObject json) {
        return "OK".equals(json.optString("estado", ""));
    }

    private List<String> leerLista(JSONArray arreglo) throws Exception {
        List<String> lista = new ArrayList<>();
        if (arreglo != null) {
            for (int i = 0; i < arreglo.length(); i++) {
                lista.add(arreglo.getString(i));
            }
        }
        return lista;
    }

    private <T> void notificarError(ApiCallback<T> callback, JSONObject json) {
        String codigo = json.optString("codigo", "");
        String mensaje = json.optString("mensaje", "Error desconocido");
        mainHandler.post(() -> callback.onError(codigo, mensaje));
    }

    private <T> void notificarExcepcion(ApiCallback<T> callback, Exception e) {
        String mensaje;
        if (e instanceof java.net.SocketTimeoutException) {
            mensaje = "Tiempo de espera agotado. Verifica que el servidor esté ejecutándose.";
        } else if (e instanceof java.net.ConnectException) {
            mensaje = "No se puede conectar al servidor. Verifica:\n1. Servidor ejecutándose\n2. Misma red WiFi\n3. Firewall desactivado";
        } else if (e instanceof java.net.UnknownHostException) {
            mensaje = "No se encuentra el servidor en " + BASE_URL;
        } else {
            mensaje = "Error: " + e.getMessage();
        }
        mainHandler.post(() -> callback.onError(ERROR_RED, mensaje));
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
```

### 5.2 `modelo/ConversionRequest.java`

```java
package ec.edu.monster.modelo;

public class ConversionRequest {
    private String categoria;
    private String unidadOrigen;
    private String unidadDestino;
    private double valor;

    public ConversionRequest() {
    }

    public ConversionRequest(String categoria, String unidadOrigen, String unidadDestino, double valor) {
        this.categoria = categoria;
        this.unidadOrigen = unidadOrigen;
        this.unidadDestino = unidadDestino;
        this.valor = valor;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getUnidadOrigen() {
        return unidadOrigen;
    }

    public void setUnidadOrigen(String unidadOrigen) {
        this.unidadOrigen = unidadOrigen;
    }

    public String getUnidadDestino() {
        return unidadDestino;
    }

    public void setUnidadDestino(String unidadDestino) {
        this.unidadDestino = unidadDestino;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
```

### 5.3 `modelo/ConversionResponse.java`

```java
package ec.edu.monster.modelo;

public class ConversionResponse {
    private String estado;
    private String codigo;
    private String mensaje;
    private Double resultado;
    private String unidadOrigen;
    private String unidadDestino;

    public ConversionResponse() {
    }

    public boolean isExito() {
        return "OK".equals(estado);
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Double getResultado() {
        return resultado;
    }

    public void setResultado(Double resultado) {
        this.resultado = resultado;
    }

    public String getUnidadOrigen() {
        return unidadOrigen;
    }

    public void setUnidadOrigen(String unidadOrigen) {
        this.unidadOrigen = unidadOrigen;
    }

    public String getUnidadDestino() {
        return unidadDestino;
    }

    public void setUnidadDestino(String unidadDestino) {
        this.unidadDestino = unidadDestino;
    }
}
```

`isExito()` se conserva por compatibilidad, pero ahora se deriva de `estado`.

### 5.4 `modelo/SesionUsuario.java` (nuevo)

```java
package ec.edu.monster.modelo;

public class SesionUsuario {
    private static String usuario;
    private static String token;

    private SesionUsuario() {
    }

    public static void iniciar(String nombreUsuario, String nuevoToken) {
        usuario = nombreUsuario;
        token = nuevoToken;
    }

    public static void cerrar() {
        usuario = null;
        token = null;
    }

    public static boolean estaActiva() {
        return token != null && !token.isEmpty();
    }

    public static String getUsuario() {
        return usuario;
    }

    public static String getToken() {
        return token;
    }
}
```

### 5.5 `vista/LoginActivity.java` (completo)

```java
package ec.edu.monster.vista;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ec.edu.monster.R;
import ec.edu.monster.controlador.ApiService;
import ec.edu.monster.modelo.SesionUsuario;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_MENSAJE = "MENSAJE_SESION";

    private EditText etUsuario, etPassword;
    private Button btnLogin;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar vistas
        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        apiService = new ApiService();

        // Mensaje cuando se llega aqui por sesion expirada (CU-08 / CU-09)
        String mensaje = getIntent().getStringExtra(EXTRA_MENSAJE);
        if (mensaje != null && !mensaje.isEmpty()) {
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarLogin();
            }
        });
    }

    private void validarLogin() {
        String usuario = etUsuario.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (usuario.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("VALIDANDO...");

        // CU-01 Autenticarse: el servidor valida y entrega el token
        apiService.login(usuario, password, new ApiService.ApiCallback<String>() {
            @Override
            public void onSuccess(String token) {
                SesionUsuario.iniciar(usuario, token);
                Toast.makeText(LoginActivity.this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("NOMBRE_USUARIO", usuario);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String codigo, String mensaje) {
                btnLogin.setEnabled(true);
                btnLogin.setText("INGRESAR");
                Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_LONG).show();
                if (ApiService.CREDENCIALES_INCORRECTAS.equals(codigo)) {
                    etPassword.setText("");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (apiService != null) {
            apiService.shutdown();
        }
    }

    // CU-08 / CU-09: cualquier pantalla vuelve aqui cuando el token ya no sirve
    public static boolean esErrorDeSesion(String codigo) {
        return ApiService.TOKEN_INVALIDO.equals(codigo) || ApiService.SESION_EXPIRADA.equals(codigo);
    }

    public static void volverAlLogin(Activity origen, String mensaje) {
        SesionUsuario.cerrar();
        Intent intent = new Intent(origen, LoginActivity.class);
        intent.putExtra(EXTRA_MENSAJE, mensaje);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        origen.startActivity(intent);
        origen.finish();
    }
}
```

### 5.6 `vista/MainActivity.java` (completo)

```java
package ec.edu.monster.vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import ec.edu.monster.R;
import ec.edu.monster.controlador.ApiService;
import ec.edu.monster.modelo.SesionUsuario;

public class MainActivity extends AppCompatActivity {

    private CardView cardTemperatura, cardMasa, cardLongitud;
    private FloatingActionButton btnSalir;
    private TextView tvNombreUsuario;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sin token no se puede usar el menu (por ejemplo, si Android cerro el proceso)
        if (!SesionUsuario.estaActiva()) {
            LoginActivity.volverAlLogin(this, "Debe iniciar sesión");
            return;
        }

        // Inicializar vistas
        cardTemperatura = findViewById(R.id.cardTemperatura);
        cardMasa = findViewById(R.id.cardMasa);
        cardLongitud = findViewById(R.id.cardLongitud);
        btnSalir = findViewById(R.id.btnSalir);
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);

        // Obtener nombre de usuario del intent
        String nombreUsuario = getIntent().getStringExtra("NOMBRE_USUARIO");
        if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
            tvNombreUsuario.setText("👤 " + nombreUsuario);
        }

        // Listeners
        cardTemperatura.setOnClickListener(v -> abrirConversiones("Temperatura"));
        cardMasa.setOnClickListener(v -> abrirConversiones("Masa"));
        cardLongitud.setOnClickListener(v -> abrirConversiones("Longitud"));

        btnSalir.setOnClickListener(v -> mostrarDialogoSalir());

        apiService = new ApiService();
        cargarCategorias();
    }

    // CU-02 Consultar catalogo: solo se muestran las tarjetas que envia el servidor
    private void cargarCategorias() {
        apiService.obtenerCategorias(new ApiService.ApiCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> categorias) {
                cardTemperatura.setVisibility(categorias.contains("TEMPERATURA") ? View.VISIBLE : View.GONE);
                cardMasa.setVisibility(categorias.contains("MASA") ? View.VISIBLE : View.GONE);
                cardLongitud.setVisibility(categorias.contains("LONGITUD") ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(String codigo, String mensaje) {
                if (LoginActivity.esErrorDeSesion(codigo)) {
                    LoginActivity.volverAlLogin(MainActivity.this, mensaje);
                } else {
                    Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void abrirConversiones(String tipo) {
        Intent intent = new Intent(MainActivity.this, TransformacionesActivity.class);
        intent.putExtra("TIPO_CONVERSION", tipo);
        startActivity(intent);
    }

    private void mostrarDialogoSalir() {
        new AlertDialog.Builder(this)
                .setTitle("Salir")
                .setMessage("¿Desea cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    SesionUsuario.cerrar();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        mostrarDialogoSalir();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (apiService != null) {
            apiService.shutdown();
        }
    }
}
```

`tvNombreUsuario` antes recibía solo el nombre, perdiendo el `👤` del layout; ahora se antepone.

### 5.7 `vista/TransformacionesActivity.java` (completo)

```java
package ec.edu.monster.vista;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ec.edu.monster.R;
import ec.edu.monster.controlador.ApiService;
import ec.edu.monster.modelo.ConversionRequest;
import ec.edu.monster.modelo.ConversionResponse;

public class TransformacionesActivity extends AppCompatActivity {

    private TextView tvTitulo, tvResultado;
    private Spinner spinnerOrigen, spinnerDestino;
    private EditText etValor;
    private Button btnConvertir, btnVolver, btnLimpiar;
    private ImageButton btnAtras;
    private ProgressBar progressBar;
    private ImageView ivIconoResultado, ivIconoCategoria;

    private String tipoConversion;   // "Temperatura" (texto en pantalla)
    private String categoria;        // "TEMPERATURA" (codigo del servidor)
    private final List<String> unidades = new ArrayList<>();
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transformaciones);

        // Obtener el tipo de conversión
        tipoConversion = getIntent().getStringExtra("TIPO_CONVERSION");
        categoria = tipoConversion.toUpperCase(Locale.ROOT);

        // Inicializar vistas
        tvTitulo = findViewById(R.id.tvTitulo);
        spinnerOrigen = findViewById(R.id.spinnerOrigen);
        spinnerDestino = findViewById(R.id.spinnerDestino);
        etValor = findViewById(R.id.etValor);
        btnConvertir = findViewById(R.id.btnConvertir);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        btnVolver = findViewById(R.id.btnVolver);
        btnAtras = findViewById(R.id.btnAtras);
        tvResultado = findViewById(R.id.tvResultado);
        progressBar = findViewById(R.id.progressBar);
        ivIconoResultado = findViewById(R.id.ivIconoResultado);
        ivIconoCategoria = findViewById(R.id.ivIconoCategoria);

        // Inicializar servicio API
        apiService = new ApiService();

        // Configurar título
        tvTitulo.setText("Conversión de " + tipoConversion);

        // Configurar iconos
        actualizarIcono();
        actualizarIconoCategoria();

        // Listeners
        btnConvertir.setOnClickListener(v -> realizarConversion());
        btnLimpiar.setOnClickListener(v -> limpiarCampos());
        btnVolver.setOnClickListener(v -> finish());
        btnAtras.setOnClickListener(v -> finish());

        // Unidades desde el servidor
        cargarUnidades();
    }

    private void limpiarCampos() {
        etValor.setText("");
        tvResultado.setText("Ingrese los datos y presione CONVERTIR");
        etValor.requestFocus();
    }

    private int obtenerIcono() {
        switch (tipoConversion) {
            case "Temperatura":
                return R.drawable.ic_temperature;
            case "Masa":
                return R.drawable.ic_weight;
            case "Longitud":
                return R.drawable.ic_ruler;
            default:
                return R.drawable.ic_result;
        }
    }

    private void actualizarIcono() {
        if (ivIconoResultado != null) {
            ivIconoResultado.setImageResource(obtenerIcono());
        }
    }

    private void actualizarIconoCategoria() {
        if (ivIconoCategoria != null) {
            ivIconoCategoria.setImageResource(obtenerIcono());
        }
    }

    // "KILOMETRO" -> "Kilometro"
    private String nombreVisible(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            return "";
        }
        return codigo.substring(0, 1) + codigo.substring(1).toLowerCase(Locale.ROOT);
    }

    // CU-03 Consultar unidades por categoria
    private void cargarUnidades() {
        progressBar.setVisibility(View.VISIBLE);
        btnConvertir.setEnabled(false);

        apiService.obtenerUnidades(categoria, new ApiService.ApiCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> respuesta) {
                progressBar.setVisibility(View.GONE);
                btnConvertir.setEnabled(true);

                unidades.clear();
                unidades.addAll(respuesta);

                List<String> nombres = new ArrayList<>();
                for (String unidad : unidades) {
                    nombres.add(nombreVisible(unidad));
                }
                spinnerOrigen.setAdapter(crearAdapter(nombres));
                spinnerDestino.setAdapter(crearAdapter(nombres));
                if (unidades.size() > 1) {
                    spinnerDestino.setSelection(1);
                }
            }

            @Override
            public void onError(String codigo, String mensaje) {
                progressBar.setVisibility(View.GONE);
                manejarError(codigo, mensaje);
            }
        });
    }

    private ArrayAdapter<String> crearAdapter(List<String> nombres) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    // CU-04 Realizar conversion
    private void realizarConversion() {
        if (unidades.isEmpty()) {
            Toast.makeText(this, "Las unidades aún no se han cargado", Toast.LENGTH_SHORT).show();
            return;
        }

        String valorStr = etValor.getText().toString().trim().replace(',', '.');

        if (valorStr.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese un valor", Toast.LENGTH_SHORT).show();
            return;
        }

        double valor;
        try {
            valor = Double.parseDouble(valorStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        String unidadOrigen = unidades.get(spinnerOrigen.getSelectedItemPosition());
        String unidadDestino = unidades.get(spinnerDestino.getSelectedItemPosition());

        // Crear request
        ConversionRequest request = new ConversionRequest(categoria, unidadOrigen, unidadDestino, valor);

        // Mostrar loading
        progressBar.setVisibility(View.VISIBLE);
        btnConvertir.setEnabled(false);
        tvResultado.setText("");

        // Llamar a la API
        apiService.realizarConversion(request, new ApiService.ApiCallback<ConversionResponse>() {
            @Override
            public void onSuccess(ConversionResponse response) {
                progressBar.setVisibility(View.GONE);
                btnConvertir.setEnabled(true);

                String resultado = String.format(Locale.getDefault(), "%s %s =\n%.4f %s",
                        valorStr, nombreVisible(response.getUnidadOrigen()),
                        response.getResultado(), nombreVisible(response.getUnidadDestino()));
                tvResultado.setText(resultado);
                tvResultado.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String codigo, String mensaje) {
                progressBar.setVisibility(View.GONE);
                btnConvertir.setEnabled(true);
                manejarError(codigo, mensaje);
            }
        });
    }

    // CU-08 a CU-11
    private void manejarError(String codigo, String mensaje) {
        if (isFinishing()) {
            return;
        }
        if (LoginActivity.esErrorDeSesion(codigo)) {
            LoginActivity.volverAlLogin(this, mensaje);
            return;
        }
        tvResultado.setText("⚠️ " + mensaje);
        String prefijo = ApiService.ERROR_RED.equals(codigo) ? "" : "[" + codigo + "] ";
        Toast.makeText(this, "Error: " + prefijo + mensaje, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (apiService != null) {
            apiService.shutdown();
        }
    }
}
```

Qué se quitó y por qué:

| Quitado | Motivo |
|---|---|
| `configurarSpinner()` con 3 pares fijos por categoría | CU-03: las unidades vienen del servidor |
| `obtenerUnidades(String conversion)` con `"Celsius"`, `"Tonelada"`… | Nombres que el servidor no reconoce (`Tonelada` no existe) |
| Listener del spinner que cambiaba el icono | El icono depende de la categoría, no del par elegido; se fija una vez en `onCreate` |

### 5.8 Cambios en layouts

**`activity_transformaciones.xml`**: en la tarjeta de selección, cambia el texto `android:text="Tipo de Conversión"` por `android:text="Unidades"` y reemplaza el bloque

```xml
<!-- Spinner con Fondo Sutil -->
<androidx.cardview.widget.CardView
    ...
    app:cardBackgroundColor="#F8F9FA">

    <Spinner
        android:id="@+id/spinnerConversion"
        ... />
</androidx.cardview.widget.CardView>
```

por:

```xml
<!-- Unidad de origen -->
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginBottom="6dp"
    android:text="Desde"
    android:textColor="#757575"
    android:textSize="14sp"
    android:textStyle="bold" />

<androidx.cardview.widget.CardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cardCornerRadius="14dp"
    app:cardElevation="6dp"
    app:cardBackgroundColor="#F8F9FA">

    <Spinner
        android:id="@+id/spinnerOrigen"
        android:layout_width="match_parent"
        android:layout_height="64dp"
        android:background="@android:color/transparent"
        android:paddingStart="18dp"
        android:paddingEnd="18dp" />
</androidx.cardview.widget.CardView>

<!-- Unidad de destino -->
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginTop="14dp"
    android:layout_marginBottom="6dp"
    android:text="Hacia"
    android:textColor="#757575"
    android:textSize="14sp"
    android:textStyle="bold" />

<androidx.cardview.widget.CardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cardCornerRadius="14dp"
    app:cardElevation="6dp"
    app:cardBackgroundColor="#F8F9FA">

    <Spinner
        android:id="@+id/spinnerDestino"
        android:layout_width="match_parent"
        android:layout_height="64dp"
        android:background="@android:color/transparent"
        android:paddingStart="18dp"
        android:paddingEnd="18dp" />
</androidx.cardview.widget.CardView>
```

Si el `ScrollView` queda justo, no hay que hacer nada más: ya envuelve todo el contenido.

**`activity_main.xml`**: subtítulos de las tarjetas.

| Tarjeta | Texto actual | Texto nuevo |
|---|---|---|
| Temperatura | `Celsius, Fahrenheit, Kelvin` | Igual |
| Masa | `Kilogramos, Gramos, Toneladas` | `Miligramo, Gramo, Kilogramo, Libra` |
| Longitud | `Kilómetros, Metros, Centímetros` | `Milímetro, Centímetro, Metro, Kilómetro, Milla, Pie` |

**`activity_login.xml`**:
- Pista inferior: `Usuario: MONSTER\nContraseña: Monster 9` → `Usuario: MONSTER\nContraseña: Monster9` (sin espacio; con espacio el servidor responde AUTH-003).
- Borra la línea 153, `android:alpha="0.7" />`, que quedó suelta después del cierre del `TextView`.

### 5.9 `probar-servidor.ps1` (reescrito)

```powershell
# Script para probar el servidor REST (contrato con token)
# Uso: .\probar-servidor.ps1            -> usa 192.168.137.1
#      .\probar-servidor.ps1 localhost

param([string]$ip = "192.168.137.1")

$url = "http://${ip}:5014/api/conversor"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Probador de Servidor REST - MONSTER  " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "URL: $url" -ForegroundColor Gray
Write-Host ""

function Llamar($metodo, $ruta, $cuerpo, $token) {
    $headers = @{}
    if ($token) { $headers["Authorization"] = "Bearer $token" }
    $parametros = @{ Uri = "$url$ruta"; Method = $metodo; Headers = $headers; TimeoutSec = 5; ContentType = "application/json" }
    if ($cuerpo) { $parametros["Body"] = ($cuerpo | ConvertTo-Json) }
    try {
        return Invoke-RestMethod @parametros
    } catch {
        # 400/401/500: el servidor envia el JSON de error en el cuerpo
        if ($_.ErrorDetails.Message) { return $_.ErrorDetails.Message | ConvertFrom-Json }
        if ($_.Exception.Response) {
            $lector = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            return $lector.ReadToEnd() | ConvertFrom-Json
        }
        Write-Host "   ❌ No se pudo conectar: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host '   Inicia el servidor: dotnet run --launch-profile http (escucha en 0.0.0.0:5014)' -ForegroundColor Yellow
        exit
    }
}

Write-Host "1. Login (CU-01)..." -ForegroundColor Yellow
$login = Llamar "POST" "/login" @{ usuario = "MONSTER"; contrasenia = "Monster9" } $null
Write-Host "   $($login.codigo) $($login.mensaje)" -ForegroundColor Green
$token = $login.token

Write-Host "2. Categorias (CU-02)..." -ForegroundColor Yellow
$cat = Llamar "GET" "/categorias" $null $token
Write-Host "   $($cat.categorias -join ', ')" -ForegroundColor Green

Write-Host "3. Unidades de MASA (CU-03)..." -ForegroundColor Yellow
$uni = Llamar "GET" "/categorias/MASA/unidades" $null $token
Write-Host "   $($uni.unidades -join ', ')" -ForegroundColor Green

Write-Host "4. 25 CELSIUS -> FAHRENHEIT (CU-04)..." -ForegroundColor Yellow
$conv = Llamar "POST" "/convertir" @{ categoria = "TEMPERATURA"; unidadOrigen = "CELSIUS"; unidadDestino = "FAHRENHEIT"; valor = 25 } $token
Write-Host "   Resultado: $($conv.resultado) ($($conv.mensaje))" -ForegroundColor Green

Write-Host "5. Sin token (CU-08)..." -ForegroundColor Yellow
$sin = Llamar "GET" "/categorias" $null $null
Write-Host "   $($sin.codigo) $($sin.mensaje)" -ForegroundColor Magenta

Write-Host "6. Unidad TONELADA (CU-10)..." -ForegroundColor Yellow
$ton = Llamar "POST" "/convertir" @{ categoria = "MASA"; unidadOrigen = "TONELADA"; unidadDestino = "KILOGRAMO"; valor = 1 } $token
Write-Host "   $($ton.codigo) $($ton.mensaje)" -ForegroundColor Magenta

Write-Host ""
Write-Host "En ApiService.java usa:" -ForegroundColor Cyan
Write-Host "   private static final String BASE_URL = `"$url`";" -ForegroundColor White
```

> `Invoke-RestMethod` lanza excepción en 400/401. Según la versión de PowerShell, el JSON de error llega en `$_.ErrorDetails.Message` o hay que leerlo del stream de `$_.Exception.Response`; el script prueba ambos.
>
> Ejecutado contra `04. SERVIDOR` (GR02) el 2026-10-25 con `.\probar-servidor.ps1 localhost` en Windows PowerShell 5.1; la salida está en la sección 8.1.

---

## 6. Casos de uso del taller en este cliente

| CU | Dónde se cumple | Cómo se ve |
|---|---|---|
| CU-01 Autenticarse | `LoginActivity.validarLogin` → `ApiService.login` | Botón "VALIDANDO...", luego `¡Bienvenido!` o `Credenciales incorrectas` |
| CU-02 Consultar catálogo | `MainActivity.cargarCategorias` | Solo las tarjetas que envía el servidor |
| CU-03 Consultar unidades | `TransformacionesActivity.cargarUnidades` | Spinners "Desde" y "Hacia" |
| CU-04 Realizar conversión | `TransformacionesActivity.realizarConversion` | `25 Celsius =\n77,0000 Fahrenheit` |
| CU-05 Validar token | Servidor; `ApiService.enviar` pone `Authorization: Bearer` | — |
| CU-06 Validar datos | Servidor; la app solo exige valor numérico | `Por favor ingrese un valor` / `Valor inválido` |
| CU-07 Generar respuesta | Servidor | Resultado en la tarjeta verde |
| CU-08 Token inválido | `manejarError` → `LoginActivity.volverAlLogin` | Login con `Toast` "Token invalido o sesion expirada" |
| CU-09 Sesión expirada | Igual con `AUTH-002` | Login con "Sesion expirada, vuelva a autenticarse" |
| CU-10 Unidad no soportada | `manejarError` | `Error: [UNIT-001] Unidad de origen no soportada: ...` |
| CU-11 Datos inconsistentes | `manejarError` | `Error: [DATA-001] ...` |

---

## 7. Flujo de pantallas

```
LoginActivity ──POST /login──> SesionUsuario.iniciar(token)
     │
     ▼
MainActivity ──GET /categorias──> muestra/oculta tarjetas
     │ tarjeta
     ▼
TransformacionesActivity
     ├─ al abrir: GET /categorias/{C}/unidades → spinners
     ├─ Convertir: POST /convertir → resultado o Toast de error
     └─ Menú / atrás → finish()

AUTH-001 / AUTH-002 en cualquier pantalla ──> volverAlLogin() (limpia la pila)
FAB salir (confirmar) ──> SesionUsuario.cerrar() ──> LoginActivity
```

---

## 8. Configuración, ejecución y verificación

### 8.1 Ejecutar

1. Inicia el servidor: `dotnet run --launch-profile http` en `04. SERVIDOR/WS_ConUnid_DOTNET_RESTFUL_GR02/WS_ConUnid_DOTNET_RESTFUL_GR02` (escucha en `http://0.0.0.0:5014`: sirve para el celular en `192.168.137.1` y para el emulador en `10.0.2.2`).
2. Comprueba desde el PC: `.\probar-servidor.ps1` (o `.\probar-servidor.ps1 localhost`). Salida real contra GR02 (2026-10-25):
   ```
   1. Login (CU-01)...                    200 Autenticacion realizada correctamente
   2. Categorias (CU-02)...               LONGITUD, MASA, TEMPERATURA
   3. Unidades de MASA (CU-03)...         MILIGRAMO, GRAMO, KILOGRAMO, LIBRA
   4. 25 CELSIUS -> FAHRENHEIT (CU-04)... Resultado: 77 (Conversion realizada correctamente)
   5. Sin token (CU-08)...                AUTH-001 Token invalido o sesion expirada
   6. Unidad TONELADA (CU-10)...          UNIT-001 Unidad de origen no soportada: TONELADA
   ```
3. Ajusta `BASE_URL` en `ApiService.java` (y `network_security_config.xml` si la IP es nueva).
4. Abre `CONUNI_CLIENTE_MOVIL` en Android Studio, sincroniza Gradle y ejecuta en el emulador o en el celular conectado a la misma red.

### 8.2 Lista de pruebas en el dispositivo

| # | Acción | Esperado |
|---|---|---|
| 1 | Servidor apagado, INGRESAR | `No se puede conectar al servidor. Verifica: ...` |
| 2 | `MONSTER` / `Monster 9` | `Credenciales incorrectas` y se borra la contraseña |
| 3 | `MONSTER` / `Monster9` | `¡Bienvenido!` y menú con 3 tarjetas, `👤 MONSTER` |
| 4 | Masa | Spinners con Miligramo, Gramo, Kilogramo, Libra; destino preseleccionado en Gramo |
| 5 | Temperatura, Celsius → Fahrenheit, 25 | `25 Celsius =\n77,0000 Fahrenheit` (separador según el idioma del teléfono) |
| 6 | Longitud, Kilometro → Milla, `1,5` | `1.5 Kilometro =\n0,9321 Milla` |
| 7 | Valor vacío | `Por favor ingrese un valor` |
| 8 | Reiniciar el servidor con la app abierta y convertir | Vuelve al login con `Token invalido o sesion expirada` |
| 9 | FAB salir → Sí | Login; botón atrás no regresa al menú |
| 10 | Logcat filtro `ApiService` | Se ven `POST .../login`, `HTTP 200 {...}` y los 400/401 con su JSON |

### 8.3 Cambios en la documentación y archivos

- Eliminar `SERVIDOR_DOTNET_CODIGO.cs` (borrador de otro contrato).
- `README.md`: sección "Formato de la API REST" con el contrato de la sección 3 (login + token, categorías, unidades, `POST` con `categoria`; respuesta `estado/codigo/mensaje/resultado`); credenciales `Monster9`; catálogo real.
- `PROBLEMA_IDENTIFICADO.md`, `SOLUCION_ERROR_CONEXION.md`, `CONFIGURACION_SERVIDOR.md`, `INICIO_RAPIDO.md`, `RESUMEN_FINAL.md`: quitar `tipoConversion`, `exito`, `Monster 9`, la ruta `c:\Users\karly\...\REST_SIN_BDD_DOTNET_GR09` y las instrucciones para escribir el servidor (el servidor real está en `04. SERVIDOR`; su contrato es `CONTRATO_GR02.md`).
- `ANDROID_STUDIO.md` y `GENERAR_APK.md`: sin cambios de contenido técnico.
- Opcional: quitar el repositorio `ksoap2` de `settings.gradle.kts` (no se usa).

---

## 9. Decisiones tomadas y puntos a confirmar

| # | Punto | Decisión de esta guía |
|---|---|---|
| 1 | Toneladas | El `.docx` no las incluye (masa: miligramo, gramo, kilogramo, libra). "Toneladas a Kilogramos" se quita; el catálogo sale del servidor |
| 2 | Cerrar sesión | El `.docx` no lo pide y el servidor no tiene logout. La app ya tenía el FAB de salir y se conserva (solo borra el token en la app). Consola y escritorio lo dejan apagado con `PERMITIR_CERRAR_SESION = false`; si se quiere lo mismo aquí, basta con ocultar el FAB (`android:visibility="gone"`) |
| 3 | El token vive en memoria (`SesionUsuario`) | Cumple "almacenar temporalmente"; si Android mata el proceso, `MainActivity` manda al login |
| 4 | Las 3 tarjetas siguen en el layout | Se ocultan las que el servidor no envía; una categoría nueva necesitaría su tarjeta |
| 5 | Decimales | El `.docx` no lo especifica. Extra: 4 decimales con `%.4f` (como ya tenía la app) y separador del idioma del teléfono; cambia el `.4` para otro número o usa `Locale.US` para tener siempre punto |
