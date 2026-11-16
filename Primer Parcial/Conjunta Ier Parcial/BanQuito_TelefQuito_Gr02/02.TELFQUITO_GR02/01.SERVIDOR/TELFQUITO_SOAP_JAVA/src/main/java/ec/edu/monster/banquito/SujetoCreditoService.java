
package ec.edu.monster.banquito;

import ec.edu.monster.banquito.WSSujetoCredito;
import ec.edu.monster.banquito.WSSujetoCredito_Service;




public class SujetoCreditoService {
    WSSujetoCredito_Service servicio = new WSSujetoCredito_Service();
        WSSujetoCredito port = servicio.getWSSujetoCreditoPort();
    public int verifica( String cedula) {
        
        return port.verifica(cedula);
    }

}
