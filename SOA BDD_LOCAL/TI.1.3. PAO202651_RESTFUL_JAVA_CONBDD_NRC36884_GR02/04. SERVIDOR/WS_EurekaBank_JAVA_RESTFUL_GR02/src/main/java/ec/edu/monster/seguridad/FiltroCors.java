package ec.edu.monster.seguridad;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

/** Permite que los clientes web del grupo, en otras maquinas de la red, consuman el servicio. */
@Provider
public class FiltroCors implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext peticion, ContainerResponseContext respuesta) {
        respuesta.getHeaders().putSingle("Access-Control-Allow-Origin", "*");
        respuesta.getHeaders().putSingle("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        respuesta.getHeaders().putSingle("Access-Control-Allow-Headers",
                "Origin, Content-Type, Accept, Authorization, X-Requested-With");
        respuesta.getHeaders().putSingle("Access-Control-Max-Age", "3600");
    }
}
