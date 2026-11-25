# Guía del cliente de consola (Java) — Conversión de Unidades REST

> Cliente: `04. CLICON` · Lenguaje: **Java 17+ con Maven** (aplicación de línea de comandos, como pide el `.docx`) · Servidor: `04. SERVIDOR` (ASP.NET Core, `WS_ConUnid_DOTNET_RESTFUL_GR02`)
>
> Esta guía se lee junto con:
> - `04. SERVIDOR/CONTRATO_GR02.md` (el contrato REST que este cliente consume; sale del código del servidor y manda sobre esta guía).
> - `04. SERVIDOR/TALLER CASOS DE USO CONVERSION UNIDADES.docx` (los casos de uso que el cliente debe cumplir).
> - El cliente de escritorio (`04. CLIESC`) debe compartir **las mismas** clases `controlador` y `modelo`.

---

## 0. Instrucciones para quien aplique esta guía (persona o IA)

1. El `.docx` define el cliente de consola como *"Aplicación Java ejecutada desde línea de comandos"*. Se crea el proyecto Java `CONUNI_CLIENTE_CONSOLA`, con la misma estructura de 3 carpetas que el servidor (`ec.edu.monster.controlador`, `modelo`, `vista`).
2. **Conserva** la identidad visual del cliente Python de referencia (sección 2.3): el ASCII de Sullivan, los separadores de 50 `=`, los emojis, los textos de menú, los 3 intentos de login y el `Presione ENTER para continuar`.
3. **No implementes conversiones en el cliente.** El taller lo exige: *"Los cuatro clientes no implementan la lógica de conversión"*. El cliente pide, muestra y maneja errores.
4. Copia el código de la sección 5 **tal cual**. Se compiló y se probó contra el servidor real (sección 8.3).
5. No pongas ñ ni tildes en identificadores. Los textos que ve el usuario sí las llevan.
6. Si algo de esta guía choca con el `.docx`, manda el `.docx` en lo funcional y esta guía en la forma. Si choca con el servidor, **manda el servidor** (`CONTRATO_GR02.md`).

| Fuente | Manda en |
|---|---|
| `.docx` del taller | Qué hace el sistema: casos de uso CU-01…CU-11, flujo, mensajes de error |
| `04. SERVIDOR/CONTRATO_GR02.md` | Rutas, JSON, códigos, token |
| Esta guía | Cómo queda **este** cliente: proyecto, clases, estilo de consola |

---

## 1. Variables del cliente

| Variable | Valor | Dónde |
|---|---|---|
| `URL_BASE` | `http://192.168.137.1:5014/api/conversor` | `Configuracion.URL_BASE` |
| Cambiar la URL sin recompilar | `java -Dconuni.url=http://localhost:5014/api/conversor -jar ...` | Propiedad del sistema `conuni.url` |
| Usuario de prueba | `MONSTER` / `Monster9` | Lo valida **el servidor**, no el cliente |
| Timeout HTTP | 10 s | `Configuracion.TIMEOUT_MS` |
| Decimales del resultado | 4 (extra opcional, el `.docx` no lo pide) | `Configuracion.DECIMALES` |
| Opción "Cerrar sesión" | `false` (el `.docx` no la pide) | `Configuracion.PERMITIR_CERRAR_SESION` |
| `groupId` / paquete | `ec.edu.monster` | `pom.xml` |
| Clase principal | `ec.edu.monster.vista.ConsolaApp` | `pom.xml` (`exec.mainClass`) |

---

## 2. Origen del diseño: cliente Python de referencia

> En `04. CLICON` de este proyecto **no hay** cliente Python: el proyecto Java se crea desde cero. Esta sección queda como registro de dónde salen la identidad visual (2.3) y las decisiones (2.2, 2.4). Si en tu copia existen esos archivos, aplica la columna "Acción".

### 2.1 Archivos

| Archivo | Qué hace hoy | Acción |
|---|---|---|
| `cliente_api.py` (474 líneas) | Todo el cliente: clase `ClienteConversionUnidades` + menús + `main()` | **Reemplazar** por el proyecto Java. El arte de Sullivan pasa a `ArteSullivan.java` |
| `verificar_servidor.py` | GET a la base, GET al endpoint, POST de prueba; siempre concluye "OK" | **Eliminar**: la opción "Verificar conexión" del menú y la sección 8.3 la reemplazan |
| `vista_previa_menus.py` | Imprime menús fijos; menciona "Kilogramos a Libras", que no existe | **Eliminar** |
| `requirements.txt`, `setup_y_ejecutar.ps1` | Instalación de Python | **Eliminar** (los reemplaza `pom.xml` y `ejecutar.bat`) |
| `README.md`, `INICIO_RAPIDO.md` | Dicen que funciona offline y que el servidor solo "registra" | **Reescribir** (sección 8.5) |
| `.gitignore` | Python | **Reemplazar** por uno de Java (sección 5.13) |

### 2.2 Funciones de `cliente_api.py` y su destino en Java

| Línea | Función Python | Hoy | Problema frente al taller | Destino Java |
|---|---|---|---|---|
| 19 | `__init__` | URL, token, usuario y 3 diccionarios con factores de conversión | Los diccionarios son lógica de conversión en el cliente | `Configuracion` + `SesionUsuario`; **sin** factores |
| 54 | `verificar_conexion_servidor` | GET a la raíz; si falla igual devuelve `True` y dice *"funcionarán localmente"* | Nunca reporta caída | `ApiService.verificarConexion()` con `GET /categorias` |
| 96–146 | `mostrar_sullivan` | ASCII braille azul + "SULLIVAN - MONSTERS INC." | — | `ArteSullivan.mostrar()`, mismas 40 líneas |
| 148 | `login` | Compara `MONSTER`/`Monster9` en el cliente; token falso | Viola CU-01 | `ApiService.login()` → `POST /login` |
| 190 | `convertir_temperatura` | Fórmulas °C/°F/K locales | Conversión en el cliente | **Eliminada** |
| 212 | `realizar_conversion` | Calcula localmente y hace un POST "de registro" con campos inexistentes | Viola CU-04/05/07 | `ApiService.realizarConversion()` → `POST /convertir` |
| 292/320/348 | `menu_conversiones_masa/longitud/temperatura` | 3 menús fijos de 3 pares | El catálogo debe venir del servidor (CU-02/03). "Toneladas a Kilogramos" fallaría | Un solo `menuConversiones(categoria)` con unidades del servidor |
| 376 | `menu_principal` | Opciones 1–3 fijas, 4 verificar, 5 salir | Categorías fijas | `menuPrincipal()` arma las opciones con `GET /categorias` |
| 421 | `main` | Banner, verificar, 3 intentos con `getpass` | No vuelve al login si la sesión expira (CU-09) | `ejecutar()` con ciclo de reautenticación |

