using ec.edu.monster.controladores.manejadores;
using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.seguridad;
using ec.edu.monster.servicios;
using Microsoft.AspNetCore.Mvc;

namespace ec.edu.monster.controladores.api
{
    [ApiController]
    [Route("api/eurekabank")]
    public class ControladorEurekaBank : ControllerBase
    {
        private readonly ServicioAutenticacion servicioAutenticacion = new();
        private readonly ServicioCuentas servicioCuentas = new();
        private readonly IServicioOperaciones servicioOperaciones = new ServicioOperaciones();

        [HttpPost("login")]
        [PermitirAnonimo]
        public ActionResult<RespuestaAutenticacion> Login([FromBody] SolicitudLogin solicitud)
        {
            return Ok(servicioAutenticacion.Autenticar(solicitud));
        }

        [HttpGet("cuentas")]
        public ActionResult<RespuestaCuentas> ListarCuentas()
        {
            return Ok(servicioCuentas.ListarCuentas());
        }

        [HttpGet("cuentas/{cuenta}/movimientos")]
        public ActionResult<RespuestaMovimientos> ConsultarMovimientos(string cuenta)
        {
            return Ok(servicioCuentas.ConsultarMovimientos(cuenta));
        }

        [HttpPost("depositos")]
        public ActionResult<RespuestaOperacion> Depositar([FromBody] SolicitudDeposito solicitud)
        {
            return Ok(servicioOperaciones.Depositar(solicitud, Sesion.CodigoEmpleado));
        }

        [HttpPost("retiros")]
        public ActionResult<RespuestaOperacion> Retirar([FromBody] SolicitudRetiro solicitud)
        {
            return Ok(servicioOperaciones.Retirar(solicitud, Sesion.CodigoEmpleado));
        }

        [HttpPost("transferencias")]
        public ActionResult<RespuestaOperacion> Transferir([FromBody] SolicitudTransferencia solicitud)
        {
            return Ok(servicioOperaciones.Transferir(solicitud, Sesion.CodigoEmpleado));
        }

        private SesionEmpleado Sesion => (SesionEmpleado)HttpContext.Items[ManejadorAutenticacion.CLAVE_SESION]!;
    }
}
