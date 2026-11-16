package ec.edu.monster.servicios;

import ec.edu.monster.modelos.DatosCuenta;
import ec.edu.monster.modelos.DatosMovimiento;
import ec.edu.monster.modelos.ExcepcionServicio;
import ec.edu.monster.modelos.Respuesta;
import ec.edu.monster.modelos.RespuestaAutenticacion;
import ec.edu.monster.modelos.RespuestaCuentas;
import ec.edu.monster.modelos.RespuestaMovimientos;
import ec.edu.monster.modelos.RespuestaOperacion;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Proxy del servicio SOAP EurekaBank escrito a mano, porque Android no trae JAX-WS ni JAXB.
 * Arma el sobre SOAP 1.1 (document/literal wrapped, espacio de nombres http://ws.monster.edu.ec/),
 * lo envia con HttpURLConnection y lee la respuesta con XmlPullParser, que ya viene en Android.
 * El XML es el mismo del contrato, asi que sirve para el servidor Java (TI.1.1) y el .NET (TI.1.2).
 */
public class ServicioEurekaBankSoap implements IServicioEurekaBank {

    public static final String URL_POR_DEFECTO = "http://localhost:8080/WS_EurekaBank_JAVA_SOAP_GR02/WSEurekaBank";
    private static final String ESPACIO_NOMBRES = "http://ws.monster.edu.ec/";
    private static final String NS_XSI = "http://www.w3.org/2001/XMLSchema-instance";
    private static final int TIEMPO_ESPERA_MS = 15000;

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
        Nodo r = llamar("iniciarSesion", "usuario", usuario, "clave", clave);
        RespuestaAutenticacion respuesta = encabezado(new RespuestaAutenticacion(), r);
        respuesta.setToken(r.texto("token"));
        respuesta.setExpiracion(r.texto("expiracion"));
        respuesta.setCodigoEmpleado(r.texto("codigoEmpleado"));
        respuesta.setNombreEmpleado(r.texto("nombreEmpleado"));
        sesion = respuesta;
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
        Nodo r = llamar("listarCuentas", "token", token());
        RespuestaCuentas respuesta = encabezado(new RespuestaCuentas(), r);
        List<DatosCuenta> cuentas = new ArrayList<>();
        for (Nodo n : r.lista("cuentas")) {
            DatosCuenta c = new DatosCuenta();
            c.setCodigo(n.texto("codigo"));
            c.setCliente(n.texto("cliente"));
            c.setMoneda(n.texto("moneda"));
            c.setSaldo(n.decimal("saldo"));
            c.setEstado(n.texto("estado"));
            cuentas.add(c);
        }
        respuesta.setCuentas(cuentas);
        return respuesta;
    }

    @Override
    public RespuestaMovimientos consultarMovimientos(String cuenta) throws ExcepcionServicio {
        Nodo r = llamar("consultarMovimientos", "token", token(), "cuenta", cuenta.trim());
        RespuestaMovimientos respuesta = encabezado(new RespuestaMovimientos(), r);
        respuesta.setCuenta(r.texto("cuenta"));
        respuesta.setMoneda(r.texto("moneda"));
        respuesta.setSaldo(r.decimal("saldo"));
        List<DatosMovimiento> movimientos = new ArrayList<>();
        for (Nodo n : r.lista("movimientos")) {
            DatosMovimiento m = new DatosMovimiento();
            String numero = n.texto("numero");
            m.setNumero(numero == null ? 0 : Integer.parseInt(numero));
            m.setFecha(n.texto("fecha"));
            m.setTipo(n.texto("tipo"));
            m.setDescripcion(n.texto("descripcion"));
            m.setAccion(n.texto("accion"));
            m.setImporte(n.decimal("importe"));
            m.setCuentaReferencia(n.texto("cuentaReferencia"));
            movimientos.add(m);
        }
        respuesta.setMovimientos(movimientos);
        return respuesta;
    }

    @Override
    public RespuestaOperacion depositar(String cuenta, BigDecimal importe) throws ExcepcionServicio {
        return operacion(llamar("registrarDeposito", "token", token(), "cuenta", cuenta, "importe", importe.toPlainString()));
    }

    @Override
    public RespuestaOperacion retirar(String cuenta, String clave, BigDecimal importe) throws ExcepcionServicio {
        return operacion(llamar("registrarRetiro", "token", token(), "cuenta", cuenta, "clave", clave,
                "importe", importe.toPlainString()));
    }

    @Override
    public RespuestaOperacion transferir(String cuentaOrigen, String claveOrigen, String cuentaDestino, BigDecimal importe)
            throws ExcepcionServicio {
        return operacion(llamar("registrarTransferencia", "token", token(), "cuentaOrigen", cuentaOrigen,
                "claveOrigen", claveOrigen, "cuentaDestino", cuentaDestino, "importe", importe.toPlainString()));
    }

    // ------------------------------------------------------------------ mapeo

    private static RespuestaOperacion operacion(Nodo r) {
        RespuestaOperacion respuesta = encabezado(new RespuestaOperacion(), r);
        respuesta.setCuenta(r.texto("cuenta"));
        respuesta.setSaldo(r.decimal("saldo"));
        return respuesta;
    }

    private static <T extends Respuesta> T encabezado(T respuesta, Nodo r) {
        respuesta.setEstado(r.texto("estado"));
        respuesta.setCodigo(r.texto("codigo"));
        respuesta.setMensaje(r.texto("mensaje"));
        return respuesta;
    }

    private String token() {
        return sesion == null || sesion.getToken() == null ? "" : sesion.getToken();
    }

    // ------------------------------------------------------------------ comunicacion SOAP

    /**
     * Envia la operacion con sus parametros (pares nombre, valor) y devuelve el elemento
     * {operacion}Result. Si el estado es ERROR o falla la red, lanza ExcepcionServicio.
     */
    private Nodo llamar(String operacion, String... parametros) throws ExcepcionServicio {
        String sobre = sobre(operacion, parametros);
        HttpURLConnection conexion = null;
        Nodo resultado;
        try {
            conexion = (HttpURLConnection) new URL(urlServidor).openConnection();
            conexion.setRequestMethod("POST");
            conexion.setConnectTimeout(TIEMPO_ESPERA_MS);
            conexion.setReadTimeout(TIEMPO_ESPERA_MS);
            conexion.setDoOutput(true);
            conexion.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            conexion.setRequestProperty("SOAPAction", "\"" + ESPACIO_NOMBRES + "WSEurekaBank/" + operacion + "\"");
            try (OutputStream salida = conexion.getOutputStream()) {
                salida.write(sobre.getBytes(StandardCharsets.UTF_8));
            }
            int estadoHttp = conexion.getResponseCode();
            InputStream entrada = estadoHttp >= 400 ? conexion.getErrorStream() : conexion.getInputStream();
            if (entrada == null) {
                throw new ExcepcionServicio(ExcepcionServicio.SIN_CONEXION,
                        "El servidor respondio HTTP " + estadoHttp + " sin contenido. Revise la URL.");
            }
            try (InputStream flujo = entrada) {
                resultado = leerResultado(flujo, operacion + "Result");
            }
        } catch (IOException | IllegalArgumentException e) {
            throw new ExcepcionServicio(ExcepcionServicio.SIN_CONEXION, "No se pudo conectar con el servidor " + urlServidor, e);
        } catch (XmlPullParserException e) {
            throw new ExcepcionServicio(ExcepcionServicio.SIN_CONEXION,
                    "El servidor no respondio con un mensaje SOAP valido. Revise la URL.", e);
        } finally {
            if (conexion != null) {
                conexion.disconnect();
            }
        }

        if (!"OK".equals(resultado.texto("estado"))) {
            String codigo = resultado.texto("codigo") == null ? "SRV-001" : resultado.texto("codigo");
            if ("AUTH-001".equals(codigo) || "AUTH-002".equals(codigo)) {
                sesion = null;
            }
            String mensaje = resultado.texto("mensaje") == null ? "Respuesta vacia del servidor" : resultado.texto("mensaje");
            throw new ExcepcionServicio(codigo, mensaje);
        }
        return resultado;
    }

    /** Sobre SOAP 1.1: <operacion xmlns="http://ws.monster.edu.ec/"><param>valor</param>...</operacion>. */
    static String sobre(String operacion, String... parametros) {
        StringBuilder xml = new StringBuilder()
                .append("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                .append("<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"><s:Body>")
                .append('<').append(operacion).append(" xmlns=\"").append(ESPACIO_NOMBRES).append("\">");
        for (int i = 0; i + 1 < parametros.length; i += 2) {
            xml.append('<').append(parametros[i]).append('>')
                    .append(escapar(parametros[i + 1]))
                    .append("</").append(parametros[i]).append('>');
        }
        return xml.append("</").append(operacion).append("></s:Body></s:Envelope>").toString();
    }

    private static String escapar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }

    /** Busca el elemento resultado (o un Fault) en la respuesta y lo convierte en un arbol de Nodo. */
    static Nodo leerResultado(InputStream entrada, String elementoResultado) throws XmlPullParserException, IOException, ExcepcionServicio {
        XmlPullParserFactory fabrica = XmlPullParserFactory.newInstance();
        fabrica.setNamespaceAware(true);
        XmlPullParser lector = fabrica.newPullParser();
        lector.setInput(entrada, null);

        for (int evento = lector.getEventType(); evento != XmlPullParser.END_DOCUMENT; evento = lector.next()) {
            if (evento != XmlPullParser.START_TAG) {
                continue;
            }
            if (elementoResultado.equals(lector.getName())) {
                return leerNodo(lector);
            }
            if ("Fault".equals(lector.getName())) {
                Nodo falla = leerNodo(lector);
                String detalle = falla.texto("faultstring");
                throw new ExcepcionServicio("SRV-001", "Falla SOAP del servidor: " + (detalle == null ? "sin detalle" : detalle));
            }
        }
        throw new XmlPullParserException("No se encontro " + elementoResultado + " en la respuesta");
    }

    /** Lee el elemento actual y sus hijos. Los elementos con xsi:nil="true" quedan con texto null. */
    private static Nodo leerNodo(XmlPullParser lector) throws XmlPullParserException, IOException {
        Nodo nodo = new Nodo(lector.getName());
        boolean nulo = "true".equals(lector.getAttributeValue(NS_XSI, "nil"));
        StringBuilder texto = new StringBuilder();
        while (true) {
            int evento = lector.next();
            if (evento == XmlPullParser.START_TAG) {
                nodo.hijos.add(leerNodo(lector));
            } else if (evento == XmlPullParser.TEXT) {
                texto.append(lector.getText());
            } else if (evento == XmlPullParser.END_TAG || evento == XmlPullParser.END_DOCUMENT) {
                break;
            }
        }
        nodo.texto = nulo ? null : texto.toString().trim();
        return nodo;
    }

    /** Elemento XML simplificado: nombre local, texto e hijos. */
    static final class Nodo {

        final String nombre;
        String texto;
        final List<Nodo> hijos = new ArrayList<>();

        Nodo(String nombre) {
            this.nombre = nombre;
        }

        Nodo hijo(String nombreHijo) {
            for (Nodo h : hijos) {
                if (h.nombre.equals(nombreHijo)) {
                    return h;
                }
            }
            return null;
        }

        String texto(String nombreHijo) {
            Nodo h = hijo(nombreHijo);
            return h == null ? null : h.texto;
        }

        BigDecimal decimal(String nombreHijo) {
            String valor = texto(nombreHijo);
            return valor == null || valor.isEmpty() ? null : new BigDecimal(valor);
        }

        /**
         * Elementos de una lista. Admite listas envueltas (<cuentas><DatosCuenta/>...</cuentas>,
         * como WCF y el servidor Java) o repetidas (<cuentas/><cuentas/>).
         */
        List<Nodo> lista(String nombreLista) {
            List<Nodo> repetidos = new ArrayList<>();
            for (Nodo h : hijos) {
                if (h.nombre.equals(nombreLista)) {
                    repetidos.add(h);
                }
            }
            if (repetidos.size() == 1) {
                // Envuelta si esta vacia o si sus hijos son elementos con campos (no campos con texto)
                Nodo unico = repetidos.get(0);
                if (unico.hijos.isEmpty() || !unico.hijos.get(0).hijos.isEmpty()) {
                    return unico.hijos;
                }
            }
            return repetidos;
        }
    }
}
