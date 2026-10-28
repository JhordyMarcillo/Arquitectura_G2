package ec.edu.monster.configuracion;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Configuración simple de la aplicación RESTful
 * @author ACER NITRO V15
 */
@ApplicationPath("/api")
public class ApplicationConfig extends Application {
    
    // Esta clase configura automáticamente JAX-RS
    // El path "/api" será el prefijo para todos los endpoints
    // Ejemplo: http://localhost:8080/CONUNI_RESTFUL_JAVA_GR09/api/conversion/...
    
}