### 2.3 Identidad visual que se conserva

- Separadores de 50 `=` arriba y abajo de cada bloque.
- Emojis: `🚀` banner, `🔐` login, `📌` intentos, `👤` usuario, `🔑` contraseña, `🌐` menú principal, `🔗` servidor, `⚖️` masa, `📏` longitud, `🌡️` temperatura, `🔌` verificar, `🚪` salir, `⬅️` volver, `👉` selección, `✅` éxito, `❌` error, `⚠️` aviso, `⏸️` pausa, `👋` despedida.
- Sullivan en azul justo después de un login correcto.
- `Presione ENTER para continuar...` después de cada conversión.
- 3 intentos de login; contraseña oculta (`System.console().readPassword`, equivalente a `getpass`).
- Mensaje de interrupción con Ctrl+C / fin de entrada.

### 2.4 Resumen de lo que violaba el taller

| Requisito del `.docx` | Cliente Python | Cliente Java |
|---|---|---|
| Aplicación Java de línea de comandos | Python | Java 17 + Maven |
| El cliente no implementa conversión | Calcula todo localmente | Solo el servidor calcula |
| CU-01: el servidor autentica y emite token | Login y token simulados | `POST /login`, guarda el `token` |
| CU-02/CU-03: catálogo del servidor | Pares fijos | `GET /categorias`, `GET /categorias/{c}/unidades` |
| CU-05: cada petición lleva token | No envía token | `Authorization: Bearer <token>` |
| CU-08/CU-09: token inválido o vencido | No existe | Muestra el mensaje y vuelve al login |
| CU-10/CU-11: errores del servidor | Se ignoran | Muestra `codigo` y `mensaje` |

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

Errores (el cuerpo **siempre** trae `estado:"ERROR"`, `codigo` y `mensaje`, también en 400 y 401):

| HTTP | `codigo` | Caso de uso | `mensaje` del servidor | Qué hace este cliente |
|---|---|---|---|---|
| 401 | `AUTH-001` | CU-08 token inválido | `Token invalido o sesion expirada` | Muestra el mensaje, borra el token y vuelve al login |
| 401 | `AUTH-002` | CU-09 sesión expirada | `Sesion expirada, vuelva a autenticarse` | Igual que AUTH-001 |
| 401 | `AUTH-003` | CU-01 credenciales incorrectas | `Credenciales incorrectas` | Resta un intento |
| 400 | `UNIT-001` | CU-10 unidad no soportada | `Unidad de origen no soportada: XYZ` | Muestra el mensaje y sigue en el menú |
| 400 | `DATA-001` | CU-11 datos inconsistentes | p. ej. `La unidad de destino CELSIUS no pertenece a la categoria LONGITUD` | Muestra el mensaje y sigue en el menú |
| 500 | `SRV-001` | Error interno | `Error interno del servidor` | Muestra el mensaje |
| — | `RED` (propio del cliente) | Servidor apagado o inalcanzable | — | Muestra el mensaje |

En `POST /convertir` los errores 400 traen además `resultado:null`, `unidadOrigen` y `unidadDestino`; el cliente solo usa `codigo` y `mensaje`. `POST /login` con usuario o contraseña vacíos da `400 DATA-001`, pero el cliente no llega a enviarlo porque los valida antes.

> La raíz del servidor responde 404 sin cuerpo. Para "verificar conexión" se usa `GET /categorias`: sin token responde 401 con JSON, lo que prueba que el servidor está vivo.
>
> `HttpURLConnection` lanza excepción al leer `getInputStream()` en un 400/401; el JSON de error se lee con `getErrorStream()`. `ApiService.enviar` ya lo hace.

---

## 4. Estructura objetivo

```
04. CLICON/
├── CONUNI_CLIENTE_CONSOLA/                  # proyecto Maven (se abre en NetBeans)
│   ├── pom.xml
│   ├── ejecutar.bat
│   ├── .gitignore
│   └── src/main/java/ec/edu/monster/
│       ├── controlador/
│       │   ├── Configuracion.java           # URL, timeout, decimales, bandera de cerrar sesión
│       │   └── ApiService.java              # único punto que hace HTTP (CU-01…CU-04)
│       ├── modelo/
│       │   ├── ConversionRequest.java
│       │   ├── ConversionResponse.java
│       │   ├── ExcepcionApi.java            # codigo + mensaje del servidor
│       │   └── SesionUsuario.java           # token en memoria ("el cliente almacena temporalmente el token")
│       └── vista/
│           ├── ConsolaApp.java              # main, menús, lectura de teclado
│           ├── ArteSullivan.java            # ASCII del cliente Python
│           └── FormatoNumero.java           # decimales configurables
├── README.md                                # reescrito
├── INICIO_RAPIDO.md                         # reescrito
└── GUIA_CLIENTE_CONSOLA.md                  # esta guía
```

