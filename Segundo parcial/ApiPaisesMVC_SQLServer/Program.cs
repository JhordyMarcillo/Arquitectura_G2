using ApiPaisesMVC_SQLServer.Models;
using ApiPaisesMVC_SQLServer.Services;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllersWithViews();

builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseSqlServer(
        builder.Configuration.GetConnectionString("SqlServerConnection")));

// Servicios que consumen las APIs publicas
builder.Services.AddHttpClient<PaisApiService>();
builder.Services.AddHttpClient<UniversidadApiService>();

var app = builder.Build();

// Aplica las migraciones pendientes (crea la base ApiPaisesDB y las tablas si no existen)
// (las herramientas de EF pasan --no-migrate para no conectarse al crear migraciones)
if (!args.Contains("--no-migrate"))
{
    using var scope = app.Services.CreateScope();
    var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
    db.Database.Migrate();
}

if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Home/Error");
    app.UseHsts();
}

app.UseHttpsRedirection();
app.UseStaticFiles();
app.UseRouting();
app.UseAuthorization();

app.MapControllerRoute(
    name: "default",
    pattern: "{controller=Pais}/{action=Index}/{id?}");

app.Run();
