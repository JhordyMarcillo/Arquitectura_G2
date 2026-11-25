using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.modelos.utilidades.enums;
using ec.edu.monster.seguridad;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;

namespace ec.edu.monster.controladores.manejadores
{
    public class ManejadorAutenticacion : IAuthorizationFilter
    {
        private const string OPERACION_LIBRE = "Login";
        private const string PREFIJO = "Bearer ";

        public void OnAuthorization(AuthorizationFilterContext contexto)
        {
            if (OPERACION_LIBRE.Equals(contexto.RouteData.Values["action"]?.ToString()))
            {
                return;
            }

            string? encabezado = contexto.HttpContext.Request.Headers.Authorization;
            string? token = encabezado != null && encabezado.StartsWith(PREFIJO, StringComparison.OrdinalIgnoreCase)
                    ? encabezado.Substring(PREFIJO.Length).Trim()
                    : null;

            EstadoToken estado = AdministradorTokens.ValidarToken(token);

            if (estado == EstadoToken.VALIDO)
            {
                return;
            }

            var respuesta = new Respuesta
            {
                Estado = CodigosRespuesta.ESTADO_ERROR,
                Codigo = estado == EstadoToken.EXPIRADO ? CodigosRespuesta.SESION_EXPIRADA : CodigosRespuesta.TOKEN_INVALIDO,
                Mensaje = estado == EstadoToken.EXPIRADO
                        ? "Sesion expirada, vuelva a autenticarse"
                        : "Token invalido o sesion expirada"
            };

            contexto.Result = new ObjectResult(respuesta) { StatusCode = StatusCodes.Status401Unauthorized };
        }
    }
}
