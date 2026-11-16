package ec.edu.monster.servicios;

import ec.edu.monster.modelos.ExcepcionServicio;
import ec.edu.monster.modelos.Respuesta;
import ec.edu.monster.modelos.RespuestaAutenticacion;
import ec.edu.monster.modelos.RespuestaCuentas;
import ec.edu.monster.modelos.RespuestaMovimientos;
import ec.edu.monster.modelos.RespuestaOperacion;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebServiceException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.function.Function;
import javax.xml.namespace.QName;

/**
 * Proxy del servicio SOAP EurekaBank con JAX-WS (Metro), creado a partir del WSDL del
 * servidor y de ContratoWSEurekaBank. Si la URL cambia en el inicio de sesion, el proxy
 * se vuelve a crear.
 */
public class ServicioEurekaBankSoap implements IServicioEurekaBank {

    public static final String URL_POR_DEFECTO = "http://localhost:8080/WS_EurekaBank_JAVA_SOAP_GR02/WSEurekaBank";
    private static final int TIEMPO_ESPERA_MS = 15000;
    private static final QName SERVICIO = new QName(ContratoWSEurekaBank.ESPACIO_NOMBRES, "WSEurekaBank");

    private String urlServidor = URL_POR_DEFECTO;
    private ContratoWSEurekaBank puerto;
    private RespuestaAutenticacion sesion;

    @Override
    public String getUrlServidor() {
        return urlServidor;
    }

    @Override
    public void setUrlServidor(String url) {
        String limpia = url == null ? "" : url.trim();
        urlServidor = limpia.endsWith("/") ? limpia.substring(0, limpia.length() - 1) : limpia;
        puerto = null;
    }

    @Override
    public RespuestaAutenticacion getSesion() {
        return sesion;
    }

    @Override
    public RespuestaAutenticacion iniciarSesion(String usuario, String clave) throws ExcepcionServicio {
        sesion = null;
        sesion = llamar(p -> p.iniciarSesion(usuario, clave));
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
        String token = token();
        return llamar(p -> p.listarCuentas(token));
    }

    @Override
    public RespuestaMovimientos consultarMovimientos(String cuenta) throws ExcepcionServicio {
        String token = token();
        return llamar(p -> p.consultarMovimientos(token, cuenta.trim()));
    }

    @Override
    public RespuestaOperacion depositar(String cuenta, BigDecimal importe) throws ExcepcionServicio {
        String token = token();
        return llamar(p -> p.registrarDeposito(token, cuenta, importe));
    }

    @Override
    public RespuestaOperacion retirar(String cuenta, String clave, BigDecimal importe) throws ExcepcionServicio {
        String token = token();
        return llamar(p -> p.registrarRetiro(token, cuenta, clave, importe));
    }

    @Override
    public RespuestaOperacion transferir(String cuentaOrigen, String claveOrigen, String cuentaDestino, BigDecimal importe)
            throws ExcepcionServicio {
        String token = token();
        return llamar(p -> p.registrarTransferencia(token, cuentaOrigen, claveOrigen, cuentaDestino, importe));
    }

    // ------------------------------------------------------------------ comunicacion

    private String token() {
        return sesion == null || sesion.getToken() == null ? "" : sesion.getToken();
    }

    /** Invoca la operacion y convierte estado ERROR o fallas de red en ExcepcionServicio. */
    private <T extends Respuesta> T llamar(Function<ContratoWSEurekaBank, T> operacion) throws ExcepcionServicio {
        T respuesta;
        try {
            respuesta = operacion.apply(puerto());
        } catch (WebServiceException | IllegalArgumentException | MalformedURLException e) {
            puerto = null;
            throw new ExcepcionServicio(ExcepcionServicio.SIN_CONEXION, "No se pudo conectar con el servidor " + urlServidor, e);
        }

        if (respuesta == null || !"OK".equals(respuesta.getEstado())) {
            String codigo = respuesta == null || respuesta.getCodigo() == null ? "SRV-001" : respuesta.getCodigo();
            if ("AUTH-001".equals(codigo) || "AUTH-002".equals(codigo)) {
                sesion = null;
            }
            String mensaje = respuesta == null || respuesta.getMensaje() == null
                    ? "Respuesta vacia del servidor" : respuesta.getMensaje();
            throw new ExcepcionServicio(codigo, mensaje);
        }
        return respuesta;
    }

    /** Descarga el WSDL una sola vez por URL y deja el proxy listo para las siguientes llamadas. */
    private ContratoWSEurekaBank puerto() throws MalformedURLException {
        if (puerto == null) {
            Service servicio = Service.create(new URL(urlServidor + "?wsdl"), SERVICIO);
            ContratoWSEurekaBank nuevo = servicio.getPort(ContratoWSEurekaBank.class);
            Map<String, Object> contexto = ((BindingProvider) nuevo).getRequestContext();
            contexto.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, urlServidor);
            contexto.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
            contexto.put("com.sun.xml.ws.connect.timeout", TIEMPO_ESPERA_MS);
            contexto.put("com.sun.xml.ws.request.timeout", TIEMPO_ESPERA_MS);
            puerto = nuevo;
        }
        return puerto;
    }
}
