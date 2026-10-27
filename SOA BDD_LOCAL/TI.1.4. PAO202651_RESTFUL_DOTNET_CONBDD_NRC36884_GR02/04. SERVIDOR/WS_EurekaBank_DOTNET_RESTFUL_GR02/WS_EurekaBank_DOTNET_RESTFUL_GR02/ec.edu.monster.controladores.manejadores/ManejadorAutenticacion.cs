using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.modelos.utilidades.enums;
using ec.edu.monster.seguridad;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;

namespace ec.edu.monster.controladores.manejadores
{
    /// <summary>Exige "Authorization: Bearer token" en todas las acciones salvo las [PermitirAnonimo].</summary>
    public class ManejadorAutenticacion : IActionFilter
    {
        public const string CLAVE_SESION = "SesionEmpleado";
        private const string PREFIJO = "Bearer ";

        public void OnActionExecuting(ActionExecutingContext contexto)
        {
            if (contexto.ActionDescriptor.EndpointMetadata.OfType<PermitirAnonimo>().Any())
            {
                return;
            }

            string cabecera = contexto.HttpContext.Request.Headers.Authorization.ToString();
            string? token = cabecera.StartsWith(PREFIJO, StringComparison.OrdinalIgnoreCase)
                    ? cabecera[PREFIJO.Length..].Trim()
                    : null;

            EstadoToken estado = AdministradorTokens.Validar(token, out SesionEmpleado? sesion);
            if (estado == EstadoToken.VALIDO)
            {
                contexto.HttpContext.Items[CLAVE_SESION] = sesion;
                return;
            }

            contexto.Result = new UnauthorizedObjectResult(new Respuesta
            {
                Estado = CodigosRespuesta.ESTADO_ERROR,
                Codigo = estado == EstadoToken.EXPIRADO ? CodigosRespuesta.SESION_EXPIRADA : CodigosRespuesta.TOKEN_INVALIDO,
                Mensaje = estado == EstadoToken.EXPIRADO ? "La sesion expiro, inicie sesion nuevamente" : "Token invalido o ausente"
            });
        }

        public void OnActionExecuted(ActionExecutedContext contexto)
        {
        }
    }
}
