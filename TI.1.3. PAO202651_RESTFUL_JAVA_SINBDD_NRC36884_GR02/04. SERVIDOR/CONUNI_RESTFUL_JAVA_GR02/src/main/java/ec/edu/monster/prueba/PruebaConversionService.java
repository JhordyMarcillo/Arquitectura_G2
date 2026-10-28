package ec.edu.monster.prueba;

import ec.edu.monster.servicios.ConversionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Pruebas unitarias para el servicio de conversión
 * Prueba la lógica de negocio directamente sin HTTP
 * @author ACER NITRO V15
 */
public class PruebaConversionService {
    
    private ConversionService conversionService;
    
    public PruebaConversionService() {
        this.conversionService = new ConversionService();
    }
    
    /**
     * Método principal para ejecutar todas las pruebas unitarias
     */
    public static void main(String[] args) {
        System.out.println("=== MONSTERS INC. CONVERTER - PRUEBAS UNITARIAS ===");
        System.out.println("Iniciando pruebas del servicio de conversión...\n");
        
        PruebaConversionService tester = new PruebaConversionService();
        
        try {
            // Probar conversiones de temperatura
            tester.probarConversionesTemperatura();
            
            // Probar conversiones de longitud
            tester.probarConversionesLongitud();
            
            // Probar conversiones de peso
            tester.probarConversionesPeso();
            
            // Probar casos edge
            tester.probarCasosEdge();
            
            System.out.println("\n=== TODAS LAS PRUEBAS UNITARIAS COMPLETADAS ===");
            
        } catch (Exception e) {
            System.err.println("Error durante las pruebas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void probarConversionesTemperatura() {
        System.out.println("Probando conversiones de temperatura...");
        double resultado = conversionService.celsiusAFahrenheit(100.0);
        System.out.printf("Celsius a Fahrenheit: 100C = %.2fF%n", resultado);
        resultado = conversionService.fahrenheitACelsius(212.0);
        System.out.printf("Fahrenheit a Celsius: 212F = %.2fC%n", resultado);
        resultado = conversionService.celsiusAKelvin(0.0);
        System.out.printf("Celsius a Kelvin: 0C = %.2fK%n", resultado);
        resultado = conversionService.kelvinACelsius(273.15);
        System.out.printf("Kelvin a Celsius: 273.15K = %.2fC%n", resultado);
        resultado = conversionService.fahrenheitAKelvin(32.0);
        System.out.printf("Fahrenheit a Kelvin: 32F = %.2fK%n", resultado);
        resultado = conversionService.kelvinAFahrenheit(273.15);
        System.out.printf("Kelvin a Fahrenheit: 273.15K = %.2fF%n", resultado);
        System.out.println();
    }
    
    public void probarConversionesLongitud() {
        System.out.println("Probando conversiones de longitud...");
        double resultado = conversionService.metrosAPies(1.0);
        System.out.printf("Metros a pies: 1m = %.4f ft%n", resultado);
        resultado = conversionService.piesAMetros(3.28084);
        System.out.printf("Pies a metros: 3.28084 ft = %.4f m%n", resultado);
        resultado = conversionService.metrosAPulgadas(1.0);
        System.out.printf("Metros a pulgadas: 1m = %.4f in%n", resultado);
        resultado = conversionService.pulgadasAMetros(39.3701);
        System.out.printf("Pulgadas a metros: 39.3701 in = %.4f m%n", resultado);
        resultado = conversionService.kilometrosAMillas(1.0);
        System.out.printf("Kilometros a millas: 1km = %.4f mi%n", resultado);
        resultado = conversionService.millasAKilometros(0.621371);
        System.out.printf("Millas a kilometros: 0.621371 mi = %.4f km%n", resultado);
        System.out.println();
    }
    
    public void probarConversionesPeso() {
        System.out.println("Probando conversiones de peso...");
        double resultado = conversionService.kilogramosALibras(1.0);
        System.out.printf("Kilogramos a libras: 1kg = %.4f lb%n", resultado);
        resultado = conversionService.librasAKilogramos(2.20462);
        System.out.printf("Libras a kilogramos: 2.20462 lb = %.4f kg%n", resultado);
        resultado = conversionService.gramosAOnzas(28.3495);
        System.out.printf("Gramos a onzas: 28.3495g = %.4f oz%n", resultado);
        resultado = conversionService.onzasAGramos(1.0);
        System.out.printf("Onzas a gramos: 1oz = %.4f g%n", resultado);
        System.out.println();
    }
    
    public void probarCasosEdge() {
        System.out.println("Probando casos edge...");
        double resultado = conversionService.celsiusAFahrenheit(-40.0);
        System.out.printf("Celsius a Fahrenheit (-40C): %.2fF%n", resultado);
        resultado = conversionService.celsiusAKelvin(-273.15);
        System.out.printf("Celsius a Kelvin (-273.15C): %.2fK%n", resultado);
        resultado = conversionService.metrosAPulgadas(0.001);
        System.out.printf("Metros a pulgadas (0.001m): %.6f in%n", resultado);
        resultado = conversionService.kilometrosAMillas(1000.0);
        System.out.printf("Kilometros a millas (1000km): %.2f mi%n", resultado);
        System.out.println();
    }
}
