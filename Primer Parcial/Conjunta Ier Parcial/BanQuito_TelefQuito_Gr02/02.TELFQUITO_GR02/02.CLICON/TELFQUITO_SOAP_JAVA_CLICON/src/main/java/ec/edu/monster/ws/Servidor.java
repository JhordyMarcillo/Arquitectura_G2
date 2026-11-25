package ec.edu.monster.ws;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Direccion del servidor TelefQuito. Se toma, en este orden, de:
 * -Dservidor.ip=..., la variable de entorno SERVIDOR_IP o "localhost".
 * Acepta solo la IP (se usa el puerto 8080) o "ip:puerto".
 */
public final class Servidor {

    private Servidor() {
    }

    public static String host() {
        String ip = System.getProperty("servidor.ip");
        if (ip == null || ip.isBlank()) {
            ip = System.getenv("SERVIDOR_IP");
        }
        if (ip == null || ip.isBlank()) {
            ip = "localhost";
        }
        ip = ip.trim();
        return ip.contains(":") ? ip : ip + ":8080";
    }

    public static URL wsdl(String servicio) {
        try {
            return URI.create("http://" + host() + "/TELFQUITO_SOAP_JAVA/" + servicio + "?wsdl").toURL();
        } catch (MalformedURLException | IllegalArgumentException e) {
            throw new IllegalStateException("IP del servidor no valida: " + host(), e);
        }
    }
}
