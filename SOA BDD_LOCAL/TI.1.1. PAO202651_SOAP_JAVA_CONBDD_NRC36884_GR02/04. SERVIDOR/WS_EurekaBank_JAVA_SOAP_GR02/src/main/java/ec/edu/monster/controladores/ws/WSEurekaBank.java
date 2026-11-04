package ec.edu.monster.controladores.ws;

import ec.edu.monster.controladores.manejadoresws.ManejadorAutenticacion;
import ec.edu.monster.modelos.utilidades.EspacioNombres;
import ec.edu.monster.modelos.utilidades.ExcepcionNegocio;
import ec.edu.monster.modelos.utilidades.FabricaRespuesta;
import ec.edu.monster.modelos.utilidades.IServicioOperaciones;
import ec.edu.monster.modelos.utilidades.Respuesta;
import ec.edu.monster.modelos.utilidades.RespuestaAutenticacion;
import ec.edu.monster.modelos.utilidades.RespuestaCuentas;
import ec.edu.monster.modelos.utilidades.RespuestaMovimientos;
import ec.edu.monster.modelos.utilidades.RespuestaOperacion;
import ec.edu.monster.modelos.utilidades.SolicitudDeposito;
import ec.edu.monster.modelos.utilidades.SolicitudLogin;
import ec.edu.monster.modelos.utilidades.SolicitudRetiro;
import ec.edu.monster.modelos.utilidades.SolicitudTransferencia;
import ec.edu.monster.modelos.utilidades.enums.CodigoRespuesta;
import ec.edu.monster.modelos.utilidades.enums.EstadoToken;
import ec.edu.monster.seguridad.AdministradorTokens;
import ec.edu.monster.seguridad.SesionEmpleado;
import ec.edu.monster.servicios.ServicioAutenticacion;
import ec.edu.monster.servicios.ServicioCuentas;
import ec.edu.monster.servicios.ServicioOperaciones;
import jakarta.annotation.Resource;
import jakarta.jws.HandlerChain;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio SOAP EurekaBank (document/literal wrapped). Publica las mismas operaciones,
 * nombres y XML que el servicio WCF del TI.1.2 (CONTRATO_EUREKABANK_GR02.md, seccion 4).
 * Los errores de negocio no lanzan fault: vuelven con estado ERROR y su codigo.
 */
@WebService(serviceName = "WSEurekaBank", name = "WSEurekaBank", portName = "WSEurekaBankPort",
        targetNamespace = EspacioNombres.WS)
@HandlerChain(file = "/manejador.xml")
public class WSEurekaBank {

    private static final Logger REGISTRO = Logger.getLogger(WSEurekaBank.class.getName());
    private static final String ACCION = EspacioNombres.WS + "WSEurekaBank/";

    private final ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion();
    private final ServicioCuentas servicioCuentas = new ServicioCuentas();
    private final IServicioOperaciones servicioOperaciones = new ServicioOperaciones();

    @Resource
    private WebServiceContext contextoWS;

    @FunctionalInterface
    private interface Llamada<T> {
        T ejecutar() throws SQLException;
    }

    @WebMethod(operationName = "iniciarSesion", action = ACCION + "iniciarSesion")
    @WebResult(name = "iniciarSesionResult", targetNamespace = EspacioNombres.WS)
    public RespuestaAutenticacion iniciarSesion(
            @WebParam(name = "usuario", targetNamespace = EspacioNombres.WS) String usuario,
            @WebParam(name = "clave", targetNamespace = EspacioNombres.WS) String clave) {

        return ejecutar(RespuestaAutenticacion::new, () -> {
            SolicitudLogin solicitud = new SolicitudLogin();
            solicitud.setUsuario(usuario);
            solicitud.setClave(clave);
            return servicioAutenticacion.autenticar(solicitud);
        });
    }

    @WebMethod(operationName = "listarCuentas", action = ACCION + "listarCuentas")
    @WebResult(name = "listarCuentasResult", targetNamespace = EspacioNombres.WS)
    public RespuestaCuentas listarCuentas(
            @WebParam(name = "token", targetNamespace = EspacioNombres.WS) String token) {

        return ejecutar(RespuestaCuentas::new, () -> {
            validarSesion(token);
            return servicioCuentas.listarCuentas();
        });
    }

    @WebMethod(operationName = "consultarMovimientos", action = ACCION + "consultarMovimientos")
    @WebResult(name = "consultarMovimientosResult", targetNamespace = EspacioNombres.WS)
    public RespuestaMovimientos consultarMovimientos(
            @WebParam(name = "token", targetNamespace = EspacioNombres.WS) String token,
            @WebParam(name = "cuenta", targetNamespace = EspacioNombres.WS) String cuenta) {

        return ejecutar(RespuestaMovimientos::new, () -> {
            validarSesion(token);
            return servicioCuentas.consultarMovimientos(cuenta);
        });
    }

