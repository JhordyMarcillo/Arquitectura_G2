# Guía del cliente de escritorio (Java Swing) — Conversión de Unidades REST

> Cliente: `04. CLIESC` · Lenguaje: **Java 17+ con Swing y Maven** (como pide el `.docx`) · Servidor: `04. SERVIDOR` (ASP.NET Core, `WS_ConUnid_DOTNET_RESTFUL_GR02`)
>
> Esta guía se lee junto con:
> - `04. SERVIDOR/CONTRATO_GR02.md` (el contrato REST que este cliente consume; sale del código del servidor y manda sobre esta guía).
> - `04. SERVIDOR/TALLER CASOS DE USO CONVERSION UNIDADES.docx` (los casos de uso que el cliente debe cumplir).
> - `04. CLICON/GUIA_CLIENTE_CONSOLA.md`: el cliente de consola comparte **las mismas** clases `controlador` y `modelo`.

---

## 0. Instrucciones para quien aplique esta guía (persona o IA)

1. El `.docx` define el cliente de escritorio como *"Java Swing"*. Se crea el proyecto Java `CONUNI_CLIENTE_ESCRITORIO`, con la misma estructura de 3 carpetas que el servidor (`ec.edu.monster.controlador`, `modelo`, `vista`).
2. **Conserva** el diseño de las ventanas Tkinter del cliente de referencia (sección 2.3): tamaños, colores, fuentes, logo circular, fondo `fondoes.jpg`, textos y diálogos. Las imágenes son opcionales: si faltan, la ventana abre sin logo ni fondo.
3. **No implementes conversiones en el cliente.** El taller lo exige: *"Los cuatro clientes no implementan la lógica de conversión"*.
4. Copia el código de la sección 5 **tal cual**. Se compiló y se probó contra el servidor real con un arnés automático (sección 8.3 y anexo A).
5. No pongas ñ ni tildes en identificadores. Los textos que ve el usuario sí las llevan.
6. Si algo de esta guía choca con el `.docx`, manda el `.docx` en lo funcional y esta guía en la forma. Si choca con el servidor, **manda el servidor** (`CONTRATO_GR02.md`).

| Fuente | Manda en |
|---|---|
| `.docx` del taller | Qué hace el sistema: casos de uso CU-01…CU-11, flujo, mensajes de error |
| `04. SERVIDOR/CONTRATO_GR02.md` | Rutas, JSON, códigos, token |
| Esta guía | Cómo queda **este** cliente: proyecto, clases, diseño |

---

## 1. Variables del cliente

| Variable | Valor | Dónde |
|---|---|---|
| `URL_BASE` | `http://192.168.137.1:5014/api/conversor` | `Configuracion.URL_BASE` |
| Cambiar la URL sin recompilar | `java -Dconuni.url=http://localhost:5014/api/conversor -jar ...` | Propiedad del sistema `conuni.url` |
| Usuario de prueba | `MONSTER` / `Monster9` | Lo valida **el servidor**, no el cliente |
| Timeout HTTP | 10 s | `Configuracion.TIMEOUT_MS` |
| Decimales del resultado | 4 (extra opcional, el `.docx` no lo pide) | `Configuracion.DECIMALES` |
| Botón "Cerrar sesión" | `false` (el `.docx` no lo pide) | `Configuracion.PERMITIR_CERRAR_SESION` |
| Imágenes | `logo_login.png`, `fondoes.jpg` | `src/main/resources/imagenes/` |
| Clase principal | `ec.edu.monster.vista.AplicacionEscritorio` | `pom.xml` (`exec.mainClass`) |

---

## 2. Origen del diseño: cliente Python + Tkinter de referencia

> En `04. CLIESC` de este proyecto **no hay** cliente Python: el proyecto Java se crea desde cero. Las imágenes `logo_login.png` y `fondoes.jpg` se tomaron del cliente de referencia y ya están en `src/main/resources/imagenes/`. Esta sección queda como registro de dónde salen el diseño (2.3) y las decisiones (2.2, 2.4).

### 2.1 Archivos

| Archivo | Qué hace hoy | Acción |
|---|---|---|
| `app.py` | Ciclo `LoginWindow` → `MainWindow`; cerrar sesión vuelve al login | **Reemplazar** por `AplicacionEscritorio.java` |
| `api_client.py` | `APIClient` con un CRUD (`obtener_todas`, `crear_conversion`, `actualizar_conversion`, `eliminar_conversion`) que el servidor **no tiene**; `verificar_conexion` da `True` ante cualquier respuesta | **Reemplazar** por `ApiService.java` |
| `login_window.py` | Ventana 480x580, logo circular; valida `MONSTER`/`Monster9` **en el cliente** | **Reemplazar** por `LoginFrame.java` con el mismo diseño |
| `main_window.py` (519 líneas) | Calculadora 650x680 con fondo; 9 conversiones **locales**; `guardar_en_servidor` comentado | **Reemplazar** por `ConversorFrame.java` con el mismo diseño |
| `test_connection.py`, `test_servidor.bat` | Prueban el CRUD inexistente | **Eliminar** (los reemplaza el anexo A) |
| `requirements.txt` | Solo `requests` (faltaba Pillow) | **Eliminar** (lo reemplaza `pom.xml`) |
| `ejecutar.bat` | Instala dependencias de Python y ejecuta `app.py` | **Reemplazar** (sección 5.14) |
| `logo_login.png`, `fondoes.jpg` | Imágenes | **Mover** a `CONUNI_CLIENTE_ESCRITORIO/src/main/resources/imagenes/` |
| `README.md`, `INICIO_RAPIDO.md`, `FLUJO_NAVEGACION.md`, `INDICE.md`, `CAPTURAS.md`, `ACTUALIZACION_FINAL.txt`, `RESUMEN_PROYECTO.txt` | Describen modo offline, base de datos, un CRUD y un `ARQUITECTURA.md` que no existen | **Reescribir** `README.md` (sección 5.15) y **eliminar** el resto |
| `.gitignore` | Python | **Reemplazar** por uno de Java (sección 5.16) |

### 2.2 Clases y métodos Python y su destino en Java

| Archivo | Método | Hoy | Problema frente al taller | Destino Java |
|---|---|---|---|---|
| `api_client.py` | `_handle_response` | `{success, data, message}`; ante 400/401 pierde el cuerpo | Se pierde el mensaje del servidor | `ApiService.enviar` lee `getErrorStream()` y lanza `ExcepcionApi(codigo, mensaje)` |
| `api_client.py` | Métodos CRUD | GET/POST/PUT/DELETE de "conversiones guardadas" | No existen en el servidor | **Eliminados** |
| `login_window.py` | `USUARIO_VALIDO`, `CONTRASENA_VALIDA`, `validar_login` | Credenciales y comparación en el cliente | Viola CU-01 | `LoginFrame.validarLogin` → `ApiService.login` |
| `main_window.py` | `self.conversiones` + 9 métodos (`celsius_a_fahrenheit`…) | Cálculo local, 2 decimales | Conversión en el cliente; "Toneladas a Kilogramos" fallaría | **Eliminados** |
| `main_window.py` | `combo_conversion` | Elige un par fijo ("Celsius a Fahrenheit") | El taller pide elegir origen y destino | `cmbCategoria`, `cmbOrigen`, `cmbDestino` llenados por el servidor |
| `main_window.py` | `guardar_en_servidor` | Comentado; contrato inexistente | — | **Eliminado** |
| `main_window.py` | `convertir` | Valida y calcula | Debe pedir al servidor | `ConversorFrame.convertir` → `ApiService.realizarConversion` |
| `main_window.py` | `limpiar`, `on_closing`, `actualizar_estado_conexion` | Correctos | — | `limpiar`, `salir`, `actualizarEstado` |
| `main_window.py` | `cerrar_sesion` (botón ⎋) | Volvía al login | El `.docx` no lo pide | `cerrarSesion()` existe, el botón solo aparece con `PERMITIR_CERRAR_SESION = true` |

### 2.3 Diseño que se conserva

