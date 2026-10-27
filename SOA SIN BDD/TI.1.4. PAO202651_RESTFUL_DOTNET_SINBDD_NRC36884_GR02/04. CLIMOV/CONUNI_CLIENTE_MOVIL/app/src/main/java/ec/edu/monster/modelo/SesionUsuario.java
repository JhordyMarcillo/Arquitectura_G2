package ec.edu.monster.modelo;

public class SesionUsuario {
    private static String usuario;
    private static String token;

    private SesionUsuario() {
    }

    public static void iniciar(String nombreUsuario, String nuevoToken) {
        usuario = nombreUsuario;
        token = nuevoToken;
    }

    public static void cerrar() {
        usuario = null;
        token = null;
    }

    public static boolean estaActiva() {
        return token != null && !token.isEmpty();
    }

    public static String getUsuario() {
        return usuario;
    }

    public static String getToken() {
        return token;
    }
}
