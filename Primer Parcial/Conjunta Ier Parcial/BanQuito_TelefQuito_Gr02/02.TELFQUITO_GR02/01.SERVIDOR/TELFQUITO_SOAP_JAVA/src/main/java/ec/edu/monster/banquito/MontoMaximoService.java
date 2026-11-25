
package ec.edu.monster.banquito;

import ec.edu.monster.banquito.WSMontoMaximo;
import ec.edu.monster.banquito.WSMontoMaximo_Service;

public class MontoMaximoService {
    public double montoMaximo(int codigo ){
        WSMontoMaximo_Service  servidor = new WSMontoMaximo_Service();
        WSMontoMaximo port = servidor.getWSMontoMaximoPort();
        return port.montoMaximo(codigo);
        
    }
}
