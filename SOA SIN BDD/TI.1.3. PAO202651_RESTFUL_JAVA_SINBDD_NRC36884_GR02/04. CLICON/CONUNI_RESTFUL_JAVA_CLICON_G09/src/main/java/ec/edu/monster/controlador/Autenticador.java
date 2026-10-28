package ec.edu.monster.controlador;

import ec.edu.monster.vista.VistaLogin;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.InputStream;
import java.util.Properties;

/**
 * Clase para manejar la autenticación del sistema RESTful.
 * <p>
 * Las credenciales NO se almacenan en el cliente. La validación
 * se delega al servidor a través del endpoint {@code POST /api/auth/login}.
 * </p>
 *
 * @author GR09 - Arquitectura de Software
 */
public class Autenticador {

    private static final int    MAX_INTENTOS    = 3;
    private static final String CONFIG_FILE     = "config.properties";
    private static final String API_BASE_URL_KEY = "api.base.url";

    private final VistaLogin vistaLogin;
    private final Client     httpClient;
    private final WebTarget  authTarget;

    public Autenticador() {
        this.vistaLogin = new VistaLogin();
        this.httpClient = ClientBuilder.newClient();
        String baseUrl  = cargarUrlDesdeConfiguracion();
        // El endpoint de login está en /api/auth/login
        this.authTarget = httpClient.target(baseUrl).path("/auth/login");
    }

    /**
     * Realiza el proceso de autenticación contra el servidor REST.
     *
     * @return {@code true} si la autenticación es exitosa
     */
    public boolean autenticar() {
        vistaLogin.mostrarBannerLogin();

        for (int intento = 1; intento <= MAX_INTENTOS; intento++) {
            vistaLogin.mostrarIntento(intento, MAX_INTENTOS);

            String usuario    = vistaLogin.leerUsuario();
            String contrasena = vistaLogin.leerContrasenaOculta();

            if (validarCredencialesEnServidor(usuario, contrasena)) {
                vistaLogin.mostrarMensajeExito();
                return true;
            } else {
                vistaLogin.mostrarMensajeError(intento, MAX_INTENTOS);
            }
        }

        vistaLogin.mostrarMensajeBloqueo();
        return false;
    }

    /**
     * Envía las credenciales al servidor REST para que las valide.
     * El cliente NUNCA conoce las credenciales correctas.
     */
    private boolean validarCredencialesEnServidor(String usuario, String contrasena) {
        try {
            // Construir body JSON de forma simple (sin dependencia extra)
            String body = "{\"usuario\":\"" + usuario + "\",\"contrasena\":\"" + contrasena + "\"}";

            Response response = authTarget
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(body, MediaType.APPLICATION_JSON));

            return response.getStatus() == 200;
        } catch (Exception e) {
            System.err.println("Error al conectar con el servidor para autenticar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Carga la URL base desde el archivo de configuración.
     */
    private String cargarUrlDesdeConfiguracion() {
        try {
            Properties props = new Properties();
            InputStream is = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);
            if (is != null) {
                props.load(is);
                is.close();
                String url = props.getProperty(API_BASE_URL_KEY);
                if (url != null && !url.trim().isEmpty()) {
                    return url.trim();
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar configuración: " + e.getMessage());
        }
        // URL por defecto si no hay configuración
        return "http://localhost:8080/CONUNI_RESTFUL_JAVA_GR02/api";
    }

    /**
     * Cierra el autenticador y libera recursos.
     */
    public void cerrar() {
        if (vistaLogin != null) {
            vistaLogin.cerrar();
        }
        if (httpClient != null) {
            httpClient.close();
        }
    }
}