| Capa | Clase | Responsabilidad |
|---|---|---|
| controlador | `Configuracion` | Valores que se pueden cambiar sin tocar la lógica |
| controlador | `ApiService` | Arma URL, headers y token; convierte cualquier fallo en `ExcepcionApi(codigo, mensaje)`. Mismos nombres que el `ApiService` del cliente móvil |
| modelo | `ConversionRequest`, `ConversionResponse` | Mismos campos que el JSON del servidor (iguales al cliente móvil) |
| modelo | `SesionUsuario` | Usuario y token mientras dura la sesión (igual al cliente móvil) |
| modelo | `ExcepcionApi` | `esErrorDeSesion()` para AUTH-001/002, `esErrorDeRed()` para `RED` |
| vista | `ConsolaApp` | Interacción por consola; no sabe de HTTP ni de JSON |

**Las carpetas `controlador` y `modelo` son idénticas en el cliente de escritorio.** Si cambias una, cámbiala en los dos.

---

## 5. Código

### 5.1 `pom.xml`

Usa `org.json` (la misma librería que trae Android) para leer y escribir JSON. `exec.mainClass` hace que el botón **Run** de NetBeans funcione, y el `maven-assembly-plugin` genera un `.jar` ejecutable con la dependencia incluida.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>ec.edu.monster</groupId>
    <artifactId>CONUNI_CLIENTE_CONSOLA</artifactId>
    <version>1.0</version>
    <packaging>jar</packaging>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.release>17</maven.compiler.release>
        <exec.mainClass>ec.edu.monster.vista.ConsolaApp</exec.mainClass>
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
            <!-- mvn package genera target/CONUNI_CLIENTE_CONSOLA-1.0-jar-with-dependencies.jar -->
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

### 5.2 `controlador/Configuracion.java`

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

### 5.3 `controlador/ApiService.java`

Versión síncrona del `ApiService` del cliente móvil: mismos métodos y mismos mensajes de red, pero devuelve el resultado o lanza `ExcepcionApi` en lugar de usar callbacks.

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

### 5.4 `modelo/ExcepcionApi.java`

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

### 5.5 `modelo/ConversionRequest.java`

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

### 5.6 `modelo/ConversionResponse.java`

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

### 5.7 `modelo/SesionUsuario.java`

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

### 5.8 `vista/FormatoNumero.java`

`formatear` muestra hasta `Configuracion.DECIMALES` decimales sin ceros sobrantes: `77.0` → `77`, `0.621371192237334` → `0.6214`, `5000.0` → `5000`. `nombreVisible` y `codigo` las usa el cliente de escritorio para mostrar `Kilometro` en lugar de `KILOMETRO`.

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

### 5.9 `vista/ArteSullivan.java`

Las 40 líneas son copia exacta de `mostrar_sullivan()` (líneas 96–146 de `cliente_api.py`). `\u001B[94m` es el azul claro de ANSI que usaba Python.

