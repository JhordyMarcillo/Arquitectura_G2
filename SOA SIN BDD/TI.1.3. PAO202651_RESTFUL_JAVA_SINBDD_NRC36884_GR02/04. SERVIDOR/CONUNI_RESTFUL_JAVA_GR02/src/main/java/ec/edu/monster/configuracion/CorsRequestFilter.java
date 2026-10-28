package ec.edu.monster.configuracion;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Filtro CORS para manejar peticiones OPTIONS (preflight)
 * Responde automáticamente a las peticiones preflight del navegador
 * @author ACER NITRO V15
 */
@Provider
public class CorsRequestFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Si es una petición OPTIONS (preflight), responder inmediatamente
        if ("OPTIONS".equals(requestContext.getMethod())) {
            Response response = Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Headers", 
                    "origin, content-type, accept, authorization, X-Requested-With")
                .header("Access-Control-Allow-Methods", 
                    "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .header("Access-Control-Max-Age", "1209600")
                .build();
            
            requestContext.abortWith(response);
        }
    }
}
