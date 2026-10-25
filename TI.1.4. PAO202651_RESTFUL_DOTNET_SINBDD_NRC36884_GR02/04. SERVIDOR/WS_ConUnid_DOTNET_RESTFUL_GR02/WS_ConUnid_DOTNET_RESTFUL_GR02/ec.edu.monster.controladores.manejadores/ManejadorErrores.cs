using ec.edu.monster.modelos.utilidades;
using Microsoft.AspNetCore.Mvc;

namespace ec.edu.monster.controladores.manejadores
{
    public class ManejadorErrores
    {
        private readonly RequestDelegate siguiente;

        public ManejadorErrores(RequestDelegate siguiente)
        {
            this.siguiente = siguiente;
        }

        public async Task InvokeAsync(HttpContext contexto)
        {
            try
            {
                await siguiente(contexto);
            }
            catch (ExcepcionNegocio e)
            {
                await EscribirError(contexto, e.EstadoHttp, e.Codigo, e.Message);
            }
            catch (ArgumentException e)
            {
                await EscribirError(contexto, StatusCodes.Status400BadRequest, CodigosRespuesta.DATOS_INCONSISTENTES, e.Message);
            }
            catch (Exception)
            {
                await EscribirError(contexto, StatusCodes.Status500InternalServerError, CodigosRespuesta.ERROR_SERVIDOR,
                        "Error interno del servidor");
            }
        }

        public static IActionResult RespuestaDatosInvalidos(ActionContext contexto)
        {
            return new BadRequestObjectResult(new Respuesta
            {
                Estado = CodigosRespuesta.ESTADO_ERROR,
                Codigo = CodigosRespuesta.DATOS_INCONSISTENTES,
                Mensaje = "Datos inconsistentes: la solicitud no tiene el formato esperado"
            });
        }

        private static Task EscribirError(HttpContext contexto, int estadoHttp, string codigo, string mensaje)
        {
            contexto.Response.StatusCode = estadoHttp;

            return contexto.Response.WriteAsJsonAsync(new Respuesta
            {
                Estado = CodigosRespuesta.ESTADO_ERROR,
                Codigo = codigo,
                Mensaje = mensaje
            });
        }
    }
}
