package ec.edu.monster.datos;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Datos de conexion a MySQL, en este orden: variables de entorno DB_URL, DB_USER y
 * DB_PASSWORD (las del .env de "03. BDD"), propiedades de sistema db.url, db.user y
 * db.password, y por ultimo db.properties dentro del WAR.
 */
public final class ConfiguracionBaseDatos {

    private static final Properties ARCHIVO = cargarArchivo();

    private ConfiguracionBaseDatos() {
    }

    public static String url() {
        return obtener("DB_URL", "db.url");
    }

    public static String usuario() {
        return obtener("DB_USER", "db.user");
    }

    public static String clave() {
        return obtener("DB_PASSWORD", "db.password");
    }

    private static String obtener(String variable, String propiedad) {
        String valor = System.getenv(variable);
        if (valor == null || valor.isBlank()) {
            valor = System.getProperty(propiedad);
        }
        if (valor == null || valor.isBlank()) {
            valor = ARCHIVO.getProperty(propiedad);
        }
        return valor;
    }

    private static Properties cargarArchivo() {
        Properties propiedades = new Properties();
        try (InputStream entrada = ConfiguracionBaseDatos.class.getResourceAsStream("/db.properties")) {
            if (entrada != null) {
                propiedades.load(entrada);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer db.properties", e);
        }
        return propiedades;
    }
}