    @WebMethod(operationName = "registrarDeposito", action = ACCION + "registrarDeposito")
    @WebResult(name = "registrarDepositoResult", targetNamespace = EspacioNombres.WS)
    public RespuestaOperacion registrarDeposito(
            @WebParam(name = "token", targetNamespace = EspacioNombres.WS) String token,
            @WebParam(name = "cuenta", targetNamespace = EspacioNombres.WS) String cuenta,
            @WebParam(name = "importe", targetNamespace = EspacioNombres.WS) BigDecimal importe) {

        return ejecutar(RespuestaOperacion::new, () -> {
            SesionEmpleado sesion = validarSesion(token);
            SolicitudDeposito solicitud = new SolicitudDeposito();
            solicitud.setCuenta(cuenta);
            solicitud.setImporte(importe);
            return servicioOperaciones.depositar(solicitud, sesion.codigoEmpleado());
        });
    }

    @WebMethod(operationName = "registrarRetiro", action = ACCION + "registrarRetiro")
    @WebResult(name = "registrarRetiroResult", targetNamespace = EspacioNombres.WS)
    public RespuestaOperacion registrarRetiro(
            @WebParam(name = "token", targetNamespace = EspacioNombres.WS) String token,
            @WebParam(name = "cuenta", targetNamespace = EspacioNombres.WS) String cuenta,
            @WebParam(name = "clave", targetNamespace = EspacioNombres.WS) String clave,
            @WebParam(name = "importe", targetNamespace = EspacioNombres.WS) BigDecimal importe) {

        return ejecutar(RespuestaOperacion::new, () -> {
            SesionEmpleado sesion = validarSesion(token);
            SolicitudRetiro solicitud = new SolicitudRetiro();
            solicitud.setCuenta(cuenta);
            solicitud.setClave(clave);
            solicitud.setImporte(importe);
            return servicioOperaciones.retirar(solicitud, sesion.codigoEmpleado());
        });
    }

    @WebMethod(operationName = "registrarTransferencia", action = ACCION + "registrarTransferencia")
    @WebResult(name = "registrarTransferenciaResult", targetNamespace = EspacioNombres.WS)
    public RespuestaOperacion registrarTransferencia(
            @WebParam(name = "token", targetNamespace = EspacioNombres.WS) String token,
            @WebParam(name = "cuentaOrigen", targetNamespace = EspacioNombres.WS) String cuentaOrigen,
            @WebParam(name = "claveOrigen", targetNamespace = EspacioNombres.WS) String claveOrigen,
            @WebParam(name = "cuentaDestino", targetNamespace = EspacioNombres.WS) String cuentaDestino,
            @WebParam(name = "importe", targetNamespace = EspacioNombres.WS) BigDecimal importe) {

        return ejecutar(RespuestaOperacion::new, () -> {
            SesionEmpleado sesion = validarSesion(token);
            SolicitudTransferencia solicitud = new SolicitudTransferencia();
            solicitud.setCuentaOrigen(cuentaOrigen);
            solicitud.setClaveOrigen(claveOrigen);
            solicitud.setCuentaDestino(cuentaDestino);
            solicitud.setImporte(importe);
            return servicioOperaciones.transferir(solicitud, sesion.codigoEmpleado());
        });
    }

    // ------------------------------------------------------------------ comun

    /** Ejecuta la operacion y convierte cualquier error en la respuesta {estado, codigo, mensaje}. */
    private static <T extends Respuesta> T ejecutar(Supplier<T> constructor, Llamada<T> llamada) {
        try {
            return llamada.ejecutar();
        } catch (ExcepcionNegocio e) {
            return FabricaRespuesta.error(constructor, e.getCodigo(), e.getMessage());
        } catch (SQLException | IllegalStateException e) {
            REGISTRO.log(Level.SEVERE, "Error de base de datos", e);
            return FabricaRespuesta.error(constructor, CodigoRespuesta.ERROR_SERVIDOR, "No se pudo acceder a la base de datos");
        } catch (RuntimeException e) {
            REGISTRO.log(Level.SEVERE, "Error no controlado", e);
            return FabricaRespuesta.error(constructor, CodigoRespuesta.ERROR_SERVIDOR, "Error interno del servidor");
        }
    }

    /** Usa el estado que dejo ManejadorAutenticacion; si no hay contexto (pruebas), valida aqui. */
    private SesionEmpleado validarSesion(String token) {
        EstadoToken estado = null;
        if (contextoWS != null) {
            MessageContext mensaje = contextoWS.getMessageContext();
            estado = (EstadoToken) mensaje.get(ManejadorAutenticacion.ESTADO_TOKEN);
        }
        if (estado == null) {
            estado = AdministradorTokens.validar(token);
        }

        switch (estado) {
            case EXPIRADO -> throw new ExcepcionNegocio(CodigoRespuesta.SESION_EXPIRADA,
                    "La sesion expiro, inicie sesion nuevamente");
            case INVALIDO -> throw new ExcepcionNegocio(CodigoRespuesta.TOKEN_INVALIDO, "Token invalido o ausente");
            default -> {
            }
        }
        SesionEmpleado sesion = AdministradorTokens.obtenerSesion(token == null ? "" : token.trim());
        if (sesion == null) {
            throw new ExcepcionNegocio(CodigoRespuesta.TOKEN_INVALIDO, "Token invalido o ausente");
        }
        return sesion;
    }
}
