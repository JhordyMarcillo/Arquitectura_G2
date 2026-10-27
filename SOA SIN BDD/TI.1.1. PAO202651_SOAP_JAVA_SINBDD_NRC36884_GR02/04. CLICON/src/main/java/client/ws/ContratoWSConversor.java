package client.ws;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

@WebService(name = "WSConversorUnidades", targetNamespace = "http://ws.controladores.monster.edu.ec/")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ContratoWSConversor {

    @WebMethod(operationName = "autenticar")
    @WebResult(name = "return")
    RespuestaAutenticacion autenticar(
            @WebParam(name = "usuario") String usuario,
            @WebParam(name = "contrasenia") String contrasenia);

    @WebMethod(operationName = "convertirLongitud")
    @WebResult(name = "return")
    RespuestaConversion convertirLongitud(
            @WebParam(name = "token") String token,
            @WebParam(name = "unidadOrigen") String unidadOrigen,
            @WebParam(name = "unidadDestino") String unidadDestino,
            @WebParam(name = "valor") Double valor);

    @WebMethod(operationName = "convertirMasa")
    @WebResult(name = "return")
    RespuestaConversion convertirMasa(
            @WebParam(name = "token") String token,
            @WebParam(name = "unidadOrigen") String unidadOrigen,
            @WebParam(name = "unidadDestino") String unidadDestino,
            @WebParam(name = "valor") Double valor);

    @WebMethod(operationName = "convertirTemperatura")
    @WebResult(name = "return")
    RespuestaConversion convertirTemperatura(
            @WebParam(name = "token") String token,
            @WebParam(name = "unidadOrigen") String unidadOrigen,
            @WebParam(name = "unidadDestino") String unidadDestino,
            @WebParam(name = "valor") Double valor);
}
