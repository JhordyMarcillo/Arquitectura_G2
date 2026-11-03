package ec.edu.monster.ws;

import java.util.List;

public class CompraServicio {
    WSCompra_Service servicio = new WSCompra_Service();
    WSCompra port = servicio.getWSCompraPort();
    
    public String comprarEfectivo(int codTelefono,
             String codcCedula) {
        return port.comprarEfectivo(codTelefono, codcCedula);
    }
     public String comprarCredito( int codTelefono,
            String cedula, int plazoMeses) {
         return port.comprarCredito(codTelefono, cedula, plazoMeses);
         
     }
      public List<Factura> obtenerFactura(String cedula) {
          return port.obtenerFactura(cedula);
      }
       public List<Tabla> consultarTablaAmortizacion(String cedula) {
        return port.consultarTablaAmortizacion(cedula);
    }
}