| Elemento | Tkinter | Swing |
|---|---|---|
| Login | 480x580, fondo `#e8f4f8`, logo circular 150x150 | Igual (`crearImagenCircular` recorta el PNG y le pone borde `#4a90d9`) |
| Título login | "Sistema de Conversión de Unidades" `#1a5490`, separador `#4a90d9`, "Iniciar Sesión" | Igual |
| Campos | Etiquetas `#1a5490` Segoe UI 11 bold, texto `#333333`, contraseña con `●`, Enter envía | Igual (`setEchoChar('●')`, `setDefaultButton`) |
| Botones login | "Iniciar Sesión" `#4a90d9` / hover `#3a7ac0`; "Cancelar" `#87ceeb` / hover `#6fb9d9` | Igual (`Estilos.boton` con hover) |
| Calculadora | 650x680, fondo `fondoes.jpg` | 650x**730** (se agregaron las filas Origen y Destino) |
| Encabezado | `#1976D2`, 60 px, "Calculadora de Conversión de Unidades" Segoe UI 15 bold blanco, usuario | Igual |
| Formulario | Marco blanco, título "Selecciona el tipo de conversión" sobre `#64B5F6`, etiquetas `#1565C0` Segoe UI 11 bold, separador `#42A5F5` 3 px | Igual |
| Botones | "CONVERTIR" `#1976D2` / `#1565C0`, "LIMPIAR" `#FF9800` / `#F57C00`, Segoe UI 13 bold | Igual |
| Resultado | Marco `#C8E6C9`, "Resultado:" `#2E7D32` 12 bold, valor Segoe UI 18 bold `#1B5E20`, inicial `---` | Igual |
| Estado | Barra blanca inferior de 50 px; info `#2196F3`, éxito `#4CAF50`, error `#f44336`, aviso `#FF9800` | Igual |
| Diálogos | "Campos vacíos", "Login Exitoso / ¡Bienvenido X!", "Error de Autenticación", "Selección requerida", "Valor requerido", "Valor inválido", "Salir / ¿Desea salir de la aplicación?" | Igual, con `JOptionPane` |

Los emojis de los textos Tkinter (🔄, 🔐, 👤, ✅, 🗑️) **se quitaron** en Swing: en Windows, Swing los dibuja como cuadros vacíos. Los colores y textos se mantienen.

### 2.4 Resumen de lo que violaba el taller

| Requisito del `.docx` | Cliente Python | Cliente Java Swing |
|---|---|---|
| Cliente de escritorio en Java Swing | Python + Tkinter | Java Swing + Maven |
| El cliente no implementa conversión | 9 funciones locales | Solo el servidor calcula |
| CU-01: el servidor autentica y emite token | Credenciales quemadas | `POST /login`, guarda el `token` |
| CU-02/CU-03: catálogo del servidor | Pares fijos | `GET /categorias`, `GET /categorias/{c}/unidades` |
| CU-05: cada petición lleva token | No hay token | `Authorization: Bearer <token>` |
| CU-08/CU-09 | No existen | Aviso "Sesión expirada" y regreso al login |
| CU-10/CU-11 | No existen | Diálogo `Error <codigo>` con el mensaje del servidor |

---

## 3. Contrato del servidor (resumen para este cliente)

Resumen de `04. SERVIDOR/CONTRATO_GR02.md`; ante cualquier duda manda ese archivo.

| Dato | Valor |
|---|---|
| URL base (red compartida) | `http://192.168.137.1:5014/api/conversor` |
| URL base (misma PC) | `http://localhost:5014/api/conversor` |
| Perfil del servidor | `http` (`http://0.0.0.0:5014`, `launchSettings.json`) |
| Prefijo | `api/conversor` (`[Route("api/conversor")]`) |

| CU | Método y ruta | Token | Cuerpo | Respuesta OK |
|---|---|---|---|---|
| CU-01 | `POST /login` | No | `{"usuario":"MONSTER","contrasenia":"Monster9"}` | `{"estado":"OK","codigo":"200","mensaje":"Autenticacion realizada correctamente","token":"<guid>","expiracion":"2026-10-25T20:50:31.060353-05:00"}` |
| CU-02 | `GET /categorias` | Sí | — | `{"estado":"OK","codigo":"200","mensaje":"Categorias consultadas correctamente","categorias":["LONGITUD","MASA","TEMPERATURA"]}` |
| CU-03 | `GET /categorias/{categoria}/unidades` | Sí | — | `{"estado":"OK","codigo":"200","mensaje":"Unidades consultadas correctamente","categoria":"MASA","unidades":["MILIGRAMO","GRAMO","KILOGRAMO","LIBRA"]}` |
| CU-04 | `POST /convertir` | Sí | `{"categoria":"LONGITUD","unidadOrigen":"KILOMETRO","unidadDestino":"METRO","valor":5}` | `{"estado":"OK","codigo":"200","mensaje":"Conversion realizada correctamente","resultado":5000,"unidadOrigen":"KILOMETRO","unidadDestino":"METRO"}` |

Header del token: `Authorization: Bearer <token>`. El token dura 30 minutos y vive en memoria del servidor: si el servidor se reinicia, el token deja de valer (`AUTH-001`). No hay endpoint de logout.

| Categoría | Unidades (catálogo del `.docx` y del servidor; **no hay toneladas**) |
|---|---|
| `LONGITUD` | `MILIMETRO`, `CENTIMETRO`, `METRO`, `KILOMETRO`, `MILLA`, `PIE` |
| `MASA` | `MILIGRAMO`, `GRAMO`, `KILOGRAMO`, `LIBRA` |
| `TEMPERATURA` | `CELSIUS`, `FAHRENHEIT`, `KELVIN` |

El servidor acepta mayúsculas o minúsculas. La ventana muestra `Kilometro` y envía `KILOMETRO` (`FormatoNumero.nombreVisible` / `codigo`).

| HTTP | `codigo` | Caso de uso | `mensaje` del servidor | Qué hace este cliente |
|---|---|---|---|---|
| 401 | `AUTH-001` | CU-08 token inválido | `Token invalido o sesion expirada` | Cierra la calculadora, abre el login con el aviso "Sesión expirada" |
| 401 | `AUTH-002` | CU-09 sesión expirada | `Sesion expirada, vuelva a autenticarse` | Igual que AUTH-001 |
| 401 | `AUTH-003` | CU-01 credenciales incorrectas | `Credenciales incorrectas` | "Error de Autenticación", borra la contraseña |
| 400 | `UNIT-001` | CU-10 unidad no soportada | `Unidad de origen no soportada: XYZ` | Diálogo `Error UNIT-001` + mensaje; estado rojo |
| 400 | `DATA-001` | CU-11 datos inconsistentes | p. ej. `La unidad de destino CELSIUS no pertenece a la categoria LONGITUD` | Diálogo `Error DATA-001` + mensaje; estado rojo |
| 500 | `SRV-001` | Error interno | `Error interno del servidor` | Diálogo `Error SRV-001` + mensaje |
| — | `RED` (propio del cliente) | Servidor apagado o inalcanzable | — | "Error de conexión"; estado naranja "Servidor no disponible" |

---

## 4. Estructura objetivo

```
04. CLIESC/
├── CONUNI_CLIENTE_ESCRITORIO/               # proyecto Maven (se abre en NetBeans)
│   ├── pom.xml
│   ├── ejecutar.bat
│   ├── .gitignore
│   └── src/main/
│       ├── java/ec/edu/monster/
│       │   ├── controlador/
│       │   │   ├── Configuracion.java       # igual que en consola
│       │   │   └── ApiService.java          # igual que en consola
│       │   ├── modelo/
│       │   │   ├── ConversionRequest.java   # igual que en consola
│       │   │   ├── ConversionResponse.java  # igual que en consola
│       │   │   ├── ExcepcionApi.java        # igual que en consola
│       │   │   └── SesionUsuario.java       # igual que en consola
│       │   └── vista/
│       │       ├── AplicacionEscritorio.java  # main
│       │       ├── LoginFrame.java            # ventana de login
│       │       ├── ConversorFrame.java        # calculadora
│       │       ├── Estilos.java               # colores, fuentes, botones con hover
│       │       ├── PanelFondo.java            # fondo con imagen
│       │       ├── TareaApi.java              # SwingWorker: llamadas sin congelar la ventana
│       │       └── FormatoNumero.java         # igual que en consola
│       └── resources/imagenes/
│           ├── logo_login.png
│           └── fondoes.jpg
├── README.md                                  # reescrito
└── GUIA_CLIENTE_ESCRITORIO.md                 # esta guía
```

| Capa | Clase | Responsabilidad |
|---|---|---|
| controlador | `Configuracion`, `ApiService` | URL y extras; único punto que hace HTTP |
| modelo | `ConversionRequest`, `ConversionResponse`, `SesionUsuario`, `ExcepcionApi` | Datos del contrato y token en memoria |
| vista | `LoginFrame`, `ConversorFrame` | Ventanas; no saben de HTTP ni de JSON |
| vista | `TareaApi` | Ejecuta cada llamada en segundo plano y vuelve al hilo de Swing con el resultado o el error |
| vista | `Estilos`, `PanelFondo` | Diseño Tkinter trasladado a Swing |

---

## 5. Código

### 5.1 `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>ec.edu.monster</groupId>
    <artifactId>CONUNI_CLIENTE_ESCRITORIO</artifactId>
    <version>1.0</version>
    <packaging>jar</packaging>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.release>17</maven.compiler.release>
        <exec.mainClass>ec.edu.monster.vista.AplicacionEscritorio</exec.mainClass>
    </properties>

    <dependencies>
        <!-- Misma libreria JSON que usa Android (org.json) -->
        <dependency>
            <groupId>org.json</groupId>
            <artifactId>json</artifactId>
            <version>20240303</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- mvn package genera target/CONUNI_CLIENTE_ESCRITORIO-1.0-jar-with-dependencies.jar -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>3.7.1</version>
                <configuration>
                    <descriptorRefs>
                        <descriptorRef>jar-with-dependencies</descriptorRef>
                    </descriptorRefs>
                    <archive>
                        <manifest>
                            <mainClass>${exec.mainClass}</mainClass>
                        </manifest>
                    </archive>
                </configuration>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

