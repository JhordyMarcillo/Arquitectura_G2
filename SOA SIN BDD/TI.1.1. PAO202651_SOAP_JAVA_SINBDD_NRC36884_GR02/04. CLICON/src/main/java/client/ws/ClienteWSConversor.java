package client.ws;

import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;

public class ClienteWSConversor {

    private static final String ESPACIO_NOMBRES = "http://ws.controladores.monster.edu.ec/";
    private static final String PROPIEDAD_BASE_WS = "ws.base.url";
    private static final String VARIABLE_BASE_WS = "WS_BASE_URL";

    private static final QName QNOMBRE_SERVICIO = new QName(ESPACIO_NOMBRES, "WSConversorUnidades");
    private static final QName QNOMBRE_PUERTO = new QName(ESPACIO_NOMBRES, "WSConversorUnidadesPort");

    private static final List<String> BASES_URL_POR_DEFECTO = List.of(
            "http://localhost:8080/WS_ConUni_Java_GR02"
    );

    private final ContratoWSConversor puerto;
    private String tokenSesion;
    private String endpointActivo;

    public ClienteWSConversor() {
        this.puerto = inicializarPuerto();
    }

    private ContratoWSConversor inicializarPuerto() {
        List<String> basesUrl = construirBasesUrl();
        List<String> wsdlsIntentados = new ArrayList<>();

        for (String baseUrl : basesUrl) {
            String endpoint = baseUrl + "/WSConversorUnidades";
            List<String> wsdlsCandidatos = List.of(
                    endpoint + "?WSDL",
                    endpoint + "?wsdl"
            );

            for (String wsdl : wsdlsCandidatos) {
                try {
                    Service servicio = Service.create(new URL(wsdl), QNOMBRE_SERVICIO);
                    ContratoWSConversor puertoTemporal = servicio.getPort(QNOMBRE_PUERTO, ContratoWSConversor.class);

                    BindingProvider proveedor = (BindingProvider) puertoTemporal;
                    proveedor.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

                    this.endpointActivo = endpoint;
                    return puertoTemporal;
                } catch (Exception ex) {
                    wsdlsIntentados.add(wsdl);
                }
            }
        }

        throw new IllegalStateException(
                "No fue posible inicializar el cliente SOAP. "
                + "No se encontro el WSDL en ninguna URL candidata: " + String.join(", ", wsdlsIntentados)
                + ". Verifique que el servidor SOAP (WS_ConUni_Java_GR02) este desplegado y activo en Payara."
        );
    }

    private List<String> construirBasesUrl() {
        List<String> basesUrl = new ArrayList<>();

        String baseConfigurada = obtenerBaseConfigurada();
        if (baseConfigurada != null && !baseConfigurada.isBlank()) {
            basesUrl.add(normalizarBase(baseConfigurada));
        }

        for (String baseDefecto : BASES_URL_POR_DEFECTO) {
            if (!basesUrl.contains(baseDefecto)) {
                basesUrl.add(baseDefecto);
            }
        }

        return basesUrl;
    }

    private String obtenerBaseConfigurada() {
        String desdePropiedad = System.getProperty(PROPIEDAD_BASE_WS);
        if (desdePropiedad != null && !desdePropiedad.isBlank()) {
            return desdePropiedad;
        }

        String desdeVariable = System.getenv(VARIABLE_BASE_WS);
        if (desdeVariable != null && !desdeVariable.isBlank()) {
            return desdeVariable;
        }

        return null;
    }

    private String normalizarBase(String base) {
        String baseNormalizada = base.trim();
        while (baseNormalizada.endsWith("/")) {
            baseNormalizada = baseNormalizada.substring(0, baseNormalizada.length() - 1);
        }
        return baseNormalizada;
    }

    public String login(String usuario, String contrasenia) {
        RespuestaAutenticacion respuesta = puerto.autenticar(usuario, contrasenia);
        if (respuesta == null || !"OK".equals(respuesta.getEstado())) {
            throw new IllegalStateException(respuesta == null || respuesta.getMensaje() == null
                    ? "El servidor no devolvio respuesta de autenticacion"
                    : respuesta.getMensaje());
        }
        this.tokenSesion = respuesta.getToken();
        return this.tokenSesion;
    }

    private double extraerResultado(RespuestaConversion respuesta) {
        if (respuesta == null || !"OK".equals(respuesta.getEstado()) || respuesta.getResultado() == null) {
            throw new IllegalStateException(respuesta == null || respuesta.getMensaje() == null
                    ? "El servidor no devolvio respuesta de conversion"
                    : respuesta.getMensaje());
        }
        return respuesta.getResultado();
    }

    private String tokenActivo() {
        if (tokenSesion == null || tokenSesion.isBlank()) {
            throw new IllegalStateException("No hay sesion activa. Debe autenticarse primero.");
        }
        return tokenSesion;
    }

    public double convertirLongitud(double valor, String unidadInicial, String unidadFinal) {
        return extraerResultado(puerto.convertirLongitud(tokenActivo(), unidadInicial, unidadFinal, valor));
    }

    public double convertirMasa(double valor, String unidadInicial, String unidadFinal) {
        return extraerResultado(puerto.convertirMasa(tokenActivo(), unidadInicial, unidadFinal, valor));
    }

    public double convertirTemperatura(double valor, String unidadInicial, String unidadFinal) {
        return extraerResultado(puerto.convertirTemperatura(tokenActivo(), unidadInicial, unidadFinal, valor));
    }

    public String getTokenSesion() {
        return tokenSesion;
    }

    public String getEndpointActivo() {
        return endpointActivo;
    }

    public void cerrarSesion() {
        this.tokenSesion = null;
    }
}
