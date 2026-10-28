package ec.edu.monster.vista;

import ec.edu.monster.modelo.ConversionRequest;
import ec.edu.monster.modelo.ConversionResponse;

import java.util.Scanner;

/**
 * Vista para la aplicación cliente de consola RESTful
 * @author Jonathan
 */
public class ClienteConsolaREST {
    
    // Colores para la consola
    public static final String CELESTE = "\033[38;5;51m";
    public static final String VERDE = "\033[92m";
    public static final String ROJO = "\033[91m";
    public static final String AMARILLO = "\033[93m";
    public static final String RESET = "\033[0m";
    
    private Scanner scanner;
    private boolean soportaColores;
    
    /**
     * Detecta si el terminal soporta colores
     */
    private boolean detectarSoporteColores() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            
            if (os.contains("windows")) {
                // En Windows 10/11, verificar si VirtualTerminalLevel está habilitado
                // O simplemente intentar siempre, ya que Windows 10+ lo soporta
                try {
                    // Verificar si estamos en Windows Terminal, PowerShell o CMD con ANSI habilitado
                    String term = System.getenv("TERM");
                    String wtSession = System.getenv("WT_SESSION");
                    
                    // Si hay WT_SESSION, estamos en Windows Terminal
                    if (wtSession != null) {
                        return true;
                    }
                    
                    // Si hay TERM configurado, usarlo
                    if (term != null && (term.contains("xterm") || term.contains("256color"))) {
                        return true;
                    }
                    
                    // Windows 10 build 1511+ soporta ANSI si está habilitado
                    // Intentar siempre en Windows, el sistema lo manejará
                    return true;
                } catch (Exception e) {
                    // Si falla, intentar de todas formas
                    return true;
                }
            }
            
