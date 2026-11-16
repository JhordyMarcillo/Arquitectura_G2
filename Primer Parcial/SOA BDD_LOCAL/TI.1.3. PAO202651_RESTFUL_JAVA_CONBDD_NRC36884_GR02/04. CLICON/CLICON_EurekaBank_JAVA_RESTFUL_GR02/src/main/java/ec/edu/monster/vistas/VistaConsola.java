package ec.edu.monster.vistas;

import ec.edu.monster.modelos.DatosCuenta;
import ec.edu.monster.modelos.DatosMovimiento;
import ec.edu.monster.modelos.ExcepcionServicio;
import ec.edu.monster.modelos.RespuestaAutenticacion;
import ec.edu.monster.modelos.RespuestaMovimientos;
import ec.edu.monster.modelos.RespuestaOperacion;
import ec.edu.monster.utilidades.Formato;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/** Todo lo que se dibuja en la consola: encabezado, tablas con bordes, paneles y colores ANSI. */
public final class VistaConsola {

    private static final String RESET = "\u001B[0m";
    private static final String NEGRITA = "\u001B[1m";
    private static final String GRIS = "\u001B[38;2;100;116;139m";
    private static final String AZUL = "\u001B[38;2;59;130;246m";
    private static final String VERDE = "\u001B[38;2;16;185;129m";
    private static final String ROJO = "\u001B[38;2;239;68;68m";
    private static final int ANCHO = 96;

    private static final PrintStream SALIDA = crearSalida();

    private VistaConsola() {
    }

