package ec.edu.monster.vista;

import java.io.Console;
import java.util.Scanner;

/**
 * Vista para el proceso de autenticación - Patrón MVC
 * Maneja la presentación del login y entrada de credenciales
 * @author Jonathan
 */
public class VistaLogin {
    
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
    
    public VistaLogin() {
        // Detectar soporte de colores
        this.soportaColores = detectarSoporteColores();
        
        // Crear Scanner con codificación UTF-8
        try {
            this.scanner = ec.edu.monster.utilidades.Entrada.SCANNER;
        } catch (Exception e) {
            this.scanner = ec.edu.monster.utilidades.Entrada.SCANNER;
        }
    }
    
    /**
     * Muestra el banner de login
     */
    public void mostrarBannerLogin() {
        mostrarLogo();
        System.out.println("=".repeat(60));
        System.out.println(aplicarColor(CELESTE, "MONSTERS INC. CONVERTER RESTFUL - AUTENTICACIÓN"));
        System.out.println(aplicarColor(CELESTE, "Sistema de Seguridad Empresarial"));
        System.out.println("=".repeat(60));
        System.out.println();
        System.out.println(aplicarColor(AMARILLO, "Acceso restringido - Ingrese sus credenciales"));
        System.out.println();
    }
    
    /**
     * Muestra el logo ASCII de MONSTERS INC
     */
    private void mostrarLogo() {
        System.out.println(
            "\n" +
            aplicarColor(CELESTE, "███╗   ███╗ ██████╗ ███╗   ██╗███████╗████████╗███████╗██████╗     ██████╗ ██████╗ ██████╗") + "\n" +
            aplicarColor(CELESTE, "████╗ ████║██╔═══██╗████╗  ██║██╔════╝╚══██╔══╝██╔════╝██╔══██╗   ██╔════╝ ██╔══██╗╚═══██╗") + "\n" +
            aplicarColor(CELESTE, "██╔████╔██║██║   ██║██╔██╗ ██║███████╗   ██║   █████╗  ██████╔╝   ██║  ███╗██████╔╝  ███╔╝") + "\n" +
            aplicarColor(CELESTE, "██║╚██╔╝██║██║   ██║██║╚██╗██║╚════██║   ██║   ██╔══╝  ██╔══██╗   ██║   ██║██╔══██╗ ███╔╝") + "\n" +
            aplicarColor(CELESTE, "██║ ╚═╝ ██║╚██████╔╝██║ ╚████║███████║   ██║   ███████╗██║  ██║   ╚██████╔╝██║  ██║██████╗") + "\n" +
            aplicarColor(CELESTE, "╚═╝     ╚═╝ ╚═════╝ ╚═╝  ╚═══╝╚══════╝   ╚═╝   ╚══════╝╚═╝  ╚═╝    ╚═════╝ ╚═╝  ╚═╝╚═════╝") + "\n"
        );
    }
    
    /**
     * Lee el nombre de usuario
     */
    public String leerUsuario() {
        System.out.print(aplicarColor(CELESTE, "Usuario: "));
        return scanner.nextLine().trim();
    }
    
    /**
     * Lee la contraseña oculta con asteriscos
     */
    public String leerContrasenaOculta() {
        System.out.print(aplicarColor(CELESTE, "Contraseña: "));
        
        try {
            // Intentar usar Console para ocultar la entrada
            Console console = System.console();
            if (console != null) {
                char[] passwordArray = console.readPassword();
                return new String(passwordArray);
            } else {
                // Fallback: usar entrada normal pero sin mostrar el mensaje feo
                return scanner.nextLine().trim();
            }
        } catch (Exception e) {
            // Si falla, usar entrada normal
            return scanner.nextLine().trim();
        }
    }
    
    /**
     * Muestra mensaje de éxito
     */
    public void mostrarMensajeExito() {
        System.out.println();
        System.out.println(aplicarColor(VERDE, "¡Autenticación exitosa!"));
        System.out.println(aplicarColor(VERDE, "Bienvenido al sistema Monsters Inc. Converter RESTful"));
        System.out.println("=".repeat(60));
        System.out.println();
    }
    
    /**
     * Muestra mensaje de error
     */
    public void mostrarMensajeError(int intentoActual, int maxIntentos) {
        System.out.println();
        System.out.println(aplicarColor(ROJO, "Credenciales incorrectas"));
        
        if (intentoActual < maxIntentos) {
            System.out.println(aplicarColor(AMARILLO, "Intente nuevamente..."));
            System.out.println();
        }
    }
    
    /**
     * Muestra mensaje de bloqueo
     */
    public void mostrarMensajeBloqueo() {
        System.out.println();
        System.out.println(aplicarColor(ROJO, "ACCESO DENEGADO"));
        System.out.println(aplicarColor(ROJO, "Demasiados intentos fallidos"));
        System.out.println(aplicarColor(ROJO, "Contacte al administrador del sistema"));
        System.out.println("=".repeat(60));
        System.out.println();
    }
    
    /**
     * Muestra información sobre el intento actual
     */
    public void mostrarIntento(int intento, int maxIntentos) {
        System.out.println(aplicarColor(AMARILLO, "INTENTO " + intento + " de " + maxIntentos));
        System.out.println();
    }
    
    /**
     * Cierra los recursos
     */
    public void cerrar() {
        if (scanner != null) {
            scanner.close();
        }
    }
}


