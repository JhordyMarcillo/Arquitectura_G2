using ec.edu.monster.modelos;
using System.ServiceModel;

namespace ec.edu.monster.servicios
{
    /// <summary>
    /// Proxy del servicio SOAP EurekaBank con ChannelFactory (WCF). Expone la misma interfaz
    /// que el proxy REST, asi las vistas no cambian entre TI.
    /// </summary>
    public class ServicioEurekaBankSoap : IServicioEurekaBank
    {
        public const string URL_POR_DEFECTO = "http://localhost:5090/WSEurekaBank.svc";

        private string urlServidor = URL_POR_DEFECTO;
        private ChannelFactory<IWSEurekaBank>? fabrica;

        public string UrlServidor
        {
            get => urlServidor;
            set
            {
                urlServidor = value.Trim();
                fabrica?.Abort();
                fabrica = null;
            }
        }

        public RespuestaAutenticacion? Sesion { get; private set; }

        public async Task<RespuestaAutenticacion> IniciarSesionAsync(string usuario, string clave)
        {
            Sesion = null;
            SoapRespuestaSesion r = await LlamarAsync(canal => canal.IniciarSesionAsync(usuario, clave));
            Sesion = new RespuestaAutenticacion
            {
                Estado = r.Estado, Codigo = r.Codigo, Mensaje = r.Mensaje, Token = r.Token,
                Expiracion = r.Expiracion, CodigoEmpleado = r.CodigoEmpleado, NombreEmpleado = r.NombreEmpleado
            };
            return Sesion;
        }

        public void CerrarSesion() => Sesion = null;

        public void RestaurarSesion(RespuestaAutenticacion sesion) => Sesion = sesion;

        public async Task<RespuestaCuentas> ListarCuentasAsync()
        {
            SoapRespuestaCuentas r = await LlamarAsync(canal => canal.ListarCuentasAsync(Token));
            return new RespuestaCuentas
            {
                Estado = r.Estado, Codigo = r.Codigo, Mensaje = r.Mensaje,
                Cuentas = r.Cuentas.Select(c => new DatosCuenta
                {
                    Codigo = c.Codigo, Cliente = c.Cliente, Moneda = c.Moneda, Saldo = c.Saldo, Estado = c.Estado
                }).ToList()
            };
        }

        public async Task<RespuestaMovimientos> ConsultarMovimientosAsync(string cuenta)
        {
            SoapRespuestaMovimientos r = await LlamarAsync(canal => canal.ConsultarMovimientosAsync(Token, cuenta.Trim()));
            return new RespuestaMovimientos
            {
                Estado = r.Estado, Codigo = r.Codigo, Mensaje = r.Mensaje, Cuenta = r.Cuenta, Moneda = r.Moneda, Saldo = r.Saldo,
                Movimientos = r.Movimientos.Select(m => new DatosMovimiento
                {
                    Numero = m.Numero, Fecha = m.Fecha, Tipo = m.Tipo, Descripcion = m.Descripcion,
                    Accion = m.Accion, Importe = m.Importe, CuentaReferencia = m.CuentaReferencia
                }).ToList()
            };
        }

        public Task<RespuestaOperacion> DepositarAsync(string cuenta, decimal importe) =>
                OperarAsync(canal => canal.RegistrarDepositoAsync(Token, cuenta, importe));

        public Task<RespuestaOperacion> RetirarAsync(string cuenta, string clave, decimal importe) =>
                OperarAsync(canal => canal.RegistrarRetiroAsync(Token, cuenta, clave, importe));

        public Task<RespuestaOperacion> TransferirAsync(string cuentaOrigen, string claveOrigen, string cuentaDestino, decimal importe) =>
                OperarAsync(canal => canal.RegistrarTransferenciaAsync(Token, cuentaOrigen, claveOrigen, cuentaDestino, importe));

        // ------------------------------------------------------------------ comunicacion

        private string Token => Sesion?.Token ?? string.Empty;

        private async Task<RespuestaOperacion> OperarAsync(Func<IWSEurekaBank, Task<SoapRespuestaOperacion>> llamada)
        {
            SoapRespuestaOperacion r = await LlamarAsync(llamada);
            return new RespuestaOperacion { Estado = r.Estado, Codigo = r.Codigo, Mensaje = r.Mensaje, Cuenta = r.Cuenta, Saldo = r.Saldo };
        }

        /// <summary>Invoca la operacion y convierte estado ERROR o fallas de red en ExcepcionServicio.</summary>
        private async Task<T> LlamarAsync<T>(Func<IWSEurekaBank, Task<T>> llamada) where T : SoapRespuesta
        {
            T respuesta;
            IWSEurekaBank canal;
            try
            {
                canal = Fabrica().CreateChannel();
            }
            catch (Exception e) when (e is UriFormatException or ArgumentException)
            {
                throw new ExcepcionServicio(ExcepcionServicio.SIN_CONEXION, "La URL del servidor no es valida: " + UrlServidor, e);
            }

            try
            {
                respuesta = await llamada(canal);
                ((ICommunicationObject)canal).Close();
            }
            catch (Exception e) when (e is CommunicationException or TimeoutException)
            {
                ((ICommunicationObject)canal).Abort();
                throw new ExcepcionServicio(ExcepcionServicio.SIN_CONEXION,
                        "No se pudo conectar con el servidor " + UrlServidor, e);
            }

            if (respuesta is null || respuesta.Estado != "OK")
            {
                if (respuesta?.Codigo is "AUTH-001" or "AUTH-002")
                {
                    Sesion = null;
                }
                throw new ExcepcionServicio(respuesta?.Codigo ?? "SRV-001", respuesta?.Mensaje ?? "Respuesta vacia del servidor");
            }
            return respuesta;
        }

        private ChannelFactory<IWSEurekaBank> Fabrica()
        {
            if (fabrica is null)
            {
                BasicHttpBinding enlace = new()
                {
                    MaxReceivedMessageSize = 4 * 1024 * 1024,
                    SendTimeout = TimeSpan.FromSeconds(15),
                    ReceiveTimeout = TimeSpan.FromSeconds(15),
                    OpenTimeout = TimeSpan.FromSeconds(15)
                };
                fabrica = new ChannelFactory<IWSEurekaBank>(enlace, new EndpointAddress(UrlServidor));
            }
            return fabrica;
        }
    }
}
