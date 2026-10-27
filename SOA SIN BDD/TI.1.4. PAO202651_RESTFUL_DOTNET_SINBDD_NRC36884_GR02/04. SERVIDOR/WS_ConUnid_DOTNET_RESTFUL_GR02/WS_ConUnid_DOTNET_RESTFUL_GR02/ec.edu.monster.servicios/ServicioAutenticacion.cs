using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.seguridad;

namespace ec.edu.monster.servicios
{
    public class ServicioAutenticacion
    {
        public RespuestaAutenticacion Autenticar(SolicitudLogin solicitud)
        {
            if (string.IsNullOrWhiteSpace(solicitud.Usuario) || string.IsNullOrWhiteSpace(solicitud.Contrasenia))
            {
                throw new ExcepcionNegocio(CodigosRespuesta.DATOS_INCONSISTENTES, "El usuario y la contrasenia son obligatorios");
            }
            if (!AdministradorCredenciales.ValidarCredenciales(solicitud.Usuario, solicitud.Contrasenia))
            {
                throw new ExcepcionNegocio(CodigosRespuesta.CREDENCIALES_INCORRECTAS, "Credenciales incorrectas",
                        StatusCodes.Status401Unauthorized);
            }

            string token = AdministradorTokens.GenerarToken();

            return new RespuestaAutenticacion
            {
                Estado = CodigosRespuesta.ESTADO_OK,
                Codigo = CodigosRespuesta.EXITO,
                Mensaje = "Autenticacion realizada correctamente",
                Token = token,
                Expiracion = AdministradorTokens.ObtenerExpiracion(token)
            };
        }
    }
}