### 5.2 Clases compartidas con el cliente de consola

Copia **sin cambios** estas clases de `04. CLICON/GUIA_CLIENTE_CONSOLA.md`, sección 5:

| Archivo | Sección de la guía de consola |
|---|---|
| `controlador/Configuracion.java` | 5.2 |
| `controlador/ApiService.java` | 5.3 |
| `modelo/ExcepcionApi.java` | 5.4 |
| `modelo/ConversionRequest.java` | 5.5 |
| `modelo/ConversionResponse.java` | 5.6 |
| `modelo/SesionUsuario.java` | 5.7 |
| `vista/FormatoNumero.java` | 5.8 |

Para que esta guía se pueda aplicar sola, van repetidas a continuación (5.3 a 5.9).

### 5.3 `controlador/Configuracion.java`

```java
package ec.edu.monster.controlador;

public final class Configuracion {

    // URL de la API. Se puede cambiar al ejecutar con:
    //   java -Dconuni.url=http://localhost:5014/api/conversor -jar ...
    // Para red compartida: http://192.168.137.1:5014/api/conversor
    // Para la misma PC:    http://localhost:5014/api/conversor
    public static final String URL_BASE = System.getProperty("conuni.url",
            "http://192.168.137.1:5014/api/conversor");

    public static final int TIMEOUT_MS = 10000;

    // Decimales maximos al mostrar el resultado (77.0 -> "77", 0.621371 -> "0.6214")
    public static final int DECIMALES = 4;

    // El taller no pide cerrar sesion. En true aparece la opcion/boton y
    // solo borra el token en el cliente (el servidor no tiene logout).
    public static final boolean PERMITIR_CERRAR_SESION = false;

    private Configuracion() {
    }
}
```

### 5.4 `controlador/ApiService.java`

```java
package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ConversionRequest;
import ec.edu.monster.modelo.ConversionResponse;
import ec.edu.monster.modelo.ExcepcionApi;
import ec.edu.monster.modelo.SesionUsuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ApiService {

    // Codigos que devuelve el servidor (tabla 4 de 04. SERVIDOR/CONTRATO_GR02.md)
    public static final String TOKEN_INVALIDO = "AUTH-001";
    public static final String SESION_EXPIRADA = "AUTH-002";
    public static final String CREDENCIALES_INCORRECTAS = "AUTH-003";
    public static final String UNIDAD_NO_SOPORTADA = "UNIT-001";
    public static final String DATOS_INCONSISTENTES = "DATA-001";
    public static final String ERROR_RED = "RED";

    // CU-01 Autenticarse: POST /login, guarda el token en SesionUsuario
    public String login(String usuario, String contrasenia) throws ExcepcionApi {
        JSONObject cuerpo = new JSONObject();
        cuerpo.put("usuario", usuario);
        cuerpo.put("contrasenia", contrasenia);

        JSONObject json = enviar("POST", "/login", cuerpo, false);
        String token = json.optString("token", "");
        SesionUsuario.iniciar(usuario, token);
        return token;
    }

    // CU-02 Consultar catalogo: GET /categorias
    public List<String> obtenerCategorias() throws ExcepcionApi {
        JSONObject json = enviar("GET", "/categorias", null, true);
        return leerLista(json.optJSONArray("categorias"));
    }

    // CU-03 Consultar unidades por categoria: GET /categorias/{categoria}/unidades
    public List<String> obtenerUnidades(String categoria) throws ExcepcionApi {
        String ruta = "/categorias/" + URLEncoder.encode(categoria, StandardCharsets.UTF_8) + "/unidades";
        JSONObject json = enviar("GET", ruta, null, true);
        return leerLista(json.optJSONArray("unidades"));
    }

    // CU-04 Realizar conversion: POST /convertir con cuerpo JSON
    public ConversionResponse realizarConversion(ConversionRequest request) throws ExcepcionApi {
        JSONObject cuerpo = new JSONObject();
        cuerpo.put("categoria", request.getCategoria());
        cuerpo.put("unidadOrigen", request.getUnidadOrigen());
        cuerpo.put("unidadDestino", request.getUnidadDestino());
        cuerpo.put("valor", request.getValor());

        JSONObject json = enviar("POST", "/convertir", cuerpo, true);

        ConversionResponse respuesta = new ConversionResponse();
        respuesta.setEstado(json.optString("estado", ""));
        respuesta.setCodigo(json.optString("codigo", ""));
        respuesta.setMensaje(json.optString("mensaje", ""));
        respuesta.setResultado(json.isNull("resultado") ? null : json.getDouble("resultado"));
        respuesta.setUnidadOrigen(json.optString("unidadOrigen", request.getUnidadOrigen()));
        respuesta.setUnidadDestino(json.optString("unidadDestino", request.getUnidadDestino()));
        return respuesta;
    }

    // Sin token el servidor responde 401 con JSON: cualquier respuesta HTTP indica que esta vivo
    public boolean verificarConexion() {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) URI.create(Configuracion.URL_BASE + "/categorias").toURL().openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.getResponseCode();
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // Envia la peticion; devuelve el JSON si estado = OK, si no lanza ExcepcionApi con codigo y mensaje
    private JSONObject enviar(String metodo, String ruta, JSONObject cuerpo, boolean conToken) throws ExcepcionApi {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) URI.create(Configuracion.URL_BASE + ruta).toURL().openConnection();
            connection.setRequestMethod(metodo);
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(Configuracion.TIMEOUT_MS);
            connection.setReadTimeout(Configuracion.TIMEOUT_MS);

            if (conToken && SesionUsuario.estaActiva()) {
                connection.setRequestProperty("Authorization", "Bearer " + SesionUsuario.getToken());
            }

            if (cuerpo != null) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(cuerpo.toString().getBytes(StandardCharsets.UTF_8));
                }
            }

            // En 400/401/500 el JSON de error viene por getErrorStream()
            int responseCode = connection.getResponseCode();
            InputStream stream = responseCode >= 400 ? connection.getErrorStream() : connection.getInputStream();
            String texto = stream == null ? "" : new String(stream.readAllBytes(), StandardCharsets.UTF_8);

            JSONObject json;
            try {
                json = new JSONObject(texto);
            } catch (JSONException e) {
                throw new ExcepcionApi(String.valueOf(responseCode),
                        "Servidor no responde correctamente (código: " + responseCode + ")");
            }

            if (!"OK".equals(json.optString("estado", ""))) {
                ExcepcionApi error = new ExcepcionApi(json.optString("codigo", String.valueOf(responseCode)),
                        json.optString("mensaje", "Error desconocido"));
                if (error.esErrorDeSesion()) {
                    SesionUsuario.cerrar();
                }
                throw error;
            }
            return json;

        } catch (SocketTimeoutException e) {
            throw new ExcepcionApi(ERROR_RED, "Tiempo de espera agotado. Verifique que el servidor esté ejecutándose.");
        } catch (ConnectException e) {
            throw new ExcepcionApi(ERROR_RED, "No se puede conectar con el servidor en " + Configuracion.URL_BASE);
        } catch (UnknownHostException e) {
            throw new ExcepcionApi(ERROR_RED, "No se encuentra el servidor en " + Configuracion.URL_BASE);
        } catch (IOException e) {
            throw new ExcepcionApi(ERROR_RED, "Error de comunicación: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private List<String> leerLista(JSONArray arreglo) {
        List<String> lista = new ArrayList<>();
        if (arreglo != null) {
            for (int i = 0; i < arreglo.length(); i++) {
                lista.add(arreglo.getString(i));
            }
        }
        return lista;
    }
}
```

### 5.5 `modelo/ExcepcionApi.java`

```java
package ec.edu.monster.modelo;

public class ExcepcionApi extends Exception {

    private final String codigo;

    public ExcepcionApi(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    // CU-08 token invalido o CU-09 sesion expirada: hay que volver a autenticarse
    public boolean esErrorDeSesion() {
        return "AUTH-001".equals(codigo) || "AUTH-002".equals(codigo);
    }

    // El servidor no respondio (apagado, IP incorrecta, timeout)
    public boolean esErrorDeRed() {
        return "RED".equals(codigo);
    }
}
```

### 5.6 `modelo/ConversionRequest.java`

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

### 5.7 `modelo/ConversionResponse.java`

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

### 5.8 `modelo/SesionUsuario.java`

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

### 5.9 `vista/FormatoNumero.java`

