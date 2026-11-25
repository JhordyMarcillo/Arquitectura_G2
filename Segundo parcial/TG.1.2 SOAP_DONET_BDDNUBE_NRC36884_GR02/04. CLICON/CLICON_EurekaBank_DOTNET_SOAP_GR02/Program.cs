using ec.edu.monster.controladores;
using ec.edu.monster.servicios;
using System.Text;

Console.OutputEncoding = Encoding.UTF8;
Console.Title = "EurekaBank - Cliente de consola";

// La URL del servidor puede venir como argumento: CLICON.exe http://192.168.1.50:5090/WSEurekaBank.svc
IServicioEurekaBank servicio = new ServicioEurekaBankSoap();
if (args.Length > 0)
{
    servicio.UrlServidor = args[0];
}

await new ControladorConsola(servicio, "SOAP .NET  ·  GR02").EjecutarAsync();
