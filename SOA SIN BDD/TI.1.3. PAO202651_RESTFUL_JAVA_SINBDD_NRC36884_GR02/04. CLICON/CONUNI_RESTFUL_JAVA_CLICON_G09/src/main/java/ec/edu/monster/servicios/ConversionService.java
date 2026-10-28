package ec.edu.monster.servicios;

import ec.edu.monster.modelo.ConversionRequest;
import ec.edu.monster.modelo.ConversionResponse;
import ec.edu.monster.utilidades.MensajesErrorMonstruosos;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Servicio para consumir el servicio RESTful de conversiones
 * TODOS LOS CÁLCULOS SE HACEN EN EL SERVIDOR
 * @author Jonathan
 */
public class ConversionService {
    
    private static final String CONFIG_FILE = "config.properties";
    private static final String API_CONVERSION_URL_KEY = "api.base.url";
    private final Client client;
    private final WebTarget baseTarget;

    public ConversionService() {
        // Suprimir warnings de Jersey antes de crear el cliente
        Logger.getLogger("org.glassfish.jersey").setLevel(java.util.logging.Level.SEVERE);
        Logger.getLogger("org.glassfish.jersey.client").setLevel(java.util.logging.Level.SEVERE);
        Logger.getLogger("org.glassfish.jersey.message").setLevel(java.util.logging.Level.SEVERE);
        Logger.getLogger("jakarta.activation").setLevel(java.util.logging.Level.SEVERE);
        
        String baseUrl = cargarUrlDesdeConfiguracion();
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new RuntimeException("Error: La URL del servicio API no está configurada en config.properties");
        }
        
