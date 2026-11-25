package ec.edu.monster.servicios;

import ec.edu.monster.modelos.RespuestaAutenticacion;
import ec.edu.monster.modelos.RespuestaCuentas;
import ec.edu.monster.modelos.RespuestaMovimientos;
import ec.edu.monster.modelos.RespuestaOperacion;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import java.math.BigDecimal;

/**
 * Contrato SOAP del servicio WSEurekaBank, espejo del publicado por el servidor
 * (espacio de nombres http://ws.monster.edu.ec/, ver CONTRATO_EUREKABANK_GR02.md, seccion 4).
 * Sirve para el servidor Java (TI.1.1) y para el .NET (TI.1.2).
 */
@WebService(name = "WSEurekaBank", targetNamespace = ContratoWSEurekaBank.ESPACIO_NOMBRES)
public interface ContratoWSEurekaBank {

    String ESPACIO_NOMBRES = "http://ws.monster.edu.ec/";
    String ACCION = ESPACIO_NOMBRES + "WSEurekaBank/";

    @WebMethod(operationName = "iniciarSesion", action = ACCION + "iniciarSesion")
    @WebResult(name = "iniciarSesionResult", targetNamespace = ESPACIO_NOMBRES)
    RespuestaAutenticacion iniciarSesion(
            @WebParam(name = "usuario", targetNamespace = ESPACIO_NOMBRES) String usuario,
            @WebParam(name = "clave", targetNamespace = ESPACIO_NOMBRES) String clave);

    @WebMethod(operationName = "listarCuentas", action = ACCION + "listarCuentas")
    @WebResult(name = "listarCuentasResult", targetNamespace = ESPACIO_NOMBRES)
    RespuestaCuentas listarCuentas(
            @WebParam(name = "token", targetNamespace = ESPACIO_NOMBRES) String token);

    @WebMethod(operationName = "consultarMovimientos", action = ACCION + "consultarMovimientos")
    @WebResult(name = "consultarMovimientosResult", targetNamespace = ESPACIO_NOMBRES)
    RespuestaMovimientos consultarMovimientos(
            @WebParam(name = "token", targetNamespace = ESPACIO_NOMBRES) String token,
            @WebParam(name = "cuenta", targetNamespace = ESPACIO_NOMBRES) String cuenta);

    @WebMethod(operationName = "registrarDeposito", action = ACCION + "registrarDeposito")
    @WebResult(name = "registrarDepositoResult", targetNamespace = ESPACIO_NOMBRES)
    RespuestaOperacion registrarDeposito(
            @WebParam(name = "token", targetNamespace = ESPACIO_NOMBRES) String token,
            @WebParam(name = "cuenta", targetNamespace = ESPACIO_NOMBRES) String cuenta,
            @WebParam(name = "importe", targetNamespace = ESPACIO_NOMBRES) BigDecimal importe);

    @WebMethod(operationName = "registrarRetiro", action = ACCION + "registrarRetiro")
    @WebResult(name = "registrarRetiroResult", targetNamespace = ESPACIO_NOMBRES)
    RespuestaOperacion registrarRetiro(
            @WebParam(name = "token", targetNamespace = ESPACIO_NOMBRES) String token,
            @WebParam(name = "cuenta", targetNamespace = ESPACIO_NOMBRES) String cuenta,
            @WebParam(name = "clave", targetNamespace = ESPACIO_NOMBRES) String clave,
            @WebParam(name = "importe", targetNamespace = ESPACIO_NOMBRES) BigDecimal importe);

    @WebMethod(operationName = "registrarTransferencia", action = ACCION + "registrarTransferencia")
    @WebResult(name = "registrarTransferenciaResult", targetNamespace = ESPACIO_NOMBRES)
    RespuestaOperacion registrarTransferencia(
            @WebParam(name = "token", targetNamespace = ESPACIO_NOMBRES) String token,
            @WebParam(name = "cuentaOrigen", targetNamespace = ESPACIO_NOMBRES) String cuentaOrigen,
            @WebParam(name = "claveOrigen", targetNamespace = ESPACIO_NOMBRES) String claveOrigen,
            @WebParam(name = "cuentaDestino", targetNamespace = ESPACIO_NOMBRES) String cuentaDestino,
            @WebParam(name = "importe", targetNamespace = ESPACIO_NOMBRES) BigDecimal importe);
}
