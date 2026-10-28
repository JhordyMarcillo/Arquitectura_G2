package ec.edu.monster.prueba;

import ec.edu.monster.controlador.ClienteController;

import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Clase principal para ejecutar el cliente RESTful de conversiones
 * @author ACER NITRO V15
 */
public class ClientePrincipal {
    
    private static final Logger logger = Logger.getLogger(ClientePrincipal.class.getName());
    
    /**
     * Configura el logging para suprimir los warnings de Jersey
     */
    private static void configurarLogging() {
        try {
            // Cargar el archivo logging.properties desde resources
            InputStream is = ClientePrincipal.class.getClassLoader()
                    .getResourceAsStream("logging.properties");
            
            if (is != null) {
                LogManager.getLogManager().readConfiguration(is);
                is.close();
            } else {
                // Si no se encuentra el archivo, configurar manualmente
                Logger.getLogger("org.glassfish.jersey").setLevel(java.util.logging.Level.SEVERE);
                Logger.getLogger("jakarta.activation").setLevel(java.util.logging.Level.SEVERE);
            }
        } catch (Exception e) {
            // Si falla la carga, configurar manualmente los loggers de Jersey
            Logger.getLogger("org.glassfish.jersey").setLevel(java.util.logging.Level.SEVERE);
            Logger.getLogger("org.glassfish.jersey.client").setLevel(java.util.logging.Level.SEVERE);
            Logger.getLogger("org.glassfish.jersey.message").setLevel(java.util.logging.Level.SEVERE);
            Logger.getLogger("jakarta.activation").setLevel(java.util.logging.Level.SEVERE);
        }
    }
    
    public static void main(String[] args) {
        // Configurar logging ANTES de crear cualquier instancia que use Jersey
        configurarLogging();
        
        try {
            ClienteController controller = new ClienteController();
            controller.ejecutar();
        } catch (Exception e) {
            System.err.println("Error fatal en la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


