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
