package ec.edu.monster.vista;

import ec.edu.monster.controlador.Configuracion;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class FormatoNumero {

    private static final DecimalFormat FORMATO = crearFormato();

    private FormatoNumero() {
    }

    // Hasta Configuracion.DECIMALES decimales, sin ceros sobrantes y con punto decimal
    private static DecimalFormat crearFormato() {
        DecimalFormat formato = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.US));
        formato.setMaximumFractionDigits(Configuracion.DECIMALES);
        return formato;
    }

    public static String formatear(double numero) {
        return FORMATO.format(numero);
    }

    // "KILOMETRO" -> "Kilometro"
    public static String nombreVisible(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            return "";
        }
        return codigo.substring(0, 1) + codigo.substring(1).toLowerCase(Locale.ROOT);
    }

    // "Kilometro" -> "KILOMETRO"
    public static String codigo(String nombreVisible) {
        return nombreVisible.trim().toUpperCase(Locale.ROOT);
    }
}
