package ec.edu.monster.ws;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import ec.edu.monster.servicio.LoginService;

@WebService(serviceName = "WSLogin")
public class WSLogin {

    /**
     * This is a sample web service operation
     * @param username
     * @param password
     * @return 
     */
   @WebMethod(operationName = "auth")
    public boolean auth(@WebParam(name = "username") String username,@WebParam(name = "password") String password) {
        LoginService service = new LoginService();
        boolean resultado = service.login(username, password);
        return resultado;
        
    }
}