package ec.edu.monster.servicio;

import java.io.InputStream;
import java.util.Properties;

public class ConfiguracionCliente {
    private static final String CONFIG_FILE = "config.properties";
    private static final String API_BASE_URL_KEY = "api.base.url";
    private static String BASE_URL = null;
    
    static {
        BASE_URL = cargarUrlDesdeConfiguracion();
        if (BASE_URL == null || BASE_URL.trim().isEmpty()) {
            throw new RuntimeException("Error: La URL del servicio API no está configurada en config.properties");
        }
    }
    
    public static String getBaseUrl() {
        return BASE_URL;
    }
    
    /**
     * Carga la URL base del servicio desde el archivo de configuración
     */
    private static String cargarUrlDesdeConfiguracion() {
        try {
            Properties props = new Properties();
            InputStream is = ConfiguracionCliente.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
            
            if (is == null) {
                System.err.println("Advertencia: No se encontró el archivo " + CONFIG_FILE);
                return null;
            }
            
            props.load(is);
            is.close();
            
            String url = props.getProperty(API_BASE_URL_KEY);
            if (url != null) {
                url = url.trim();
            }
            return url;
        } catch (Exception e) {
            System.err.println("Error al cargar la configuración: " + e.getMessage());
            return null;
        }
    }
    
    private ConfiguracionCliente() {}
}




