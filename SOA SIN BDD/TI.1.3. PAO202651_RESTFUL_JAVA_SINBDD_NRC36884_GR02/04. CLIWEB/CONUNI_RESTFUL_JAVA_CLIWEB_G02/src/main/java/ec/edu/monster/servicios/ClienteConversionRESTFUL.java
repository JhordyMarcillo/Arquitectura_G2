package ec.edu.monster.servicios;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ec.edu.monster.modelo.Conversion;
import ec.edu.monster.modelo.ConversionRequest;
import ec.edu.monster.modelo.ConversionResponse;
import ec.edu.monster.servicio.ConfiguracionCliente;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Cliente para consumir el servicio RESTFUL de conversiones
 * @author ACER NITRO V15
 */
public class ClienteConversionRESTFUL {
    
    private static final String BASE_URL = ConfiguracionCliente.getBaseUrl();
    private static final int TIMEOUT = 30000; // 30 segundos
    private final ObjectMapper objectMapper;
    
    public ClienteConversionRESTFUL() {
        this.objectMapper = new ObjectMapper();
        // Configurar para ignorar propiedades desconocidas (por si el servidor agrega campos nuevos)
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    /**
     * Realiza una llamada RESTFUL al servicio web
     * @param unidadOrigen - Unidad de origen
     * @param unidadDestino - Unidad de destino
     * @param valor - Valor a convertir
     * @return Conversion con el resultado
     */
    private Conversion llamadaRESTFUL(String categoria, String unidadOrigen, String unidadDestino, double valor) {
        Conversion resultado = new Conversion();
        resultado.setValorOriginal(valor);
        resultado.setTipoConversion(categoria);
        
        try {
            System.out.println("INFO:   === INICIANDO LLAMADA RESTFUL ===");
            System.out.println("INFO:   Categoría: " + categoria);
            System.out.println("INFO:   Unidad Origen: " + unidadOrigen);
            System.out.println("INFO:   Unidad Destino: " + unidadDestino);
            System.out.println("INFO:   Valor: " + valor);
            System.out.println("INFO:   URL: " + BASE_URL);
            
            ConversionRequest request = new ConversionRequest(valor, unidadOrigen, unidadDestino, categoria);
            String jsonRequest = objectMapper.writeValueAsString(request);
            System.out.println("INFO:   JSON Request: " + jsonRequest);
            
            ConversionResponse response = enviarPeticionRESTFUL(jsonRequest);
            
            if (response != null && response.isExito()) {
                resultado.setValorConvertido(response.getValorConvertido());
                resultado.setExitosa(true);
                resultado.setUnidadOriginal(unidadOrigen);
                resultado.setUnidadConvertida(unidadDestino);
                resultado.setMensajeError("");
                System.out.println("INFO:   Resultado exitoso: " + response.getValorConvertido());
            } else {
                resultado.setExitosa(false);
                resultado.setMensajeError(response != null ? response.getMensaje() : "Error desconocido");
                System.out.println("INFO:   Error: " + (response != null ? response.getMensaje() : "Error desconocido"));
            }
            
        } catch (Exception e) {
            System.out.println("SEVERE:   ERROR en llamada RESTFUL: " + e.getMessage());
            e.printStackTrace();
            resultado.setExitosa(false);
            resultado.setMensajeError("Error de conexión: " + e.getMessage());
        }
        
        return resultado;
    }
    
    /**
     * Envía la petición RESTFUL al servicio web
     * @param jsonRequest - JSON con la petición
     * @return ConversionResponse con la respuesta
     * @throws Exception si hay error
     */
    private ConversionResponse enviarPeticionRESTFUL(String jsonRequest) throws Exception {
        URL url = new URL(BASE_URL + "/conversion/convertir");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        // Configurar la conexión para POST
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        
        // Configurar headers
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Length", String.valueOf(jsonRequest.getBytes(StandardCharsets.UTF_8).length));
        
        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(TIMEOUT);
        
        System.out.println("INFO:   Enviando petición RESTFUL...");
        
        // Enviar el JSON
        try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8)) {
            writer.write(jsonRequest);
            writer.flush();
            System.out.println("INFO:   JSON enviado");
        }
        