```java
package ec.edu.monster.vista;

import ec.edu.monster.controlador.Configuracion;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class FormatoNumero {

    private static final DecimalFormat FORMATO = crearFormato();

    private FormatoNumero() {
    }

    // Hasta Configuracion.DECIMALES decimales, sin ceros sobrantes y con punto decimal
    private static DecimalFormat crearFormato() {
        DecimalFormat formato = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.US));
        formato.setMaximumFractionDigits(Configuracion.DECIMALES);
        return formato;
    }

    public static String formatear(double numero) {
        return FORMATO.format(numero);
    }

    // "KILOMETRO" -> "Kilometro"
    public static String nombreVisible(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            return "";
        }
        return codigo.substring(0, 1) + codigo.substring(1).toLowerCase(Locale.ROOT);
    }

    // "Kilometro" -> "KILOMETRO"
    public static String codigo(String nombreVisible) {
        return nombreVisible.trim().toUpperCase(Locale.ROOT);
    }
}
```

### 5.10 `vista/Estilos.java`

`BasicButtonUI` hace que el botón respete el color de fondo (el look and feel de Windows lo ignora). Los colores son los mismos hexadecimales del cliente Tkinter.

```java
package ec.edu.monster.vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicButtonUI;

// Colores y fuentes del cliente de escritorio original (Tkinter)
public final class Estilos {

    // Login
    public static final Color FONDO_LOGIN = new Color(0xE8F4F8);
    public static final Color TITULO_LOGIN = new Color(0x1A5490);
    public static final Color BOTON_LOGIN = new Color(0x4A90D9);
    public static final Color BOTON_LOGIN_HOVER = new Color(0x3A7AC0);
    public static final Color BOTON_CANCELAR = new Color(0x87CEEB);
    public static final Color BOTON_CANCELAR_HOVER = new Color(0x6FB9D9);
    public static final Color TEXTO_CAMPO = new Color(0x333333);

    // Calculadora
    public static final Color ENCABEZADO = new Color(0x1976D2);
    public static final Color TITULO_SECCION = new Color(0x64B5F6);
    public static final Color ETIQUETA = new Color(0x1565C0);
    public static final Color SEPARADOR = new Color(0x42A5F5);
    public static final Color BOTON_CONVERTIR = new Color(0x1976D2);
    public static final Color BOTON_CONVERTIR_HOVER = new Color(0x1565C0);
    public static final Color BOTON_LIMPIAR = new Color(0xFF9800);
    public static final Color BOTON_LIMPIAR_HOVER = new Color(0xF57C00);
    public static final Color BOTON_SESION = new Color(0x455A64);
    public static final Color BOTON_SESION_HOVER = new Color(0x37474F);
    public static final Color FONDO_RESULTADO = new Color(0xC8E6C9);
    public static final Color BORDE_RESULTADO = new Color(0x81C784);
    public static final Color TITULO_RESULTADO = new Color(0x2E7D32);
    public static final Color VALOR_RESULTADO = new Color(0x1B5E20);

    // Barra de estado
    public static final Color ESTADO_INFO = new Color(0x2196F3);
    public static final Color ESTADO_EXITO = new Color(0x4CAF50);
    public static final Color ESTADO_ERROR = new Color(0xF44336);
    public static final Color ESTADO_AVISO = new Color(0xFF9800);

    public static final String FUENTE = "Segoe UI";

    private Estilos() {
    }

    public static Font fuente(int estilo, int tamanio) {
        return new Font(FUENTE, estilo, tamanio);
    }

    public static JLabel etiqueta(String texto, int estilo, int tamanio, Color color) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(fuente(estilo, tamanio));
        etiqueta.setForeground(color);
        return etiqueta;
    }

    // Boton plano con color propio y efecto hover (BasicButtonUI respeta el color de fondo)
    public static JButton boton(String texto, Color fondo, Color hover, int tamanio, Dimension medida) {
        JButton boton = new JButton(texto);
        boton.setUI(new BasicButtonUI());
        boton.setFont(fuente(Font.BOLD, tamanio));
        boton.setForeground(Color.WHITE);
        boton.setBackground(fondo);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (medida != null) {
            boton.setPreferredSize(medida);
            boton.setMaximumSize(medida);
        }
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(fondo);
            }
        });
        return boton;
    }

    // Imagen de src/main/resources/imagenes (null si no existe)
    public static Image imagen(String nombre) {
        URL url = Estilos.class.getResource("/imagenes/" + nombre);
        return url == null ? null : new ImageIcon(url).getImage();
    }
}
```

### 5.11 `vista/PanelFondo.java` y `vista/TareaApi.java`

```java
package ec.edu.monster.vista;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import javax.swing.JPanel;

// Panel que dibuja una imagen estirada como fondo (fondoes.jpg)
public class PanelFondo extends JPanel {

    private final Image fondo;

    public PanelFondo(Image fondo, LayoutManager layout) {
        super(layout);
        this.fondo = fondo;
        setBackground(new Color(0xF0F0F0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fondo != null) {
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
```

`TareaApi` evita que la ventana se congele mientras espera al servidor (hasta 10 s si está apagado) y pone el cursor de espera.

```java
package ec.edu.monster.vista;

import ec.edu.monster.modelo.ExcepcionApi;

import java.awt.Component;
import java.awt.Cursor;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.SwingWorker;

// Ejecuta una llamada al servidor fuera del hilo de Swing para que la ventana no se congele
public final class TareaApi {

    public interface Accion<T> {
        T ejecutar() throws ExcepcionApi;
    }

    private TareaApi() {
    }

    public static <T> void ejecutar(Component ventana, Accion<T> accion,
                                    Consumer<T> alTerminar, Consumer<ExcepcionApi> alFallar) {
        ventana.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return accion.ejecutar();
            }

            @Override
            protected void done() {
                ventana.setCursor(Cursor.getDefaultCursor());
                T resultado;
                try {
                    resultado = get();
                } catch (ExecutionException e) {
                    Throwable causa = e.getCause();
                    alFallar.accept(causa instanceof ExcepcionApi
                            ? (ExcepcionApi) causa
                            : new ExcepcionApi("RED", "Error: " + causa.getMessage()));
                    return;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                alTerminar.accept(resultado);
            }
        }.execute();
    }
}
```

### 5.12 `vista/AplicacionEscritorio.java` y `vista/LoginFrame.java`

```java
package ec.edu.monster.vista;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class AplicacionEscritorio {

    public static void main(String[] args) {
        try {
            // Apariencia de Windows para campos y combos; los botones de color usan su propio estilo
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("No se pudo aplicar el look and feel del sistema: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
```

```java
package ec.edu.monster.vista;

import ec.edu.monster.controlador.ApiService;
import ec.edu.monster.modelo.ExcepcionApi;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class LoginFrame extends JFrame {

    private final ApiService apiService = new ApiService();

    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnLogin;

    public LoginFrame() {
        this(null);
    }

    // mensaje: aviso a mostrar al abrir (por ejemplo, sesion expirada)
    public LoginFrame(String mensaje) {
        setTitle("Login - Sistema de Conversión de Unidades");
        setSize(480, 580);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        crearInterfaz();

        if (mensaje != null) {
            addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowOpened(java.awt.event.WindowEvent e) {
                    JOptionPane.showMessageDialog(LoginFrame.this, mensaje,
                            "Sesión expirada", JOptionPane.WARNING_MESSAGE);
                }
            });
        }
    }

    private void crearInterfaz() {
        JPanel principal = new JPanel();
        principal.setLayout(new BoxLayout(principal, BoxLayout.Y_AXIS));
        principal.setBackground(Estilos.FONDO_LOGIN);
        principal.setBorder(BorderFactory.createEmptyBorder(25, 20, 20, 20));

        // Logo circular
        Image logo = Estilos.imagen("logo_login.png");
        if (logo != null) {
            agregarCentrado(principal, new JLabel(new ImageIcon(crearImagenCircular(logo, 150))));
            principal.add(Box.createVerticalStrut(15));
        }

        agregarCentrado(principal, Estilos.etiqueta("Sistema de Conversión de Unidades", Font.BOLD, 16, Estilos.TITULO_LOGIN));
        principal.add(Box.createVerticalStrut(10));

        JPanel separador = new JPanel();
        separador.setBackground(Estilos.BOTON_LOGIN);
        separador.setMaximumSize(new Dimension(200, 3));
        separador.setPreferredSize(new Dimension(200, 3));
        agregarCentrado(principal, separador);
        principal.add(Box.createVerticalStrut(12));

        agregarCentrado(principal, Estilos.etiqueta("Iniciar Sesión", Font.BOLD, 14, Estilos.TITULO_LOGIN));
        principal.add(Box.createVerticalStrut(15));

        txtUsuario = new JTextField();
        agregarCampo(principal, "Usuario:", txtUsuario);
        principal.add(Box.createVerticalStrut(10));

        txtContrasena = new JPasswordField();
        txtContrasena.setEchoChar('●');
        agregarCampo(principal, "Contraseña:", txtContrasena);
        principal.add(Box.createVerticalStrut(20));

        Dimension medidaBoton = new Dimension(300, 45);
        btnLogin = Estilos.boton("Iniciar Sesión", Estilos.BOTON_LOGIN, Estilos.BOTON_LOGIN_HOVER, 14, medidaBoton);
        btnLogin.addActionListener(e -> validarLogin());
        agregarCentrado(principal, btnLogin);
        principal.add(Box.createVerticalStrut(8));

        JButton btnCancelar = Estilos.boton("Cancelar", Estilos.BOTON_CANCELAR, Estilos.BOTON_CANCELAR_HOVER, 12, medidaBoton);
        btnCancelar.addActionListener(e -> System.exit(0));
        agregarCentrado(principal, btnCancelar);

        // Enter en cualquier campo = Iniciar Sesion
        getRootPane().setDefaultButton(btnLogin);

        setContentPane(principal);
    }

    private void agregarCampo(JPanel panel, String texto, JTextField campo) {
        JLabel etiqueta = Estilos.etiqueta(texto, Font.BOLD, 11, Estilos.TITULO_LOGIN);
        etiqueta.setAlignmentX(Component.CENTER_ALIGNMENT);
        etiqueta.setMaximumSize(new Dimension(300, 20));
        panel.add(etiqueta);
        panel.add(Box.createVerticalStrut(4));

        campo.setFont(Estilos.fuente(Font.PLAIN, 11));
        campo.setForeground(Estilos.TEXTO_CAMPO);
        campo.setMaximumSize(new Dimension(300, 30));
        campo.setPreferredSize(new Dimension(300, 30));
        campo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(campo);
    }

    private void agregarCentrado(JPanel panel, javax.swing.JComponent componente) {
        componente.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(componente);
    }

    private Image crearImagenCircular(Image original, int tamanio) {
        BufferedImage circular = new BufferedImage(tamanio, tamanio, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = circular.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setClip(new Ellipse2D.Float(0, 0, tamanio, tamanio));
        g.drawImage(original, 0, 0, tamanio, tamanio, null);
        g.setClip(null);
        g.setColor(Estilos.BOTON_LOGIN);
        g.setStroke(new BasicStroke(3));
        g.draw(new Ellipse2D.Float(1.5f, 1.5f, tamanio - 3, tamanio - 3));
        g.dispose();
        return circular;
    }

    // CU-01 Autenticarse: el servidor valida y entrega el token
    private void validarLogin() {
        String usuario = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword()).trim();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese usuario y contraseña",
                    "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnLogin.setEnabled(false);
        TareaApi.ejecutar(this,
                () -> apiService.login(usuario, contrasena),
                token -> {
                    JOptionPane.showMessageDialog(this, "¡Bienvenido " + usuario + "!",
                            "Login Exitoso", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new ConversorFrame(usuario).setVisible(true);
                },
                this::mostrarError);
    }

    private void mostrarError(ExcepcionApi e) {
        btnLogin.setEnabled(true);
        if (ApiService.CREDENCIALES_INCORRECTAS.equals(e.getCodigo())) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
            txtContrasena.setText("");
            txtUsuario.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    }
}
```