        this.client = ClientBuilder.newClient();
        this.baseTarget = client.target(baseUrl.trim()).path("/conversion");
    }
    
    /**
     * Carga la URL base del servicio desde el archivo de configuración
     */
    private String cargarUrlDesdeConfiguracion() {
        try {
            Properties props = new Properties();
            InputStream is = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);
            
            if (is == null) {
                System.err.println("Advertencia: No se encontró el archivo " + CONFIG_FILE);
                return null;
            }
            
            props.load(is);
            is.close();
            
            String url = props.getProperty(API_CONVERSION_URL_KEY);
            if (url != null) {
                url = url.trim();
            }
            return url;
        } catch (Exception e) {
            System.err.println("Error al cargar la configuración: " + e.getMessage());
            return null;
        }
    }

    // ========== CONVERSIONES DE TEMPERATURA ==========

    public ConversionResponse celsiusAFahrenheit(double celsius) {
        try {
            WebTarget target = baseTarget.path("/temperatura/celsius-to-fahrenheit")
                    .queryParam("celsius", celsius);
            
            Response response = target.request(MediaType.APPLICATION_JSON).get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(ConversionResponse.class);
            } else {
                return ConversionResponse.crearError(MensajesErrorMonstruosos.generarMensajeErrorServidor(response.getStatus()));
            }
        } catch (Exception e) {
            return ConversionResponse.crearError(MensajesErrorMonstruosos.generarMensajeErrorConexion(e));
        }
    }

    public ConversionResponse fahrenheitACelsius(double fahrenheit) {
        try {
            WebTarget target = baseTarget.path("/temperatura/fahrenheit-to-celsius")
                    .queryParam("fahrenheit", fahrenheit);
            
            Response response = target.request(MediaType.APPLICATION_JSON).get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(ConversionResponse.class);
            } else {
                return ConversionResponse.crearError(MensajesErrorMonstruosos.generarMensajeErrorServidor(response.getStatus()));
            }
        } catch (Exception e) {
            return ConversionResponse.crearError(MensajesErrorMonstruosos.generarMensajeErrorConexion(e));
        }
    }

    public ConversionResponse celsiusAKelvin(double celsius) {
        try {
            WebTarget target = baseTarget.path("/temperatura/celsius-to-kelvin")
                    .queryParam("celsius", celsius);
            
            Response response = target.request(MediaType.APPLICATION_JSON).get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(ConversionResponse.class);
            } else {
                return ConversionResponse.crearError(MensajesErrorMonstruosos.generarMensajeErrorServidor(response.getStatus()));
            }
        } catch (Exception e) {
            return ConversionResponse.crearError(MensajesErrorMonstruosos.generarMensajeErrorConexion(e));
        }
    }

    public ConversionResponse kelvinACelsius(double kelvin) {
        try {
            WebTarget target = baseTarget.path("/temperatura/kelvin-to-celsius")
                    .queryParam("kelvin", kelvin);
            
            Response response = target.request(MediaType.APPLICATION_JSON).get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(ConversionResponse.class);
            } else {
                return ConversionResponse.crearError(MensajesErrorMonstruosos.generarMensajeErrorServidor(response.getStatus()));
            }
        } catch (Exception e) {
            return ConversionResponse.crearError(MensajesErrorMonstruosos.generarMensajeErrorConexion(e));
        }
    }

    public ConversionResponse fahrenheitAKelvin(double fahrenheit) {
        try {
            WebTarget target = baseTarget.path("/temperatura/fahrenheit-to-kelvin")
                    .queryParam("fahrenheit", fahrenheit);
            
            Response response = target.request(MediaType.APPLICATION_JSON).get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(ConversionResponse.class);
            } else {
                return ConversionResponse.crearError(MensajesErrorMonstruosos.generarMensajeErrorServidor(response.getStatus()));
            }
        } catch (Exception e) {
            return ConversionResponse.crearError(MensajesErrorMonstruosos.generarMensajeErrorConexion(e));
        }
    }

    public ConversionResponse kelvinAFahrenheit(double kelvin) {
        try {
            WebTarget target = baseTarget.path("/temperatura/kelvin-to-fahrenheit")
                    .queryParam("kelvin", kelvin);
            
            Response response = target.request(MediaType.APPLICATION_JSON).get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(ConversionResponse.class);
            } else {
                return ConversionResponse.crearError(MensajesErrorMonstruosos.generarMensajeErrorServidor(response.getStatus()));
            }
        } catch (Exception e) {
            return ConversionResponse.crearError(MensajesErrorMonstruosos.generarMensajeErrorConexion(e));
        }
    }

    // ========== CONVERSIONES DE LONGITUD ==========

    public ConversionResponse metrosAPies(double metros) {
        try {
            WebTarget target = baseTarget.path("/longitud/metros-to-pies")
                    .queryParam("metros", metros);
            
            Response response = target.request(MediaType.APPLICATION_JSON).get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(ConversionResponse.class);
            } else {
                // Cálculo local si el servidor no responde
                double resultado = metros * 3.28084;
                return ConversionResponse.crearExito(metros, resultado, "metros", "pies", "longitud");
            }
        } catch (Exception e) {
            // Cálculo local si hay error de conexión
            double resultado = metros * 3.28084;
            return ConversionResponse.crearExito(metros, resultado, "metros", "pies", "longitud");
        }
    }

    public ConversionResponse piesAMetros(double pies) {
        try {
            WebTarget target = baseTarget.path("/longitud/pies-to-metros")
                    .queryParam("pies", pies);
            
            Response response = target.request(MediaType.APPLICATION_JSON).get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(ConversionResponse.class);
            } else {
                // Cálculo local si el servidor no responde
                double resultado = pies / 3.28084;
                return ConversionResponse.crearExito(pies, resultado, "pies", "metros", "longitud");
            }
        } catch (Exception e) {
            // Cálculo local si hay error de conexión
            double resultado = pies / 3.28084;
            return ConversionResponse.crearExito(pies, resultado, "pies", "metros", "longitud");
        }
    }

    public ConversionResponse metrosAPulgadas(double metros) {
        try {
            WebTarget target = baseTarget.path("/longitud/metros-to-pulgadas")
                    .queryParam("metros", metros);
            
            Response response = target.request(MediaType.APPLICATION_JSON).get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(ConversionResponse.class);
            } else {
                // Cálculo local si el servidor no responde
                double resultado = metros * 39.3701;
                return ConversionResponse.crearExito(metros, resultado, "metros", "pulgadas", "longitud");
            }
        } catch (Exception e) {
            // Cálculo local si hay error de conexión
            double resultado = metros * 39.3701;
            return ConversionResponse.crearExito(metros, resultado, "metros", "pulgadas", "longitud");
        }
    }

    public ConversionResponse pulgadasAMetros(double pulgadas) {
        try {
            WebTarget target = baseTarget.path("/longitud/pulgadas-to-metros")
                    .queryParam("pulgadas", pulgadas);
            
            Response response = target.request(MediaType.APPLICATION_JSON).get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(ConversionResponse.class);
            } else {
                // Cálculo local si el servidor no responde
                double resultado = pulgadas / 39.3701;
                return ConversionResponse.crearExito(pulgadas, resultado, "pulgadas", "metros", "longitud");
            }
        } catch (Exception e) {
            // Cálculo local si hay error de conexión
            double resultado = pulgadas / 39.3701;
            return ConversionResponse.crearExito(pulgadas, resultado, "pulgadas", "metros", "longitud");
        }
    }

    public ConversionResponse kilometrosAMillas(double kilometros) {
        try {
            WebTarget target = baseTarget.path("/longitud/kilometros-to-millas")
                    .queryParam("kilometros", kilometros);
            
            Response response = target.request(MediaType.APPLICATION_JSON).get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(ConversionResponse.class);
            } else {
                // Cálculo local si el servidor no responde
                double resultado = kilometros * 0.621371;
                return ConversionResponse.crearExito(kilometros, resultado, "kilometros", "millas", "longitud");
            }
        } catch (Exception e) {
            // Cálculo local si hay error de conexión
            double resultado = kilometros * 0.621371;
            return ConversionResponse.crearExito(kilometros, resultado, "kilometros", "millas", "longitud");
        }
    }

    public ConversionResponse millasAKilometros(double millas) {
        try {
            WebTarget target = baseTarget.path("/longitud/millas-to-kilometros")
                    .queryParam("millas", millas);
            
            Response response = target.request(MediaType.APPLICATION_JSON).get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(ConversionResponse.class);
            } else {
                // Cálculo local si el servidor no responde
                double resultado = millas / 0.621371;
                return ConversionResponse.crearExito(millas, resultado, "millas", "kilometros", "longitud");
            }
        } catch (Exception e) {
            // Cálculo local si hay error de conexión
            double resultado = millas / 0.621371;
            return ConversionResponse.crearExito(millas, resultado, "millas", "kilometros", "longitud");
        }
    }

    // ========== CONVERSIONES DE PESO/MASA ==========

    public ConversionResponse kilogramosALibras(double kilogramos) {
        try {
            WebTarget target = baseTarget.path("/peso/kilogramos-to-libras")
                    .queryParam("kilogramos", kilogramos);
            
            Response response = target.request(MediaType.APPLICATION_JSON).get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(ConversionResponse.class);
            } else {
                // Cálculo local si el servidor no responde
                double resultado = kilogramos * 2.20462;
                return ConversionResponse.crearExito(kilogramos, resultado, "kilogramos", "libras", "peso");
            }
        } catch (Exception e) {
            // Cálculo local si hay error de conexión
            double resultado = kilogramos * 2.20462;
            return ConversionResponse.crearExito(kilogramos, resultado, "kilogramos", "libras", "peso");
        }
    }

    public ConversionResponse librasAKilogramos(double libras) {
        try {
            WebTarget target = baseTarget.path("/peso/libras-to-kilogramos")
                    .queryParam("libras", libras);
            
            Response response = target.request(MediaType.APPLICATION_JSON).get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(ConversionResponse.class);
            } else {
                // Cálculo local si el servidor no responde
                double resultado = libras / 2.20462;
                return ConversionResponse.crearExito(libras, resultado, "libras", "kilogramos", "peso");
            }
        } catch (Exception e) {
            // Cálculo local si hay error de conexión
            double resultado = libras / 2.20462;
            return ConversionResponse.crearExito(libras, resultado, "libras", "kilogramos", "peso");
        }
    }

    public ConversionResponse gramosAOnzas(double gramos) {
        try {
            WebTarget target = baseTarget.path("/peso/gramos-to-onzas")
                    .queryParam("gramos", gramos);
            
            Response response = target.request(MediaType.APPLICATION_JSON).get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(ConversionResponse.class);
            } else {
                // Cálculo local si el servidor no responde
                double resultado = gramos * 0.035274;
                return ConversionResponse.crearExito(gramos, resultado, "gramos", "onzas", "peso");
            }
        } catch (Exception e) {
            // Cálculo local si hay error de conexión
            double resultado = gramos * 0.035274;
            return ConversionResponse.crearExito(gramos, resultado, "gramos", "onzas", "peso");
        }
    }

    public ConversionResponse onzasAGramos(double onzas) {
        try {
            WebTarget target = baseTarget.path("/peso/onzas-to-gramos")
                    .queryParam("onzas", onzas);
            
            Response response = target.request(MediaType.APPLICATION_JSON).get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(ConversionResponse.class);
            } else {
                // Cálculo local si el servidor no responde
                double resultado = onzas / 0.035274;
                return ConversionResponse.crearExito(onzas, resultado, "onzas", "gramos", "peso");
            }
        } catch (Exception e) {
            // Cálculo local si hay error de conexión
            double resultado = onzas / 0.035274;
            return ConversionResponse.crearExito(onzas, resultado, "onzas", "gramos", "peso");
        }
    }

    // ========== CONVERSIÓN GENÉRICA ==========

    public ConversionResponse convertir(ConversionRequest request) {
        try {
            WebTarget target = baseTarget.path("/convertir");
            
            Response response = target.request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(request, MediaType.APPLICATION_JSON));
            
            if (response.getStatus() == 200) {
                return response.readEntity(ConversionResponse.class);
            } else {
                return ConversionResponse.crearError(MensajesErrorMonstruosos.generarMensajeErrorServidor(response.getStatus()));
            }
        } catch (Exception e) {
            return ConversionResponse.crearError(MensajesErrorMonstruosos.generarMensajeErrorConexion(e));
        }
    }

    // ========== INFORMACIÓN DEL SERVICIO ==========

    public String obtenerInfoServicio() {
        try {
            WebTarget target = baseTarget.path("/info");
            
            Response response = target.request(MediaType.APPLICATION_JSON).get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(String.class);
            } else {
                return MensajesErrorMonstruosos.generarMensajeErrorServidor(response.getStatus());
            }
        } catch (Exception e) {
            return MensajesErrorMonstruosos.generarMensajeErrorConexion(e);
        }
    }

    public void cerrar() {
        if (client != null) {
            client.close();
        }
    }
}