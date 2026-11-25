package ec.edu.monster.utilidades;

import ec.edu.monster.modelos.DatosCuenta;
import ec.edu.monster.modelos.DatosMovimiento;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Formatos comunes de los clientes: dinero, importes, cuentas e iniciales. */
public final class Formato {

    private static final DecimalFormatSymbols SIMBOLOS = new DecimalFormatSymbols(Locale.US);

    private Formato() {
    }

    public static String simbolo(String moneda) {
        return moneda != null && moneda.toLowerCase(Locale.ROOT).startsWith("dol") ? "US$" : "S/";
    }

    public static String dinero(String moneda, BigDecimal valor) {
        return simbolo(moneda) + " " + new DecimalFormat("#,##0.00", SIMBOLOS).format(valor == null ? BigDecimal.ZERO : valor);
    }

    public static boolean esActiva(DatosCuenta cuenta) {
        return "ACTIVO".equals(cuenta.getEstado());
    }

    public static boolean esIngreso(DatosMovimiento movimiento) {
        return "INGRESO".equals(movimiento.getAccion());
    }

    public static boolean esCuenta(String texto) {
        return texto != null && texto.trim().matches("\\d{8}");
    }

    /** Acepta "150.50" o "150,50"; devuelve null si no es un importe positivo. */
    public static BigDecimal leerImporte(String texto) {
        try {
            BigDecimal importe = new BigDecimal(texto.trim().replace(',', '.'));
            return importe.signum() > 0 ? importe : null;
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    public static String iniciales(String nombre) {
        StringBuilder iniciales = new StringBuilder();
        for (String parte : nombre.trim().split("\\s+")) {
            if (!parte.isEmpty() && iniciales.length() < 2) {
                iniciales.append(Character.toUpperCase(parte.charAt(0)));
            }
        }
        return iniciales.toString();
    }
}
