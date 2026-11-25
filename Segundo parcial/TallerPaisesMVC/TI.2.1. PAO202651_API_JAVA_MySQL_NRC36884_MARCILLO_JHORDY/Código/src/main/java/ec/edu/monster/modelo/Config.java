package ec.edu.monster.modelo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Lee la configuracion desde config.properties (carpeta de trabajo).
 * La API key tambien puede venir de la variable de entorno RESTCOUNTRIES_API_KEY.
 */
public class Config {

    private static final String ARCHIVO = "config.properties";
    private static final Properties props = cargar();

    private static Properties cargar() {
        Properties p = new Properties();
        // valores por defecto
        p.setProperty("api.url", "https://api.restcountries.com/countries/v5");
        p.setProperty("api.key", "");
        p.setProperty("db.host", "localhost");
        p.setProperty("db.port", "3306");
        p.setProperty("db.name", "api_paises");
        p.setProperty("db.user", "root");
        p.setProperty("db.pass", "12345");
        try (InputStream in = new FileInputStream(ARCHIVO)) {
            p.load(new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Aviso: no se pudo leer " + ARCHIVO
                    + ", se usan valores por defecto.");
        }
        return p;
    }

    public static String get(String clave) {
        return props.getProperty(clave, "").trim();
    }

    public static String getApiKey() {
        String env = System.getenv("RESTCOUNTRIES_API_KEY");
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        return get("api.key");
    }

    /** Guarda la API key en config.properties. */
    public static void setApiKey(String key) throws IOException {
        props.setProperty("api.key", key == null ? "" : key.trim());
        try (OutputStream out = new FileOutputStream(ARCHIVO)) {
            props.store(new java.io.OutputStreamWriter(out, java.nio.charset.StandardCharsets.UTF_8),
                    "Configuracion de la aplicacion (no subir a Git)");
        }
    }
}
