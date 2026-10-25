using System.Collections.Concurrent;
using ec.edu.monster.modelos.utilidades.enums;

namespace ec.edu.monster.seguridad
{
    public static class AdministradorTokens
    {
        public const int MINUTOS_EXPIRACION = 30;

        private static readonly ConcurrentDictionary<string, DateTime> tokensActivos = new();

        public static string GenerarToken()
        {
            string token = Guid.NewGuid().ToString();
            tokensActivos[token] = DateTime.Now.AddMinutes(MINUTOS_EXPIRACION);
            return token;
        }

        public static DateTime? ObtenerExpiracion(string token)
        {
            return tokensActivos.TryGetValue(token, out DateTime expiracion) ? expiracion : null;
        }

        public static EstadoToken ValidarToken(string? token)
        {
            if (string.IsNullOrWhiteSpace(token) || !tokensActivos.TryGetValue(token, out DateTime expiracion))
            {
                return EstadoToken.INVALIDO;
            }
            if (DateTime.Now > expiracion)
            {
                tokensActivos.TryRemove(token, out _);
                return EstadoToken.EXPIRADO;
            }
            return EstadoToken.VALIDO;
        }
    }
}
