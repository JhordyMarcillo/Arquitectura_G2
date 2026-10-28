package ec.edu.monster.prueba;

import ec.edu.monster.modelo.ConversionRequest;
import ec.edu.monster.modelo.ConversionResponse;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

/**
 * Cliente de prueba para el servicio RESTful de conversiones
 * Prueba todos los endpoints del servicio RESTful
 * @author ACER NITRO V15
 */
public class PruebaClienteREST {
    
    private static final String BASE_URL = "http://localhost:8080/CONUNI_RESTFUL_JAVA_GR09/api/conversion";
    private Client client;
    private WebTarget target;
    
    public PruebaClienteREST() {
        this.client = ClientBuilder.newClient();
        this.target = client.target(BASE_URL);
    }
    
    /**
     * Método principal para ejecutar todas las pruebas
     */
    public static void main(String[] args) {
        System.out.println("=== MONSTERS INC. CONVERTER - PRUEBAS RESTFUL ===");
        System.out.println("Iniciando pruebas del servicio RESTful...\n");
        
        PruebaClienteREST tester = new PruebaClienteREST();
        
        try {
            // Probar información del servicio
            tester.probarInfoServicio();
            
            // Probar conversiones de temperatura
            tester.probarConversionesTemperatura();
            
            // Probar conversiones de longitud
            tester.probarConversionesLongitud();
            
            // Probar conversiones de peso
            tester.probarConversionesPeso();

            // Probar endpoint genérico
            tester.probarEndpointGenerico();
            
            System.out.println("\n=== TODAS LAS PRUEBAS COMPLETADAS ===");
            
        } catch (Exception e) {
            System.err.println("Error durante las pruebas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            tester.cerrarCliente();
        }
    }
    
    /**
     * Prueba el endpoint de información del servicio
     */
    public void probarInfoServicio() {
        System.out.println("🌡️ Probando información del servicio...");
        
        try {
            Response response = target.path("/info")
                    .request()
                    .get();
            
            if (response.getStatus() == 200) {
                String info = response.readEntity(String.class);
                System.out.println("✅ Info del servicio: " + info);
            } else {
                System.out.println("❌ Error al obtener info: " + response.getStatus());
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Prueba todas las conversiones de temperatura
     */
    public void probarConversionesTemperatura() {
        System.out.println("🌡️ Probando conversiones de temperatura...");
        
        // Celsius a Fahrenheit
        probarEndpoint("temperatura/celsius-to-fahrenheit", "celsius", 100.0);
        
        // Fahrenheit a Celsius
        probarEndpoint("temperatura/fahrenheit-to-celsius", "fahrenheit", 212.0);
        
        // Celsius a Kelvin
        probarEndpoint("temperatura/celsius-to-kelvin", "celsius", 0.0);
        
        // Kelvin a Celsius
        probarEndpoint("temperatura/kelvin-to-celsius", "kelvin", 273.15);
        
        // Fahrenheit a Kelvin
        probarEndpoint("temperatura/fahrenheit-to-kelvin", "fahrenheit", 32.0);
        
        // Kelvin a Fahrenheit
        probarEndpoint("temperatura/kelvin-to-fahrenheit", "kelvin", 273.15);
        
        System.out.println();
    }
    
    /**
     * Prueba todas las conversiones de longitud
     */
    public void probarConversionesLongitud() {
        System.out.println("📏 Probando conversiones de longitud...");
        
        // Metros a pies
        probarEndpoint("longitud/metros-to-pies", "metros", 1.0);
        
        // Pies a metros
        probarEndpoint("longitud/pies-to-metros", "pies", 3.28084);
        
        // Metros a pulgadas
        probarEndpoint("longitud/metros-to-pulgadas", "metros", 1.0);
        
        // Pulgadas a metros
        probarEndpoint("longitud/pulgadas-to-metros", "pulgadas", 39.3701);
        
        // Kilómetros a millas
        probarEndpoint("longitud/kilometros-to-millas", "kilometros", 1.0);
        
        // Millas a kilómetros
        probarEndpoint("longitud/millas-to-kilometros", "millas", 0.621371);
        
        System.out.println();
    }
    
    /**
     * Prueba todas las conversiones de peso
     */
    public void probarConversionesPeso() {
        System.out.println("⚖️ Probando conversiones de peso...");
        
        // Kilogramos a libras
        probarEndpoint("peso/kilogramos-to-libras", "kilogramos", 1.0);
        
        // Libras a kilogramos
        probarEndpoint("peso/libras-to-kilogramos", "libras", 2.20462);
        
        // Gramos a onzas
        probarEndpoint("peso/gramos-to-onzas", "gramos", 28.3495);
        
        // Onzas a gramos
        probarEndpoint("peso/onzas-to-gramos", "onzas", 1.0);
        
        System.out.println();
    }

    /**
     * Prueba el endpoint genérico de conversión
     */
    public void probarEndpointGenerico() {
        System.out.println("🔄 Probando endpoint genérico...");
        
        try {
            ConversionRequest request = new ConversionRequest();
            request.setValor(100.0);
            request.setUnidadOrigen("celsius");
            request.setUnidadDestino("fahrenheit");
            request.setCategoria("temperatura");
            
            Response response = target.path("/convertir")
                    .request()
                    .post(jakarta.ws.rs.client.Entity.json(request));
            
            if (response.getStatus() == 200) {
                ConversionResponse conversionResponse = response.readEntity(ConversionResponse.class);
                System.out.println("✅ Conversión genérica exitosa:");
                System.out.println("   Valor original: " + conversionResponse.getValorOriginal());
                System.out.println("   Valor convertido: " + conversionResponse.getValorConvertido());
                System.out.println("   Mensaje: " + conversionResponse.getMensaje());
            } else {
                System.out.println("❌ Error en conversión genérica: " + response.getStatus());
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Método auxiliar para probar un endpoint específico
     */
    private void probarEndpoint(String path, String paramName, double valor) {
        try {
            Response response = target.path(path)
                    .queryParam(paramName, valor)
                    .request()
                    .get();
            
            if (response.getStatus() == 200) {
                ConversionResponse conversionResponse = response.readEntity(ConversionResponse.class);
                System.out.printf("✅ %s: %.2f → %.4f%n", 
                    path, conversionResponse.getValorOriginal(), conversionResponse.getValorConvertido());
            } else {
                System.out.println("❌ Error en " + path + ": " + response.getStatus());
            }
        } catch (Exception e) {
            System.out.println("❌ Error en " + path + ": " + e.getMessage());
        }
    }
    
    /**
     * Cierra el cliente HTTP
     */
    public void cerrarCliente() {
        if (client != null) {
            client.close();
        }
    }
}
