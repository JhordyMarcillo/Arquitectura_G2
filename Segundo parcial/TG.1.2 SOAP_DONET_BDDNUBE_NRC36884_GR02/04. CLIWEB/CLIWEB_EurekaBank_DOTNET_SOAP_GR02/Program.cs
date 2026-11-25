var builder = WebApplication.CreateBuilder(args);

builder.Services.AddRazorPages(opciones => opciones.RootDirectory = "/ec.edu.monster.vistas");
builder.Services.AddDistributedMemoryCache();
builder.Services.AddSession(opciones =>
{
    opciones.Cookie.Name = "EurekaBank.Sesion";
    opciones.Cookie.HttpOnly = true;
    opciones.IdleTimeout = TimeSpan.FromMinutes(60);
});

var app = builder.Build();

if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Error");
}

app.UseStaticFiles();
app.UseRouting();
app.UseSession();
app.MapRazorPages();

app.Run();
