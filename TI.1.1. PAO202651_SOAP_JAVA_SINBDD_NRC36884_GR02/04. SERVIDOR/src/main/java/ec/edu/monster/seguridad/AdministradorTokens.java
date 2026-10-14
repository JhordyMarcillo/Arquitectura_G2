package ec.edu.monster.seguridad;

import ec.edu.monster.modelos.utilidades.enums.EstadoToken;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AdministradorTokens {

    private static final long MINUTOS_EXPIRACION = 5;

    private static final Map<String, Instant> tokensActivos = new ConcurrentHashMap<>();

    public static String generarToken() {

        Instant ahora = Instant.now();
        tokensActivos.values().removeIf(expiracion -> ahora.isAfter(expiracion));

        String token = UUID.randomUUID().toString();
        tokensActivos.put(token, ahora.plus(Duration.ofMinutes(MINUTOS_EXPIRACION)));
        return token;
    }

    public static EstadoToken validarToken(String token) {
        if (token == null || token.isBlank()) {
            return EstadoToken.INVALIDO;
        }

        String tokenLimpio = token.trim();
        Instant expiracion = tokensActivos.get(tokenLimpio);

        if (expiracion == null) {
            return EstadoToken.INVALIDO;
        }
        if (Instant.now().isAfter(expiracion)) {
            tokensActivos.remove(tokenLimpio);
            return EstadoToken.EXPIRADO;
        }
        return EstadoToken.VALIDO;
    }

    public static long getMinutosExpiracion() {
        return MINUTOS_EXPIRACION;
    }
}
