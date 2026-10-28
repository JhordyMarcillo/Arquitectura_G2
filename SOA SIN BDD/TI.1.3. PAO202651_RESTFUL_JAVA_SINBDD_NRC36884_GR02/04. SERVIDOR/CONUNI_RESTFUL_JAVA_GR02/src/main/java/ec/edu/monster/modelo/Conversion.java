package ec.edu.monster.modelo;

/**
 * Modelo para las conversiones de unidades
 * Contiene las constantes y métodos auxiliares para las conversiones
 * @author ACER NITRO V15
 */
public class Conversion {
    
    // ========== CONSTANTES DE CONVERSIÓN ==========
    
    // Temperatura
    public static final double CELSIUS_TO_FAHRENHEIT_MULTIPLIER = 9.0 / 5.0;
    public static final double CELSIUS_TO_FAHRENHEIT_OFFSET = 32.0;
    public static final double KELVIN_OFFSET = 273.15;
    
    // Longitud
    public static final double METERS_TO_FEET = 3.28084;
    public static final double METERS_TO_INCHES = 39.3701;
    public static final double KILOMETERS_TO_MILES = 0.621371;
    
    // Peso/Masa
    public static final double KILOGRAMS_TO_POUNDS = 2.20462;
    public static final double GRAMS_TO_OUNCES = 0.035274;
    
    // ========== CATEGORÍAS DE CONVERSIÓN ==========
    
    public static final String CATEGORIA_TEMPERATURA = "temperatura";
    public static final String CATEGORIA_LONGITUD = "longitud";
    public static final String CATEGORIA_PESO = "peso";
    
    // ========== UNIDADES POR CATEGORÍA ==========
    
    // Temperatura
    public static final String CELSIUS = "celsius";
    public static final String FAHRENHEIT = "fahrenheit";
    public static final String KELVIN = "kelvin";
    
    // Longitud
    public static final String METROS = "metros";
    public static final String PIES = "pies";
    public static final String PULGADAS = "pulgadas";
    public static final String KILOMETROS = "kilometros";
    public static final String MILLAS = "millas";
    
    // Peso/Masa
    public static final String KILOGRAMOS = "kilogramos";
    public static final String LIBRAS = "libras";
    public static final String GRAMOS = "gramos";
    public static final String ONZAS = "onzas";
    
    
    // ========== MÉTODOS AUXILIARES ==========
    
    /**
     * Valida si una conversión es válida
     */
    public static boolean esConversionValida(String unidadOrigen, String unidadDestino, String categoria) {
        if (unidadOrigen == null || unidadDestino == null || categoria == null) {
            return false;
        }
        
        switch (categoria.toLowerCase()) {
            case CATEGORIA_TEMPERATURA:
                return esUnidadTemperatura(unidadOrigen) && esUnidadTemperatura(unidadDestino);
            case CATEGORIA_LONGITUD:
                return esUnidadLongitud(unidadOrigen) && esUnidadLongitud(unidadDestino);
            case CATEGORIA_PESO:
                return esUnidadPeso(unidadOrigen) && esUnidadPeso(unidadDestino);
            default:
                return false;
        }
    }
    
    private static boolean esUnidadTemperatura(String unidad) {
        return CELSIUS.equals(unidad) || FAHRENHEIT.equals(unidad) || KELVIN.equals(unidad);
    }
    
    private static boolean esUnidadLongitud(String unidad) {
        return METROS.equals(unidad) || PIES.equals(unidad) || PULGADAS.equals(unidad) ||
               KILOMETROS.equals(unidad) || MILLAS.equals(unidad);
    }
    
    private static boolean esUnidadPeso(String unidad) {
        return KILOGRAMOS.equals(unidad) || LIBRAS.equals(unidad) || 
               GRAMOS.equals(unidad) || ONZAS.equals(unidad);
    }
    
    
    /**
     * Obtiene el símbolo de una unidad
     */
    public static String obtenerSimbolo(String unidad) {
        switch (unidad.toLowerCase()) {
            // Temperatura
            case CELSIUS: return "°C";
            case FAHRENHEIT: return "°F";
            case KELVIN: return "K";
            
            // Longitud
            case METROS: return "m";
            case PIES: return "ft";
            case PULGADAS: return "in";
            case KILOMETROS: return "km";
            case MILLAS: return "mi";
            
            // Peso/Masa
            case KILOGRAMOS: return "kg";
            case LIBRAS: return "lb";
            case GRAMOS: return "g";
            case ONZAS: return "oz";
            
            default: return unidad;
        }
    }
}


