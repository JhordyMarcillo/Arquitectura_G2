using ec.edu.monster.modelos.utilidades.enums;
using System.Collections.Concurrent;
using System.Security.Cryptography;

namespace ec.edu.monster.seguridad
{
    public static class AdministradorTokens
    {
        public const int MINUTOS_EXPIRACION = 60;

        private static readonly ConcurrentDictionary<string, SesionEmpleado> SESIONES = new();

        public static string GenerarToken(string codigoEmpleado, string nombreEmpleado)
        {
            string token = Convert.ToHexString(RandomNumberGenerator.GetBytes(24)).ToLowerInvariant();
            SESIONES[token] = new SesionEmpleado(codigoEmpleado, nombreEmpleado,
                    DateTime.Now.AddMinutes(MINUTOS_EXPIRACION));
            return token;
        }

        public static EstadoToken Validar(string? token, out SesionEmpleado? sesion)
        {
            sesion = null;
            if (string.IsNullOrWhiteSpace(token) || !SESIONES.TryGetValue(token, out SesionEmpleado? encontrada))
            {
                return EstadoToken.INVALIDO;
            }
            if (encontrada.Expiracion < DateTime.Now)
            {
                SESIONES.TryRemove(token, out _);
                return EstadoToken.EXPIRADO;
            }
            sesion = encontrada;
            return EstadoToken.VALIDO;
        }

        public static DateTime ObtenerExpiracion(string token)
        {
            return SESIONES.TryGetValue(token, out SesionEmpleado? sesion) ? sesion.Expiracion : DateTime.MinValue;
        }
    }
}