```java
package ec.edu.monster.vista;

public final class ArteSullivan {

    private static final String AZUL = "\u001B[94m";
    private static final String RESET = "\u001B[0m";

    private static final String[] LINEAS = {
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⡾⡛⠷⣂⣄⠀⢀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣾⠟⢀⣼⣿⡿⠿⢷⡿⠓⠿⣖⣤⣤⣤⢄⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢻⣦⡸⣤⣀⣀⣀⡾⢥⡀⠀⠙⡿⣽⢿⣌⠉⢻⣦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣠⣼⠟⢋⣭⡟⣏⠷⠖⣽⣦⢀⡇⡬⢿⠿⡈⢹⡟⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣽⣶⣶⠾⠟⠚⣽⠒⠉⢦⠽⠟⠸⡐⣿⡨⠈⠉⠀⡇⠀⢣⠈⣺⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡿⣿⠁⠠⡀⠀⠀⠙⠧⣤⣬⣆⠀⠀⠀⠀⠀⠀⠀⠀⠈⠓⣾⡶⠗⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⢿⣏⠀⠀⠈⠲⣄⠀⠀⠀⠀⠀⢀⡀⠀⢀⠽⠒⠀⠀⠀⠀⢹⣣⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⢹⣯⠀⠀⠀⠀⠀⠙⠲⢄⡴⣢⣆⠼⠚⠁⠀⠀⠀⡄⠀⠀⠀⠻⣧⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⣤⣴⣶⣄⣤⣤⣤⣼⣿⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡇⠀⠀⠀⠀⠙⢾⣆⢄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢈⣿⠶⠀⠉⠀⠀⠀⠀⠘⣦⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣼⠀⠀⠀⢰⠀⠀⠀⠈⠻⢮⣖⠄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿⣵⠖⠀⠀⠀⠀⠀⠀⠀⠈⠙⢦⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣰⠇⠀⠀⠀⠈⠀⠀⠀⠀⠀⠀⠈⠛⠾⣔⡄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠀⠀⠀⠀⠀⢀⣤⣴⡷⠧⠤⠤⣤⣄⡀⡀⠀⠀⠀⠀⠀⢳⠈⠉⠀⠀⠀⠀⠠⣤⡤⠴⡺⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠤⠀⠈⠙⠾⣕⣄⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠀⠀⠀⢀⢰⣿⡵⠛⠲⠦⠤⣄⠀⠀⠙⣧⠀⠀⠀⠀⠀⠀⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠂⠀⠀⠀⠀⠀⠀⠈⠻⢯⡦⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠀⠀⢠⣾⣯⠼⠤⠤⣄⠀⠀⠈⣷⣀⣀⣼⣧⡤⠴⠶⠶⠶⠶⠶⠒⡒⠒⠒⠒⠲⢤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠻⣯⣤⡀⠀⠀⠀⠀",
        "⠀⠀⠀⣰⡟⣹⡇⠀⠀⣀⣬⠷⠚⠛⠉⠉⠁⢰⠀⠀⠀⠀⠀⠀⠀⢀⠔⠁⠀⠀⠀⠀⠀⠙⢦⡀⠀⠀⠀⠀⠀⡰⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠃⣼⣼⣿⠉⠀⠀⠀⠀",
        "⠀⠀⢀⣧⠟⠁⢙⠷⠋⠁⠀⠀⠀⠀⠀⠀⠀⡞⠀⠀⠀⠀⠀⠀⠀⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠹⣄⠀⠀⢠⡞⠁⠀⠀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣰⣻⠿⠃⠀⠀⠀⠀⠀",
        "⠀⣰⡿⠁⠀⠀⡔⠀⠀⠀⠀⠀⠀⠀⠀⠀⡜⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡠⠀⠀⠀⠈⢣⡴⠋⠀⠀⠀⠐⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣰⣻⠁⠀⠀⠀⠀⠀⠀⠀",
        "⣼⠞⠀⠀⠀⠀⠁⠀⠀⠀⠀⠀⠀⠀⠠⠊⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣠⠖⠛⠛⠛⠒⠲⠶⢯⣤⣀⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣰⡻⠃⠀⠀⠀⠀⠀⠀⠀⠀",
        "⣼⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⡴⠛⠁⠀⠀⠀⠀⠀⠀⠀⠀⠰⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣰⡻⠃⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⡴⠞⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠃⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣼⡛⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠘⣿⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣴⠞⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠐⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣾⠝⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠻⣷⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣤⡴⠟⠻⣄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣟⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠹⡿⣄⠀⢀⣀⣀⣀⣀⣠⣤⣴⡶⠛⠉⠁⠀⠀⠀⠈⠳⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣼⡯⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠀⠈⠫⢷⣾⣿⢻⠿⠛⠉⠉⣿⠇⠀⠀⠠⠀⠂⠈⠀⢀⠈⠻⣦⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣰⠿⣿⡁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠀⠀⠀⠀⠈⠉⠁⠀⠀⠀⢠⣿⠀⠀⠐⠀⠀⠄⠐⠈⠀⠀⠀⠀⠙⢷⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⡾⠋⠀⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢨⣿⠀⠀⠄⠂⠀⠠⠀⢀⠐⠈⠀⠐⠀⠀⠈⠛⠷⣤⣀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡴⠏⠀⠀⠀⢿⣧⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢨⣟⠀⠀⢀⠠⠐⠀⢀⠀⠀⠠⠐⠀⠀⠁⡀⠀⠀⠀⢉⣻⡷⢆⣀⣀⠀⣠⡶⠋⠀⢀⠀⠀⠀⢸⡾⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢨⣟⠀⠀⠀⠀⡀⠠⠀⠀⠈⠀⢀⠠⠈⠀⠀⡀⠁⠠⠀⠈⠉⠉⣽⠷⠚⠉⠀⠀⠀⠈⠒⠐⠊⠀⣷⡆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⣿⠀⠈⠀⠀⢀⠀⠈⠀⠐⠀⠀⢀⠀⠁⠀⠀⠄⠀⠄⠀⠄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿⢳⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠿⣿⣷⡀⠀⠁⠀⢀⠀⠁⠀⠄⠈⠀⠀⠠⠈⠀⠀⠄⠀⠠⠀⠐⠀⠀⠀⠀⠀⢀⣀⠀⠀⠀⠀⢹⡏⢫⡆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢼⡏⠙⢦⣄⠂⠀⠀⠐⠀⡀⠄⠂⠁⠀⡀⠐⠀⠠⠐⠀⠀⠄⠀⠀⠀⠀⠀⠁⠀⠉⠲⢄⠀⠈⣷⢾⣿⣀⣀⣀⣀⠀⠀⠀⢀⡀⠀⠀⠀",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣼⡇⡄⠀⠈⠓⠦⠨⣀⠀⠀⠀⡀⠄⠀⢀⠠⠐⠀⠀⡀⠂⡠⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠙⡀⢿⡄⠙⠿⣍⡉⠹⡵⡀⣴⠟⠙⣧⠶⣶",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⢿⠁⠘⡄⠀⠀⠀⠀⠀⠀⠈⠦⣀⡀⠠⠀⠀⢀⠠⠀⠀⠊⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠃⢸⣧⠀⠀⠈⠙⠓⠳⠛⠛⠉⠁⠀⡴⠏",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣼⣿⢀⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠈⠉⠛⣷⣶⠶⢶⣶⢶⡋⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠐⠁⠀⠀⣿⠆⠀⡠⠒⠰⠄⠀⠀⠀⢠⢾⠋⠀",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠑⣽⣿⡀⠀⠀⠀⠀⠀⣀⠀⠀⠀⠀⠀⠀⡟⡇⠀⠀⠀⢿⣇⠀⠀⠀⠀⠀⠀⣀⣤⡀⠀⠀⠀⣠⢷⡏⠀⠀⠀⡠⠊⠀⠀⢀⣴⡟⠁⠀⠀",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⠈⢽⣷⡄⠀⠀⠀⠏⠀⠉⠑⢦⠀⠀⠀⢿⣧⠀⠀⠀⢿⠇⠀⠀⠀⠀⠀⢀⠀⠀⠈⡇⠀⣴⣏⣀⠀⠀⣀⣀⣤⡤⣶⠿⠛⠁⠀⠀⠀⠀",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡟⠋⣉⠵⠂⢀⣀⠄⠀⠀⠀⢀⠰⠁⠀⠀⠘⣿⡄⠀⣼⡟⠀⠀⠀⠀⠀⠀⠀⠁⢢⣈⠀⠐⠤⣬⠉⣻⣿⡟⠛⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⡿⠉⢺⡗⠒⣄⣏⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⡁⠀⢿⣅⣀⠀⠀⠀⠀⠀⠀⠀⠀⡠⢽⣦⠚⠚⢯⠀⠱⣵⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿⣀⣤⡎⠀⢀⡞⠁⠀⢱⠀⠀⠀⢀⣀⣤⢶⡾⠛⠋⠁⠀⠀⠉⠋⠿⣶⣤⣄⡀⠀⠀⢰⠃⠀⠙⣆⣀⠈⣷⠶⠿⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
        "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠙⠉⠹⠾⣽⣿⡤⣴⢶⣿⣴⣶⡟⡯⠛⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠙⠹⢿⣶⣶⣿⠶⣦⣿⡉⠛⢿⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
    };

    private ArteSullivan() {
    }

    // Sullivan en azul, igual que mostrar_sullivan() del cliente Python original
    public static void mostrar() {
        System.out.println(AZUL);
        for (String linea : LINEAS) {
            System.out.println(linea);
        }
        System.out.println(RESET);
        System.out.println(AZUL + "        SULLIVAN - MONSTERS INC." + RESET);
        System.out.println(AZUL + "        ¡BIENVENIDO AL CONVERSOR!" + RESET);
        System.out.println("=".repeat(50));
    }
}
```

