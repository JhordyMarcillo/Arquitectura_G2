using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.modelos.utilidades.enums;
using ec.edu.monster.seguridad;
using Microsoft.Data.SqlClient;

namespace ec.edu.monster.controladores.manejadoresws
{
    /// <summary>
    /// Punto unico de cada operacion SOAP: valida el token (salvo en el login) y convierte
    /// cualquier error en una respuesta con estado ERROR y su codigo del contrato.
    /// </summary>
    public static class ManejadorSoap
    {
        public static T Ejecutar<T>(string? token, Func<SesionEmpleado?, T> operacion, bool exigeToken = true)
                where T : Respuesta, new()
        {
            try
            {
                SesionEmpleado? sesion = null;
                if (exigeToken)
                {
                    EstadoToken estado = AdministradorTokens.Validar(token, out sesion);
                    if (estado == EstadoToken.EXPIRADO)
                    {
                        return Respuesta.Error<T>(CodigosRespuesta.SESION_EXPIRADA, "La sesion expiro, inicie sesion nuevamente");
                    }
                    if (estado != EstadoToken.VALIDO)
                    {
                        return Respuesta.Error<T>(CodigosRespuesta.TOKEN_INVALIDO, "Token invalido o ausente");
                    }
                }
                return operacion(sesion);
            }
            catch (ExcepcionNegocio e)
            {
                return Respuesta.Error<T>(e.Codigo, e.Message);
            }
            catch (SqlException)
            {
                return Respuesta.Error<T>(CodigosRespuesta.ERROR_SERVIDOR, "No se pudo acceder a la base de datos");
            }
            catch (Exception)
            {
                return Respuesta.Error<T>(CodigosRespuesta.ERROR_SERVIDOR, "Error interno del servidor");
            }
        }
    }
}
