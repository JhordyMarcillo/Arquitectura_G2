package ec.edu.monster.servicios;

import ec.edu.monster.datos.RepositorioUsuario;
import ec.edu.monster.modelos.utilidades.DatosEmpleado;
import ec.edu.monster.modelos.utilidades.ExcepcionNegocio;
import ec.edu.monster.modelos.utilidades.FabricaRespuesta;
import ec.edu.monster.modelos.utilidades.RespuestaAutenticacion;
import ec.edu.monster.modelos.utilidades.SolicitudLogin;
import ec.edu.monster.modelos.utilidades.enums.CodigoRespuesta;
import ec.edu.monster.seguridad.AdministradorTokens;
import ec.edu.monster.seguridad.CifradoClave;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class ServicioAutenticacion {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final RepositorioUsuario repositorioUsuario = new RepositorioUsuario();

    public RespuestaAutenticacion autenticar(SolicitudLogin solicitud) throws SQLException {
        if (solicitud == null || vacio(solicitud.getUsuario()) || vacio(solicitud.getClave())) {
            throw new ExcepcionNegocio(CodigoRespuesta.DATOS_INCONSISTENTES, "El usuario y la clave son obligatorios");
        }

        DatosEmpleado empleado = repositorioUsuario.buscarActivo(solicitud.getUsuario().trim(),
                CifradoClave.sha1(solicitud.getClave()));
        if (empleado == null) {
            throw new ExcepcionNegocio(CodigoRespuesta.CREDENCIALES_INCORRECTAS, "Usuario o clave incorrectos");
        }

        String token = AdministradorTokens.generarToken(empleado.codigo(), empleado.nombre());
        RespuestaAutenticacion respuesta = FabricaRespuesta.exito(RespuestaAutenticacion::new, "Sesion iniciada correctamente");
        respuesta.setToken(token);
        respuesta.setExpiracion(AdministradorTokens.obtenerSesion(token).expiracion().format(FORMATO));
        respuesta.setCodigoEmpleado(empleado.codigo());
        respuesta.setNombreEmpleado(empleado.nombre());
        return respuesta;
    }

    private static boolean vacio(String valor) {
        return valor == null || valor.isBlank();
    }
}
