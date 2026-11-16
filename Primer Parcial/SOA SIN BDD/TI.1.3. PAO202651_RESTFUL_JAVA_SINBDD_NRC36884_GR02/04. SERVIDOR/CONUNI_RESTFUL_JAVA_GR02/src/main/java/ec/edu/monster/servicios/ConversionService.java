package ec.edu.monster.servicios;

import ec.edu.monster.modelo.Conversion;
import ec.edu.monster.modelo.ConversionResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

/**
 * Servicio para conversiones de temperatura y unidades
 * Capa de servicios que contiene toda la lógica de negocio
 * @author ACER NITRO V15
 */
@Named
@ApplicationScoped
public class ConversionService {
    
    // ========== CONVERSIONES DE TEMPERATURA ==========
    
    /**
     * Convierte Celsius a Fahrenheit
     */
    public double celsiusAFahrenheit(double celsius) {
        return (celsius * Conversion.CELSIUS_TO_FAHRENHEIT_MULTIPLIER) + Conversion.CELSIUS_TO_FAHRENHEIT_OFFSET;
    }
    
    /**
     * Convierte Fahrenheit a Celsius
     */
    public double fahrenheitACelsius(double fahrenheit) {
        return (fahrenheit - Conversion.CELSIUS_TO_FAHRENHEIT_OFFSET) / Conversion.CELSIUS_TO_FAHRENHEIT_MULTIPLIER;
    }
    
    /**
     * Convierte Celsius a Kelvin
     */
    public double celsiusAKelvin(double celsius) {
        return celsius + Conversion.KELVIN_OFFSET;
    }
    
    /**
     * Convierte Kelvin a Celsius
     */
    public double kelvinACelsius(double kelvin) {
        return kelvin - Conversion.KELVIN_OFFSET;
    }
    
    /**
     * Convierte Fahrenheit a Kelvin
     */
    public double fahrenheitAKelvin(double fahrenheit) {
        return fahrenheitACelsius(fahrenheit) + Conversion.KELVIN_OFFSET;
    }
    
    /**
     * Convierte Kelvin a Fahrenheit
     */
    public double kelvinAFahrenheit(double kelvin) {
        return celsiusAFahrenheit(kelvinACelsius(kelvin));
    }
    
    // ========== CONVERSIONES DE LONGITUD ==========
    
    /**
     * Convierte metros a pies
     */
    public double metrosAPies(double metros) {
        return metros * Conversion.METERS_TO_FEET;
    }
    
    /**
     * Convierte pies a metros
     */
    public double piesAMetros(double pies) {
        return pies / Conversion.METERS_TO_FEET;
    }
    
    /**
     * Convierte metros a pulgadas
     */
    public double metrosAPulgadas(double metros) {
        return metros * Conversion.METERS_TO_INCHES;
    }
    
    /**
     * Convierte pulgadas a metros
     */
    public double pulgadasAMetros(double pulgadas) {
        return pulgadas / Conversion.METERS_TO_INCHES;
    }
    
    /**
     * Convierte kilómetros a millas
     */
    public double kilometrosAMillas(double kilometros) {
        return kilometros * Conversion.KILOMETERS_TO_MILES;
    }
    
    /**
     * Convierte millas a kilómetros
     */
    public double millasAKilometros(double millas) {
        return millas / Conversion.KILOMETERS_TO_MILES;
    }
    
    // ========== CONVERSIONES DE PESO/MASA ==========
    
    /**
     * Convierte kilogramos a libras
     */
    public double kilogramosALibras(double kilogramos) {
        return kilogramos * Conversion.KILOGRAMS_TO_POUNDS;
    }
    
    /**
     * Convierte libras a kilogramos
     */
    public double librasAKilogramos(double libras) {
        return libras / Conversion.KILOGRAMS_TO_POUNDS;
    }
    
