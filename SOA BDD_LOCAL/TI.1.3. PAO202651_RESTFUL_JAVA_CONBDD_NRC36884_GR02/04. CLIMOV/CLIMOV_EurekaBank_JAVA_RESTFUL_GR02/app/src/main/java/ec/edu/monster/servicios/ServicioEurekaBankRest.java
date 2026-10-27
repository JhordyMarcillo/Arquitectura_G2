package ec.edu.monster.servicios;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import ec.edu.monster.modelos.ExcepcionServicio;
import ec.edu.monster.modelos.Respuesta;
import ec.edu.monster.modelos.RespuestaAutenticacion;
import ec.edu.monster.modelos.RespuestaCuentas;
import ec.edu.monster.modelos.RespuestaMovimientos;
import ec.edu.monster.modelos.RespuestaOperacion;
import ec.edu.monster.modelos.SolicitudDeposito;
import ec.edu.monster.modelos.SolicitudLogin;
import ec.edu.monster.modelos.SolicitudRetiro;
import ec.edu.monster.modelos.SolicitudTransferencia;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Proxy del servicio REST EurekaBank. Usa HttpURLConnection y Gson para funcionar igual
 * en la JVM (consola, escritorio, web) y en Android. Sirve para el servidor Java y el .NET.
 */
public class ServicioEurekaBankRest implements IServicioEurekaBank {

    public static final String URL_POR_DEFECTO = "http://localhost:8080/WS_EurekaBank_JAVA_RESTFUL_GR02/api/eurekabank";
    private static final int TIEMPO_ESPERA_MS = 15000;

    private final Gson gson = new Gson();
    private String urlServidor = URL_POR_DEFECTO;
    private RespuestaAutenticacion sesion;

    @Override
    public String getUrlServidor() {
        return urlServidor;
    }

    @Override
    public void setUrlServidor(String url) {
        String limpia = url == null ? "" : url.trim();
        urlServidor = limpia.endsWith("/") ? limpia.substring(0, limpia.length() - 1) : limpia;
    }

    @Override
    public RespuestaAutenticacion getSesion() {
        return sesion;
    }

    @Override
    public RespuestaAutenticacion iniciarSesion(String usuario, String clave) throws ExcepcionServicio {
        sesion = null;
        SolicitudLogin solicitud = new SolicitudLogin();
        solicitud.setUsuario(usuario);
        solicitud.setClave(clave);
        sesion = enviar("POST", "/login", solicitud, RespuestaAutenticacion.class);
        return sesion;
    }

    @Override
    public void cerrarSesion() {
        sesion = null;
    }

    @Override
    public void restaurarSesion(RespuestaAutenticacion sesionGuardada) {
        sesion = sesionGuardada;
    }

    @Override
    public RespuestaCuentas listarCuentas() throws ExcepcionServicio {
        return enviar("GET", "/cuentas", null, RespuestaCuentas.class);
    }

    @Override
    public RespuestaMovimientos consultarMovimientos(String cuenta) throws ExcepcionServicio {
        try {
            String codigo = URLEncoder.encode(cuenta.trim(), "UTF-8");
            return enviar("GET", "/cuentas/" + codigo + "/movimientos", null, RespuestaMovimientos.class);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public RespuestaOperacion depositar(String cuenta, BigDecimal importe) throws ExcepcionServicio {
        SolicitudDeposito solicitud = new SolicitudDeposito();
        solicitud.setCuenta(cuenta);
        solicitud.setImporte(importe);
        return enviar("POST", "/depositos", solicitud, RespuestaOperacion.class);
    }

    @Override
    public RespuestaOperacion retirar(String cuenta, String clave, BigDecimal importe) throws ExcepcionServicio {
        SolicitudRetiro solicitud = new SolicitudRetiro();
        solicitud.setCuenta(cuenta);
        solicitud.setClave(clave);
        solicitud.setImporte(importe);
        return enviar("POST", "/retiros", solicitud, RespuestaOperacion.class);
    }

    @Override
    public RespuestaOperacion transferir(String cuentaOrigen, String claveOrigen, String cuentaDestino, BigDecimal importe)
            throws ExcepcionServicio {
        SolicitudTransferencia solicitud = new SolicitudTransferencia();
        solicitud.setCuentaOrigen(cuentaOrigen);
        solicitud.setClaveOrigen(claveOrigen);
        solicitud.setCuentaDestino(cuentaDestino);
        solicitud.setImporte(importe);
        return enviar("POST", "/transferencias", solicitud, RespuestaOperacion.class);
    }

    // ------------------------------------------------------------------ comunicacion

    private <T extends Respuesta> T enviar(String metodo, String ruta, Object cuerpo, Class<T> tipo) throws ExcepcionServicio {
        HttpURLConnection conexion = null;
        String texto;
        int estadoHttp;
        try {
            conexion = (HttpURLConnection) new URL(urlServidor + ruta).openConnection();
            conexion.setRequestMethod(metodo);
            conexion.setConnectTimeout(TIEMPO_ESPERA_MS);
            conexion.setReadTimeout(TIEMPO_ESPERA_MS);
            conexion.setRequestProperty("Accept", "application/json");
            if (sesion != null) {
                conexion.setRequestProperty("Authorization", "Bearer " + sesion.getToken());
            }
            if (cuerpo != null) {
                conexion.setDoOutput(true);
                conexion.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                try (OutputStream salida = conexion.getOutputStream()) {
                    salida.write(gson.toJson(cuerpo).getBytes(StandardCharsets.UTF_8));
                }
            }
            estadoHttp = conexion.getResponseCode();
            InputStream entrada = estadoHttp >= 400 ? conexion.getErrorStream() : conexion.getInputStream();
            texto = entrada == null ? "" : leer(entrada);
        } catch (IOException | IllegalArgumentException e) {
            throw new ExcepcionServicio(ExcepcionServicio.SIN_CONEXION, "No se pudo conectar con el servidor " + urlServidor, e);
        } finally {
            if (conexion != null) {
                conexion.disconnect();
            }
        }

        T respuesta;
        try {
            respuesta = gson.fromJson(texto, tipo);
        } catch (JsonParseException e) {
            throw new ExcepcionServicio(ExcepcionServicio.SIN_CONEXION,
                    "El servidor respondio HTTP " + estadoHttp + " sin el formato esperado. Revise la URL.", e);
        }
        if (respuesta == null || !"OK".equals(respuesta.getEstado())) {
            String codigo = respuesta == null || respuesta.getCodigo() == null ? "SRV-001" : respuesta.getCodigo();
            if ("AUTH-001".equals(codigo) || "AUTH-002".equals(codigo)) {
                sesion = null;
            }
            String mensaje = respuesta == null || respuesta.getMensaje() == null
                    ? "Respuesta vacia del servidor (HTTP " + estadoHttp + ")" : respuesta.getMensaje();
            throw new ExcepcionServicio(codigo, mensaje);
        }
        return respuesta;
    }

    private static String leer(InputStream entrada) throws IOException {
        try (InputStream flujo = entrada) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] bloque = new byte[4096];
            int leidos;
            while ((leidos = flujo.read(bloque)) != -1) {
                bytes.write(bloque, 0, leidos);
            }
            return bytes.toString("UTF-8");
        }
    }
}