### 5.10 `vista/ConsolaApp.java`

```java
package ec.edu.monster.vista;

import ec.edu.monster.controlador.ApiService;
import ec.edu.monster.controlador.Configuracion;
import ec.edu.monster.modelo.ConversionRequest;
import ec.edu.monster.modelo.ConversionResponse;
import ec.edu.monster.modelo.ExcepcionApi;
import ec.edu.monster.modelo.SesionUsuario;

import java.io.Console;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConsolaApp {

    private static final String LINEA = "=".repeat(50);

    private static final Map<String, String> ICONOS_CATEGORIA = Map.of(
            "MASA", "⚖️ ",
            "LONGITUD", "📏",
            "TEMPERATURA", "🌡️ ");

    private enum FinMenu { SALIR, CERRAR_SESION, REAUTENTICAR }

    private final ApiService apiService = new ApiService();
    private final Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

    public static void main(String[] args) {
        // Emojis y braille: la consola debe estar en UTF-8 (chcp 65001)
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        try {
            new ConsolaApp().ejecutar();
        } catch (NoSuchElementException e) {
            System.out.println("\n\n⚠️ Programa interrumpido por el usuario.");
        }
    }

    private void ejecutar() {
        System.out.println(LINEA);
        System.out.println("🚀 CONVERSOR DE UNIDADES");
        System.out.println(LINEA);
        System.out.println("Cliente de consola Java para API RESTful .NET");
        System.out.println("Servidor: " + Configuracion.URL_BASE);
        System.out.println(LINEA);

        // Informativo, no bloquea
        verificarConexionServidor();

        while (true) {
            if (!iniciarSesion()) {
                System.out.println("\n💡 Pista: Usuario = MONSTER, Contraseña = Monster9");
                return;
            }

            FinMenu fin = menuPrincipal();
            if (fin == FinMenu.SALIR) {
                return;
            }
            if (fin == FinMenu.REAUTENTICAR) {
                // CU-09: el servidor respondio AUTH-001 o AUTH-002
                System.out.println("\n🔒 Su sesión terminó. Vuelva a iniciar sesión.");
            }
        }
    }

    // ------------------------------------------------------------------
    // Conexion
    // ------------------------------------------------------------------
    private boolean verificarConexionServidor() {
        System.out.println("\n🔌 Verificando conexión con el servidor...");
        System.out.println("   Endpoint: " + Configuracion.URL_BASE);
        if (apiService.verificarConexion()) {
            System.out.println("✅ Servidor ACTIVO");
            return true;
        }
        System.out.println("⚠️  No se pudo conectar con el servidor");
        System.out.println("   Las conversiones necesitan el servidor encendido");
        return false;
    }

    // ------------------------------------------------------------------
    // CU-01 Autenticarse
    // ------------------------------------------------------------------
    private boolean iniciarSesion() {
        System.out.println("\n" + LINEA);
        System.out.println("🔐 INICIAR SESIÓN");
        System.out.println(LINEA);

        int intentos = 3;
        while (intentos > 0) {
            System.out.println("\n📌 Intentos restantes: " + intentos);
            String usuario = leerLinea("👤 Usuario: ").trim();
            String contrasena = leerContrasena("🔑 Contraseña: ");

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                System.out.println("⚠️  Usuario y contraseña son obligatorios");
                continue;
            }

            System.out.println("\n" + LINEA);
            System.out.println("🔐 Verificando credenciales...");
            System.out.println(LINEA);

            try {
                apiService.login(usuario, contrasena);
                System.out.println("\n✅ Login exitoso!");
                ArteSullivan.mostrar();
                System.out.println("\n👤 Bienvenido/a: " + usuario);
                return true;
            } catch (ExcepcionApi e) {
                mostrarError(e);
            }

            intentos--;
            if (intentos > 0) {
                System.out.println("\n⚠️  Intenta nuevamente.");
            } else {
                System.out.println("\n❌ Número máximo de intentos alcanzado. Programa terminado.");
            }
        }
        return false;
    }

    // ------------------------------------------------------------------
    // CU-02 Consultar catalogo (menu principal)
    // ------------------------------------------------------------------
    private FinMenu menuPrincipal() {
        while (SesionUsuario.estaActiva()) {
            List<String> categorias;
            try {
                categorias = apiService.obtenerCategorias();
            } catch (ExcepcionApi e) {
                mostrarError(e);
                if (e.esErrorDeSesion()) {
                    break;
                }
                pausa("\n⏸️  Presione ENTER para reintentar...");
                continue;
            }

            int opcionVerificar = categorias.size() + 1;
            int opcionCerrarSesion = Configuracion.PERMITIR_CERRAR_SESION ? categorias.size() + 2 : -1;
            int opcionSalir = Configuracion.PERMITIR_CERRAR_SESION ? categorias.size() + 3 : categorias.size() + 2;

            System.out.println("\n" + LINEA);
            System.out.println("🌐 CONVERSOR DE UNIDADES");
            System.out.println(LINEA);
            System.out.println("👤 Usuario: " + SesionUsuario.getUsuario());
            System.out.println("🔗 Servidor: " + Configuracion.URL_BASE);
            System.out.println(LINEA);
            for (int i = 0; i < categorias.size(); i++) {
                String categoria = categorias.get(i);
                System.out.println((i + 1) + ". " + ICONOS_CATEGORIA.getOrDefault(categoria, "📐") + " Conversiones de " + categoria);
            }
            System.out.println(opcionVerificar + ". 🔌 Verificar conexión con servidor");
            if (Configuracion.PERMITIR_CERRAR_SESION) {
                System.out.println(opcionCerrarSesion + ". 🔒 Cerrar sesión");
            }
            System.out.println(opcionSalir + ". 🚪 Salir");
            System.out.println(LINEA);

            int opcion = leerEntero("\n👉 Seleccione una opción: ");

            if (opcion >= 1 && opcion <= categorias.size()) {
                menuConversiones(categorias.get(opcion - 1));
            } else if (opcion == opcionVerificar) {
                verificarConexionServidor();
                pausa("\n⏸️  Presione ENTER para continuar...");
            } else if (Configuracion.PERMITIR_CERRAR_SESION && opcion == opcionCerrarSesion) {
                System.out.println("\n🔒 Cerrando sesión de " + SesionUsuario.getUsuario() + "...");
                SesionUsuario.cerrar();
                return FinMenu.CERRAR_SESION;
            } else if (opcion == opcionSalir) {
                System.out.println("\n👋 Saliendo...");
                System.out.println("Hasta pronto, " + SesionUsuario.getUsuario() + "!");
                return FinMenu.SALIR;
            } else {
                System.out.println("\n❌ Opción no válida. Por favor, seleccione una opción del 1 al " + opcionSalir + ".");
            }
        }
        return FinMenu.REAUTENTICAR;
    }

    // ------------------------------------------------------------------
    // CU-03 Consultar unidades y CU-04 Realizar conversion
    // ------------------------------------------------------------------
    private void menuConversiones(String categoria) {
        List<String> unidades;
        try {
            unidades = apiService.obtenerUnidades(categoria);
        } catch (ExcepcionApi e) {
            mostrarError(e);
            return;
        }

        String icono = ICONOS_CATEGORIA.getOrDefault(categoria, "📐");
        while (SesionUsuario.estaActiva()) {
            System.out.println("\n" + LINEA);
            System.out.println(icono + " CONVERSIONES DE " + categoria);
            System.out.println(LINEA);
            for (int i = 0; i < unidades.size(); i++) {
                System.out.println((i + 1) + ". " + unidades.get(i));
            }
            System.out.println("0. ⬅️  Volver al menú principal");
            System.out.println(LINEA);

            String origen = elegirUnidad(unidades, "origen");
            if (origen == null) {
                break;
            }
            String destino = elegirUnidad(unidades, "destino");
            if (destino == null) {
                break;
            }

            Double valor = leerValor(origen);
            if (valor != null) {
                realizarConversion(categoria, origen, destino, valor);
            }

            pausa("\n⏸️  Presione ENTER para continuar...");
        }
    }

    private void realizarConversion(String categoria, String origen, String destino, double valor) {
        System.out.println("\n" + LINEA);
        try {
            ConversionResponse respuesta = apiService.realizarConversion(
                    new ConversionRequest(categoria, origen, destino, valor));
            System.out.println("✅ RESULTADO DE LA CONVERSIÓN");
            System.out.println(LINEA);
            System.out.println("   " + FormatoNumero.formatear(valor) + " " + respuesta.getUnidadOrigen()
                    + " = " + FormatoNumero.formatear(respuesta.getResultado()) + " " + respuesta.getUnidadDestino());
            System.out.println("   " + respuesta.getMensaje());
        } catch (ExcepcionApi e) {
            mostrarError(e);
        }
        System.out.println(LINEA);
    }

    // ------------------------------------------------------------------
    // Utilidades de consola
    // ------------------------------------------------------------------
    // CU-08 a CU-11: el cliente presenta el mensaje del servidor
    private void mostrarError(ExcepcionApi e) {
        if (e.esErrorDeRed()) {
            System.out.println("❌ " + e.getMessage());
        } else {
            System.out.println("❌ [" + e.getCodigo() + "] " + e.getMessage());
        }
    }

    private String elegirUnidad(List<String> unidades, String rol) {
        while (true) {
            int opcion = leerEntero("\n👉 Seleccione la unidad de " + rol + ": ");
            if (opcion == 0) {
                return null;
            }
            if (opcion >= 1 && opcion <= unidades.size()) {
                return unidades.get(opcion - 1);
            }
            System.out.println("❌ Opción no válida");
        }
    }

    // Acepta coma o punto decimal
    private Double leerValor(String unidad) {
        String texto = leerLinea("\n🔢 Ingrese el valor en " + unidad + ": ").trim().replace(',', '.');
        try {
            double valor = Double.parseDouble(texto);
            if (Double.isFinite(valor)) {
                return valor;
            }
        } catch (NumberFormatException e) {
            // se informa abajo
        }
        System.out.println("❌ Error: Debe ingresar un número válido");
        return null;
    }

    private int leerEntero(String mensaje) {
        try {
            return Integer.parseInt(leerLinea(mensaje).trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String leerLinea(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }

    // Oculta la contrasena si hay consola real; en el IDE se lee como texto normal
    private String leerContrasena(String mensaje) {
        Console consola = System.console();
        if (consola != null) {
            char[] clave = consola.readPassword(mensaje);
            return clave == null ? "" : new String(clave);
        }
        return leerLinea(mensaje);
    }

    private void pausa(String mensaje) {
        leerLinea(mensaje);
    }
}
```

