using ec.edu.monster.modelos;

namespace ec.edu.monster.servicios
{
    /// <summary>
    /// Operaciones del contrato EurekaBank que consumen los clientes. La implementacion
    /// REST y la SOAP exponen la misma interfaz, asi las vistas no dependen del protocolo.
    /// </summary>
    public interface IServicioEurekaBank
    {
        string UrlServidor { get; set; }
        RespuestaAutenticacion? Sesion { get; }

        Task<RespuestaAutenticacion> IniciarSesionAsync(string usuario, string clave);
        void CerrarSesion();

        /// <summary>Retoma una sesion guardada (por ejemplo, en la sesion web del navegador).</summary>
        void RestaurarSesion(RespuestaAutenticacion sesion);
        Task<RespuestaCuentas> ListarCuentasAsync();
        Task<RespuestaMovimientos> ConsultarMovimientosAsync(string cuenta);
        Task<RespuestaOperacion> DepositarAsync(string cuenta, decimal importe);
        Task<RespuestaOperacion> RetirarAsync(string cuenta, string clave, decimal importe);
        Task<RespuestaOperacion> TransferirAsync(string cuentaOrigen, string claveOrigen, string cuentaDestino, decimal importe);
    }
}