### 5.13 `vista/ConversorFrame.java`

```java
package ec.edu.monster.vista;

import ec.edu.monster.controlador.ApiService;
import ec.edu.monster.controlador.Configuracion;
import ec.edu.monster.modelo.ConversionRequest;
import ec.edu.monster.modelo.ExcepcionApi;
import ec.edu.monster.modelo.SesionUsuario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class ConversorFrame extends JFrame {

    private static final int ANCHO = 650;
    private static final int ALTO = 730;

    private final ApiService apiService = new ApiService();
    private final String usuario;

    // Catalogo que envia el servidor (CU-03), por categoria
    private final Map<String, List<String>> unidadesPorCategoria = new HashMap<>();

    private JComboBox<String> cmbCategoria;
    private JComboBox<String> cmbOrigen;
    private JComboBox<String> cmbDestino;
    private JTextField txtValor;
    private JButton btnConvertir;
    private JLabel lblResultado;
    private JLabel lblEstado;

    public ConversorFrame(String usuario) {
        this.usuario = usuario;

        setTitle("Calculadora de Conversión de Unidades - Usuario: " + usuario);
        setSize(ANCHO, ALTO);
        setResizable(false);
        setLocationRelativeTo(null);

        // Confirmar antes de salir
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salir();
            }
        });

        crearInterfaz();
        cargarCategorias();
    }

    // ------------------------------------------------------------------
    // Interfaz
    // ------------------------------------------------------------------
    private void crearInterfaz() {
        PanelFondo fondo = new PanelFondo(Estilos.imagen("fondoes.jpg"), new BorderLayout());
        fondo.add(crearEncabezado(), BorderLayout.NORTH);
        fondo.add(crearFormulario(), BorderLayout.CENTER);
        fondo.add(crearBarraEstado(), BorderLayout.SOUTH);
        setContentPane(fondo);
    }

    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(Estilos.ENCABEZADO);
        encabezado.setPreferredSize(new Dimension(ANCHO, 60));
        encabezado.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        encabezado.add(Estilos.etiqueta("Calculadora de Conversión de Unidades", Font.BOLD, 15, Color.WHITE),
                BorderLayout.WEST);

        JPanel panelUsuario = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        panelUsuario.setOpaque(false);
        panelUsuario.add(Estilos.etiqueta(usuario, Font.BOLD, 11, Color.WHITE));

        // El taller no pide cerrar sesion: el boton solo aparece si se activa en Configuracion
        if (Configuracion.PERMITIR_CERRAR_SESION) {
            JButton btnCerrarSesion = Estilos.boton("Cerrar sesión", Estilos.BOTON_SESION,
                    Estilos.BOTON_SESION_HOVER, 11, new Dimension(115, 30));
            btnCerrarSesion.addActionListener(e -> cerrarSesion());
            panelUsuario.add(btnCerrarSesion);
        }
        encabezado.add(panelUsuario, BorderLayout.EAST);
        return encabezado;
    }

    private JPanel crearFormulario() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setOpaque(false);
        contenedor.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));

        JPanel principal = new JPanel();
        principal.setLayout(new BoxLayout(principal, BoxLayout.Y_AXIS));
        principal.setBackground(Color.WHITE);
        principal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(15, 15, 12, 15)));

        // Titulo de seccion
        JLabel titulo = Estilos.etiqueta("Selecciona el tipo de conversión", Font.BOLD, 13, Color.WHITE);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel barraTitulo = new JPanel(new BorderLayout());
        barraTitulo.setBackground(Estilos.TITULO_SECCION);
        barraTitulo.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        barraTitulo.add(titulo);
        agregar(principal, barraTitulo, 38);
        principal.add(Box.createVerticalStrut(10));

        // Categoria, origen y destino (CU-02 y CU-03)
        cmbCategoria = new JComboBox<>();
        cmbCategoria.addActionListener(e -> alCambiarCategoria());
        cmbOrigen = new JComboBox<>();
        cmbDestino = new JComboBox<>();
        agregarFila(principal, "Categoría:", cmbCategoria);
        agregarFila(principal, "Origen:", cmbOrigen);
        agregarFila(principal, "Destino:", cmbDestino);

        // Linea separadora
        principal.add(Box.createVerticalStrut(10));
        JPanel separador = new JPanel();
        separador.setBackground(Estilos.SEPARADOR);
        agregar(principal, separador, 3);
        principal.add(Box.createVerticalStrut(10));

        // Valor a convertir
        txtValor = new JTextField();
        txtValor.addActionListener(e -> convertir());
        agregarFila(principal, "Valor a convertir:", txtValor);

        // Botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        botones.setOpaque(false);
        btnConvertir = Estilos.boton("CONVERTIR", Estilos.BOTON_CONVERTIR, Estilos.BOTON_CONVERTIR_HOVER,
                13, new Dimension(210, 48));
        btnConvertir.addActionListener(e -> convertir());
        JButton btnLimpiar = Estilos.boton("LIMPIAR", Estilos.BOTON_LIMPIAR, Estilos.BOTON_LIMPIAR_HOVER,
                13, new Dimension(210, 48));
        btnLimpiar.addActionListener(e -> limpiar());
        botones.add(btnConvertir);
        botones.add(btnLimpiar);
        principal.add(Box.createVerticalStrut(12));
        agregar(principal, botones, 48);
        principal.add(Box.createVerticalStrut(12));

        // Resultado
        JPanel panelResultado = new JPanel(new BorderLayout(0, 4));
        panelResultado.setBackground(Estilos.FONDO_RESULTADO);
        panelResultado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilos.BORDE_RESULTADO, 3),
                BorderFactory.createEmptyBorder(10, 10, 12, 10)));
        JLabel tituloResultado = Estilos.etiqueta("Resultado:", Font.BOLD, 12, Estilos.TITULO_RESULTADO);
        tituloResultado.setHorizontalAlignment(SwingConstants.CENTER);
        lblResultado = Estilos.etiqueta("---", Font.BOLD, 18, Estilos.VALOR_RESULTADO);
        lblResultado.setHorizontalAlignment(SwingConstants.CENTER);
        panelResultado.add(tituloResultado, BorderLayout.NORTH);
        panelResultado.add(lblResultado, BorderLayout.CENTER);
        agregar(principal, panelResultado, 85);

        contenedor.add(principal, BorderLayout.NORTH);
        return contenedor;
    }

    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);
        barra.setBorder(BorderFactory.createEmptyBorder(0, 25, 20, 25));

        lblEstado = Estilos.etiqueta("Verificando conexión...", Font.BOLD, 11, new Color(0x666666));
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel caja = new JPanel(new BorderLayout());
        caja.setBackground(Color.WHITE);
        caja.setBorder(BorderFactory.createRaisedBevelBorder());
        caja.setPreferredSize(new Dimension(ANCHO - 50, 50));
        caja.add(lblEstado);
        barra.add(caja);
        return barra;
    }

    private void agregarFila(JPanel panel, String texto, Component campo) {
        JPanel fila = new JPanel(new BorderLayout(10, 0));
        fila.setOpaque(false);
        JLabel etiqueta = Estilos.etiqueta(texto, Font.BOLD, 11, Estilos.ETIQUETA);
        etiqueta.setPreferredSize(new Dimension(140, 30));
        campo.setFont(Estilos.fuente(Font.PLAIN, 12));
        fila.add(etiqueta, BorderLayout.WEST);
        fila.add(campo, BorderLayout.CENTER);
        panel.add(Box.createVerticalStrut(8));
        agregar(panel, fila, 32);
    }

    private void agregar(JPanel panel, javax.swing.JComponent componente, int alto) {
        componente.setAlignmentX(Component.CENTER_ALIGNMENT);
        componente.setMaximumSize(new Dimension(Integer.MAX_VALUE, alto));
        componente.setPreferredSize(new Dimension(componente.getPreferredSize().width, alto));
        panel.add(componente);
    }

    // ------------------------------------------------------------------
    // Comunicacion con el servidor
    // ------------------------------------------------------------------
    // CU-02 Consultar catalogo
    private void cargarCategorias() {
        TareaApi.ejecutar(this,
                apiService::obtenerCategorias,
                categorias -> {
                    cmbCategoria.removeAllItems();
                    for (String categoria : categorias) {
                        cmbCategoria.addItem(FormatoNumero.nombreVisible(categoria));
                    }
                    actualizarEstado("Servidor disponible", Estilos.ESTADO_EXITO);
                },
                this::procesarError);
    }

    // CU-03 Consultar unidades por categoria
    private void alCambiarCategoria() {
        Object seleccion = cmbCategoria.getSelectedItem();
        if (seleccion == null) {
            return;
        }
        String categoria = FormatoNumero.codigo(seleccion.toString());
        lblResultado.setText("---");

        if (unidadesPorCategoria.containsKey(categoria)) {
            mostrarUnidades(unidadesPorCategoria.get(categoria));
            return;
        }
        TareaApi.ejecutar(this,
                () -> apiService.obtenerUnidades(categoria),
                unidades -> {
                    unidadesPorCategoria.put(categoria, unidades);
                    mostrarUnidades(unidades);
                },
                this::procesarError);
    }

    private void mostrarUnidades(List<String> unidades) {
        List<String> nombres = new ArrayList<>();
        for (String unidad : unidades) {
            nombres.add(FormatoNumero.nombreVisible(unidad));
        }
        cmbOrigen.removeAllItems();
        cmbDestino.removeAllItems();
        for (String nombre : nombres) {
            cmbOrigen.addItem(nombre);
            cmbDestino.addItem(nombre);
        }
        if (nombres.size() > 1) {
            cmbDestino.setSelectedIndex(1);
        }
    }

    // CU-04 Realizar conversion
    private void convertir() {
        if (cmbCategoria.getSelectedItem() == null) {
            avisar("Selección requerida", "Por favor, seleccione una categoría");
            return;
        }
        if (cmbOrigen.getSelectedItem() == null || cmbDestino.getSelectedItem() == null) {
            avisar("Selección requerida", "Por favor, seleccione las unidades de origen y destino");
            return;
        }

        String valorTexto = txtValor.getText().trim().replace(',', '.');
        if (valorTexto.isEmpty()) {
            avisar("Valor requerido", "Por favor, ingrese un valor a convertir");
            txtValor.requestFocus();
            return;
        }

        double valor;
        try {
            valor = Double.parseDouble(valorTexto);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un número válido",
                    "Valor inválido", JOptionPane.ERROR_MESSAGE);
            txtValor.requestFocus();
            return;
        }

        ConversionRequest request = new ConversionRequest(
                FormatoNumero.codigo(cmbCategoria.getSelectedItem().toString()),
                FormatoNumero.codigo(cmbOrigen.getSelectedItem().toString()),
                FormatoNumero.codigo(cmbDestino.getSelectedItem().toString()),
                valor);

        btnConvertir.setEnabled(false);
        TareaApi.ejecutar(this,
                () -> apiService.realizarConversion(request),
                respuesta -> {
                    btnConvertir.setEnabled(true);
                    lblResultado.setText(FormatoNumero.formatear(valor) + " "
                            + FormatoNumero.nombreVisible(respuesta.getUnidadOrigen()) + " = "
                            + FormatoNumero.formatear(respuesta.getResultado()) + " "
                            + FormatoNumero.nombreVisible(respuesta.getUnidadDestino()));
                    actualizarEstado(respuesta.getMensaje(), Estilos.ESTADO_EXITO);
                },
                error -> {
                    btnConvertir.setEnabled(true);
                    procesarError(error);
                });
    }

    // CU-08 a CU-11: muestra el mensaje del servidor y reacciona segun el codigo
    private void procesarError(ExcepcionApi e) {
        if (e.esErrorDeSesion()) {
            dispose();
            new LoginFrame(e.getMessage() + "\n\nVuelva a iniciar sesión.").setVisible(true);
            return;
        }

        lblResultado.setText("---");
        if (e.esErrorDeRed()) {
            actualizarEstado("Servidor no disponible", Estilos.ESTADO_AVISO);
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error de conexión", JOptionPane.ERROR_MESSAGE);
        } else {
            actualizarEstado("Error " + e.getCodigo(), Estilos.ESTADO_ERROR);
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error " + e.getCodigo(), JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------------------------------------------------------
    // Acciones de la ventana
    // ------------------------------------------------------------------
    private void limpiar() {
        if (cmbCategoria.getItemCount() > 0) {
            cmbCategoria.setSelectedIndex(0);
        }
        txtValor.setText("");
        lblResultado.setText("---");
        txtValor.requestFocus();

        // Verificar conexion nuevamente (solo informativo)
        TareaApi.ejecutar(this,
                () -> apiService.verificarConexion(),
                conectado -> {
                    if (conectado) {
                        actualizarEstado("Servidor disponible", Estilos.ESTADO_EXITO);
                    } else {
                        actualizarEstado("Servidor no disponible", Estilos.ESTADO_AVISO);
                    }
                },
                this::procesarError);
    }

    private void actualizarEstado(String mensaje, Color color) {
        lblEstado.setText(mensaje);
        lblEstado.setForeground(color);
    }

    private void avisar(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.WARNING_MESSAGE);
    }

    // Solo se usa si Configuracion.PERMITIR_CERRAR_SESION = true
    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Desea cerrar la sesión de " + usuario + "?", "Cerrar Sesión", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            SesionUsuario.cerrar();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private void salir() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Desea salir de la aplicación?", "Salir", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
```

