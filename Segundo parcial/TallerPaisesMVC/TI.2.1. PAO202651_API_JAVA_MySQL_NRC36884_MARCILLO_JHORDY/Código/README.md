# Taller: Consumo de API pública (REST Countries v5) + MySQL con patrón MVC

## Importante: la API ahora pide API key
La versión v3.1 (`/v3.1/all`) del enunciado fue **dada de baja**. REST Countries ahora usa
`https://api.restcountries.com/countries/v5` con header `Authorization: Bearer <API_KEY>`
y un JSON de otra forma (`data.objects[]`, `names.common`, `capitals[]`, `currencies[]`...).

1. Crea tu key gratis en https://restcountries.com/sign-up
2. Ponla en `config.properties` (`api.key=...`), o en la variable de entorno
   `RESTCOUNTRIES_API_KEY`, o desde el botón **API Key...** de la aplicación.

## Requisitos
JDK 17+, Maven, MySQL 8 (usuario/clave en `config.properties`, por defecto root / 12345).
La app crea la base `api_paises` y la tabla `pais` sola (también está en `sql/script.sql`).

## Ejecutar
```bash
mvn package
java -jar target/taller-paises-mvc.jar
```
(o `mvn compile exec:java`). Ejecutar desde esta carpeta para que lea `config.properties`.

## Estructura MVC
| Capa | Clases |
|---|---|
| Modelo | `Pais`, `Conexion`, `Config`, `ConsumoAPI`, `PaisDAO`, `ApiException` |
| Vista | `vista.FrmPais` (Swing) |
| Controlador | `controlador.PaisController` |

## Actividades
1. **Consultar país por nombre** → campo "País en la API" + *Consultar en API* (y *Guardar resultado en BD*).
2. **CRUD** → formulario inferior (Crear / Actualizar / Eliminar / Nuevo).
3. **JTable** → tabla central con todos los países (ordenable por columna).
4. **Búsqueda por región** → combo América, Europa, Asia, África, Oceanía (+ búsqueda por nombre en la BD).
5. **Reportes** → pestaña *Reportes*: países por región, país más poblado, región con más países.

Notas: `nombre` es `UNIQUE`, así que importar varias veces actualiza en vez de duplicar.
La región se guarda como la entrega la API (en inglés: Americas, Europe, Asia, Africa, Oceania).
