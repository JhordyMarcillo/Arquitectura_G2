using ec.edu.monster.modelos;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Net.Http.Json;
using System.Text.Json;

namespace ec.edu.monster.servicios
{
    /// <summary>Proxy del servicio REST EurekaBank (sirve para el servidor .NET y el Java).</summary>
    public class ServicioEurekaBankRest : IServicioEurekaBank
    {
        public const string URL_POR_DEFECTO = "http://localhost:5080/api/eurekabank";

        private static readonly JsonSerializerOptions FORMATO_JSON = new(JsonSerializerDefaults.Web);
        private readonly HttpClient clienteHttp = new() { Timeout = TimeSpan.FromSeconds(15) };
        private string urlServidor = URL_POR_DEFECTO;

        public string UrlServidor
        {
            get => urlServidor;
            set => urlServidor = value.Trim().TrimEnd('/');
        }

        public RespuestaAutenticacion? Sesion { get; private set; }

        public async Task<RespuestaAutenticacion> IniciarSesionAsync(string usuario, string clave)
        {
            Sesion = null;
            RespuestaAutenticacion respuesta = await EnviarAsync<RespuestaAutenticacion>(HttpMethod.Post, "/login",
                    new { usuario, clave });
            Sesion = respuesta;
            return respuesta;
        }

        public void CerrarSesion() => Sesion = null;

        public void RestaurarSesion(RespuestaAutenticacion sesion) => Sesion = sesion;

        public Task<RespuestaCuentas> ListarCuentasAsync() =>
                EnviarAsync<RespuestaCuentas>(HttpMethod.Get, "/cuentas");

        public Task<RespuestaMovimientos> ConsultarMovimientosAsync(string cuenta) =>
                EnviarAsync<RespuestaMovimientos>(HttpMethod.Get, $"/cuentas/{Uri.EscapeDataString(cuenta.Trim())}/movimientos");

        public Task<RespuestaOperacion> DepositarAsync(string cuenta, decimal importe) =>
                EnviarAsync<RespuestaOperacion>(HttpMethod.Post, "/depositos", new { cuenta, importe });

        public Task<RespuestaOperacion> RetirarAsync(string cuenta, string clave, decimal importe) =>
                EnviarAsync<RespuestaOperacion>(HttpMethod.Post, "/retiros", new { cuenta, clave, importe });

        public Task<RespuestaOperacion> TransferirAsync(string cuentaOrigen, string claveOrigen, string cuentaDestino, decimal importe) =>
                EnviarAsync<RespuestaOperacion>(HttpMethod.Post, "/transferencias",
                        new { cuentaOrigen, claveOrigen, cuentaDestino, importe });

        private async Task<T> EnviarAsync<T>(HttpMethod metodo, string ruta, object? cuerpo = null) where T : Respuesta
        {
            using HttpRequestMessage peticion = new(metodo, UrlServidor + ruta);
            if (Sesion is not null)
            {
                peticion.Headers.Authorization = new AuthenticationHeaderValue("Bearer", Sesion.Token);
            }
            if (cuerpo is not null)
            {
                peticion.Content = JsonContent.Create(cuerpo, options: FORMATO_JSON);
            }

            HttpResponseMessage respuestaHttp;
            try
            {
                respuestaHttp = await clienteHttp.SendAsync(peticion);
            }
            catch (Exception e) when (e is HttpRequestException or TaskCanceledException or InvalidOperationException or UriFormatException)
            {
                throw new ExcepcionServicio(ExcepcionServicio.SIN_CONEXION,
                        "No se pudo conectar con el servidor " + UrlServidor, e);
            }

            using (respuestaHttp)
            {
                T? respuesta;
                try
                {
                    respuesta = await respuestaHttp.Content.ReadFromJsonAsync<T>(FORMATO_JSON);
                }
                catch (JsonException e)
                {
                    throw new ExcepcionServicio(ExcepcionServicio.SIN_CONEXION,
                            $"El servidor respondio HTTP {(int)respuestaHttp.StatusCode} sin el formato esperado. Revise la URL.", e);
                }

                if (respuesta is null || !respuesta.EsExito)
                {
                    if (respuesta?.Codigo is "AUTH-001" or "AUTH-002")
                    {
                        Sesion = null;
                    }
                    throw new ExcepcionServicio(respuesta?.Codigo ?? "SRV-001",
                            respuesta?.Mensaje ?? "Respuesta vacia del servidor");
                }
                return respuesta;
            }
        }
    }
}