### 5.14 `ejecutar.bat`

```bat
@echo off
cd /d "%~dp0"

if not exist "target\CONUNI_CLIENTE_ESCRITORIO-1.0-jar-with-dependencies.jar" (
    echo Compilando el proyecto...
    call mvn -q package
)

REM Para usar el servidor en la misma PC:
REM start "" javaw -Dconuni.url=http://localhost:5014/api/conversor -jar target\CONUNI_CLIENTE_ESCRITORIO-1.0-jar-with-dependencies.jar
start "" javaw -jar target\CONUNI_CLIENTE_ESCRITORIO-1.0-jar-with-dependencies.jar
```

### 5.15 `README.md` (reemplaza al actual)

````markdown
# Cliente de escritorio — Conversión de Unidades (Java Swing)

Aplicación Java Swing que consume la API REST .NET `WS_ConUnid_DOTNET_RESTFUL_GR02`.
El cliente **no** calcula conversiones: autentica, pide el catálogo y envía cada conversión al servidor.

## Requisitos
- JDK 17 o superior y Maven (NetBeans ya trae Maven).
- Servidor encendido en `http://192.168.137.1:5014` (API en `/api/conversor`).

## Ejecutar
- NetBeans: File > Open Project > `CONUNI_CLIENTE_ESCRITORIO` > Run.
- Doble clic en `ejecutar.bat`, o `mvn package` y
  `java -jar target/CONUNI_CLIENTE_ESCRITORIO-1.0-jar-with-dependencies.jar`.
- Otro servidor: `java -Dconuni.url=http://localhost:5014/api/conversor -jar ...`

## Credenciales
`MONSTER` / `Monster9` (las valida el servidor).