### 5.11 `ejecutar.bat`

```bat
@echo off
REM Consola en UTF-8 para que se vean los emojis y el arte de Sullivan
chcp 65001 > nul < nul
cd /d "%~dp0"

if not exist "target\CONUNI_CLIENTE_CONSOLA-1.0-jar-with-dependencies.jar" (
    echo Compilando el proyecto...
    call mvn -q package
)

REM Para usar el servidor en la misma PC:
REM java -Dconuni.url=http://localhost:5014/api/conversor -jar target\CONUNI_CLIENTE_CONSOLA-1.0-jar-with-dependencies.jar
java -jar target\CONUNI_CLIENTE_CONSOLA-1.0-jar-with-dependencies.jar
pause
```

### 5.12 `README.md` (reemplaza al actual)

````markdown
# Cliente de consola — Conversión de Unidades (Java)

Aplicación Java de línea de comandos que consume la API REST .NET `WS_ConUnid_DOTNET_RESTFUL_GR02`.
El cliente **no** calcula conversiones: autentica, pide el catálogo y envía cada conversión al servidor.

## Requisitos
- JDK 17 o superior y Maven (NetBeans ya trae Maven).
- Servidor encendido en `http://192.168.137.1:5014` (API en `/api/conversor`).

## Ejecutar
- NetBeans: File > Open Project > `CONUNI_CLIENTE_CONSOLA` > Run.
- Terminal: doble clic en `CONUNI_CLIENTE_CONSOLA/ejecutar.bat`, o `mvn package` y
  `java -jar target/CONUNI_CLIENTE_CONSOLA-1.0-jar-with-dependencies.jar`.
