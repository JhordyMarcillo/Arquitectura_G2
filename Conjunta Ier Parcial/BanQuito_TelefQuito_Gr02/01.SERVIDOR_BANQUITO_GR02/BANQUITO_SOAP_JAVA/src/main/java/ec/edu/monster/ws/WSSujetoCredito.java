package ec.edu.monster.ws;

import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;

import ec.edu.monster.servicio.SujetoCreditoService;
@WebService(serviceName = "WSSujetoCredito")
public class WSSujetoCredito {
    /**
     * Web service operation to verify a customer
     */
    @WebMethod(operationName = "verifica")
    public int verifica(@WebParam(name = "cedula") String cedula) {
        SujetoCreditoService sujetoCredito = new SujetoCreditoService();
        return sujetoCredito.verificar(cedula);
    }
}