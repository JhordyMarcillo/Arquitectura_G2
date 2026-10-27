package ec.edu.monster.seguridad;

public final class AdministradorCredenciales {

    private static final String USUARIO = "MONSTER9";
    private static final String CONTRASENIA = "MONSTER9";

    private AdministradorCredenciales() {
    }

    public static boolean validarCredenciales(String usuario, String contraseniaIngresada) {
        return USUARIO.equals(usuario) && CONTRASENIA.equals(contraseniaIngresada);
    }
}
