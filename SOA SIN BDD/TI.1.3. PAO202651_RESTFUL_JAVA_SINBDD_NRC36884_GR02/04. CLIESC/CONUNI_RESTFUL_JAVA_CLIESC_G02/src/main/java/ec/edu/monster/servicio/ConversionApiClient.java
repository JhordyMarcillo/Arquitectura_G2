package ec.edu.monster.servicio;

import ec.edu.monster.modelo.ConversionRequest;
import ec.edu.monster.modelo.ConversionResponse;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.InputStream;
import java.util.Properties;

/**
 * Cliente RESTful para consumir el servicio de conversiones.
 * Solo expone las tres categorías pedidas: temperatura, longitud y peso.
 */
public class ConversionApiClient {

    private static final String CONFIG_FILE = "config.properties";
    private static final String API_BASE_URL_KEY = "api.base.url";

    private final Client client;
    private final WebTarget baseTarget;
    private final String baseUrl;

    public ConversionApiClient() {
        this.baseUrl = cargarUrlDesdeConfiguracion();
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new RuntimeException("Error: La URL del servicio API no está configurada en config.properties");
        }
        this.client = ClientBuilder.newClient();
        this.baseTarget = client.target(baseUrl.trim()).path("/conversion");
    }

    /** URL base del servicio (prefijo /api) leída de config.properties. */
    public static String urlBase() {
        return cargarUrlDesdeConfiguracion();
    }

    private static String cargarUrlDesdeConfiguracion() {
        try {
            Properties props = new Properties();
            InputStream input = ConversionApiClient.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
            if (input == null) {
                System.err.println("Advertencia: No se encontró el archivo " + CONFIG_FILE);
                return null;
            }

            try (InputStream stream = input) {
                props.load(stream);
            }

            String url = props.getProperty(API_BASE_URL_KEY);
            return url == null ? null : url.trim();
        } catch (Exception e) {
            System.err.println("Error al cargar la configuración: " + e.getMessage());
            return null;
        }
    }

    public ConversionResponse convertirTemperatura(String operacion, double valor) {
        return ejecutarConversion(operacion, valor, "temperatura");
    }

    public ConversionResponse convertirLongitud(String operacion, double valor) {
        return ejecutarConversion(operacion, valor, "longitud");
    }

    public ConversionResponse convertirPeso(String operacion, double valor) {
        return ejecutarConversion(operacion, valor, "peso");
    }

    private ConversionResponse ejecutarConversion(String operacion, double valor, String categoria) {
        try {
            ConversionRequest request = crearRequest(operacion, valor, categoria);
            if (request == null) {
                ConversionResponse error = new ConversionResponse();
                error.setExito(false);
                error.setMensaje("Operación no válida");
                return error;
            }

            Response response = baseTarget.path("/convertir")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(request, MediaType.APPLICATION_JSON));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return response.readEntity(ConversionResponse.class);
            }

            ConversionResponse error = new ConversionResponse();
            error.setExito(false);
            error.setMensaje("Error del servidor: " + response.getStatus());
            return error;
        } catch (Exception e) {
            ConversionResponse error = new ConversionResponse();
            error.setExito(false);
            error.setMensaje("ERROR DE CONEXION REST\n"
                    + "El servidor no responde. Verifica que este encendido en:\n"
                    + "URL: " + baseUrl + "\n"
                    + "Detalle: " + e.getMessage());
            return error;
        }
    }

    private ConversionRequest crearRequest(String operacion, double valor, String categoria) {
        String unidadOrigen;
        String unidadDestino;

        switch (categoria) {
            case "temperatura":
                switch (operacion) {
                    case "celsiusAFahrenheit":
                        unidadOrigen = "celsius";
                        unidadDestino = "fahrenheit";
                        break;
                    case "fahrenheitACelsius":
                        unidadOrigen = "fahrenheit";
                        unidadDestino = "celsius";
                        break;
                    case "celsiusAKelvin":
                        unidadOrigen = "celsius";
                        unidadDestino = "kelvin";
                        break;
                    case "kelvinACelsius":
                        unidadOrigen = "kelvin";
                        unidadDestino = "celsius";
                        break;
                    case "fahrenheitAKelvin":
                        unidadOrigen = "fahrenheit";
                        unidadDestino = "kelvin";
                        break;
                    case "kelvinAFahrenheit":
                        unidadOrigen = "kelvin";
                        unidadDestino = "fahrenheit";
                        break;
                    default:
                        return null;
                }
                break;
            case "longitud":
                switch (operacion) {
                    case "metrosAPies":
                        unidadOrigen = "metros";
                        unidadDestino = "pies";
                        break;
                    case "piesAMetros":
                        unidadOrigen = "pies";
                        unidadDestino = "metros";
                        break;
                    case "metrosAPulgadas":
                        unidadOrigen = "metros";
                        unidadDestino = "pulgadas";
                        break;
                    case "pulgadasAMetros":
                        unidadOrigen = "pulgadas";
                        unidadDestino = "metros";
                        break;
                    case "kilometrosAMillas":
                        unidadOrigen = "kilometros";
                        unidadDestino = "millas";
                        break;
                    case "millasAKilometros":
                        unidadOrigen = "millas";
                        unidadDestino = "kilometros";
                        break;
                    default:
                        return null;
                }
                break;
            case "peso":
                switch (operacion) {
                    case "kilogramosALibras":
                        unidadOrigen = "kilogramos";
                        unidadDestino = "libras";
                        break;
                    case "librasAKilogramos":
                        unidadOrigen = "libras";
                        unidadDestino = "kilogramos";
                        break;
                    case "gramosAOnzas":
                        unidadOrigen = "gramos";
                        unidadDestino = "onzas";
                        break;
                    case "onzasAGramos":
                        unidadOrigen = "onzas";
                        unidadDestino = "gramos";
                        break;
                    default:
                        return null;
                }
                break;
            default:
                return null;
        }

        return new ConversionRequest(valor, unidadOrigen, unidadDestino, categoria);
    }

    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