## Uso
1. Iniciar sesión.
2. Elegir categoría, unidad de origen y unidad de destino (las envía el servidor).
3. Escribir el valor (acepta coma o punto) y pulsar CONVERTIR.

## Imágenes
`logo_login.png` y `fondoes.jpg` van en `src/main/resources/imagenes/`. Son opcionales: sin ellas la ventana abre sin logo ni fondo.

## Configuración (`controlador/Configuracion.java`)
- `URL_BASE`: `http://192.168.137.1:5014/api/conversor` (o `-Dconuni.url=...`).
- `DECIMALES`: decimales máximos del resultado (4).
- `PERMITIR_CERRAR_SESION`: muestra el botón "Cerrar sesión" (apagado; el taller no lo pide).
````

### 5.16 `.gitignore`

```gitignore
target/
nbproject/private/
nbactions.xml
*.class
```

---

## 6. Casos de uso del taller en este cliente

| CU | Dónde se cumple | Cómo se ve |
|---|---|---|
| CU-01 Autenticarse | `LoginFrame.validarLogin` → `ApiService.login` | "Login Exitoso / ¡Bienvenido MONSTER!" o "Error de Autenticación / Credenciales incorrectas" |
| CU-02 Consultar catálogo | `ConversorFrame.cargarCategorias` | Combo "Categoría": Longitud, Masa, Temperatura; estado verde "Servidor disponible" |
| CU-03 Consultar unidades | `ConversorFrame.alCambiarCategoria` (guarda cada lista para no repetir la petición) | Combos "Origen" y "Destino" |
| CU-04 Realizar conversión | `ConversorFrame.convertir` | `25 Celsius = 77 Fahrenheit` en el marco verde |
| CU-05 Validar token | Servidor; el cliente envía `Authorization: Bearer` en `ApiService.enviar` | — |
| CU-06 Validar datos | Servidor; el cliente solo valida selección, vacío y número | "Selección requerida", "Valor requerido", "Valor inválido" |
| CU-07 Generar respuesta | Servidor; el cliente muestra `mensaje` en la barra de estado | "Conversion realizada correctamente" en verde |
| CU-08 Token inválido | `procesarError` con `AUTH-001` | Se cierra la calculadora; login con aviso "Sesión expirada" |
| CU-09 Sesión expirada | `procesarError` con `AUTH-002` | Igual |
| CU-10 Unidad no soportada | `procesarError` con `UNIT-001` | Diálogo "Error UNIT-001" + mensaje; estado rojo |
| CU-11 Datos inconsistentes | `procesarError` con `DATA-001` | Diálogo "Error DATA-001" + mensaje; estado rojo |

---

## 7. Flujo de ventanas

```
AplicacionEscritorio.main  (look and feel de Windows)
 └─ LoginFrame ── Cancelar / X ──> fin
       └─ POST /login → token en SesionUsuario
           └─ ConversorFrame
                 ├─ al abrir: GET /categorias
                 ├─ elegir categoría: GET /categorias/{c}/unidades
                 ├─ CONVERTIR / Enter en el valor: POST /convertir → resultado o diálogo de error
                 ├─ LIMPIAR: reinicia el formulario y verifica la conexión
                 ├─ [Cerrar sesión]  solo si PERMITIR_CERRAR_SESION ──┐
                 ├─ AUTH-001 / AUTH-002 ──────────────────────────────┼─> LoginFrame
                 └─ X → "¿Desea salir de la aplicación?" → fin        │
                                                                      ┘
```

---

## 8. Configuración, ejecución y verificación

### 8.1 Creación del proyecto

1. Crea `04. CLIESC/CONUNI_CLIENTE_ESCRITORIO/` con los archivos de la sección 5 (en NetBeans: New Project > Java with Maven > Java Application, `groupId` `ec.edu.monster`, `artifactId` `CONUNI_CLIENTE_ESCRITORIO`, y reemplaza el `pom.xml` y las clases).
2. Pon `logo_login.png` y `fondoes.jpg` en `src/main/resources/imagenes/` (en este proyecto ya están). Sin ellas la ventana funciona igual, sin logo ni fondo.
3. Si en `04. CLIESC/` hubiera archivos del cliente Python (sección 2.1), elimínalos o muévelos a `_respaldo_python/` fuera de la entrega. En este proyecto no los hay.
4. Escribe `README.md` (sección 5.15).

### 8.2 Ejecutar

- NetBeans: Run (F6).
- `ejecutar.bat` (abre la ventana sin consola con `javaw`).
- `mvn package` y `java -jar target\CONUNI_CLIENTE_ESCRITORIO-1.0-jar-with-dependencies.jar`.

### 8.3 Pruebas realizadas contra el servidor GR02

El código de la sección 5 se compiló con Maven 3.9.16 y JDK 21 (`release 17`) y el 2026-10-25 se probó contra `04. SERVIDOR` (`http://localhost:5014/api/conversor`), con logo y fondo, con el arnés del anexo A, que llena los campos, pulsa los botones, lee los diálogos y guarda capturas. Terminó con `TODAS LAS PRUEBAS OK` (pruebas 1–11). Las pruebas 12–14 vienen de la versión original de la guía y **no se repitieron** contra GR02; no dependen de rutas ni JSON:

| # | Acción | Resultado obtenido |
|---|---|---|
| 1 | Login con campos vacíos | "Campos vacíos / Por favor, ingrese usuario y contraseña" |
| 2 | Login `MONSTER` / `mala` | "Error de Autenticación / Credenciales incorrectas" |
| 3 | Login `MONSTER` / `Monster9` | "Login Exitoso / ¡Bienvenido MONSTER!" y se abre la calculadora |
| 4 | Combo Categoría | `[Longitud, Masa, Temperatura]`; sin botón "Cerrar sesión" |
| 5 | Temperatura | Origen/Destino `[Celsius, Fahrenheit, Kelvin]` |
| 6 | Celsius → Fahrenheit, 25 | `25 Celsius = 77 Fahrenheit`; estado "Conversion realizada correctamente" |
| 7 | Longitud, Kilometro → Milla, `1,5` | `1.5 Kilometro = 0.9321 Milla` (acepta coma) |
| 8 | Valor `abc` | "Valor inválido / Por favor, ingrese un número válido" |
| 9 | Valor vacío | "Valor requerido / Por favor, ingrese un valor a convertir" |
| 10 | LIMPIAR | Resultado vuelve a `---` |
| 11 | Token inválido y CONVERTIR | Se cierra la calculadora; login con "Sesión expirada / Token invalido o sesion expirada. Vuelva a iniciar sesión." |
| 12 | `PERMITIR_CERRAR_SESION = true` | Aparece el botón "Cerrar sesión" `#455A64` en el encabezado |
| 13 | `ejecutar.bat` | Abre la ventana de login con `javaw` (sin consola) |
| 14 | Servidor apagado, login | "Error de conexión / No se puede conectar con el servidor en ..." |

Pruebas manuales que quedan para hacer en la PC de entrega:

| # | Acción | Esperado |
|---|---|---|
| 15 | Pasar el mouse por los botones | Cambian al color hover |
| 16 | X en la calculadora | "Salir / ¿Desea salir de la aplicación?"; Sí cierra, No se queda |

Así se ven las ventanas (capturas del arnés):

- Login: logo circular de Sullivan con borde azul (si existe `logo_login.png`), título, separador, campos y los dos botones azules.
- Calculadora: encabezado azul con el usuario a la derecha, marco blanco sobre `fondoes.jpg` (gris claro si no existe), tres combos, separador, valor, CONVERTIR azul y LIMPIAR naranja, marco verde con el resultado y la barra de estado blanca abajo.

### 8.4 Extras configurables

| Extra | Cómo activarlo | Efecto |
|---|---|---|
| Decimales | `Configuracion.DECIMALES = 2` | `0.62` en lugar de `0.6214` |
| Cerrar sesión | `Configuracion.PERMITIR_CERRAR_SESION = true` | Botón en el encabezado con confirmación; borra el token y vuelve al login. El servidor no tiene logout: el token vence solo a los 30 min |

---

## 9. Decisiones tomadas

| # | Punto | Decisión |
|---|---|---|
| 1 | Lenguaje | Java Swing, como dice el `.docx` |
| 2 | Toneladas | El `.docx` no las incluye. El catálogo sale del servidor |
| 3 | Cerrar sesión | El `.docx` no lo pide y el servidor no tiene endpoint. Desactivado con `PERMITIR_CERRAR_SESION = false`; `cerrarSesion()` ya está escrito |
| 4 | Decimales | El `.docx` no lo especifica. Extra configurable en `DECIMALES`, por defecto 4 (antes Tkinter usaba 2) |
| 5 | Origen y destino | El `.docx` pide elegir unidad de origen y destino; el combo de pares fijos se reemplazó por dos combos. Por eso la ventana pasa de 680 a 730 px de alto |
| 6 | Emojis | Se quitaron de las ventanas Swing porque Windows los dibuja como cuadros |
| 7 | Estructura | 3 carpetas `controlador`/`modelo`/`vista` y paquete `ec.edu.monster`, como el servidor base |