- Otro servidor: `java -Dconuni.url=http://localhost:5014/api/conversor -jar ...`

## Credenciales
`MONSTER` / `Monster9` (las valida el servidor).

## Endpoints que consume
| CU | Método y ruta |
|---|---|
| CU-01 | `POST /api/conversor/login` |
| CU-02 | `GET /api/conversor/categorias` |
| CU-03 | `GET /api/conversor/categorias/{categoria}/unidades` |
| CU-04 | `POST /api/conversor/convertir` |

## Catálogo (lo envía el servidor)
| Categoría | Unidades |
|---|---|
| LONGITUD | MILIMETRO, CENTIMETRO, METRO, KILOMETRO, MILLA, PIE |
| MASA | MILIGRAMO, GRAMO, KILOGRAMO, LIBRA |
| TEMPERATURA | CELSIUS, FAHRENHEIT, KELVIN |

## Configuración (`controlador/Configuracion.java`)
- `URL_BASE`: `http://192.168.137.1:5014/api/conversor` (o `-Dconuni.url=...`).
- `DECIMALES`: decimales máximos del resultado (4).
- `PERMITIR_CERRAR_SESION`: muestra la opción "Cerrar sesión" (apagada; el taller no la pide).
````

`INICIO_RAPIDO.md` queda con las secciones "Requisitos", "Ejecutar" y "Credenciales" del README.

### 5.13 `.gitignore`

```gitignore
target/
nbproject/private/
nbactions.xml
*.class
```

---

## 6. Casos de uso del taller en este cliente

| CU | Dónde se cumple | Cómo se ve en consola |
|---|---|---|
| CU-01 Autenticarse | `iniciarSesion` → `ApiService.login` → `POST /login` | `🔐 Verificando credenciales...`, `✅ Login exitoso!`, Sullivan, `👤 Bienvenido/a: MONSTER` |
| CU-02 Consultar catálogo | `menuPrincipal` → `obtenerCategorias` | `1. 📏 Conversiones de LONGITUD` … |
| CU-03 Consultar unidades | `menuConversiones` → `obtenerUnidades` | Lista numerada de unidades |
| CU-04 Realizar conversión | `realizarConversion` → `POST /convertir` | `✅ RESULTADO DE LA CONVERSIÓN` / `25 CELSIUS = 77 FAHRENHEIT` |
| CU-05 Validar token | Servidor; el cliente envía `Authorization: Bearer` en `ApiService.enviar` | — |
| CU-06 Validar datos | Servidor; el cliente solo verifica que el valor sea número | `❌ Error: Debe ingresar un número válido` |
| CU-07 Generar respuesta | Servidor; el cliente imprime `mensaje` | `Conversion realizada correctamente` bajo el resultado |
| CU-08 Token inválido | `ApiService.enviar` detecta `AUTH-001` y borra la sesión | `❌ [AUTH-001] Token invalido o sesion expirada` → `🔒 Su sesión terminó...` → login |
| CU-09 Sesión expirada | Igual con `AUTH-002` | Igual, con el mensaje de AUTH-002 |
| CU-10 Unidad no soportada | `mostrarError` | `❌ [UNIT-001] Unidad de origen no soportada: XYZ` |
| CU-11 Datos inconsistentes | `mostrarError` | `❌ [DATA-001] ...` |

Con el menú numerado el usuario no puede escribir una unidad inexistente, así que CU-10 y CU-11 solo aparecen si el catálogo cambia en el servidor. El cliente evita el error y el servidor lo valida igual.

---

## 7. Flujo de la consola

```
ConsolaApp.main  (System.out en UTF-8)
 ├─ banner + verificarConexionServidor()          (informativo)
 └─ ciclo
     ├─ iniciarSesion()  ── 3 fallos ──> pista y fin
     │      └─ POST /login → token en SesionUsuario
     ├─ menuPrincipal()
     │      ├─ GET /categorias
     │      ├─ n. categoría → menuConversiones()
     │      │       ├─ GET /categorias/{c}/unidades
     │      │       ├─ origen, destino, valor  (0 = volver)
     │      │       └─ POST /convertir → resultado o error
     │      ├─ verificar conexión
     │      ├─ [cerrar sesión]  solo si PERMITIR_CERRAR_SESION ──> login
     │      └─ salir ──> fin
     └─ AUTH-001 / AUTH-002 ──> "Su sesión terminó" ──> login
```

---

## 8. Configuración, ejecución y verificación

### 8.1 Creación del proyecto

1. Crea `04. CLICON/CONUNI_CLIENTE_CONSOLA/` con los archivos de la sección 5 (en NetBeans: New Project > Java with Maven > Java Application, `groupId` `ec.edu.monster`, `artifactId` `CONUNI_CLIENTE_CONSOLA`, y reemplaza el `pom.xml` y las clases).
2. Si en `04. CLICON/` hubiera archivos del cliente Python (sección 2.1), elimínalos o muévelos a `_respaldo_python/` fuera de la entrega. En este proyecto no los hay.
3. Escribe `README.md` e `INICIO_RAPIDO.md` (sección 5.12).

### 8.2 Ejecutar

