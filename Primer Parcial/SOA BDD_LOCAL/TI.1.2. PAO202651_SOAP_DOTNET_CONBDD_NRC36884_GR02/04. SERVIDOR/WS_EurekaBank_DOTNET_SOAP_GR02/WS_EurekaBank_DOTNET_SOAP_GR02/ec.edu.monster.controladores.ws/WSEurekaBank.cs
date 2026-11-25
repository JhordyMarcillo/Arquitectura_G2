using CoreWCF;
using ec.edu.monster.controladores.manejadoresws;
using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.servicios;

namespace ec.edu.monster.controladores.ws
{
    /// <summary>
    /// Servicio SOAP EurekaBank. Valida el token y delega en los mismos servicios de negocio
    /// del servidor REST; los errores se devuelven en la respuesta (estado ERROR), no como fault.
    /// </summary>
    [ServiceBehavior(Namespace = EspacioNombres.WS, InstanceContextMode = InstanceContextMode.PerCall)]
    public class WSEurekaBank : IWSEurekaBank
    {
        private readonly ServicioAutenticacion servicioAutenticacion = new();
        private readonly ServicioCuentas servicioCuentas = new();
        private readonly IServicioOperaciones servicioOperaciones = new ServicioOperaciones();

        public RespuestaAutenticacion IniciarSesion(string usuario, string clave) =>
                ManejadorSoap.Ejecutar<RespuestaAutenticacion>(null, _ =>
                        servicioAutenticacion.Autenticar(new SolicitudLogin { Usuario = usuario, Clave = clave }), exigeToken: false);

        public RespuestaCuentas ListarCuentas(string token) =>
                ManejadorSoap.Ejecutar<RespuestaCuentas>(token, _ => servicioCuentas.ListarCuentas());

        public RespuestaMovimientos ConsultarMovimientos(string token, string cuenta) =>
                ManejadorSoap.Ejecutar<RespuestaMovimientos>(token, _ => servicioCuentas.ConsultarMovimientos(cuenta));

        public RespuestaOperacion RegistrarDeposito(string token, string cuenta, decimal importe) =>
                ManejadorSoap.Ejecutar<RespuestaOperacion>(token, sesion =>
                        servicioOperaciones.Depositar(new SolicitudDeposito { Cuenta = cuenta, Importe = importe },
                                sesion!.CodigoEmpleado));

        public RespuestaOperacion RegistrarRetiro(string token, string cuenta, string clave, decimal importe) =>
                ManejadorSoap.Ejecutar<RespuestaOperacion>(token, sesion =>
                        servicioOperaciones.Retirar(new SolicitudRetiro { Cuenta = cuenta, Clave = clave, Importe = importe },
                                sesion!.CodigoEmpleado));

        public RespuestaOperacion RegistrarTransferencia(string token, string cuentaOrigen, string claveOrigen,
                string cuentaDestino, decimal importe) =>
                ManejadorSoap.Ejecutar<RespuestaOperacion>(token, sesion =>
                        servicioOperaciones.Transferir(new SolicitudTransferencia
                        {
                            CuentaOrigen = cuentaOrigen,
                            ClaveOrigen = claveOrigen,
                            CuentaDestino = cuentaDestino,
                            Importe = importe
                        }, sesion!.CodigoEmpleado));
    }
}