            // Para Linux/Mac, verificar TERM
            String term = System.getenv("TERM");
            return term != null && !term.equals("dumb");
        } catch (Exception e) {
            // En caso de error, intentar usar colores de todas formas
            return true;
        }
    }
    
    /**
     * Aplica color solo si el terminal lo soporta
     */
    private String aplicarColor(String color, String texto) {
        return soportaColores ? color + texto + RESET : texto;
    }

    public ClienteConsolaREST() {
        // Detectar soporte de colores
        this.soportaColores = detectarSoporteColores();
        
        // Crear Scanner con codificación UTF-8
        try {
            this.scanner = ec.edu.monster.utilidades.Entrada.SCANNER;
        } catch (Exception e) {
            this.scanner = ec.edu.monster.utilidades.Entrada.SCANNER;
        }
    }

    public void mostrarBienvenida() {
        mostrarEncabezado();
    }
    
    /**
     * Muestra el encabezado principal
     */
    private void mostrarEncabezado() {
        System.out.println();
        System.out.println(aplicarColor(CELESTE, "████████╗███████╗███╗   ███╗██████╗ ███████╗██████╗  █████╗ ████████╗██╗   ██╗██████╗  █████╗ "));
        System.out.println(aplicarColor(CELESTE, "╚══██╔══╝██╔════╝████╗ ████║██╔══██╗██╔════╝██╔══██╗██╔══██╗╚══██╔══╝██║   ██║██╔══██╗██╔══██╗"));
        System.out.println(aplicarColor(CELESTE, "   ██║   █████╗  ██╔████╔██║██████╔╝█████╗  ██████╔╝███████║   ██║   ██║   ██║██████╔╝███████║"));
        System.out.println(aplicarColor(CELESTE, "   ██║   ██╔══╝  ██║╚██╔╝██║██╔══██╗██╔══╝  ██╔══██╗██╔══██║   ██║   ██║   ██║██╔══██╗██╔══██║"));
        System.out.println(aplicarColor(CELESTE, "   ██║   ███████╗██║ ╚═╝ ██║██║  ██║███████╗██║  ██║██║  ██║   ██║   ╚██████╔╝██║  ██║██║  ██║"));
        System.out.println(aplicarColor(CELESTE, "   ╚═╝   ╚══════╝╚═╝     ╚═╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝"));
        System.out.println();
        System.out.println(aplicarColor(VERDE, "╔══════════════════════════════════════════════════════════════════════════════╗"));
        System.out.println(aplicarColor(VERDE, "                SISTEMA DE CONVERSIÓN DE UNIDADES RESTFUL               "));
        System.out.println(aplicarColor(VERDE, "╚══════════════════════════════════════════════════════════════════════════════╝"));
        System.out.println();
    }

    public int mostrarMenu() {
        System.out.println(aplicarColor(CELESTE, "╔══════════════════════════════════════════════════════════════════════════════╗"));
        System.out.println(aplicarColor(CELESTE, "                         MENÚ PRINCIPAL                            "));
        System.out.println(aplicarColor(CELESTE, "╠══════════════════════════════════════════════════════════════════════════════╣"));
        System.out.println(aplicarColor(CELESTE, "") + "  " + aplicarColor(VERDE, "[1]") + " Conversiones de " + aplicarColor(AMARILLO, "Temperatura") + "                                         " + aplicarColor(CELESTE, ""));
        System.out.println(aplicarColor(CELESTE, "") + "  " + aplicarColor(VERDE, "[2]") + " Conversiones de " + aplicarColor(AMARILLO, "Longitud") + "                                       " + aplicarColor(CELESTE, ""));
        System.out.println(aplicarColor(CELESTE, "") + "  " + aplicarColor(VERDE, "[3]") + " Conversiones de " + aplicarColor(AMARILLO, "Peso/Masa") + "                                    " + aplicarColor(CELESTE, ""));
        System.out.println(aplicarColor(CELESTE, "") + "  " + aplicarColor(ROJO, "[0]") + " Salir                                                                   " + aplicarColor(CELESTE, ""));
        System.out.println(aplicarColor(CELESTE, "╚══════════════════════════════════════════════════════════════════════════════╝"));
        System.out.println();
        System.out.print(aplicarColor(VERDE, "Seleccione una opción: "));
        
        try {
            String entrada = scanner.nextLine().trim();
            return Integer.parseInt(entrada);
        } catch (NumberFormatException e) {
            return -1; // Para forzar mensaje "Opción no válida"
        }
    }

    public void mostrarSubmenuTemperatura() {
        System.out.println(aplicarColor(VERDE, "╔══════════════════════════════════════════════════════════════════════════════╗"));
        System.out.println(aplicarColor(VERDE, "                     CONVERSIONES DE TEMPERATURA                             "));
        System.out.println(aplicarColor(VERDE, "╚══════════════════════════════════════════════════════════════════════════════╝"));
        System.out.println();
        System.out.println(aplicarColor(CELESTE, "╔══════════════════════════════════════════════════════════════════════════════╗"));
        System.out.println(aplicarColor(CELESTE, "                              MENÚ TEMPERATURA                                "));
        System.out.println(aplicarColor(CELESTE, "╠══════════════════════════════════════════════════════════════════════════════╣"));
        System.out.println(aplicarColor(CELESTE, "") + "  " + aplicarColor(VERDE, "[1]") + " " + aplicarColor(AMARILLO, "Celsius") + " → " + aplicarColor(AMARILLO, "Fahrenheit") + "                                        " + aplicarColor(CELESTE, ""));
        System.out.println(aplicarColor(CELESTE, "") + "  " + aplicarColor(VERDE, "[2]") + " " + aplicarColor(AMARILLO, "Fahrenheit") + " → " + aplicarColor(AMARILLO, "Celsius") + "                                        " + aplicarColor(CELESTE, ""));
        System.out.println(aplicarColor(CELESTE, "") + "  " + aplicarColor(VERDE, "[3]") + " " + aplicarColor(AMARILLO, "Celsius") + " → " + aplicarColor(AMARILLO, "Kelvin") + "                                          " + aplicarColor(CELESTE, ""));
        System.out.println(aplicarColor(CELESTE, "") + "  " + aplicarColor(VERDE, "[4]") + " " + aplicarColor(AMARILLO, "Kelvin") + " → " + aplicarColor(AMARILLO, "Celsius") + "                                          " + aplicarColor(CELESTE, ""));
        System.out.println(aplicarColor(CELESTE, "") + "  " + aplicarColor(VERDE, "[5]") + " " + aplicarColor(AMARILLO, "Fahrenheit") + " → " + aplicarColor(AMARILLO, "Kelvin") + "                                        " + aplicarColor(CELESTE, ""));
        System.out.println(aplicarColor(CELESTE, "") + "  " + aplicarColor(VERDE, "[6]") + " " + aplicarColor(AMARILLO, "Kelvin") + " → " + aplicarColor(AMARILLO, "Fahrenheit") + "                                        " + aplicarColor(CELESTE, ""));
        System.out.println(aplicarColor(CELESTE, "╚══════════════════════════════════════════════════════════════════════════════╝"));
        System.out.println();
        System.out.print(aplicarColor(VERDE, "Seleccione la conversión: "));
    }

    public void mostrarSubmenuLongitud() {
        System.out.println(aplicarColor(VERDE, "╔══════════════════════════════════════════════════════════════════════════════╗"));
        System.out.println(aplicarColor(VERDE, "                    CONVERSIONES DE LONGITUD                                 "));
        System.out.println(aplicarColor(VERDE, "╚══════════════════════════════════════════════════════════════════════════════╝"));
        System.out.println();
        System.out.println(aplicarColor(CELESTE, "╔══════════════════════════════════════════════════════════════════════════════╗"));
        System.out.println(aplicarColor(CELESTE, "                         MENÚ LONGITUD                                   "));
        System.out.println(aplicarColor(CELESTE, "╠══════════════════════════════════════════════════════════════════════════════╣"));
        System.out.println(aplicarColor(CELESTE, "") + "  " + aplicarColor(VERDE, "[1]") + " " + aplicarColor(AMARILLO, "Metros") + " → " + aplicarColor(AMARILLO, "Pies") + "                                            " + aplicarColor(CELESTE, ""));
        System.out.println(aplicarColor(CELESTE, "") + "  " + aplicarColor(VERDE, "[2]") + " " + aplicarColor(AMARILLO, "Pies") + " → " + aplicarColor(AMARILLO, "Metros") + "                                            " + aplicarColor(CELESTE, ""));
        System.out.println(aplicarColor(CELESTE, "") + "  " + aplicarColor(VERDE, "[3]") + " " + aplicarColor(AMARILLO, "Metros") + " → " + aplicarColor(AMARILLO, "Pulgadas") + "                                        " + aplicarColor(CELESTE, ""));
        System.out.println(aplicarColor(CELESTE, "") + "  " + aplicarColor(VERDE, "[4]") + " " + aplicarColor(AMARILLO, "Pulgadas") + " → " + aplicarColor(AMARILLO, "Metros") + "                                        " + aplicarColor(CELESTE, ""));
        System.out.println(aplicarColor(CELESTE, "") + "  " + aplicarColor(VERDE, "[5]") + " " + aplicarColor(AMARILLO, "Kilómetros") + " → " + aplicarColor(AMARILLO, "Millas") + "                                      " + aplicarColor(CELESTE, ""));
        System.out.println(aplicarColor(CELESTE, "") + "  " + aplicarColor(VERDE, "[6]") + " " + aplicarColor(AMARILLO, "Millas") + " → " + aplicarColor(AMARILLO, "Kilómetros") + "                                      " + aplicarColor(CELESTE, ""));
        System.out.println(aplicarColor(CELESTE, "╚══════════════════════════════════════════════════════════════════════════════╝"));
        System.out.println();
        System.out.print(aplicarColor(VERDE, "Seleccione la conversión: "));
    }

    public void mostrarSubmenuPeso() {
        System.out.println(aplicarColor(VERDE, "╔══════════════════════════════════════════════════════════════════════════════╗"));
        System.out.println(aplicarColor(VERDE, "                     CONVERSIONES DE PESO/MASA                                 "));
        System.out.println(aplicarColor(VERDE, "╚══════════════════════════════════════════════════════════════════════════════╝"));
        System.out.println();
        System.out.println(aplicarColor(CELESTE, "╔══════════════════════════════════════════════════════════════════════════════╗"));
        System.out.println(aplicarColor(CELESTE, "                         MENÚ PESO/MASA                                  "));
        System.out.println(aplicarColor(CELESTE, "╠══════════════════════════════════════════════════════════════════════════════╣"));
        System.out.println(aplicarColor(CELESTE, "") + "  " + aplicarColor(VERDE, "[1]") + " " + aplicarColor(AMARILLO, "Kilogramos") + " → " + aplicarColor(AMARILLO, "Libras") + "                                        " + aplicarColor(CELESTE, ""));
        System.out.println(aplicarColor(CELESTE, "") + "  " + aplicarColor(VERDE, "[2]") + " " + aplicarColor(AMARILLO, "Libras") + " → " + aplicarColor(AMARILLO, "Kilogramos") + "                                        " + aplicarColor(CELESTE, ""));
        System.out.println(aplicarColor(CELESTE, "") + "  " + aplicarColor(VERDE, "[3]") + " " + aplicarColor(AMARILLO, "Gramos") + " → " + aplicarColor(AMARILLO, "Onzas") + "                                            " + aplicarColor(CELESTE, ""));
        System.out.println(aplicarColor(CELESTE, "") + "  " + aplicarColor(VERDE, "[4]") + " " + aplicarColor(AMARILLO, "Onzas") + " → " + aplicarColor(AMARILLO, "Gramos") + "                                            " + aplicarColor(CELESTE, ""));
        System.out.println(aplicarColor(CELESTE, "╚══════════════════════════════════════════════════════════════════════════════╝"));
        System.out.println();
        System.out.print(aplicarColor(VERDE, "Seleccione la conversión: "));
    }


    public double solicitarValor(String unidad) {
        while (true) {
            System.out.println();
            System.out.println(aplicarColor(VERDE, "╔══════════════════════════════════════════════════════════════════════════════╗"));
            System.out.println(aplicarColor(VERDE, "                         INGRESO DE VALOR                                  "));
            System.out.println(aplicarColor(VERDE, "╚══════════════════════════════════════════════════════════════════════════════╝"));
            System.out.println();
            System.out.print(aplicarColor(AMARILLO, "Ingrese el valor a convertir: "));
            try {
                String entrada = scanner.nextLine().replace(",", "."); // reemplaza ',' por '.'
                double valor = Double.parseDouble(entrada);
                
                if (valor < 0) {
                    mostrarErrorEntrada("No se permiten valores negativos");
                    continue;
                }
                
                return valor;
            } catch (NumberFormatException e) {
                mostrarErrorEntrada("Debe ingresar un valor numérico válido");
            }
        }
    }
    
    /**
     * Muestra error de entrada
     */
    public void mostrarErrorEntrada(String mensaje) {
        System.out.println(aplicarColor(ROJO, "ERROR: " + mensaje));
    }

    public void mostrarResultado(ConversionResponse response) {
        System.out.println();
        
        if (response.isExito()) {
            System.out.println(aplicarColor(VERDE, "╔══════════════════════════════════════════════════════════════════════════════╗"));
            System.out.println(aplicarColor(VERDE, "                             RESULTADO                                        "));
            System.out.println(aplicarColor(VERDE, "╚══════════════════════════════════════════════════════════════════════════════╝"));
            System.out.println();
            System.out.println("  " + aplicarColor(AMARILLO, response.getValorOriginal() + " " + response.getUnidadOrigen() + " = " + response.getValorConvertido() + " " + response.getUnidadDestino()));
            System.out.println(aplicarColor(CELESTE, "Operación: " + response.getCategoria()));
        } else {
            System.out.println(aplicarColor(ROJO, "╔══════════════════════════════════════════════════════════════════════════════╗"));
            System.out.println(aplicarColor(ROJO, "                        ¡ERROR MONSTRUOSO DETECTADO!                          "));
            System.out.println(aplicarColor(ROJO, "╚══════════════════════════════════════════════════════════════════════════════╝"));
            System.out.println();
            System.out.println(aplicarColor(ROJO, response.getMensaje()));
            System.out.println();
            System.out.println(aplicarColor(AMARILLO, "╔══════════════════════════════════════════════════════════════════════════════╗"));
            System.out.println(aplicarColor(AMARILLO, "                    ¡ESPERA A QUE EL MONSTRUO SE CALME!                      "));
            System.out.println(aplicarColor(AMARILLO, "╚══════════════════════════════════════════════════════════════════════════════╝"));
        }
        System.out.println();
    }

    public void mostrarError(String mensaje) {
        System.out.println("\n❌ ERROR: " + mensaje);
    }

    public void mostrarPausa() {
        System.out.print(aplicarColor(AMARILLO, "\nPresione cualquier tecla para continuar..."));
        scanner.nextLine();
    }

    public void mostrarDespedida() {
        System.out.println();
        System.out.println(aplicarColor(VERDE, "╔══════════════════════════════════════════════════════════════════════════════╗"));
        System.out.println(aplicarColor(VERDE, "                             HASTA PRONTO                                      "));
        System.out.println(aplicarColor(VERDE, "╚══════════════════════════════════════════════════════════════════════════════╝"));
        System.out.println();
        System.out.println(aplicarColor(CELESTE, "Gracias por utilizar el sistema Monster de conversión de unidades."));
        System.out.println(aplicarColor(AMARILLO, "¡Que tenga un excelente día!"));
    }
}
