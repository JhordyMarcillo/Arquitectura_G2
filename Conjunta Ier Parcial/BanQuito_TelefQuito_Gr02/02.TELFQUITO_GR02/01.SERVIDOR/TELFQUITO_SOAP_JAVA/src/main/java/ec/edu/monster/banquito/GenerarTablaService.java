
package ec.edu.monster.banquito;

import ec.edu.monster.banquito.WSGenerarTabla;
import ec.edu.monster.banquito.WSGenerarTabla_Service;



import java.util.List;

public class GenerarTablaService {
    WSGenerarTabla_Service  servidor= new WSGenerarTabla_Service();
     WSGenerarTabla port = servidor.getWSGenerarTablaPort();
     public String crearCreditoYTablaAmortizacion(int codCliente,double monto,int plazoMeses) {        
         return port.crearCreditoYTablaAmortizacion(codCliente, monto, plazoMeses);
     }
     
     public List<ec.edu.monster.banquito.Tabla> consultarTablaAmortizacion( String cedula) {
     return port.consultarTablaAmortizacion(cedula);
     }
         public List<ec.edu.monster.banquito.Credito> consultarCreditosPorCedula( String cedula) {
             return port.consultarCreditosPorCedula(cedula);
         }
     
     
}
