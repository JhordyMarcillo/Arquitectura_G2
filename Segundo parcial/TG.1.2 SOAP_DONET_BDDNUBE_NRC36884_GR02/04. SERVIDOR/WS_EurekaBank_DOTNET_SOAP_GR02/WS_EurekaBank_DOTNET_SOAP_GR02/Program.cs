using CoreWCF;
using CoreWCF.Configuration;
using CoreWCF.Description;
using ec.edu.monster.controladores.ws;
using ec.edu.monster.datos;

var builder = WebApplication.CreateBuilder(args);

// Cadena de conexion: variable DB_CONNECTION_STRING, .env de "03. BDD" o appsettings.json
ConfiguracionBaseDatos.Inicializar(builder.Configuration);

builder.Services.AddServiceModelServices();
builder.Services.AddServiceModelMetadata();
// El WSDL publica la direccion con la que llego la peticion (localhost o la IP de la red)
builder.Services.AddSingleton<IServiceBehavior, UseRequestHeadersForMetadataAddressBehavior>();

var app = builder.Build();

app.UseServiceModel(configuracion =>
{
    configuracion.AddService<WSEurekaBank>();
    configuracion.AddServiceEndpoint<WSEurekaBank, IWSEurekaBank>(
            new BasicHttpBinding { MaxReceivedMessageSize = 1024 * 1024 }, "/WSEurekaBank.svc");

    ServiceMetadataBehavior metadatos = app.Services.GetRequiredService<ServiceMetadataBehavior>();
    metadatos.HttpGetEnabled = true;
});

app.MapGet("/", () => Results.Text(
        "WS EurekaBank SOAP .NET (GR02). Servicio: /WSEurekaBank.svc  -  WSDL: /WSEurekaBank.svc?wsdl"));

app.Logger.LogInformation("Base de datos: {Origen}", ConfiguracionBaseDatos.Origen);

app.Run();
