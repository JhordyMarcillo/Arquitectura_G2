package ec.edu.monster.controladores.manejadores;

import ec.edu.monster.modelos.utilidades.FabricaRespuesta;
import ec.edu.monster.modelos.utilidades.enums.CodigoRespuesta;
import ec.edu.monster.modelos.utilidades.enums.EstadoToken;
import ec.edu.monster.seguridad.AdministradorTokens;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

/** Exige "Authorization: Bearer token" en todas las operaciones salvo las @PermitirAnonimo. */
@Provider
public class ManejadorAutenticacion implements ContainerRequestFilter {

    public static final String CLAVE_SESION = "SesionEmpleado";
    private static final String PREFIJO = "Bearer ";

    @Context
    private ResourceInfo recurso;

    @Override
    public void filter(ContainerRequestContext peticion) {
        if (HttpMethod.OPTIONS.equals(peticion.getMethod()) || recurso.getResourceMethod() == null
                || recurso.getResourceMethod().isAnnotationPresent(PermitirAnonimo.class)) {
            return;
        }

        String cabecera = peticion.getHeaderString(HttpHeaders.AUTHORIZATION);
        String token = cabecera != null && cabecera.regionMatches(true, 0, PREFIJO, 0, PREFIJO.length())
                ? cabecera.substring(PREFIJO.length()).trim()
                : null;

        EstadoToken estado = AdministradorTokens.validar(token);
        if (estado == EstadoToken.VALIDO) {
            peticion.setProperty(CLAVE_SESION, AdministradorTokens.obtenerSesion(token));
            return;
        }

        CodigoRespuesta codigo = estado == EstadoToken.EXPIRADO ? CodigoRespuesta.SESION_EXPIRADA : CodigoRespuesta.TOKEN_INVALIDO;
        String mensaje = estado == EstadoToken.EXPIRADO ? "La sesion expiro, inicie sesion nuevamente" : "Token invalido o ausente";
        peticion.abortWith(Response.status(codigo.getEstadoHttp())
                .type(MediaType.APPLICATION_JSON)
                .entity(FabricaRespuesta.error(codigo, mensaje))
                .build());
    }
}
