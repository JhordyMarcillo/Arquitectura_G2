using ec.edu.monster.controladores.manejadores;
using ec.edu.monster.datos;
using ec.edu.monster.seguridad;

var builder = WebApplication.CreateBuilder(args);

// Cadena de conexion: variable DB_CONNECTION_STRING, .env de "03. BDD" o appsettings.json
ConfiguracionBaseDatos.Inicializar(builder.Configuration);

builder.Services.AddControllers(opciones => opciones.Filters.Add<ManejadorAutenticacion>())
    .ConfigureApiBehaviorOptions(opciones =>
        opciones.InvalidModelStateResponseFactory = ManejadorErrores.RespuestaDatosInvalidos);
builder.Services.AgregarCors();

var app = builder.Build();

app.UseMiddleware<ManejadorErrores>();

// Solo HTTP para trabajar en la red local
// app.UseHttpsRedirection();

app.UseCors(ConfiguracionCors.POLITICA);

app.MapControllers();

app.Logger.LogInformation("Base de datos: {Origen}", ConfiguracionBaseDatos.Origen);

app.Run();
