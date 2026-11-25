# Taller: Consumo de APIs públicas en .NET con SQL Server (ASP.NET Core MVC + EF Core)

## La API ahora pide API key
`https://restcountries.com/v3.1/all` (el endpoint del enunciado) fue dado de baja. Se usa
`https://api.restcountries.com/countries/v5` con `Authorization: Bearer <API_KEY>`, que devuelve otro
JSON (`data.objects[]`, `names.common`, `capitals[]`, `currencies[]`, `area.kilometers`...).
Key gratis: https://restcountries.com/sign-up

## Configuración (los secretos NO van en el código)
Se guardan con *user-secrets* (fuera del repositorio):
```bash
dotnet user-secrets set "RestCountries:ApiKey" "<tu API key>"
dotnet user-secrets set "ConnectionStrings:SqlServerConnection" "Server=<host>,1433;Database=ApiPaisesDB;User Id=<usuario>;Password=<clave>;TrustServerCertificate=True;Encrypt=True;"
```
`appsettings.json` trae la cadena de conexión sin contraseña (RDS `arqg2-sqlserver`). Para SQL Server local:
`Server=localhost;Database=ApiPaisesDB;Trusted_Connection=True;TrustServerCertificate=True;`

## Ejecutar
```bash
dotnet run
```
Al iniciar aplica las migraciones (`InitialCreate`): crea la base `ApiPaisesDB` y las tablas `Paises` y `Universidades`.
Manual: `dotnet dotnet-ef database update`.

## Estructura MVC
| Capa | Archivos |
|---|---|
| Modelo | `Models/Pais`, `Models/Universidad`, `Models/AppDbContext`, `Services/PaisApiService`, `Services/UniversidadApiService` |
| Vista | `Views/Pais/*`, `Views/Universidad/Lista` |
| Controlador | `Controllers/PaisController`, `Controllers/UniversidadController` |

## Actividades
1. **Consultar un país en la API** → *Consultar en API* (y guardar el resultado).
2. **CRUD** → *Países* → Nuevo / Editar / Eliminar.
3. **Búsquedas** por nombre, región y capital → formulario del listado.
4. **Filtros dinámicos** por región (América, Europa, Asia, África, Oceanía) → botones y combo.
5. **Reportes** → países por región, país más poblado, promedio poblacional por región.
6. **Actividad avanzada** → *Universidades*: API `universities.hipolabs.com`, guarda nombre, dominio, sitio web y país.

Notas: `Nombre` es único (importar varias veces actualiza, no duplica). La región se guarda como la entrega la API (inglés).
