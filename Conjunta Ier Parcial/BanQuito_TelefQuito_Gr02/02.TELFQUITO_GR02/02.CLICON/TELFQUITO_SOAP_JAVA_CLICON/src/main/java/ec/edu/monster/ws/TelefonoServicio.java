package ec.edu.monster.ws;

import java.util.List;


public class TelefonoServicio {
        
        WSTelefonos_Service servicio = new WSTelefonos_Service();
        WSTelefonos port = servicio.getWSTelefonosPort();
     public Telefonos getTelefonoById( int id) {
         return port.getTelefonoById(id);
     }
     public String insertTelefono( Telefonos telefono) {
         return port.insertTelefono(telefono);
     }
     public String updateTelefono( Telefonos telefono) {
         return port.updateTelefono(telefono);
         
     }
     public List<Telefonos> getAllTelefonos() {
         return port.getAllTelefonos();
     }
}