    private static PrintStream crearSalida() {
        try {
            return new PrintStream(System.out, true, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return System.out;
        }
    }

    public static void limpiar() {
        SALIDA.print("\u001B[H\u001B[2J");
        SALIDA.flush();
    }

    public static void encabezado(String subtitulo, RespuestaAutenticacion sesion) {
        limpiar();
        String[] logo = {
            " ███████╗██╗   ██╗██████╗ ███████╗██╗  ██╗ █████╗     ██████╗  █████╗ ███╗   ██╗██╗  ██╗",
            " ██╔════╝██║   ██║██╔══██╗██╔════╝██║ ██╔╝██╔══██╗    ██╔══██╗██╔══██╗████╗  ██║██║ ██╔╝",
            " █████╗  ██║   ██║██████╔╝█████╗  █████╔╝ ███████║    ██████╔╝███████║██╔██╗ ██║█████╔╝ ",
            " ██╔══╝  ██║   ██║██╔══██╗██╔══╝  ██╔═██╗ ██╔══██║    ██╔══██╗██╔══██║██║╚██╗██║██╔═██╗ ",
            " ███████╗╚██████╔╝██║  ██║███████╗██║  ██╗██║  ██║    ██████╔╝██║  ██║██║ ╚████║██║  ██╗",
            " ╚══════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝    ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝"
        };
        for (String linea : logo) {
            SALIDA.println(AZUL + linea + RESET);
        }
        String derecha = sesion == null ? "" : NEGRITA + sesion.getNombreEmpleado() + RESET + GRIS + " · empleado " + sesion.getCodigoEmpleado();
        String izquierda = GRIS + subtitulo;
        int relleno = Math.max(1, ANCHO - visible(izquierda) - visible(derecha));
        SALIDA.println();
        SALIDA.println(izquierda + " ".repeat(relleno) + derecha + RESET);
        SALIDA.println(GRIS + "─".repeat(ANCHO) + RESET);
        SALIDA.println();
    }

    public static void titulo(String texto) {
        SALIDA.println(AZUL + "── " + RESET + NEGRITA + texto + RESET + " " + AZUL + "─".repeat(Math.max(0, ANCHO - texto.length() - 4)) + RESET);
        SALIDA.println();
    }

    public static void cuentas(List<DatosCuenta> cuentas) {
        List<String[]> filas = new ArrayList<>();
        for (DatosCuenta c : cuentas) {
            String estado = Formato.esActiva(c) ? VERDE + "● ACTIVO" + RESET : GRIS + "○ " + c.getEstado() + RESET;
            filas.add(new String[]{NEGRITA + c.getCodigo() + RESET, c.getCliente(), c.getMoneda(),
                NEGRITA + Formato.dinero(c.getMoneda(), c.getSaldo()) + RESET, estado});
        }
        tabla(new String[]{"Cuenta", "Cliente", "Moneda", "Saldo", "Estado"}, new boolean[]{false, false, false, true, false}, filas);
    }

    public static void movimientos(RespuestaMovimientos datos) {
        String moneda = datos.getMoneda();
        long ingresos = datos.getMovimientos().stream().filter(Formato::esIngreso).count();
        tarjetas(new String[]{"Cuenta", "Saldo disponible", "Movimientos"},
                new String[]{datos.getCuenta(), Formato.dinero(moneda, datos.getSaldo()),
                    datos.getMovimientos().size() + "  (" + ingresos + " ingresos)"},
                new String[]{AZUL, VERDE, GRIS});

        List<String[]> filas = new ArrayList<>();
        for (DatosMovimiento m : datos.getMovimientos()) {
            boolean ingreso = Formato.esIngreso(m);
            String color = ingreso ? VERDE : ROJO;
            filas.add(new String[]{String.valueOf(m.getNumero()), m.getFecha(),
                color + (ingreso ? "▲ " : "▼ ") + RESET + m.getDescripcion(),
                GRIS + (m.getCuentaReferencia() == null ? "-" : m.getCuentaReferencia()) + RESET,
                color + (ingreso ? "+ " : "- ") + Formato.dinero(moneda, m.getImporte()) + RESET});
        }
        tabla(new String[]{"N°", "Fecha", "Tipo", "Referencia", "Importe"}, new boolean[]{true, false, false, false, true}, filas);
    }

    public static void exito(String titulo, RespuestaOperacion respuesta, String moneda) {
        panel(VERDE, "✔ " + titulo, new String[]{
            NEGRITA + respuesta.getMensaje() + RESET,
            "Cuenta " + NEGRITA + respuesta.getCuenta() + RESET + "  ·  Nuevo saldo " + VERDE + NEGRITA
            + Formato.dinero(moneda, respuesta.getSaldo()) + RESET});
    }

    public static void error(ExcepcionServicio error) {
        panel(ROJO, "✖ No se pudo completar la operacion", new String[]{error.getMessage(), GRIS + "Codigo: " + error.getCodigo() + RESET});
    }

    public static void informacion(String mensaje) {
        SALIDA.println(GRIS + mensaje + RESET);
    }

    public static void exitoSimple(String mensaje) {
        SALIDA.println(VERDE + "✔ " + RESET + mensaje);
    }

    public static void pregunta(String texto) {
        SALIDA.print(NEGRITA + texto + RESET + " ");
        SALIDA.flush();
    }

    public static void opcion(int numero, String texto) {
        SALIDA.println("  " + AZUL + NEGRITA + numero + RESET + "  " + texto);
    }

    public static void grupo(String nombre) {
        SALIDA.println(GRIS + nombre + RESET);
    }

    public static void lineaEnBlanco() {
        SALIDA.println();
    }

    // ------------------------------------------------------------------ dibujo

    private static void tabla(String[] encabezados, boolean[] derecha, List<String[]> filas) {
        int[] anchos = new int[encabezados.length];
        for (int i = 0; i < encabezados.length; i++) {
            anchos[i] = encabezados[i].length();
            for (String[] fila : filas) {
                anchos[i] = Math.max(anchos[i], visible(fila[i]));
            }
        }
        SALIDA.println(GRIS + borde('╭', '┬', '╮', anchos) + RESET);
        SALIDA.println(fila(encabezados, anchos, derecha, true));
        SALIDA.println(GRIS + borde('├', '┼', '┤', anchos) + RESET);
        for (String[] f : filas) {
            SALIDA.println(fila(f, anchos, derecha, false));
        }
        SALIDA.println(GRIS + borde('╰', '┴', '╯', anchos) + RESET);
        SALIDA.println();
    }

    private static String fila(String[] celdas, int[] anchos, boolean[] derecha, boolean encabezado) {
        StringBuilder linea = new StringBuilder(GRIS + "│" + RESET);
        for (int i = 0; i < celdas.length; i++) {
            String texto = encabezado ? NEGRITA + celdas[i] + RESET : celdas[i];
            String espacio = " ".repeat(anchos[i] - visible(texto));
            linea.append(' ').append(derecha[i] ? espacio + texto : texto + espacio).append(' ').append(GRIS).append("│").append(RESET);
        }
        return linea.toString();
    }

    private static String borde(char izquierda, char medio, char derecha, int[] anchos) {
        StringBuilder linea = new StringBuilder().append(izquierda);
        for (int i = 0; i < anchos.length; i++) {
            linea.append("─".repeat(anchos[i] + 2)).append(i == anchos.length - 1 ? derecha : medio);
        }
        return linea.toString();
    }

    private static void tarjetas(String[] etiquetas, String[] valores, String[] colores) {
        StringBuilder arriba = new StringBuilder();
        StringBuilder medio1 = new StringBuilder();
        StringBuilder medio2 = new StringBuilder();
        StringBuilder abajo = new StringBuilder();
        for (int i = 0; i < etiquetas.length; i++) {
            int ancho = Math.max(etiquetas[i].length(), valores[i].length()) + 2;
            arriba.append(colores[i]).append('╭').append("─".repeat(ancho)).append('╮').append(RESET).append("  ");
            medio1.append(colores[i]).append('│').append(RESET).append(' ').append(GRIS).append(etiquetas[i]).append(RESET)
                    .append(" ".repeat(ancho - etiquetas[i].length() - 1)).append(colores[i]).append('│').append(RESET).append("  ");
            medio2.append(colores[i]).append('│').append(RESET).append(' ').append(NEGRITA).append(valores[i]).append(RESET)
                    .append(" ".repeat(ancho - valores[i].length() - 1)).append(colores[i]).append('│').append(RESET).append("  ");
            abajo.append(colores[i]).append('╰').append("─".repeat(ancho)).append('╯').append(RESET).append("  ");
        }
        SALIDA.println(arriba);
        SALIDA.println(medio1);
        SALIDA.println(medio2);
        SALIDA.println(abajo);
    }

    private static void panel(String color, String titulo, String[] lineas) {
        int ancho = visible(titulo) + 4;
        for (String l : lineas) {
            ancho = Math.max(ancho, visible(l) + 4);
        }
        SALIDA.println(color + "╭─" + RESET + NEGRITA + titulo + RESET + color + "─".repeat(ancho - visible(titulo) - 1) + "╮" + RESET);
        SALIDA.println(color + "│" + RESET + " ".repeat(ancho) + color + "│" + RESET);
        for (String l : lineas) {
            SALIDA.println(color + "│" + RESET + "  " + l + " ".repeat(ancho - visible(l) - 2) + color + "│" + RESET);
        }
        SALIDA.println(color + "│" + RESET + " ".repeat(ancho) + color + "│" + RESET);
        SALIDA.println(color + "╰" + "─".repeat(ancho) + "╯" + RESET);
    }

    /** Largo del texto sin contar los codigos de color. */
    private static int visible(String texto) {
        return texto.replaceAll("\u001B\\[[0-9;]*m", "").length();
    }
}
