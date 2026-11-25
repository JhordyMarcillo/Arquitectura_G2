using ec.edu.monster.datos;
using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.seguridad;

namespace ec.edu.monster.servicios
{
    public class ServicioAutenticacion
    {
        private readonly RepositorioUsuario repositorioUsuario = new();

        public RespuestaAutenticacion Autenticar(SolicitudLogin solicitud)
        {
            if (string.IsNullOrWhiteSpace(solicitud.Usuario) || string.IsNullOrWhiteSpace(solicitud.Clave))
            {
                throw new ExcepcionNegocio(CodigosRespuesta.DATOS_INCONSISTENTES, "El usuario y la clave son obligatorios");
            }

            DatosEmpleado? empleado = repositorioUsuario.BuscarActivo(solicitud.Usuario.Trim(), CifradoClave.Sha1(solicitud.Clave));
            if (empleado is null)
            {
                throw new ExcepcionNegocio(CodigosRespuesta.CREDENCIALES_INCORRECTAS, "Usuario o clave incorrectos",
                        StatusCodes.Status401Unauthorized);
            }

            string token = AdministradorTokens.GenerarToken(empleado.Codigo, empleado.Nombre);
            RespuestaAutenticacion respuesta = Respuesta.Exito<RespuestaAutenticacion>("Sesion iniciada correctamente");
            respuesta.Token = token;
            respuesta.Expiracion = AdministradorTokens.ObtenerExpiracion(token).ToString("yyyy-MM-ddTHH:mm:ss");
            respuesta.CodigoEmpleado = empleado.Codigo;
            respuesta.NombreEmpleado = empleado.Nombre;
            return respuesta;
        }
    }
}
