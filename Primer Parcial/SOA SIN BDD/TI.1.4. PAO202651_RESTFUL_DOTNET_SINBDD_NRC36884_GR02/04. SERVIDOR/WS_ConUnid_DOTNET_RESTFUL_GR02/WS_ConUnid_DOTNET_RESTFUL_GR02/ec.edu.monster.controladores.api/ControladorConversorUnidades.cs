using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.servicios;
using Microsoft.AspNetCore.Mvc;

namespace ec.edu.monster.controladores.api
{
    [ApiController]
    [Route("api/conversor")]
    public class ControladorConversorUnidades : ControllerBase
    {
        private readonly ServicioAutenticacion servicioAutenticacion = new();
        private readonly ServicioCatalogo servicioCatalogo = new();
        private readonly ServicioConversion servicioConversion = new();

        [HttpPost("login")]
        public ActionResult<RespuestaAutenticacion> Login([FromBody] SolicitudLogin solicitud)
        {
            return Ok(servicioAutenticacion.Autenticar(solicitud));
        }

        [HttpGet("categorias")]
        public ActionResult<RespuestaCatalogo> ConsultarCategorias()
        {
            return Ok(servicioCatalogo.ConsultarCategorias());
        }

        [HttpGet("categorias/{categoria}/unidades")]
        public ActionResult<RespuestaUnidades> ConsultarUnidades(string categoria)
        {
            return Ok(servicioCatalogo.ConsultarUnidades(categoria));
        }

        [HttpPost("convertir")]
        public ActionResult<RespuestaConversion> Convertir([FromBody] SolicitudConversion solicitud)
        {
            try
            {
                return Ok(servicioConversion.Convertir(solicitud));
            }
            catch (ExcepcionNegocio e)
            {
                return StatusCode(e.EstadoHttp, new RespuestaConversion
                {
                    Estado = CodigosRespuesta.ESTADO_ERROR,
                    Codigo = e.Codigo,
                    Mensaje = e.Message,
                    Resultado = null,
                    UnidadOrigen = solicitud.UnidadOrigen,
                    UnidadDestino = solicitud.UnidadDestino
                });
            }
        }
    }
}
