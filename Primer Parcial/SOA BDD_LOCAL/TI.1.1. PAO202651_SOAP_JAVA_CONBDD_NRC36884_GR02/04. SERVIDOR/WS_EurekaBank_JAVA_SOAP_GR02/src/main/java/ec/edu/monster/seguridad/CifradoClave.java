package ec.edu.monster.seguridad;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/** Equivalente a SHA() de MySQL: SHA1 en hexadecimal en minusculas. */
public final class CifradoClave {

    private CifradoClave() {
    }

    public static String sha1(String texto) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-1").digest(texto.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 no disponible", e);
        }
    }
}
