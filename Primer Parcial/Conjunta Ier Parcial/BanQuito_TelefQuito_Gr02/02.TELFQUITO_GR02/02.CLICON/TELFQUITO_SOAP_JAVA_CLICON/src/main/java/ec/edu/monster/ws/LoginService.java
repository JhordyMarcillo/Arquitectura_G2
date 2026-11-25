package ec.edu.monster.ws;

public class LoginService {
    public boolean auth(String username, String password){
        WSLogin_Service servicio = new WSLogin_Service(Servidor.wsdl("WSLogin"));
        WSLogin port = servicio.getWSLoginPort();
        return port.auth(username, password);
    }
}