package ec.edu.monster.seguridad;

import ec.edu.monster.modelos.utilidades.enums.EstadoToken;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AdministradorTokens {

    public static final int MINUTOS_EXPIRACION = 60;

    private static final Map<String, SesionEmpleado> SESIONES = new ConcurrentHashMap<>();
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private AdministradorTokens() {
    }

    public static String generarToken(String codigoEmpleado, String nombreEmpleado) {
        byte[] bytes = new byte[24];
        ALEATORIO.nextBytes(bytes);
        String token = HexFormat.of().formatHex(bytes);
        SESIONES.put(token, new SesionEmpleado(codigoEmpleado, nombreEmpleado,
                LocalDateTime.now().plusMinutes(MINUTOS_EXPIRACION)));
        return token;
    }

    public static EstadoToken validar(String token) {
        if (token == null || token.isBlank() || !SESIONES.containsKey(token)) {
            return EstadoToken.INVALIDO;
        }
        if (SESIONES.get(token).expiracion().isBefore(LocalDateTime.now())) {
            SESIONES.remove(token);
            return EstadoToken.EXPIRADO;
        }
        return EstadoToken.VALIDO;
    }

    public static SesionEmpleado obtenerSesion(String token) {
        return SESIONES.get(token);
    }
}