---

## Anexo A. Arnés de prueba automática (opcional)

No forma parte de la entrega. Sirve para repetir las pruebas 1–11 de la sección 8.3 sin tocar la ventana. Guárdalo fuera del proyecto, por ejemplo en `prueba/PruebaEscritorio.java`, con el servidor corriendo en la misma PC:

```powershell
cd prueba
$JAR = "..\CONUNI_CLIENTE_ESCRITORIO\target\CONUNI_CLIENTE_ESCRITORIO-1.0-jar-with-dependencies.jar"
javac -encoding UTF-8 -cp $JAR PruebaEscritorio.java
mkdir capturas
java -Dconuni.url=http://localhost:5014/api/conversor -cp "$JAR;." PruebaEscritorio capturas
```

Debe terminar con `TODAS LAS PRUEBAS OK` y dejar 4 capturas en `capturas/`.

```java
import ec.edu.monster.modelo.SesionUsuario;
import ec.edu.monster.vista.AplicacionEscritorio;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

// Arnes de prueba: maneja las ventanas Swing sin intervencion y guarda capturas
public class PruebaEscritorio {

    static int fallos = 0;
    static String carpeta;

    public static void main(String[] args) {
        try {
            probar(args);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(99);
        }
    }

    static void probar(String[] args) throws Exception {
        carpeta = args[0];
        AplicacionEscritorio.main(new String[0]);

        JFrame login = esperarFrame("LoginFrame");
        Thread.sleep(800);
        captura(login, "01_login.png");

        // Campos vacios
        clic(login, "Iniciar Sesión");
        verificar(cerrarDialogo(), "Campos vacíos");

        // Credenciales incorrectas -> AUTH-003
        escribir(login, 0, "MONSTER");
        escribirClave(login, "mala");
        clic(login, "Iniciar Sesión");
        verificar(cerrarDialogo(), "Credenciales incorrectas");

        // Login correcto
        escribirClave(login, "Monster9");
        clic(login, "Iniciar Sesión");
        verificar(cerrarDialogo(), "¡Bienvenido MONSTER!");

        JFrame conversor = esperarFrame("ConversorFrame");
        Thread.sleep(2000);
        @SuppressWarnings("unchecked") List<JComboBox<Object>> combos = (List<JComboBox<Object>>) (List<?>) buscar(conversor, JComboBox.class);
        System.out.println("Categorias: " + items(combos.get(0)));
        verificar(String.valueOf(combos.get(0).getItemCount()), "3");
        verificar(String.valueOf(buscarBotones(conversor).stream().anyMatch(b -> b.getText().contains("sesión"))), "false");
        captura(conversor, "02_conversor.png");

        // Temperatura: 25 Celsius -> Fahrenheit
        enEdt(() -> combos.get(0).setSelectedItem("Temperatura"));
        Thread.sleep(1500);
        System.out.println("Unidades Temperatura: " + items(combos.get(1)));
        enEdt(() -> { combos.get(1).setSelectedItem("Celsius"); combos.get(2).setSelectedItem("Fahrenheit"); });
        escribir(conversor, 0, "25");
        clic(conversor, "CONVERTIR");
        Thread.sleep(1500);
        verificar(resultado(conversor), "25 Celsius = 77 Fahrenheit");
        captura(conversor, "03_resultado.png");

        // Longitud: 1,5 Kilometro -> Milla (coma decimal, 4 decimales)
        enEdt(() -> combos.get(0).setSelectedItem("Longitud"));
        Thread.sleep(1500);
        enEdt(() -> { combos.get(1).setSelectedItem("Kilometro"); combos.get(2).setSelectedItem("Milla"); });
        escribir(conversor, 0, "1,5");
        clic(conversor, "CONVERTIR");
        Thread.sleep(1500);
        verificar(resultado(conversor), "1.5 Kilometro = 0.9321 Milla");

        // Valor invalido y valor vacio
        escribir(conversor, 0, "abc");
        clic(conversor, "CONVERTIR");
        verificar(cerrarDialogo(), "Por favor, ingrese un número válido");
        escribir(conversor, 0, "");
        clic(conversor, "CONVERTIR");
        verificar(cerrarDialogo(), "Por favor, ingrese un valor a convertir");

        // Limpiar
        escribir(conversor, 0, "10");
        clic(conversor, "LIMPIAR");
        Thread.sleep(1000);
        verificar(resultado(conversor), "---");

        // Token invalido (CU-08): debe volver al login con aviso
        SesionUsuario.iniciar("MONSTER", "token-falso");
        escribir(conversor, 0, "5");
        clic(conversor, "CONVERTIR");
        JFrame login2 = esperarFrame("LoginFrame");
        String aviso = cerrarDialogo();
        verificar(aviso.contains("Token invalido o sesion expirada") ? "ok" : aviso, "ok");
        verificar(String.valueOf(conversor.isDisplayable()), "false");
        captura(login2, "04_login_otra_vez.png");

        System.out.println(fallos == 0 ? "TODAS LAS PRUEBAS OK" : "FALLOS: " + fallos);
        System.exit(fallos);
    }

    // ---------------------------------------------------------------
    static void verificar(String real, String esperado) {
        boolean ok = esperado.equals(real);
        if (!ok) fallos++;
        System.out.println((ok ? "  OK   " : "  FALLA ") + esperado + (ok ? "" : "  (obtenido: " + real + ")"));
    }

    static JFrame esperarFrame(String clase) throws Exception {
        for (int i = 0; i < 100; i++) {
            for (Window w : Window.getWindows()) {
                if (w.isShowing() && w.getClass().getSimpleName().equals(clase)) return (JFrame) w;
            }
            Thread.sleep(100);
        }
        throw new IllegalStateException("No aparecio " + clase);
    }

    // Espera un JOptionPane, devuelve titulo/mensaje y lo cierra
    static String cerrarDialogo() throws Exception {
        for (int i = 0; i < 100; i++) {
            for (Window w : Window.getWindows()) {
                if (w instanceof JDialog d && d.isShowing()) {
                    List<JOptionPane> panes = buscar(d, JOptionPane.class);
                    if (!panes.isEmpty()) {
                        Thread.sleep(300);
                        String mensaje = String.valueOf(panes.get(0).getMessage());
                        System.out.println("  [dialogo] " + d.getTitle() + " / " + mensaje.replace("\n", " "));
                        SwingUtilities.invokeAndWait(d::dispose);
                        Thread.sleep(300);
                        return d.getTitle().equals("Campos vacíos") ? d.getTitle() : mensaje;
                    }
                }
            }
            Thread.sleep(100);
        }
        return "(sin dialogo)";
    }

    static void clic(Container c, String texto) throws Exception {
        for (JButton b : buscarBotones(c)) {
            if (b.getText().equals(texto)) {
                SwingUtilities.invokeLater(b::doClick);
                Thread.sleep(400);
                return;
            }
        }
        throw new IllegalStateException("Sin boton " + texto);
    }

    static List<JButton> buscarBotones(Container c) {
        return buscar(c, JButton.class);
    }

    static void escribir(Container c, int indice, String texto) throws Exception {
        List<JTextField> campos = new ArrayList<>();
        for (JTextField t : buscar(c, JTextField.class)) {
            if (!(t instanceof JPasswordField) && !(t.getParent() instanceof JComboBox)) campos.add(t);
        }
        enEdt(() -> campos.get(indice).setText(texto));
    }

    static void escribirClave(Container c, String texto) throws Exception {
        JPasswordField p = buscar(c, JPasswordField.class).get(0);
        enEdt(() -> p.setText(texto));
    }

    static String resultado(Container c) {
        for (JLabel l : buscar(c, JLabel.class)) {
            if (l.getFont().getSize() == 18) return l.getText();
        }
        return "(sin resultado)";
    }

    static String items(JComboBox<?> combo) {
        List<Object> lista = new ArrayList<>();
        for (int i = 0; i < combo.getItemCount(); i++) lista.add(combo.getItemAt(i));
        return lista.toString();
    }

    static void enEdt(Runnable r) throws Exception {
        SwingUtilities.invokeAndWait(r);
    }

    static void captura(JFrame f, String nombre) throws Exception {
        Container contenido = f.getContentPane();
        BufferedImage img = new BufferedImage(contenido.getWidth(), contenido.getHeight(), BufferedImage.TYPE_INT_RGB);
        enEdt(() -> contenido.printAll(img.getGraphics()));
        ImageIO.write(img, "png", new File(carpeta, nombre));
    }

    @SuppressWarnings("unchecked")
    static <T> List<T> buscar(Container raiz, Class<T> tipo) {
        List<T> lista = new ArrayList<>();
        for (Component c : raiz.getComponents()) {
            if (tipo.isInstance(c)) lista.add((T) c);
            if (c instanceof Container cc) lista.addAll(buscar(cc, tipo));
        }
        if (raiz instanceof JFrame jf && lista.isEmpty()) lista.addAll(buscar(jf.getContentPane(), tipo));
        return lista;
    }
}
```
