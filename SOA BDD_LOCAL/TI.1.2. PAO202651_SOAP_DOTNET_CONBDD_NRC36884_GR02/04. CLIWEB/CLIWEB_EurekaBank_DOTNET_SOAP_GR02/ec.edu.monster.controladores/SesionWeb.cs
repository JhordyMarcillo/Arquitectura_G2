using ec.edu.monster.modelos;
using ec.edu.monster.servicios;
using System.Text.Json;

namespace ec.edu.monster.controladores
{
    /// <summary>
    /// Guarda en la sesion del navegador la URL del servidor y los datos de inicio de sesion,
    /// y construye un servicio EurekaBank listo para usar en cada peticion.
    /// </summary>
    public static class SesionWeb
    {
        private const string CLAVE_URL = "UrlServidor";
        private const string CLAVE_SESION = "SesionEurekaBank";
        private const string CLAVE_AVISO = "Aviso";

        public static string UrlServidor(HttpContext contexto, IConfiguration configuracion) =>
                contexto.Session.GetString(CLAVE_URL) ?? configuracion["UrlServidor"] ?? string.Empty;

        public static RespuestaAutenticacion? Sesion(HttpContext contexto)
        {
            string? json = contexto.Session.GetString(CLAVE_SESION);
            return json is null ? null : JsonSerializer.Deserialize<RespuestaAutenticacion>(json);
        }

        public static IServicioEurekaBank CrearServicio(HttpContext contexto, IConfiguration configuracion)
        {
            IServicioEurekaBank servicio = new ServicioEurekaBankSoap { UrlServidor = UrlServidor(contexto, configuracion) };
            RespuestaAutenticacion? sesion = Sesion(contexto);
            if (sesion is not null)
            {
                servicio.RestaurarSesion(sesion);
            }
            return servicio;
        }

        public static void Guardar(HttpContext contexto, string urlServidor, RespuestaAutenticacion sesion)
        {
            contexto.Session.SetString(CLAVE_URL, urlServidor);
            contexto.Session.SetString(CLAVE_SESION, JsonSerializer.Serialize(sesion));
        }

        public static void Cerrar(HttpContext contexto) => contexto.Session.Remove(CLAVE_SESION);

        /// <summary>Mensaje que se muestra una sola vez en la siguiente pagina (por ejemplo, sesion expirada).</summary>
        public static void Avisar(HttpContext contexto, string mensaje) => contexto.Session.SetString(CLAVE_AVISO, mensaje);

        public static string? TomarAviso(HttpContext contexto)
        {
            string? aviso = contexto.Session.GetString(CLAVE_AVISO);
            contexto.Session.Remove(CLAVE_AVISO);
            return aviso;
        }
    }
}
