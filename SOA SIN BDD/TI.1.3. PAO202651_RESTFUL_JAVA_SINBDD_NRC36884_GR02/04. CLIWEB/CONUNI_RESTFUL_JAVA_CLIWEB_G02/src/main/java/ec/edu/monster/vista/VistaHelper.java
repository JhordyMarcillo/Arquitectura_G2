package ec.edu.monster.vista;

public class VistaHelper {
    private VistaHelper() {}

    public static String simboloUnidad(String unidad) {
        if (unidad == null) return "";
        switch (unidad) {
            case "celsius": return "°C";
            case "fahrenheit": return "°F";
            case "kelvin": return "K";
            case "metros": return "m";
            case "pies": return "ft";
            case "pulgadas": return "in";
            case "kilometros": return "km";
            case "millas": return "mi";
            case "kilogramos": return "kg";
            case "libras": return "lb";
            case "gramos": return "g";
            case "onzas": return "oz";
            case "onzasFluidas": return "fl oz";
            case "metrosCuadrados": return "m²";
            case "piesCuadrados": return "ft²";
            default: return unidad;
        }
    }

    public static String formatearResultado(double original, String uOrigen, double convertido, String uDestino) {
        return String.format("%.4f %s → %.4f %s", original, simboloUnidad(uOrigen), convertido, simboloUnidad(uDestino));
    }
}



