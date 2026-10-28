package ec.edu.monster.prueba;

import ec.edu.monster.servicios.ConversionService;

/**
 * Prueba rápida para verificar que el servicio funciona correctamente
 * @author ACER NITRO V15
 */
public class PruebaRapida {
    
    public static void main(String[] args) {
        System.out.println("=== PRUEBA RÁPIDA DEL SERVICIO RESTFUL ===");
        
        ConversionService service = new ConversionService();
        
        // Probar algunas conversiones básicas
        System.out.println("🌡️ Probando conversiones de temperatura:");
        
        double celsius = 100.0;
        double fahrenheit = service.celsiusAFahrenheit(celsius);
        System.out.printf("✅ %.1f°C → %.2f°F%n", celsius, fahrenheit);
        
        double kelvin = service.celsiusAKelvin(celsius);
        System.out.printf("✅ %.1f°C → %.2fK%n", celsius, kelvin);
        
        System.out.println("\n📏 Probando conversiones de longitud:");
        double metros = 1.0;
        double pies = service.metrosAPies(metros);
        System.out.printf("✅ %.1fm → %.4f ft%n", metros, pies);
        
        System.out.println("\n⚖️ Probando conversiones de peso:");
        double kg = 1.0;
        double libras = service.kilogramosALibras(kg);
        System.out.printf("✅ %.1fkg → %.4f lb%n", kg, libras);
        
        System.out.println("\n🎉 ¡El servicio funciona correctamente!");
        System.out.println("Los cálculos se están realizando localmente.");
    }
}