| Dónde | Cómo | Nota |
|---|---|---|
| NetBeans | Run (F6) | La ventana Output no oculta la contraseña (`System.console()` es `null` en el IDE) y puede no dibujar los emojis |
| Terminal | `ejecutar.bat` | Hace `chcp 65001`. En Windows Terminal se ven emojis y Sullivan; en la consola clásica (`conhost`) algunos emojis salen como cuadros, el resto funciona igual |
| Terminal manual | `mvn package` y `java -jar target\CONUNI_CLIENTE_CONSOLA-1.0-jar-with-dependencies.jar` | — |

El servidor debe estar corriendo: `dotnet run --launch-profile http` en `04. SERVIDOR/WS_ConUnid_DOTNET_RESTFUL_GR02/WS_ConUnid_DOTNET_RESTFUL_GR02` (escucha en `http://0.0.0.0:5014`). `192.168.137.1` solo existe con el hotspot de Windows encendido en la PC servidor; para la misma PC usa `-Dconuni.url=http://localhost:5014/api/conversor`.

### 8.3 Pruebas realizadas contra el servidor GR02

El código de la sección 5 se compiló con Maven 3.9.16 y JDK 21 (`release 17`) y se ejecutó el 2026-10-25 contra `04. SERVIDOR` (`http://localhost:5014/api/conversor`), alimentando la consola por tubería. Las pruebas 11 y 12 usaron copias temporales (bandera en `true` y `.bat` con `localhost`):

| # | Acción | Resultado obtenido |
|---|---|---|
| 1 | Servidor encendido, iniciar | `✅ Servidor ACTIVO` |
| 2 | Login `MONSTER` / `x` | `❌ [AUTH-003] Credenciales incorrectas`, `📌 Intentos restantes: 2` |
| 3 | Login `MONSTER` / `Monster9` | `✅ Login exitoso!`, Sullivan, `👤 Bienvenido/a: MONSTER` |
| 4 | Menú principal | `LONGITUD`, `MASA`, `TEMPERATURA`, `4. Verificar`, `5. Salir` (sin cerrar sesión) |
| 5 | TEMPERATURA, CELSIUS → FAHRENHEIT, 25 | `25 CELSIUS = 77 FAHRENHEIT` |
| 6 | LONGITUD, KILOMETRO → MILLA, `1,5` | `1.5 KILOMETRO = 0.9321 MILLA` (acepta coma) |
| 7 | Valor `abc` | `❌ Error: Debe ingresar un número válido` (no se envía) |
| 8 | Opción `9` | `❌ Opción no válida. Por favor, seleccione una opción del 1 al 5.` |
| 9 | Reiniciar el servidor con la sesión abierta y elegir una categoría | `❌ [AUTH-001] Token invalido o sesion expirada` → `🔒 Su sesión terminó` → login |
| 10 | Servidor apagado | `⚠️ No se pudo conectar con el servidor`; 3 intentos con `❌ No se puede conectar con el servidor en ...` y la pista final |
| 11 | `PERMITIR_CERRAR_SESION = true` | Aparece `5. 🔒 Cerrar sesión`, `6. 🚪 Salir`; cerrar sesión vuelve al login |
| 12 | `ejecutar.bat` con la entrada redirigida | Login, `25 CELSIUS = 77 FAHRENHEIT` y `👋 Saliendo...` |

Para repetir las pruebas 2–8 sin teclear (PowerShell, dentro de `CONUNI_CLIENTE_CONSOLA`, servidor en la misma PC):

```powershell
Set-Content -Encoding ascii entrada.txt "MONSTER","x","MONSTER","Monster9","3","1","2","25","","0","9","5"
cmd /c "java -Dconuni.url=http://localhost:5014/api/conversor -jar target\CONUNI_CLIENTE_CONSOLA-1.0-jar-with-dependencies.jar < entrada.txt"
```

> No uses `"texto" | java ...` en PowerShell 5.1: antepone un BOM UTF-8 al texto, el usuario llega como `﻿MONSTER` y el servidor responde AUTH-003. Al teclear no pasa. Por lo mismo `ejecutar.bat` usa `chcp 65001 > nul < nul`: sin el `< nul`, `chcp` se come la entrada redirigida.

### 8.4 Extras configurables

| Extra | Cómo activarlo | Efecto |
|---|---|---|
| Decimales | `Configuracion.DECIMALES = 2` | `0.62` en lugar de `0.6214`. Con 0 se redondea a entero |
| Cerrar sesión | `Configuracion.PERMITIR_CERRAR_SESION = true` | Agrega la opción al menú. Solo borra el token en el cliente: el servidor no tiene logout y el token vence a los 30 min |

### 8.5 Documentación

En este proyecto `README.md` solo tenía el título del repositorio; se escribe con la sección 5.12. Si heredas los README del cliente Python (dicen "funciona offline", "el servidor solo registra" y ofrecen "Toneladas"), reemplázalos igual.

---

## 9. Decisiones tomadas

| # | Punto | Decisión |
|---|---|---|
| 1 | Lenguaje | Java, porque el `.docx` dice *"Aplicación Java ejecutada desde línea de comandos"* |
| 2 | Toneladas | El `.docx` no las incluye (masa: miligramo, gramo, kilogramo, libra). El catálogo sale del servidor |
| 3 | Cerrar sesión | El `.docx` no lo pide y el servidor no tiene endpoint. Desactivado con `PERMITIR_CERRAR_SESION = false`; el código ya está listo |
| 4 | Decimales | El `.docx` no lo especifica (solo el ejemplo 5 KM → 5000 M). Extra configurable en `DECIMALES`, por defecto 4 |
| 5 | Pista `💡 Usuario = MONSTER, Contraseña = Monster9` | Se mantiene porque estaba en el cliente Python; bórrala de `ejecutar()` si el docente lo pide |
| 6 | Estructura | 3 carpetas `controlador`/`modelo`/`vista` y paquete `ec.edu.monster`, como el servidor base |
