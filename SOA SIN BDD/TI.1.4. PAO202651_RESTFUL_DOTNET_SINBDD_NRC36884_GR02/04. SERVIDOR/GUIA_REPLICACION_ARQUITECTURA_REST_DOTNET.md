# Guía de replicación de arquitectura: Servidor RESTful .NET sin BDD

Esta guía sirve para crear servidores **REST en ASP.NET Core (C#)** con la **misma arquitectura, estructura, nombres, idioma y estilo** que la guía SOAP Java (`GUIA_REPLICACION_ARQUITECTURA.md`), adaptada a .NET. Toma como base el proyecto `ConUni_Rest_Dotnet_GR05` (Conversor de Unidades, Grupo 5). Si la sigues paso a paso, el resultado es idéntico en **forma**: las mismas carpetas, el mismo estilo de clases y los mismos archivos de configuración.

Tiene tres partes:

1. **Reglas de estilo y convenciones**: lo que nunca cambia.
2. **Plantillas archivo por archivo**: código de referencia, ya compilado y probado (17 tests xUnit en verde y pruebas HTTP de cada endpoint).
3. **Procedimiento para crear un proyecto nuevo**: una lista de verificación y un ejemplo completo.

---

## 🤖 Instrucciones para quien construya el proyecto (persona o IA)

Si esta guía está en una carpeta junto con un documento de requisitos (`.docx`), sigue estos pasos en orden:

1. **Lee el `.docx` completo**: el texto, las tablas y **también las imágenes** (diagramas de casos de uso, actividad, secuencia, componentes). Las imágenes están dentro del archivo, en `word/media/`; si el texto remite a un diagrama o le falta información, extráelas y revísalas.
2. **Extrae del documento** lo funcional:
   - Alcance y categorías.
   - Operaciones del servicio con sus nombres y parámetros.
   - Unidades y fórmulas.
   - Forma de la respuesta.
   - Reglas de autenticación (token, expiración).
   - Códigos y textos de error.
   - Validaciones.
3. **Traduce la tecnología:** si el documento habla de SOAP, JAX-WS, WSDL, NetBeans o Java, esa parte **no aplica**. El servidor se construye en REST con ASP.NET Core siguiendo esta guía (la sección 4.1 muestra la equivalencia de operación SOAP a endpoint REST).
4. **Si el documento es ambiguo o se contradice** (por ejemplo, menciona 6 categorías en un lugar y exige 3 en otro), **pregunta antes de construir**. No inventes ni rellenes con lo que hacía el proyecto original. La sección 6 lista las ambigüedades que ya se conocen del documento de Conversor de Unidades.
5. **Obtén los valores de los marcadores** (sección 0): `{SIGLA}`, `{GR}`, `{PROYECTO}`, `{Controlador}`, `{ruta}`. Si no están en el documento ni en el pedido, pregúntalos.
6. **Construye el proyecto aplicando las reglas de esta guía** a todo lo que extrajiste: las 8 carpetas, los nombres, el idioma (sin ñ ni tildes), el estilo y los archivos de configuración.
7. **Verifica** con `dotnet build`, `dotnet test` y las peticiones del archivo `.http`. Después revisa el resultado contra esta guía punto por punto (carpetas, namespaces, nombres, ausencia de tildes) y contra el documento (cada requisito tiene su implementación).
8. **Reporta** qué requisito del documento cubre cada clase o endpoint, y qué decisiones se tomaron sobre los puntos ambiguos.

---

## ⚠️ Cómo se usa esta guía junto con el documento de requisitos

Cada proyecto nuevo se construye con **dos recursos**, y cada uno manda en algo distinto:

| Recurso | Manda en | Ejemplos |
|---|---|---|
| **Documento de requisitos** (`.docx` del proyecto) | **TODO lo funcional** | Operaciones y sus nombres, parámetros, categorías y unidades, forma de la respuesta, validación y expiración del token, códigos y textos de error, catálogos, alcance |
| **Esta guía** (`.md`) | **La tecnología y la forma** | REST + ASP.NET Core + C#, Visual Studio, namespace raíz `ec.edu.monster`, las 8 carpetas, prefijos y sufijos de clases, idioma español, sin ñ ni tildes, `Program.cs`, `.csproj`, `launchSettings.json`, CORS, filtro de token, middleware de errores, tests xUnit y archivo `.http` |

**Reglas de prioridad:**

1. Si el documento pide algo funcional, **se implementa como lo dice el documento**, aunque el proyecto original lo haga distinto. Ejemplos: respuesta estructurada, tokens que expiran, operaciones de catálogo.
2. Si el documento nombra una **tecnología** distinta (SOAP, JAX-WS, NetBeans, Java Swing), **manda esta guía**: el servidor es REST en .NET. Lo funcional de esas secciones (qué operaciones hay y qué devuelven) sí se respeta.
3. Si el proyecto original tiene algo que el documento **no pide**, **no se incluye**. Ejemplos: `cambiarContrasenia`, cerrar sesión, unidades como `YARDA` o `RANKINE`.
4. Los **nombres de operaciones, parámetros y unidades** salen del documento. Se escriben en español, sin ñ ni tildes, y con las convenciones de la sección 1: enums en MAYÚSCULAS, métodos y propiedades en PascalCase y JSON en camelCase.
5. **Siempre existen exactamente estas 8 carpetas de código, con estos nombres, ni una más ni una menos.** Son el equivalente directo de los 8 Source Packages de la guía Java:

   | # | Carpeta (= namespace) | Qué contiene | Equivale en Java a |
   |---|---|---|---|
   | 1 | `ec.edu.monster.controladores.api` | El controlador REST (`Controlador...`) | `controladores.ws` |
   | 2 | `ec.edu.monster.controladores.manejadores` | Filtro de token y middleware de errores (`Manejador...`) | `controladores.manejadoresws` |
   | 3 | `ec.edu.monster.modelos.utilidades` | Interfaz `I...`, clases de cálculo (`Conversor...`), solicitudes (`Solicitud...`), respuestas (`Respuesta...`), `CodigosRespuesta` y `ExcepcionNegocio` | `modelos.utilidades` |
   | 4 | `ec.edu.monster.modelos.utilidades.enums` | Enums (`Unidad...`, `Categoria`, `Estado...`) | `modelos.utilidades.enums` |
   | 5 | `ec.edu.monster.modelos.utilidades.mapeadores` | Mapeadores de string a enum (`...Mapper`) | `modelos.utilidades.mapeadores` |
   | 6 | `ec.edu.monster.pruebas` | Peticiones HTTP de prueba manual (`.http`) | `pruebas` (los `main` con `Scanner`) |
   | 7 | `ec.edu.monster.seguridad` | Credenciales, tokens y CORS (`Administrador...`, `ConfiguracionCors`) | `seguridad` |
   | 8 | `ec.edu.monster.servicios` | Servicios de negocio (`Servicio...`) | `servicios` |

   Los tests xUnit van en un **proyecto aparte**, `{PROYECTO}.Tests`, sin namespace (el equivalente de los *Test Packages* en el paquete por defecto).

   - Si el documento propone otra estructura (por ejemplo `com.convertidor.service/business/domain/security`), **se ignora** y cada pieza se ubica en una de las 8 carpetas según la tabla.
   - **No se crean carpetas nuevas** (nada de `Controllers`, `Models`, `DTOs`, `Excepciones`, `Util`, etc.). Toda clase nueva entra en una de las 8.
   - **No se elimina ninguna:** las 8 deben tener al menos un archivo, aunque el documento no pida algo específico para esa carpeta.
6. **Sin ñ ni tildes en el código:** identificadores, mensajes de error, textos de respuesta y comentarios. Se escribe `"Unidad no soportada"`, `"Conversion realizada correctamente"`, `"Token invalido o sesion expirada"`.

---

## 0. Variables de la plantilla

En toda la guía se usan estos marcadores. Cámbialos por los valores del proyecto nuevo.

| Marcador | Valor en el proyecto original | Ejemplo para otro proyecto |
|---|---|---|
| `{SIGLA}` | `ConUni` (Conversor de Unidades) | `ConMon` (Conversor de Monedas) |
| `{GR}` | `GR05` | `GR03` |
| `{PROYECTO}` | `ConUni_Rest_Dotnet_GR05` | `ConMon_Rest_Dotnet_GR03` |
| `{Controlador}` | `ControladorConversorUnidades` | `ControladorConversorMonedas` |
| `{ruta}` (prefijo de la API) | `api/conversor` | `api/conversor` |
| `{PUERTO}` | `5014` (HTTP) / `7102` (HTTPS) | `5014` / `7102` |
| `{IP_RED}` (IP que usan los clientes) | `192.168.137.1` | la IP de tu PC en la red |
| `{Dominio}` (familias de operación) | `Longitud`, `Masa`, `Temperatura` | `Divisa`, `Criptomoneda` |
| `{Operacion}` (verbo del negocio) | `Convertir` | `Convertir` / `Calcular` |
| `{IOperador}` (interfaz genérica) | `IConversor<T>` | `IConversor<T>` / `ICalculadora<T>` |
| `{Servicio}` (envoltorio genérico) | `ServicioConversor<T>` | `ServicioConversor<T>` |
| `{USUARIO}` / `{CLAVE}` | `MONSTER` / `Monster9` | `MONSTER` / `Monster9` |

> **Regla de oro:** el namespace raíz **siempre** es `ec.edu.monster`, sin el nombre del proyecto delante. Visual Studio genera por defecto `namespace {PROYECTO}.ec.edu.monster.modelo` al crear una clase dentro de una carpeta: **se borra el prefijo a mano** y queda `namespace ec.edu.monster.modelos.utilidades`.

---

## 1. Reglas de estilo y convenciones (no negociables)

### 1.1 Idioma
- **Todo el código está en español:** clases, métodos, propiedades, variables, parámetros, mensajes y carpetas.
- Las únicas excepciones son los términos técnicos ya consagrados: `Mapper` (`UnidadMapper`), `ToLongitud`/`ToMasa` (prefijo `To` de mapeo), `token`, `login`, `api`, `Cors`, `Bearer`, `Test`, y los nombres impuestos por ASP.NET (`Program`, `InvokeAsync`, `OnAuthorization`).
- **Sin tildes ni eñes en el código** (identificadores, mensajes y comentarios): `Contrasenia` (no `Contraseña`), `"Categoria inexistente: "`, `"Conversion no soportada"`.
- Prefijos de rol en español:
  - `Controlador...`: el controlador REST (`ControladorConversorUnidades`).
  - `I...`: la interfaz (`IConversor`).
  - `Servicio...`: la lógica de negocio (`ServicioConversor`, `ServicioConversion`, `ServicioCatalogo`, `ServicioAutenticacion`).
  - `Conversor...`: la implementación concreta del cálculo (`ConversorLongitud`).
  - `Unidad...`, `Categoria`, `Estado...`: los enums.
  - `Solicitud...` / `Respuesta...`: los cuerpos JSON de entrada y salida.
  - `Administrador...`: las clases estáticas de seguridad (`AdministradorTokens`, `AdministradorCredenciales`).
  - `Manejador...`: el filtro de token y el middleware de errores (`ManejadorAutenticacion`, `ManejadorErrores`).
  - `Configuracion...`: extensiones de arranque (`ConfiguracionCors`).
  - `...Test`: las clases de test xUnit (`ConversorMasaTest`).
- Nombres de campos del contrato: `valor`, `unidadOrigen`, `unidadDestino`, `categoria`, `usuario`, `contrasenia`, `token`.
- Métodos de test con forma de oración: `DeberiaConvertirCorrectamente`, `DeberiaConvertirTemperatura`, `DeberiaRechazarSolicitudInvalida`.

### 1.2 Forma del código
- C# con `Nullable` e `ImplicitUsings` activados (como lo genera la plantilla).
- **Namespaces con llaves** (bloque), igual que el `ConversionUnidades.cs` original. No se usan namespaces de archivo (`namespace x;`).
- Sangría de 4 espacios. Las llaves van en **línea propia** (estilo C# estándar).
- `switch` como expresión (`=>`) con `_ => throw ...` como caso por defecto, y `using static` del enum en el conversor de temperatura (equivalente al `import static` de Java).
- Un constructor vacío explícito en cada conversor: `public ConversorX() { }`.
- En la interfaz, el método se declara con `public` explícito: `public double Convertir(double valor, T unidadOrigen, T unidadFinal);`.
- Los campos de servicio son `private readonly`, **sin guion bajo**, y se inicializan en línea con `new(...)`, partiendo la línea después del `=`:
  ```csharp
  private readonly ServicioConversor<UnidadLongitud> servicioLongitud =
          new(new ConversorLongitud());
  ```
- Propiedades en PascalCase (`UnidadOrigen`). ASP.NET las serializa en camelCase (`unidadOrigen`) sin configurar nada.
- Errores:
  - En el modelo (conversores): `ArgumentException` con `"Unidad no soportada"` y `"Conversion no soportada"`.
  - En mapeadores y servicios: `ExcepcionNegocio(codigo, mensaje, estadoHttp)`, con el código tomado de `CodigosRespuesta`.
  - En el servicio genérico: `InvalidOperationException("No se ha inyectado un conversor")`.
  - Nunca se devuelve un error sin cuerpo: todo error sale como JSON `{ estado, codigo, mensaje }` (ver 3.15 y 3.16).
- Casi sin comentarios. Solo se conservan los comentarios que trae la plantilla en `Program.cs`.
- **No hay inyección de dependencias para la lógica**: los servicios y conversores se instancian con `new` dentro del controlador, como en Java. Lo único que se registra en `Program.cs` es lo propio de ASP.NET (controladores, filtro, CORS, OpenAPI).
- No hay base de datos. Credenciales y tokens viven en memoria (se pierden al reiniciar).

### 1.3 Estructura del repositorio padre (entregable académico)
```
TI1.4 RESTFUL_DOTNET_{SIGLA}_{GR}/
├── 01. SERVIDOR/
│   ├── {PROYECTO}/                         ← la solución (ver sección 2)
│   ├── GUIA_REPLICACION_ARQUITECTURA.md    (guía SOAP Java)
│   ├── GUIA_REPLICACION_ARQUITECTURA_REST_DOTNET.md   (esta guía)
│   └── TALLER CASOS DE USO CONVERSION UNIDADES.docx   (requisitos)
├── 02. CLICON/                             Cliente de consola
├── 02. CLIESC/                             Cliente de escritorio
├── 02. CLIEWEB/                            Cliente web
└── 02.  CLIMOV/                            Cliente móvil Android
```
> Respeta los nombres tal cual: `02.  CLIMOV` lleva **dos** espacios después del punto; las demás carpetas llevan uno.

---

## 2. Estructura del servidor (`01. SERVIDOR/{PROYECTO}`)

```
{PROYECTO}/
├── {PROYECTO}.sln
├── {PROYECTO}/                                      (proyecto ASP.NET Core Web API)
│   ├── {PROYECTO}.csproj
│   ├── Program.cs
│   ├── appsettings.json
│   ├── appsettings.Development.json
│   ├── Properties/launchSettings.json
│   ├── ec.edu.monster.controladores.api/
│   │   └── {Controlador}.cs
│   ├── ec.edu.monster.controladores.manejadores/
│   │   ├── ManejadorAutenticacion.cs                (filtro: valida el token)
│   │   └── ManejadorErrores.cs                      (middleware: errores a JSON)
│   ├── ec.edu.monster.modelos.utilidades/
│   │   ├── IConversor.cs
│   │   ├── Conversor{Dominio}.cs                    (uno por dominio)
│   │   ├── CodigosRespuesta.cs
│   │   ├── ExcepcionNegocio.cs
│   │   ├── SolicitudLogin.cs / SolicitudConversion.cs
│   │   └── Respuesta.cs / RespuestaAutenticacion.cs / RespuestaCatalogo.cs /
│   │       RespuestaUnidades.cs / RespuestaConversion.cs
│   ├── ec.edu.monster.modelos.utilidades.enums/
│   │   ├── Unidad{Dominio}.cs                       (uno por dominio)
│   │   ├── Categoria.cs
│   │   └── EstadoToken.cs
│   ├── ec.edu.monster.modelos.utilidades.mapeadores/
│   │   ├── UnidadMapper.cs
│   │   └── CategoriaMapper.cs
│   ├── ec.edu.monster.pruebas/
│   │   └── {SIGLA}.http                             (peticiones manuales, NO son tests)
│   ├── ec.edu.monster.seguridad/
│   │   ├── AdministradorCredenciales.cs
│   │   ├── AdministradorTokens.cs
│   │   └── ConfiguracionCors.cs
│   └── ec.edu.monster.servicios/
│       ├── ServicioConversor.cs                     (envoltorio genérico)
│       ├── ServicioConversion.cs                    (valida y despacha por categoría)
│       ├── ServicioCatalogo.cs
│       └── ServicioAutenticacion.cs
└── {PROYECTO}.Tests/                                (proyecto xUnit, AL LADO del web, no dentro)
    ├── {PROYECTO}.Tests.csproj
    ├── Conversor{Dominio}Test.cs
    └── ServicioConversionTest.cs
```

> Las carpetas llevan **puntos en el nombre** (`ec.edu.monster.modelos.utilidades`), igual que el proyecto original (`ec.edu.monster.modelo`). Así el Explorador de soluciones se ve como los paquetes de Java.
>
> ⚠️ El proyecto de tests va **al lado** del proyecto web. Si lo pones dentro de su carpeta, el proyecto web intenta compilar los `.cs` de los tests y falla.
>
> Se eliminan los restos de la plantilla: `WeatherForecast.cs`, `Controllers/WeatherForecastController.cs`, la carpeta `Controllers`, el `.http` de la raíz y las carpetas `ec.edu.monster.controlador`, `ec.edu.monster.modelo` y `ec.edu.monster.vista` del borrador inicial.

### Capas y flujo de una petición
```
Cliente (consola / escritorio / web / móvil)
   │  HTTP + JSON, header  Authorization: Bearer <token>
   ▼
ManejadorErrores (middleware)        → controladores.manejadores   atrapa todo error y responde JSON
   ▼
CORS (ConfiguracionCors)             → seguridad
   ▼
ManejadorAutenticacion (filtro)      → controladores.manejadores   valida el token, deja pasar "Login"
   ▼
{Controlador} [ApiController]        → controladores.api           solo recibe y responde
   ▼
ServicioConversion / ServicioCatalogo / ServicioAutenticacion → servicios
   │  string ──► CategoriaMapper / UnidadMapper ──► enum   (ExcepcionNegocio si no existe)
   ▼
ServicioConversor<T> (envoltorio genérico)                   → servicios
   ▼
IConversor<T> ──► Conversor{Dominio}                         → modelos.utilidades
                   └─ enums/Unidad{Dominio}
```

---

## 3. Plantillas archivo por archivo

### 3.1 `{PROYECTO}.csproj`
```xml
<Project Sdk="Microsoft.NET.Sdk.Web">

  <PropertyGroup>
    <TargetFramework>net9.0</TargetFramework>
    <Nullable>enable</Nullable>
    <ImplicitUsings>enable</ImplicitUsings>
  </PropertyGroup>

  <ItemGroup>
    <PackageReference Include="Microsoft.AspNetCore.OpenApi" Version="9.0.9" />
  </ItemGroup>

</Project>
```
> ⚠️ **Versión de .NET:** el original apunta a `net9.0`. Si en tu PC solo tienes el SDK y el runtime de **.NET 10** (`dotnet --list-runtimes`), el proyecto compila pero **no arranca**. Hay dos opciones: instalar el runtime ASP.NET Core 9, o cambiar a `<TargetFramework>net10.0</TargetFramework>` y `Microsoft.AspNetCore.OpenApi` `10.0.x`. El código de esta guía se probó en `net10.0` y no usa nada exclusivo de esa versión.

### 3.2 `Properties/launchSettings.json`
```json
{
  "profiles": {
    "http": {
      "commandName": "Project",
      "environmentVariables": {
        "ASPNETCORE_ENVIRONMENT": "Development"
      },
      "dotnetRunMessages": true,
      "applicationUrl": "http://0.0.0.0:5014"
    },
    "https": {
      "commandName": "Project",
      "environmentVariables": {
        "ASPNETCORE_ENVIRONMENT": "Development"
      },
      "dotnetRunMessages": true,
      "applicationUrl": "https://localhost:7102;http://localhost:5014"
    }
  },
  "$schema": "https://json.schemastore.org/launchsettings.json"
}
```
- `0.0.0.0` hace que el servidor escuche en **todas** las interfaces de red, así lo alcanzan `localhost`, el emulador Android (`10.0.2.2`) y los dispositivos de la red (`{IP_RED}`).
- El original usaba `http://192.168.137.1:5014`. Funciona, pero **si esa interfaz no existe** (por ejemplo, con la zona de cobertura de Windows apagada), el servidor no arranca.
- Para que un móvil de la red llegue al servidor, abre el puerto en el firewall (PowerShell como administrador):
  ```powershell
  New-NetFirewallRule -DisplayName "{PROYECTO} {PUERTO}" -Direction Inbound -Protocol TCP -LocalPort 5014 -Action Allow
  ```

### 3.3 `appsettings.json` y `appsettings.Development.json` (sin cambios de la plantilla)
```json
{
  "Logging": {
    "LogLevel": {
      "Default": "Information",
      "Microsoft.AspNetCore": "Warning"
    }
  },
  "AllowedHosts": "*"
}
```

### 3.4 `Program.cs`
Se conserva la forma de la plantilla y sus comentarios. Solo se agregan el filtro de token, la respuesta de datos inválidos, CORS y el middleware de errores. `UseAuthorization` se quita porque no se usa la autorización de ASP.NET (el token lo valida `ManejadorAutenticacion`).
```csharp
using ec.edu.monster.controladores.manejadores;
using ec.edu.monster.seguridad;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.AddControllers(opciones => opciones.Filters.Add<ManejadorAutenticacion>())
    .ConfigureApiBehaviorOptions(opciones =>
        opciones.InvalidModelStateResponseFactory = ManejadorErrores.RespuestaDatosInvalidos);
builder.Services.AgregarCors();
// Learn more about configuring OpenAPI at https://aka.ms/aspnet/openapi
builder.Services.AddOpenApi();

var app = builder.Build();

app.UseMiddleware<ManejadorErrores>();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

// Comentado para desarrollo solo con HTTP
// app.UseHttpsRedirection();

app.UseCors(ConfiguracionCors.POLITICA);

app.MapControllers();

app.Run();
```
> El orden importa: `ManejadorErrores` va **primero** para atrapar cualquier excepción del resto del pipeline, y `UseCors` va **antes** de `MapControllers` para responder las peticiones `OPTIONS` (preflight) sin pedir token.

### 3.5 Modelo: enums (`ec.edu.monster.modelos.utilidades.enums/`)
Un enum por dominio, con valores en MAYÚSCULAS, en español y en singular. Los clientes envían estos nombres como texto (en mayúsculas o minúsculas).
```csharp
namespace ec.edu.monster.modelos.utilidades.enums
{
    public enum UnidadLongitud
    {
        MILIMETRO,
        CENTIMETRO,
        METRO,
        KILOMETRO,
        MILLA,
        PIE
    }
}
```
```csharp
namespace ec.edu.monster.modelos.utilidades.enums
{
    public enum UnidadMasa
    {
        MILIGRAMO,
        GRAMO,
        KILOGRAMO,
        LIBRA
    }
}
```
```csharp
namespace ec.edu.monster.modelos.utilidades.enums
{
    public enum UnidadTemperatura
    {
        CELSIUS,
        FAHRENHEIT,
        KELVIN
    }
}
```
Además, uno con las categorías del catálogo y otro con el resultado de validar un token:
```csharp
namespace ec.edu.monster.modelos.utilidades.enums
{
    public enum Categoria
    {
        LONGITUD,
        MASA,
        TEMPERATURA
    }
}
```
```csharp
namespace ec.edu.monster.modelos.utilidades.enums
{
    public enum EstadoToken
    {
        VALIDO,
        INVALIDO,
        EXPIRADO
    }
}
```

### 3.6 Modelo: interfaz genérica (`modelos.utilidades/IConversor.cs`)
```csharp
namespace ec.edu.monster.modelos.utilidades
{
    public interface IConversor<T>
    {
        public double Convertir(double valor, T unidadOrigen, T unidadFinal);
    }
}
```

### 3.7 Modelo: conversor **por factores** (patrón A)
Úsalo cuando la conversión es lineal y proporcional (longitud, masa, volumen, área, tiempo, monedas con tasa fija). Cada unidad tiene un factor respecto a la **unidad base** (factor `1.0`). La fórmula es `valor * factor(origen) / factor(destino)`, la misma que pide el documento (`valor × factorOrigen / factorDestino`).
```csharp
using ec.edu.monster.modelos.utilidades.enums;

namespace ec.edu.monster.modelos.utilidades
{
    public class ConversorLongitud : IConversor<UnidadLongitud>
    {
        private readonly Dictionary<UnidadLongitud, double> factores = new()
        {
            { UnidadLongitud.MILIMETRO, 0.001 },
            { UnidadLongitud.CENTIMETRO, 0.01 },
            { UnidadLongitud.METRO, 1.0 },
            { UnidadLongitud.KILOMETRO, 1000.0 },
            { UnidadLongitud.MILLA, 1609.344 },
            { UnidadLongitud.PIE, 0.3048 }
        };

        public ConversorLongitud() { }

        public double Convertir(double valor, UnidadLongitud unidadOrigen, UnidadLongitud unidadFinal)
        {
            if (!factores.ContainsKey(unidadOrigen) || !factores.ContainsKey(unidadFinal))
            {
                throw new ArgumentException("Unidad no soportada");
            }

            double valorEnBase = valor * factores[unidadOrigen];

            return valorEnBase / factores[unidadFinal];
        }
    }
}
```
`ConversorMasa` es idéntico, cambiando el tipo y los factores. Su base es el **KILOGRAMO**, como indica el documento:
```csharp
        private readonly Dictionary<UnidadMasa, double> factores = new()
        {
            { UnidadMasa.MILIGRAMO, 0.000001 },
            { UnidadMasa.GRAMO, 0.001 },
            { UnidadMasa.KILOGRAMO, 1.0 },
            { UnidadMasa.LIBRA, 0.45359237 }
        };
```
> En C# los enums no pueden ser `null`, así que no hace falta el `"Las unidades no pueden ser null"` de Java: los `null` se rechazan antes, en `ServicioConversion` (3.13).

### 3.8 Modelo: conversor **por switch** (patrón B)
Úsalo cuando la conversión no es proporcional (tiene desplazamientos, como la temperatura). Lleva `using static` del enum, devuelve `valor` sin cambios si `origen == destino` y usa un `switch` anidado con `_ => throw`.
```csharp
using ec.edu.monster.modelos.utilidades.enums;
using static ec.edu.monster.modelos.utilidades.enums.UnidadTemperatura;

namespace ec.edu.monster.modelos.utilidades
{
    public class ConversorTemperatura : IConversor<UnidadTemperatura>
    {
        public ConversorTemperatura() { }

        public double Convertir(double valor, UnidadTemperatura origen, UnidadTemperatura destino)
        {
            if (origen == destino)
            {
                return valor;
            }

            return origen switch
            {
                CELSIUS => destino switch
                {
                    FAHRENHEIT => (valor * 9 / 5) + 32,
                    KELVIN => valor + 273.15,
                    _ => throw new ArgumentException("Conversion no soportada")
                },

                FAHRENHEIT => destino switch
                {
                    CELSIUS => (valor - 32) * 5 / 9,
                    KELVIN => (valor - 32) * 5 / 9 + 273.15,
                    _ => throw new ArgumentException("Conversion no soportada")
                },

                KELVIN => destino switch
                {
                    CELSIUS => valor - 273.15,
                    FAHRENHEIT => (valor - 273.15) * 9 / 5 + 32,
                    _ => throw new ArgumentException("Conversion no soportada")
                },

                _ => throw new ArgumentException("Unidad no soportada")
            };
        }
    }
}
```

### 3.9 Modelo: códigos y excepción de negocio
Los códigos de respuesta se centralizan en una sola clase, para que ningún texto de código quede repetido a mano.
```csharp
namespace ec.edu.monster.modelos.utilidades
{
    public static class CodigosRespuesta
    {
        public const string ESTADO_OK = "OK";
        public const string ESTADO_ERROR = "ERROR";

        public const string EXITO = "200";
        public const string TOKEN_INVALIDO = "AUTH-001";
        public const string SESION_EXPIRADA = "AUTH-002";
        public const string CREDENCIALES_INCORRECTAS = "AUTH-003";
        public const string UNIDAD_NO_SOPORTADA = "UNIT-001";
        public const string DATOS_INCONSISTENTES = "DATA-001";
        public const string ERROR_SERVIDOR = "SRV-001";
    }
}
```
`ExcepcionNegocio` es el equivalente REST del `RuntimeException` que JAX-WS convertía en `SOAP Fault`: lleva el código y el estado HTTP con el que se debe responder.
```csharp
namespace ec.edu.monster.modelos.utilidades
{
    public class ExcepcionNegocio : Exception
    {
        public string Codigo { get; }
        public int EstadoHttp { get; }

        public ExcepcionNegocio(string codigo, string mensaje, int estadoHttp = StatusCodes.Status400BadRequest)
            : base(mensaje)
        {
            Codigo = codigo;
            EstadoHttp = estadoHttp;
        }
    }
}
```

### 3.10 Modelo: solicitudes y respuestas (cuerpos JSON)
Las solicitudes usan tipos **anulables** (`string?`, `double?`). Así un campo que falta llega como `null` y lo rechaza `ServicioConversion` con un mensaje propio, en vez del error genérico de ASP.NET.
```csharp
namespace ec.edu.monster.modelos.utilidades
{
    public class SolicitudLogin
    {
        public string? Usuario { get; set; }
        public string? Contrasenia { get; set; }
    }
}
```
```csharp
namespace ec.edu.monster.modelos.utilidades
{
    public class SolicitudConversion
    {
        public string? Categoria { get; set; }
        public string? UnidadOrigen { get; set; }
        public string? UnidadDestino { get; set; }
        public double? Valor { get; set; }
    }
}
```
Todas las respuestas heredan de `Respuesta`, que lleva el **estado de la operación** que exige el documento. `JsonPropertyOrder` hace que `estado`, `codigo` y `mensaje` salgan primero en el JSON.
```csharp
using System.Text.Json.Serialization;

namespace ec.edu.monster.modelos.utilidades
{
    public class Respuesta
    {
        [JsonPropertyOrder(-3)]
        public string Estado { get; set; } = string.Empty;

        [JsonPropertyOrder(-2)]
        public string Codigo { get; set; } = string.Empty;

        [JsonPropertyOrder(-1)]
        public string Mensaje { get; set; } = string.Empty;
    }
}
```
```csharp
namespace ec.edu.monster.modelos.utilidades
{
    public class RespuestaAutenticacion : Respuesta
    {
        public string? Token { get; set; }
        public DateTime? Expiracion { get; set; }
    }
}
```
```csharp
namespace ec.edu.monster.modelos.utilidades
{
    public class RespuestaCatalogo : Respuesta
    {
        public List<string> Categorias { get; set; } = new();
    }
}
```
```csharp
namespace ec.edu.monster.modelos.utilidades
{
    public class RespuestaUnidades : Respuesta
    {
        public string? Categoria { get; set; }
        public List<string> Unidades { get; set; } = new();
    }
}
```
```csharp
namespace ec.edu.monster.modelos.utilidades
{
    public class RespuestaConversion : Respuesta
    {
        public double? Resultado { get; set; }
        public string? UnidadOrigen { get; set; }
        public string? UnidadDestino { get; set; }
    }
}
```

### 3.11 Mapeadores (`ec.edu.monster.modelos.utilidades.mapeadores/`)
Hay un método estático `To{Dominio}` por dominio. Hace `Trim().ToUpperInvariant()`, así que acepta mayúsculas y minúsculas. Compara contra los **nombres** del enum (no usa `Enum.TryParse`, que aceptaría números como `"1"`).
```csharp
using ec.edu.monster.modelos.utilidades.enums;

namespace ec.edu.monster.modelos.utilidades.mapeadores
{
    public static class UnidadMapper
    {
        public static UnidadLongitud ToLongitud(string unidad)
        {
            return Mapear<UnidadLongitud>(unidad, "Unidad de longitud no soportada: ");
        }

        public static UnidadMasa ToMasa(string unidad)
        {
            return Mapear<UnidadMasa>(unidad, "Unidad de masa no soportada: ");
        }

        public static UnidadTemperatura ToTemperatura(string unidad)
        {
            return Mapear<UnidadTemperatura>(unidad, "Unidad de temperatura no soportada: ");
        }

        private static T Mapear<T>(string unidad, string mensaje) where T : struct, Enum
        {
            string nombre = unidad.Trim().ToUpperInvariant();

            if (!Enum.GetNames<T>().Contains(nombre))
            {
                throw new ExcepcionNegocio(CodigosRespuesta.UNIDAD_NO_SOPORTADA, mensaje + unidad);
            }
            return Enum.Parse<T>(nombre);
        }
    }
}
```
```csharp
using ec.edu.monster.modelos.utilidades.enums;

namespace ec.edu.monster.modelos.utilidades.mapeadores
{
    public static class CategoriaMapper
    {
        public static Categoria ToCategoria(string categoria)
        {
            string nombre = categoria.Trim().ToUpperInvariant();

            if (!Enum.GetNames<Categoria>().Contains(nombre))
            {
                throw new ExcepcionNegocio(CodigosRespuesta.DATOS_INCONSISTENTES, "Categoria inexistente: " + categoria);
            }
            return Enum.Parse<Categoria>(nombre);
        }
    }
}
```

### 3.12 Servicio genérico (`servicios/ServicioConversor.cs`)
```csharp
using ec.edu.monster.modelos.utilidades;

namespace ec.edu.monster.servicios
{
    public class ServicioConversor<T>
    {
        private readonly IConversor<T> conversor;

        public ServicioConversor(IConversor<T> conversor)
        {
            this.conversor = conversor;
        }

        public double Convertir(double valor, T origen, T destino)
        {
            if (conversor == null)
            {
                throw new InvalidOperationException("No se ha inyectado un conversor");
            }
            return conversor.Convertir(valor, origen, destino);
        }
    }
}
```

### 3.13 Servicios de negocio (`servicios/`)
Aquí está lo que en Java hacía el endpoint, separado del punto de exposición como pide el documento ("separar la lógica de cálculo de los puntos de exposición").

**`ServicioConversion.cs`**: valida los datos (CU-06), detecta unidades no soportadas (CU-10) y categorías incompatibles (CU-11), despacha al conversor y arma la respuesta estructurada (CU-07).
```csharp
using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.modelos.utilidades.enums;
using ec.edu.monster.modelos.utilidades.mapeadores;

namespace ec.edu.monster.servicios
{
    public class ServicioConversion
    {
        private readonly ServicioCatalogo servicioCatalogo = new();

        private readonly ServicioConversor<UnidadLongitud> servicioLongitud =
                new(new ConversorLongitud());

        private readonly ServicioConversor<UnidadMasa> servicioMasa =
                new(new ConversorMasa());

        private readonly ServicioConversor<UnidadTemperatura> servicioTemperatura =
                new(new ConversorTemperatura());

        public RespuestaConversion Convertir(SolicitudConversion solicitud)
        {
            if (string.IsNullOrWhiteSpace(solicitud.Categoria))
            {
                throw new ExcepcionNegocio(CodigosRespuesta.DATOS_INCONSISTENTES, "La categoria es obligatoria");
            }
            if (string.IsNullOrWhiteSpace(solicitud.UnidadOrigen))
            {
                throw new ExcepcionNegocio(CodigosRespuesta.DATOS_INCONSISTENTES, "La unidad de origen es obligatoria");
            }
            if (string.IsNullOrWhiteSpace(solicitud.UnidadDestino))
            {
                throw new ExcepcionNegocio(CodigosRespuesta.DATOS_INCONSISTENTES, "La unidad de destino es obligatoria");
            }
            if (solicitud.Valor == null || !double.IsFinite(solicitud.Valor.Value))
            {
                throw new ExcepcionNegocio(CodigosRespuesta.DATOS_INCONSISTENTES, "El valor es obligatorio y debe ser numerico");
            }

            Categoria categoria = CategoriaMapper.ToCategoria(solicitud.Categoria);
            ValidarUnidad(categoria, solicitud.UnidadOrigen, "origen");
            ValidarUnidad(categoria, solicitud.UnidadDestino, "destino");

            double valor = solicitud.Valor.Value;

            double resultado = categoria switch
            {
                Categoria.LONGITUD => servicioLongitud.Convertir(valor,
                        UnidadMapper.ToLongitud(solicitud.UnidadOrigen),
                        UnidadMapper.ToLongitud(solicitud.UnidadDestino)),

                Categoria.MASA => servicioMasa.Convertir(valor,
                        UnidadMapper.ToMasa(solicitud.UnidadOrigen),
                        UnidadMapper.ToMasa(solicitud.UnidadDestino)),

                Categoria.TEMPERATURA => servicioTemperatura.Convertir(valor,
                        UnidadMapper.ToTemperatura(solicitud.UnidadOrigen),
                        UnidadMapper.ToTemperatura(solicitud.UnidadDestino)),

                _ => throw new ExcepcionNegocio(CodigosRespuesta.DATOS_INCONSISTENTES, "Categoria inexistente: " + solicitud.Categoria)
            };

            return new RespuestaConversion
            {
                Estado = CodigosRespuesta.ESTADO_OK,
                Codigo = CodigosRespuesta.EXITO,
                Mensaje = "Conversion realizada correctamente",
                Resultado = resultado,
                UnidadOrigen = solicitud.UnidadOrigen.Trim().ToUpperInvariant(),
                UnidadDestino = solicitud.UnidadDestino.Trim().ToUpperInvariant()
            };
        }

        private void ValidarUnidad(Categoria categoria, string unidad, string rol)
        {
            Categoria? categoriaDeUnidad = servicioCatalogo.BuscarCategoriaDeUnidad(unidad);

            if (categoriaDeUnidad == null)
            {
                throw new ExcepcionNegocio(CodigosRespuesta.UNIDAD_NO_SOPORTADA, "Unidad de " + rol + " no soportada: " + unidad);
            }
            if (categoriaDeUnidad != categoria)
            {
                throw new ExcepcionNegocio(CodigosRespuesta.DATOS_INCONSISTENTES,
                        "La unidad de " + rol + " " + unidad + " no pertenece a la categoria " + categoria);
            }
        }
    }
}
```

**`ServicioCatalogo.cs`**: CU-02 y CU-03. El catálogo se arma a partir de los enums, así que una unidad nueva aparece sola en el catálogo.
```csharp
using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.modelos.utilidades.enums;
using ec.edu.monster.modelos.utilidades.mapeadores;

namespace ec.edu.monster.servicios
{
    public class ServicioCatalogo
    {
        public RespuestaCatalogo ConsultarCategorias()
        {
            return new RespuestaCatalogo
            {
                Estado = CodigosRespuesta.ESTADO_OK,
                Codigo = CodigosRespuesta.EXITO,
                Mensaje = "Categorias consultadas correctamente",
                Categorias = Enum.GetNames<Categoria>().ToList()
            };
        }

        public RespuestaUnidades ConsultarUnidades(string categoria)
        {
            Categoria tipo = CategoriaMapper.ToCategoria(categoria);

            return new RespuestaUnidades
            {
                Estado = CodigosRespuesta.ESTADO_OK,
                Codigo = CodigosRespuesta.EXITO,
                Mensaje = "Unidades consultadas correctamente",
                Categoria = tipo.ToString(),
                Unidades = ObtenerUnidades(tipo)
            };
        }

        public List<string> ObtenerUnidades(Categoria categoria)
        {
            return categoria switch
            {
                Categoria.LONGITUD => Enum.GetNames<UnidadLongitud>().ToList(),
                Categoria.MASA => Enum.GetNames<UnidadMasa>().ToList(),
                Categoria.TEMPERATURA => Enum.GetNames<UnidadTemperatura>().ToList(),
                _ => throw new ExcepcionNegocio(CodigosRespuesta.DATOS_INCONSISTENTES, "Categoria inexistente: " + categoria)
            };
        }

        public Categoria? BuscarCategoriaDeUnidad(string unidad)
        {
            string nombre = unidad.Trim().ToUpperInvariant();

            foreach (Categoria categoria in Enum.GetValues<Categoria>())
            {
                if (ObtenerUnidades(categoria).Contains(nombre))
                {
                    return categoria;
                }
            }
            return null;
        }
    }
}
```

**`ServicioAutenticacion.cs`**: CU-01.
```csharp
using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.seguridad;

namespace ec.edu.monster.servicios
{
    public class ServicioAutenticacion
    {
        public RespuestaAutenticacion Autenticar(SolicitudLogin solicitud)
        {
            if (string.IsNullOrWhiteSpace(solicitud.Usuario) || string.IsNullOrWhiteSpace(solicitud.Contrasenia))
            {
                throw new ExcepcionNegocio(CodigosRespuesta.DATOS_INCONSISTENTES, "El usuario y la contrasenia son obligatorios");
            }
            if (!AdministradorCredenciales.ValidarCredenciales(solicitud.Usuario, solicitud.Contrasenia))
            {
                throw new ExcepcionNegocio(CodigosRespuesta.CREDENCIALES_INCORRECTAS, "Credenciales incorrectas",
                        StatusCodes.Status401Unauthorized);
            }

            string token = AdministradorTokens.GenerarToken();

            return new RespuestaAutenticacion
            {
                Estado = CodigosRespuesta.ESTADO_OK,
                Codigo = CodigosRespuesta.EXITO,
                Mensaje = "Autenticacion realizada correctamente",
                Token = token,
                Expiracion = AdministradorTokens.ObtenerExpiracion(token)
            };
        }
    }
}
```

### 3.14 Controlador REST (`controladores.api/{Controlador}.cs`)
- `[ApiController]` y una **ruta fija** `[Route("{ruta}")]`. No se usa `api/[controller]`, porque eso metería el nombre de la clase en la URL.
- **Las rutas y los nombres de los campos JSON son el contrato**: si se renombran, todos los clientes dejan de funcionar.
- El controlador solo recibe y responde; no tiene lógica. `Convertir` atrapa `ExcepcionNegocio` para que el error salga como `RespuestaConversion` completa, con `resultado: null`, como pide el documento.
```csharp
using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.servicios;
using Microsoft.AspNetCore.Mvc;

namespace ec.edu.monster.controladores.api
{
    [ApiController]
    [Route("api/conversor")]
    public class ControladorConversorUnidades : ControllerBase
    {
        private readonly ServicioAutenticacion servicioAutenticacion = new();
        private readonly ServicioCatalogo servicioCatalogo = new();
        private readonly ServicioConversion servicioConversion = new();

        [HttpPost("login")]
        public ActionResult<RespuestaAutenticacion> Login([FromBody] SolicitudLogin solicitud)
        {
            return Ok(servicioAutenticacion.Autenticar(solicitud));
        }

        [HttpGet("categorias")]
        public ActionResult<RespuestaCatalogo> ConsultarCategorias()
        {
            return Ok(servicioCatalogo.ConsultarCategorias());
        }

        [HttpGet("categorias/{categoria}/unidades")]
        public ActionResult<RespuestaUnidades> ConsultarUnidades(string categoria)
        {
            return Ok(servicioCatalogo.ConsultarUnidades(categoria));
        }

        [HttpPost("convertir")]
        public ActionResult<RespuestaConversion> Convertir([FromBody] SolicitudConversion solicitud)
        {
            try
            {
                return Ok(servicioConversion.Convertir(solicitud));
            }
            catch (ExcepcionNegocio e)
            {
                return StatusCode(e.EstadoHttp, new RespuestaConversion
                {
                    Estado = CodigosRespuesta.ESTADO_ERROR,
                    Codigo = e.Codigo,
                    Mensaje = e.Message,
                    Resultado = null,
                    UnidadOrigen = solicitud.UnidadOrigen,
                    UnidadDestino = solicitud.UnidadDestino
                });
            }
        }
    }
}
```

### 3.15 Filtro de token (`controladores.manejadores/ManejadorAutenticacion.cs`)
Es el equivalente del `SOAPHandler` de Java: lee el token del header `Authorization: Bearer <token>` y **no exige token a la acción `Login`**. Implementa `IAuthorizationFilter`, que ASP.NET ejecuta **antes** de leer el cuerpo, así que el token se valida antes de procesar la petición (CU-05), como pide el documento. Se registra para todo el proyecto en `Program.cs` (`Filters.Add<ManejadorAutenticacion>()`); a diferencia del original Java, aquí **sí queda conectado**.
```csharp
using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.modelos.utilidades.enums;
using ec.edu.monster.seguridad;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;

namespace ec.edu.monster.controladores.manejadores
{
    public class ManejadorAutenticacion : IAuthorizationFilter
    {
        private const string OPERACION_LIBRE = "Login";
        private const string PREFIJO = "Bearer ";

        public void OnAuthorization(AuthorizationFilterContext contexto)
        {
            if (OPERACION_LIBRE.Equals(contexto.RouteData.Values["action"]?.ToString()))
            {
                return;
            }

            string? encabezado = contexto.HttpContext.Request.Headers.Authorization;
            string? token = encabezado != null && encabezado.StartsWith(PREFIJO, StringComparison.OrdinalIgnoreCase)
                    ? encabezado.Substring(PREFIJO.Length).Trim()
                    : null;

            EstadoToken estado = AdministradorTokens.ValidarToken(token);

            if (estado == EstadoToken.VALIDO)
            {
                return;
            }

            var respuesta = new Respuesta
            {
                Estado = CodigosRespuesta.ESTADO_ERROR,
                Codigo = estado == EstadoToken.EXPIRADO ? CodigosRespuesta.SESION_EXPIRADA : CodigosRespuesta.TOKEN_INVALIDO,
                Mensaje = estado == EstadoToken.EXPIRADO
                        ? "Sesion expirada, vuelva a autenticarse"
                        : "Token invalido o sesion expirada"
            };

            contexto.Result = new ObjectResult(respuesta) { StatusCode = StatusCodes.Status401Unauthorized };
        }
    }
}
```

### 3.16 Middleware de errores (`controladores.manejadores/ManejadorErrores.cs`)
Garantiza que **el cliente siempre reciba una respuesta estructurada**, aunque algo falle. Tiene dos partes:
- `InvokeAsync`: atrapa cualquier excepción del pipeline y la convierte en `{ estado, codigo, mensaje }`.
- `RespuestaDatosInvalidos`: reemplaza el error por defecto de ASP.NET cuando el JSON no se puede leer (por ejemplo `"valor": "ABC"`).
```csharp
using ec.edu.monster.modelos.utilidades;
using Microsoft.AspNetCore.Mvc;

namespace ec.edu.monster.controladores.manejadores
{
    public class ManejadorErrores
    {
        private readonly RequestDelegate siguiente;

        public ManejadorErrores(RequestDelegate siguiente)
        {
            this.siguiente = siguiente;
        }

        public async Task InvokeAsync(HttpContext contexto)
        {
            try
            {
                await siguiente(contexto);
            }
            catch (ExcepcionNegocio e)
            {
                await EscribirError(contexto, e.EstadoHttp, e.Codigo, e.Message);
            }
            catch (ArgumentException e)
            {
                await EscribirError(contexto, StatusCodes.Status400BadRequest, CodigosRespuesta.DATOS_INCONSISTENTES, e.Message);
            }
            catch (Exception)
            {
                await EscribirError(contexto, StatusCodes.Status500InternalServerError, CodigosRespuesta.ERROR_SERVIDOR,
                        "Error interno del servidor");
            }
        }

        public static IActionResult RespuestaDatosInvalidos(ActionContext contexto)
        {
            return new BadRequestObjectResult(new Respuesta
            {
                Estado = CodigosRespuesta.ESTADO_ERROR,
                Codigo = CodigosRespuesta.DATOS_INCONSISTENTES,
                Mensaje = "Datos inconsistentes: la solicitud no tiene el formato esperado"
            });
        }

        private static Task EscribirError(HttpContext contexto, int estadoHttp, string codigo, string mensaje)
        {
            contexto.Response.StatusCode = estadoHttp;

            return contexto.Response.WriteAsJsonAsync(new Respuesta
            {
                Estado = CodigosRespuesta.ESTADO_ERROR,
                Codigo = codigo,
                Mensaje = mensaje
            });
        }
    }
}
```

### 3.17 Seguridad: credenciales (`seguridad/AdministradorCredenciales.cs`)
Es una clase `static` con un único usuario fijo, como pide el documento ("un único usuario con credenciales creadas previamente"). El usuario `MONSTER` y la clave `Monster9` son los que ya usan los clientes del repositorio.
```csharp
namespace ec.edu.monster.seguridad
{
    public static class AdministradorCredenciales
    {
        private const string USUARIO = "MONSTER";
        private const string CONTRASENIA = "Monster9";

        public static bool ValidarCredenciales(string usuario, string contrasenia)
        {
            return USUARIO == usuario && CONTRASENIA == contrasenia;
        }
    }
}
```

### 3.18 Seguridad: tokens (`seguridad/AdministradorTokens.cs`)
Los tokens son GUID guardados en un `ConcurrentDictionary` (seguro con varias peticiones a la vez) junto con su fecha de expiración. **Sí expiran**, porque el documento exige manejar la sesión caducada (CU-09). Un token vencido se borra y se informa como `EXPIRADO`.
```csharp
using System.Collections.Concurrent;
using ec.edu.monster.modelos.utilidades.enums;

namespace ec.edu.monster.seguridad
{
    public static class AdministradorTokens
    {
        public const int MINUTOS_EXPIRACION = 30;

        private static readonly ConcurrentDictionary<string, DateTime> tokensActivos = new();

        public static string GenerarToken()
        {
            string token = Guid.NewGuid().ToString();
            tokensActivos[token] = DateTime.Now.AddMinutes(MINUTOS_EXPIRACION);
            return token;
        }

        public static DateTime? ObtenerExpiracion(string token)
        {
            return tokensActivos.TryGetValue(token, out DateTime expiracion) ? expiracion : null;
        }

        public static EstadoToken ValidarToken(string? token)
        {
            if (string.IsNullOrWhiteSpace(token) || !tokensActivos.TryGetValue(token, out DateTime expiracion))
            {
                return EstadoToken.INVALIDO;
            }
            if (DateTime.Now > expiracion)
            {
                tokensActivos.TryRemove(token, out _);
                return EstadoToken.EXPIRADO;
            }
            return EstadoToken.VALIDO;
        }
    }
}
```
> Para probar la expiración sin esperar 30 minutos, baja `MINUTOS_EXPIRACION` a `1` temporalmente.

### 3.19 Seguridad: CORS (`seguridad/ConfiguracionCors.cs`)
Es el equivalente de `FiltroCors` + `web.xml`: los mismos orígenes, métodos, headers y `max-age`. Se copia **sin cambios** en cualquier proyecto nuevo. Solo afecta a clientes que llaman desde el **navegador**; los clientes de consola, escritorio, móvil y un web que llama desde el servidor (Flask) no lo necesitan.
```csharp
namespace ec.edu.monster.seguridad
{
    public static class ConfiguracionCors
    {
        public const string POLITICA = "PoliticaCors";

        private static readonly string[] ORIGENES = { "http://localhost:4200", "http://localhost:5173", "http://localhost:3000", "http://localhost:5141" };
        private static readonly string[] METODOS = { "GET", "POST", "PUT", "DELETE", "OPTIONS" };
        private static readonly string[] HEADERS = { "Origin", "Content-Type", "Accept", "Authorization", "X-Requested-With" };
        private const int MAX_AGE = 3600;

        public static IServiceCollection AgregarCors(this IServiceCollection servicios)
        {
            servicios.AddCors(opciones =>
                opciones.AddPolicy(POLITICA, politica => politica
                    .WithOrigins(ORIGENES)
                    .WithMethods(METODOS)
                    .WithHeaders(HEADERS)
                    .SetPreflightMaxAge(TimeSpan.FromSeconds(MAX_AGE))));

            return servicios;
        }
    }
}
```
Orígenes permitidos: 4200 (Angular), 5173 (Vite), 3000 (React/Node) y 5141 (.NET web). Si un cliente web nuevo usa otro puerto, agrégalo aquí.

### 3.20 Pruebas manuales (`ec.edu.monster.pruebas/{SIGLA}.http`)
Reemplazan a los `main` con `Scanner` de Java y al proyecto de SoapUI. Visual Studio (17.12 o superior) y VS Code (extensión *REST Client*) muestran un botón **Send Request** sobre cada petición. La línea `# @name login` guarda la respuesta del login, y las demás peticiones toman el token de ahí. En versiones anteriores de Visual Studio, copia el token a mano.

Hay **una petición por caso de uso**, incluidos los de error:
```http
@Servidor = http://localhost:5014

### CU-01 Autenticarse (guarda el token en la variable "login")
# @name login
POST {{Servidor}}/api/conversor/login
Content-Type: application/json

{
  "usuario": "MONSTER",
  "contrasenia": "Monster9"
}

### CU-02 Consultar catalogo de categorias
GET {{Servidor}}/api/conversor/categorias
Authorization: Bearer {{login.response.body.$.token}}

### CU-03 Consultar unidades por categoria
GET {{Servidor}}/api/conversor/categorias/LONGITUD/unidades
Authorization: Bearer {{login.response.body.$.token}}

### CU-04 Realizar conversion
POST {{Servidor}}/api/conversor/convertir
Content-Type: application/json
Authorization: Bearer {{login.response.body.$.token}}

{
  "categoria": "LONGITUD",
  "unidadOrigen": "KILOMETRO",
  "unidadDestino": "METRO",
  "valor": 5
}

### CU-08 Token invalido (AUTH-001)
POST {{Servidor}}/api/conversor/convertir
Content-Type: application/json
Authorization: Bearer token-falso

{
  "categoria": "LONGITUD",
  "unidadOrigen": "KILOMETRO",
  "unidadDestino": "METRO",
  "valor": 5
}

### CU-10 Unidad no soportada (UNIT-001)
POST {{Servidor}}/api/conversor/convertir
Content-Type: application/json
Authorization: Bearer {{login.response.body.$.token}}

{
  "categoria": "LONGITUD",
  "unidadOrigen": "XYZ",
  "unidadDestino": "METRO",
  "valor": 5
}

### CU-11 Datos inconsistentes: categorias incompatibles (DATA-001)
POST {{Servidor}}/api/conversor/convertir
Content-Type: application/json
Authorization: Bearer {{login.response.body.$.token}}

{
  "categoria": "LONGITUD",
  "unidadOrigen": "METRO",
  "unidadDestino": "CELSIUS",
  "valor": 5
}

### CU-11 Datos inconsistentes: valor no numerico (DATA-001)
POST {{Servidor}}/api/conversor/convertir
Content-Type: application/json
Authorization: Bearer {{login.response.body.$.token}}

{
  "categoria": "LONGITUD",
  "unidadOrigen": "METRO",
  "unidadDestino": "KILOMETRO",
  "valor": "ABC"
}
```

### 3.21 Proyecto de tests (`{PROYECTO}.Tests/`)
Se crea con la plantilla **xUnit** y referencia al proyecto web. Las clases de test van **sin namespace** (como los tests Java en el paquete por defecto).

**`{PROYECTO}.Tests.csproj`** (generado por la plantilla, más la referencia):
```xml
<Project Sdk="Microsoft.NET.Sdk">

  <PropertyGroup>
    <TargetFramework>net9.0</TargetFramework>
    <ImplicitUsings>enable</ImplicitUsings>
    <Nullable>enable</Nullable>
    <IsPackable>false</IsPackable>
  </PropertyGroup>

  <ItemGroup>
    <PackageReference Include="coverlet.collector" Version="6.0.4" />
    <PackageReference Include="Microsoft.NET.Test.Sdk" Version="17.14.1" />
    <PackageReference Include="xunit" Version="2.9.3" />
    <PackageReference Include="xunit.runner.visualstudio" Version="3.1.4" />
  </ItemGroup>

  <ItemGroup>
    <Using Include="Xunit" />
  </ItemGroup>

  <ItemGroup>
    <ProjectReference Include="..\{PROYECTO}\{PROYECTO}.csproj" />
  </ItemGroup>

</Project>
```
Los tests son parametrizados con `[Theory]` + `[InlineData(valor, ORIGEN, DESTINO, esperado)]` (el equivalente de `@CsvSource`) y comparan con 4 decimales de precisión (`Assert.Equal(esperado, resultado, 4)`, el equivalente de la tolerancia `0.0001`).
```csharp
using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.modelos.utilidades.enums;

public class ConversorLongitudTest
{
    private readonly ConversorLongitud conversor = new();

    [Theory]
    [InlineData(5, UnidadLongitud.KILOMETRO, UnidadLongitud.METRO, 5000.0)]
    [InlineData(100, UnidadLongitud.CENTIMETRO, UnidadLongitud.METRO, 1.0)]
    [InlineData(1, UnidadLongitud.MILLA, UnidadLongitud.KILOMETRO, 1.609344)]
    public void DeberiaConvertirCorrectamente(double valor,
                                              UnidadLongitud origen,
                                              UnidadLongitud destino,
                                              double esperado)
    {
        double resultado = conversor.Convertir(valor, origen, destino);

        Assert.Equal(esperado, resultado, 4);
    }
}
```
- Dominios por factores: el método se llama `DeberiaConvertirCorrectamente` y lleva 3 casos.
- Dominios por switch: el método se llama `DeberiaConvertir{Dominio}` y cubre **todos los pares de ida y vuelta** (en temperatura, con 3 unidades, son 6 casos):
```csharp
    [Theory]
    [InlineData(100, UnidadTemperatura.CELSIUS, UnidadTemperatura.FAHRENHEIT, 212.0)]
    [InlineData(212, UnidadTemperatura.FAHRENHEIT, UnidadTemperatura.CELSIUS, 100.0)]
    [InlineData(0, UnidadTemperatura.CELSIUS, UnidadTemperatura.KELVIN, 273.15)]
    [InlineData(273.15, UnidadTemperatura.KELVIN, UnidadTemperatura.CELSIUS, 0.0)]
    [InlineData(32, UnidadTemperatura.FAHRENHEIT, UnidadTemperatura.KELVIN, 273.15)]
    [InlineData(273.15, UnidadTemperatura.KELVIN, UnidadTemperatura.FAHRENHEIT, 32.0)]
    public void DeberiaConvertirTemperatura(double valor,
                                            UnidadTemperatura origen,
                                            UnidadTemperatura destino,
                                            double esperado)
```
- Además hay un test del servicio que cubre los **errores que exige el documento** (unidad no soportada, categorías incompatibles, categoría inexistente, dato vacío):
```csharp
using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.servicios;

public class ServicioConversionTest
{
    private readonly ServicioConversion servicio = new();

    [Fact]
    public void DeberiaConvertirConRespuestaEstructurada()
    {
        var solicitud = new SolicitudConversion { Categoria = "longitud", UnidadOrigen = "kilometro", UnidadDestino = "metro", Valor = 5 };

        RespuestaConversion respuesta = servicio.Convertir(solicitud);

        Assert.Equal(CodigosRespuesta.ESTADO_OK, respuesta.Estado);
        Assert.Equal(CodigosRespuesta.EXITO, respuesta.Codigo);
        Assert.Equal(5000.0, respuesta.Resultado!.Value, 4);
    }

    [Theory]
    [InlineData("LONGITUD", "XYZ", "METRO", CodigosRespuesta.UNIDAD_NO_SOPORTADA)]
    [InlineData("LONGITUD", "METRO", "CELSIUS", CodigosRespuesta.DATOS_INCONSISTENTES)]
    [InlineData("VOLUMEN", "LITRO", "MILILITRO", CodigosRespuesta.DATOS_INCONSISTENTES)]
    [InlineData("MASA", "", "GRAMO", CodigosRespuesta.DATOS_INCONSISTENTES)]
    public void DeberiaRechazarSolicitudInvalida(string categoria, string origen, string destino, string codigoEsperado)
    {
        var solicitud = new SolicitudConversion { Categoria = categoria, UnidadOrigen = origen, UnidadDestino = destino, Valor = 1 };

        var error = Assert.Throws<ExcepcionNegocio>(() => servicio.Convertir(solicitud));

        Assert.Equal(codigoEsperado, error.Codigo);
    }
}
```

---

## 4. Contrato REST del proyecto (referencia)

> En un proyecto nuevo, las operaciones y los campos salen del documento de requisitos. Lo que se conserva es la forma: ruta fija bajo `api/`, JSON en camelCase, token en `Authorization: Bearer` y respuesta siempre con `estado`, `codigo` y `mensaje`.

### 4.1 De operación SOAP (documento) a endpoint REST

| Caso de uso | Operación SOAP del documento | Endpoint REST | Token |
|---|---|---|---|
| CU-01 Autenticarse | `autenticar(usuario, password)` | `POST /api/conversor/login` | No |
| CU-02 Consultar catálogo | `obtenerCategorias(token)` | `GET /api/conversor/categorias` | Sí |
| CU-03 Consultar unidades por categoría | `obtenerUnidades(token, categoria)` | `GET /api/conversor/categorias/{categoria}/unidades` | Sí |
| CU-04 Realizar conversión | `convertir(token, categoria, unidadOrigen, unidadDestino, valor)` | `POST /api/conversor/convertir` | Sí |
| CU-05 Validar token | `validarToken()` | No es un endpoint: lo hace `ManejadorAutenticacion` en cada petición | — |

| Elemento | Valor |
|---|---|
| URL base local | `http://localhost:{PUERTO}/api/conversor` |
| URL base en la red | `http://{IP_RED}:{PUERTO}/api/conversor` |
| URL desde el emulador Android | `http://10.0.2.2:{PUERTO}/api/conversor` |
| Header del token | `Authorization: Bearer <token>` |
| Descripción OpenAPI (solo en Development) | `http://localhost:{PUERTO}/openapi/v1.json` |

### 4.2 Respuestas

| Situación | HTTP | `codigo` | `mensaje` |
|---|---|---|---|
| Operación correcta | 200 | `200` | `Conversion realizada correctamente` (o el de cada operación) |
| Sin token, token falso o alterado | 401 | `AUTH-001` | `Token invalido o sesion expirada` |
| Token vencido | 401 | `AUTH-002` | `Sesion expirada, vuelva a autenticarse` |
| Usuario o clave incorrectos | 401 | `AUTH-003` | `Credenciales incorrectas` |
| Unidad fuera del catálogo | 400 | `UNIT-001` | `Unidad de origen no soportada: XYZ` |
| Categoría inexistente, unidades incompatibles, campo vacío, JSON mal formado | 400 | `DATA-001` | según el caso |
| Error inesperado | 500 | `SRV-001` | `Error interno del servidor` |

Ejemplo de conversión correcta (salida real del servidor):
```http
POST /api/conversor/convertir
Authorization: Bearer 3f2b...-guid
Content-Type: application/json

{ "categoria": "LONGITUD", "unidadOrigen": "KILOMETRO", "unidadDestino": "METRO", "valor": 5 }
```
```json
{"estado":"OK","codigo":"200","mensaje":"Conversion realizada correctamente","resultado":5000,"unidadOrigen":"KILOMETRO","unidadDestino":"METRO"}
```
Ejemplo de unidad no soportada (HTTP 400):
```json
{"estado":"ERROR","codigo":"UNIT-001","mensaje":"Unidad de origen no soportada: XYZ","resultado":null,"unidadOrigen":"XYZ","unidadDestino":"METRO"}
```
Ejemplo de token inválido (HTTP 401):
```json
{"estado":"ERROR","codigo":"AUTH-001","mensaje":"Token invalido o sesion expirada"}
```

---

## 5. Procedimiento para crear un proyecto nuevo

### 5.1 Lista de verificación
1. **Define el dominio:** decide qué familias (`{Dominio}`) tendrá, qué unidades tiene cada una y si cada familia es **lineal** (patrón A, factores) o **no lineal** (patrón B, switch).
2. **Crea la solución** en Visual Studio 2022: *Crear un proyecto → ASP.NET Core Web API*, con nombre `{PROYECTO}`, marcando **Usar controladores** y **Habilitar compatibilidad con OpenAPI**. Desde la terminal es equivalente:
   ```bash
   dotnet new webapi -n {PROYECTO} --use-controllers -o {PROYECTO}
   dotnet new xunit -n {PROYECTO}.Tests -o {PROYECTO}.Tests
   dotnet add {PROYECTO}.Tests reference {PROYECTO}
   dotnet new sln -n {PROYECTO}
   dotnet sln add {PROYECTO} {PROYECTO}.Tests
   ```
3. **Ajusta la configuración:** `.csproj` (3.1), `launchSettings.json` (3.2) y `Program.cs` (3.4). Borra `WeatherForecast.cs`, la carpeta `Controllers`, el `.http` de la raíz y `UnitTest1.cs` del proyecto de tests.
4. **Crea las 8 carpetas obligatorias** (regla 5 del inicio; no se agregan ni se quitan), exactamente así, con clic derecho sobre el proyecto → *Agregar → Nueva carpeta*:
   `ec.edu.monster.controladores.api`, `ec.edu.monster.controladores.manejadores`, `ec.edu.monster.modelos.utilidades`, `ec.edu.monster.modelos.utilidades.enums`, `ec.edu.monster.modelos.utilidades.mapeadores`, `ec.edu.monster.pruebas`, `ec.edu.monster.seguridad` y `ec.edu.monster.servicios`.
5. **En cada clase nueva, corrige el namespace** a `ec.edu.monster.<carpeta>` (borra el prefijo `{PROYECTO}.` que pone Visual Studio).
6. **Copia sin cambios** la infraestructura: `ConfiguracionCors`, `ManejadorErrores`, `ExcepcionNegocio`, `Respuesta`, `ServicioConversor`, `IConversor`, `appsettings*.json`. Las piezas con comportamiento (`ManejadorAutenticacion`, `AdministradorTokens`, `AdministradorCredenciales`, `CodigosRespuesta`, los servicios y el controlador) **se usan como base de estilo** y se ajustan a lo que pida el documento: expiración, códigos de error, operaciones. Si el documento no necesita una de ellas, no se crea, pero su carpeta sigue existiendo con los archivos que sí correspondan.
7. **Por cada `{Dominio}`:**
   1. Crea `enums/Unidad{Dominio}.cs` y agrega el valor a `enums/Categoria.cs`.
   2. Crea `Conversor{Dominio}.cs` con el patrón A o el B.
   3. Agrega `To{Dominio}(string)` a `UnidadMapper` con el mensaje `"Unidad de {dominio en minuscula} no soportada: "`.
   4. Agrega la línea del dominio en `ServicioCatalogo.ObtenerUnidades`.
   5. Agrega el campo `servicio{Dominio}` y su caso en el `switch` de `ServicioConversion.Convertir`.
   6. Crea `{PROYECTO}.Tests/Conversor{Dominio}Test.cs`.
   7. Agrega al `.http` una petición de conversión del dominio.
8. **Compila y prueba:**
   ```bash
   dotnet build
   dotnet test
   dotnet run --project {PROYECTO} --launch-profile http
   ```
9. **Prueba el contrato** ejecutando de arriba abajo las peticiones del archivo `.http` (primero el login).
10. **Revisa** contra esta guía (carpetas, namespaces, sin tildes) y contra el documento (cada caso de uso tiene su endpoint o su clase).

### 5.2 Ejemplo completo: agregar el dominio `Volumen` (patrón A)

**`enums/UnidadVolumen.cs`**
```csharp
namespace ec.edu.monster.modelos.utilidades.enums
{
    public enum UnidadVolumen
    {
        MILILITRO,
        LITRO,
        METRO_CUBICO,
        GALON
    }
}
```

**`enums/Categoria.cs`** (valor nuevo)
```csharp
    public enum Categoria
    {
        LONGITUD,
        MASA,
        TEMPERATURA,
        VOLUMEN
    }
```

**`ConversorVolumen.cs`** (base: LITRO)
```csharp
using ec.edu.monster.modelos.utilidades.enums;

namespace ec.edu.monster.modelos.utilidades
{
    public class ConversorVolumen : IConversor<UnidadVolumen>
    {
        private readonly Dictionary<UnidadVolumen, double> factores = new()
        {
            { UnidadVolumen.MILILITRO, 0.001 },
            { UnidadVolumen.LITRO, 1.0 },
            { UnidadVolumen.METRO_CUBICO, 1000.0 },
            { UnidadVolumen.GALON, 3.78541 }
        };

        public ConversorVolumen() { }

        public double Convertir(double valor, UnidadVolumen unidadOrigen, UnidadVolumen unidadFinal)
        {
            if (!factores.ContainsKey(unidadOrigen) || !factores.ContainsKey(unidadFinal))
            {
                throw new ArgumentException("Unidad no soportada");
            }

            double valorEnBase = valor * factores[unidadOrigen];

            return valorEnBase / factores[unidadFinal];
        }
    }
}
```

**`UnidadMapper`** (método nuevo)
```csharp
        public static UnidadVolumen ToVolumen(string unidad)
        {
            return Mapear<UnidadVolumen>(unidad, "Unidad de volumen no soportada: ");
        }
```

**`ServicioCatalogo.ObtenerUnidades`** (línea nueva)
```csharp
                Categoria.VOLUMEN => Enum.GetNames<UnidadVolumen>().ToList(),
```

**`ServicioConversion`** (campo y caso nuevos)
```csharp
        private readonly ServicioConversor<UnidadVolumen> servicioVolumen =
                new(new ConversorVolumen());
```
```csharp
                Categoria.VOLUMEN => servicioVolumen.Convertir(valor,
                        UnidadMapper.ToVolumen(solicitud.UnidadOrigen),
                        UnidadMapper.ToVolumen(solicitud.UnidadDestino)),
```

**`ConversorVolumenTest.cs`** (sin namespace)
```csharp
using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.modelos.utilidades.enums;

public class ConversorVolumenTest
{
    private readonly ConversorVolumen conversor = new();

    [Theory]
    [InlineData(1000, UnidadVolumen.MILILITRO, UnidadVolumen.LITRO, 1.0)]
    [InlineData(1, UnidadVolumen.METRO_CUBICO, UnidadVolumen.LITRO, 1000.0)]
    [InlineData(500, UnidadVolumen.MILILITRO, UnidadVolumen.LITRO, 0.5)]
    public void DeberiaConvertirCorrectamente(double valor,
                                              UnidadVolumen origen,
                                              UnidadVolumen destino,
                                              double esperado)
    {
        double resultado = conversor.Convertir(valor, origen, destino);

        Assert.Equal(esperado, resultado, 4);
    }
}
```
> Ojo: en `ServicioConversionTest` el caso `"VOLUMEN", "LITRO", "MILILITRO"` espera `DATA-001` porque `VOLUMEN` no existe. Si agregas el dominio, cambia ese caso por otra categoría inexistente (por ejemplo `"AREA"`).

### 5.3 Adaptar a un proyecto que no sea de conversiones
Si el proyecto nuevo es de otro tipo (por ejemplo, una calculadora de áreas o de intereses), conserva **la misma forma de capas** y cambia solo los nombres del verbo:

| Original | Equivalente genérico |
|---|---|
| `IConversor<T>.Convertir(valor, unidadOrigen, unidadFinal)` | `I{Operador}<T>.{Operacion}(valor, ...)` |
| `ServicioConversor<T>` | `Servicio{Operador}<T>` (mismo cuerpo, mismo `InvalidOperationException`) |
| `Conversor{Dominio}` | `{Operador}{Dominio}` |
| `Unidad{Dominio}` (enum) | `Tipo{Dominio}` o `Unidad{Dominio}` (enum en MAYÚSCULAS) |
| `UnidadMapper.To{Dominio}` | `{Tipo}Mapper.To{Dominio}` |
| `POST {ruta}/convertir` | `POST {ruta}/{operacion}` |

**Nunca cambies:** el namespace raíz `ec.edu.monster`, las 8 carpetas de la sección 2, el formato `{ estado, codigo, mensaje }` de las respuestas, el token en `Authorization: Bearer` ni las reglas de idioma. Las operaciones, sus campos, la duración del token y la existencia de BDD **los define el documento de requisitos**.

---

## 6. Decisiones tomadas sobre el documento de Conversor de Unidades

> El `.docx` (`TALLER CASOS DE USO CONVERSION UNIDADES.docx`) deja abiertos estos puntos. El código de esta guía los resolvió así; **confírmalos con el docente antes de entregar** y, si alguno cambia, ajusta solo la pieza indicada.

| # | Punto abierto en el documento | Decisión en esta guía | Dónde se cambia |
|---|---|---|---|
| 1 | El catálogo menciona 6 categorías (longitud, peso, volumen, tiempo, temperatura, área), pero los servicios piden solo Longitud, Masa y Temperatura (el propio documento lo señala en su sección 30) | **Alternativa A, alcance restringido:** 3 categorías | `Categoria` + sección 5.2 por cada dominio extra |
| 2 | Solo define `AUTH-001` y `UNIT-001` | Se agregan `AUTH-002` (sesión expirada), `AUTH-003` (credenciales), `DATA-001` (datos inconsistentes) y `SRV-001` (error interno) | `CodigosRespuesta` |
| 3 | Dice que la sesión caduca, pero no cuánto dura | 30 minutos fijos desde el login | `AdministradorTokens.MINUTOS_EXPIRACION` |
| 4 | Los ejemplos usan abreviaturas (`KM`, `M`) | Se usan los nombres completos del catálogo (`KILOMETRO`, `METRO`), sin abreviaturas | `Unidad{Dominio}` / `UnidadMapper` |
| 5 | Los errores de token muestran `resultado = null` | Los errores de token responden `{ estado, codigo, mensaje }`, sin `resultado`, porque el filtro corre antes de saber qué operación se pidió. Los errores de conversión sí traen `resultado: null` | `ManejadorAutenticacion` |
| 6 | No dice si el catálogo exige token | Lo exige: el boceto del documento pasa `token` a `obtenerCategorias` y `obtenerUnidades` | `ManejadorAutenticacion.OPERACION_LIBRE` |
| 7 | Credenciales "creadas previamente", sin valores | `MONSTER` / `Monster9`, las que ya usan los clientes | `AdministradorCredenciales` |
| 8 | No pide cambiar contraseña ni cerrar sesión | No se implementan | — |

### 6.1 Compatibilidad con los clientes que ya están en el repositorio

Los clientes actuales (`02. CLICON`, `02. CLIESC`, `02. CLIEWEB`, `02.  CLIMOV`) se hicieron contra un borrador anterior del servidor y **no hablan este contrato**. Al conectarlos con el servidor de esta guía, hay que actualizarlos así:

| Hoy en los clientes | Con el servidor de esta guía |
|---|---|
| `.../api/ConversionUnidades_Controlador` | `.../api/conversor/convertir` (y las demás rutas de 4.1) |
| `GET` con `?valor=&unidadOrigen=&unidadDestino=` (móvil) | `POST` con cuerpo JSON `{ categoria, unidadOrigen, unidadDestino, valor }` |
| `tipoConversion: "Longitud"` | `categoria: "LONGITUD"` |
| Unidades `"Kilómetros"`, `"Celsius"` | Unidades del catálogo `"KILOMETRO"`, `"CELSIUS"`, o pedirlas a `GET .../categorias/{categoria}/unidades` |
| Login simulado en el cliente | `POST .../login` y guardar el `token` |
| Sin header de token | `Authorization: Bearer <token>` en cada petición |
| Lee `resultado`, `mensaje`, `exito` | Lee `estado` (`OK`/`ERROR`), `codigo`, `mensaje` y `resultado` |
| El móvil solo lee el cuerpo si la respuesta es 200 | Leer el cuerpo también en 400 y 401: ahí viene el mensaje de error |
| IP fija `192.168.137.1:5014` | Igual, siempre que el servidor escuche en `0.0.0.0:5014` (3.2) |