        // Verificar código de respuesta
        int responseCode = connection.getResponseCode();
        System.out.println("INFO:   Código de respuesta HTTP: " + responseCode);
        
        if (responseCode != HttpURLConnection.HTTP_OK) {
            // Leer mensaje de error
            StringBuilder errorResponse = new StringBuilder();
            try (BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorResponse.append(line).append("\n");
                }
            }
            throw new Exception("Error HTTP " + responseCode + ": " + errorResponse.toString());
        }
        
        // Leer respuesta exitosa
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
        }
        
        // Parsear respuesta JSON
        String responseText = response.toString();
        System.out.println("INFO:   Respuesta recibida: " + responseText);
        
        return objectMapper.readValue(responseText, ConversionResponse.class);
    }
    
    // ========== MÉTODOS DE TEMPERATURA ==========
    
    public Conversion celsiusAFahrenheit(double valor) {
        return llamadaRESTFUL("temperatura", "celsius", "fahrenheit", valor);
    }
    
    public Conversion fahrenheitACelsius(double valor) {
        return llamadaRESTFUL("temperatura", "fahrenheit", "celsius", valor);
    }
    
    public Conversion celsiusAKelvin(double valor) {
        return llamadaRESTFUL("temperatura", "celsius", "kelvin", valor);
    }
    
    public Conversion kelvinACelsius(double valor) {
        return llamadaRESTFUL("temperatura", "kelvin", "celsius", valor);
    }
    
    public Conversion fahrenheitAKelvin(double valor) {
        return llamadaRESTFUL("temperatura", "fahrenheit", "kelvin", valor);
    }
    
    public Conversion kelvinAFahrenheit(double valor) {
        return llamadaRESTFUL("temperatura", "kelvin", "fahrenheit", valor);
    }
    
    // ========== MÉTODOS DE LONGITUD ==========
    
    public Conversion metrosAPies(double valor) {
        return llamadaRESTFUL("longitud", "metros", "pies", valor);
    }
    
    public Conversion piesAMetros(double valor) {
        return llamadaRESTFUL("longitud", "pies", "metros", valor);
    }
    
    public Conversion metrosAPulgadas(double valor) {
        return llamadaRESTFUL("longitud", "metros", "pulgadas", valor);
    }
    
    public Conversion pulgadasAMetros(double valor) {
        return llamadaRESTFUL("longitud", "pulgadas", "metros", valor);
    }
    
    public Conversion kilometrosAMillas(double valor) {
        return llamadaRESTFUL("longitud", "kilometros", "millas", valor);
    }
    
    public Conversion millasAKilometros(double valor) {
        return llamadaRESTFUL("longitud", "millas", "kilometros", valor);
    }
    
    // ========== MÉTODOS DE PESO ==========
    
    public Conversion kilogramosALibras(double valor) {
        return llamadaRESTFUL("peso", "kilogramos", "libras", valor);
    }
    
    public Conversion librasAKilogramos(double valor) {
        return llamadaRESTFUL("peso", "libras", "kilogramos", valor);
    }
    
    public Conversion gramosAOnzas(double valor) {
        return llamadaRESTFUL("peso", "gramos", "onzas", valor);
    }
    
    public Conversion onzasAGramos(double valor) {
        return llamadaRESTFUL("peso", "onzas", "gramos", valor);
    }
    
    
    // ========== MÉTODO DE AUTENTICACIÓN ==========
    
    public boolean login(String usuario, String contrasena) {
        try {
            System.out.println("INFO:   === INICIANDO LLAMADA RESTFUL LOGIN ===");
            String jsonRequest = "{\"usuario\":\"" + usuario + "\",\"contrasena\":\"" + contrasena + "\"}";
            
            URL url = new URL(BASE_URL + "/auth/login");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setRequestProperty("Accept", "application/json");
            
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);
            
            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8)) {
                writer.write(jsonRequest);
                writer.flush();
            }
            
            int responseCode = connection.getResponseCode();
            System.out.println("INFO:   Código de respuesta HTTP Login: " + responseCode);
            
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            System.out.println("SEVERE:   ERROR en llamada RESTFUL Login: " + e.getMessage());
            return false;
        }
    }
}