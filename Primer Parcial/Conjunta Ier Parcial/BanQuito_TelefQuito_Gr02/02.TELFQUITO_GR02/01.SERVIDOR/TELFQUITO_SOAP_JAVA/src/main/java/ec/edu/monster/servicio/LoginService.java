package ec.edu.monster.servicio;

public class LoginService {

    private static final String USUARIO = "Monster";
    private static final String CLAVE = "Monster9";
    // SHA-256 de "Monster9": lo envian los clientes que cifran la clave (CLIESC y CLIMOV)
    private static final String CLAVE_SHA256 = "7fbdfcdd431cb77a5bce52182f863d807544fdbea2edb49dc396d5ba30481f64";

    public boolean login(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        return username.equalsIgnoreCase(USUARIO)
                && (password.equals(CLAVE) || password.equalsIgnoreCase(CLAVE_SHA256));
    }
}