    /**
     * Convierte gramos a onzas
     */
    public double gramosAOnzas(double gramos) {
        return gramos * Conversion.GRAMS_TO_OUNCES;
    }
    
    /**
     * Convierte onzas a gramos
     */
    public double onzasAGramos(double onzas) {
        return onzas / Conversion.GRAMS_TO_OUNCES;
    }
    
    // ========== MÉTODO PRINCIPAL DE CONVERSIÓN ==========
    
    /**
     * Método principal que maneja todas las conversiones
     * @param valor Valor a convertir
     * @param unidadOrigen Unidad de origen
     * @param unidadDestino Unidad de destino
     * @param categoria Categoría de conversión
     * @return ConversionResponse con el resultado
     */
    public ConversionResponse convertir(double valor, String unidadOrigen, String unidadDestino, String categoria) {
        try {
            // Validar entrada
            if (valor < 0 && !categoria.equals(Conversion.CATEGORIA_TEMPERATURA)) {
                return ConversionResponse.crearError("El valor no puede ser negativo para esta categoría");
            }
            
            if (!Conversion.esConversionValida(unidadOrigen, unidadDestino, categoria)) {
                return ConversionResponse.crearError("Conversión no válida entre " + unidadOrigen + " y " + unidadDestino);
            }
            
            double resultado = realizarConversion(valor, unidadOrigen, unidadDestino, categoria);
            
            return ConversionResponse.crearExito(valor, resultado, unidadOrigen, unidadDestino, categoria);
            
        } catch (Exception e) {
            return ConversionResponse.crearError("Error en la conversión: " + e.getMessage());
        }
    }
    
    /**
     * Realiza la conversión específica según los parámetros
     */
    private double realizarConversion(double valor, String unidadOrigen, String unidadDestino, String categoria) {
        String operacion = unidadOrigen + "A" + unidadDestino;
        
        switch (categoria.toLowerCase()) {
            case Conversion.CATEGORIA_TEMPERATURA:
                return convertirTemperatura(valor, operacion);
            case Conversion.CATEGORIA_LONGITUD:
                return convertirLongitud(valor, operacion);
            case Conversion.CATEGORIA_PESO:
                return convertirPeso(valor, operacion);
            default:
                throw new IllegalArgumentException("Categoría no soportada: " + categoria);
        }
    }
    
    private double convertirTemperatura(double valor, String operacion) {
        switch (operacion) {
            case "celsiusAfahrenheit": return celsiusAFahrenheit(valor);
            case "fahrenheitAcelsius": return fahrenheitACelsius(valor);
            case "celsiusAkelvin": return celsiusAKelvin(valor);
            case "kelvinAcelsius": return kelvinACelsius(valor);
            case "fahrenheitAkelvin": return fahrenheitAKelvin(valor);
            case "kelvinAfahrenheit": return kelvinAFahrenheit(valor);
            default: throw new IllegalArgumentException("Operación de temperatura no soportada: " + operacion);
        }
    }
    
    private double convertirLongitud(double valor, String operacion) {
        switch (operacion) {
            case "metrosApies": return metrosAPies(valor);
            case "piesAmetros": return piesAMetros(valor);
            case "metrosApulgadas": return metrosAPulgadas(valor);
            case "pulgadasAmetros": return pulgadasAMetros(valor);
            case "kilometrosAmillas": return kilometrosAMillas(valor);
            case "millasAkilometros": return millasAKilometros(valor);
            default: throw new IllegalArgumentException("Operación de longitud no soportada: " + operacion);
        }
    }
    
    private double convertirPeso(double valor, String operacion) {
        switch (operacion) {
            case "kilogramosAlibras": return kilogramosALibras(valor);
            case "librasAkilogramos": return librasAKilogramos(valor);
            case "gramosAonzas": return gramosAOnzas(valor);
            case "onzasAgramos": return onzasAGramos(valor);
            default: throw new IllegalArgumentException("Operación de peso no soportada: " + operacion);
        }
    }
    
}
