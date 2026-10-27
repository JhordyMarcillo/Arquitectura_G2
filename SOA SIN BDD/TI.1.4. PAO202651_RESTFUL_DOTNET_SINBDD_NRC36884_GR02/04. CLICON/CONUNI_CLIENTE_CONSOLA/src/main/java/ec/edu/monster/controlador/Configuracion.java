package ec.edu.monster.controlador;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public final class Configuracion {

    private static final String URL_RED = "http://192.168.137.1:5014/api/conversor";
    private static final String URL_LOCAL = "http://localhost:5014/api/conversor";

    // URL de la API. Se puede fijar al ejecutar con:
    //   java -Dconuni.url=http://localhost:5014/api/conversor -jar ...
    // Si no se fija, usa la red compartida (192.168.137.1) y, si ahi no
    // responde nadie, el servidor de la misma PC (localhost).
    public static final String URL_BASE = System.getProperty("conuni.url", elegirUrl());

    public static final int TIMEOUT_MS = 10000;

    // Decimales maximos al mostrar el resultado (77.0 -> "77", 0.621371 -> "0.6214")
    public static final int DECIMALES = 4;

    // El taller no pide cerrar sesion. En true aparece la opcion/boton y
    // solo borra el token en el cliente (el servidor no tiene logout).
    public static final boolean PERMITIR_CERRAR_SESION = false;

    private Configuracion() {
    }

    private static String elegirUrl() {
        if (System.getProperty("conuni.url") != null) {
            return null;
        }
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("192.168.137.1", 5014), 1500);
            return URL_RED;
        } catch (IOException e) {
            return URL_LOCAL;
        }
    }
}
