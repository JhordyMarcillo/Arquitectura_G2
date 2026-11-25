package ec.edu.monster.ws;

import java.util.List;

public class CompraServicio {
    WSCompra_Service servicio = new WSCompra_Service(Servidor.wsdl("WSCompra"));
    WSCompra port = servicio.getWSCompraPort();
    
    public String comprarEfectivo(int codTelefono,
             String codcCedula) {
        return port.comprarEfectivo(codTelefono, codcCedula);
    }
     public String comprarCredito( int codTelefono,
            String cedula, int plazoMeses) {
         return port.comprarCredito(codTelefono, cedula, plazoMeses);
         
     }
    /** Compra de 1 a 3 teléfonos ("1,2,3") en efectivo. Devuelve la factura. */
    public String comprarEfectivoMultiple(String codTelefonos, String cedula) {
        return port.comprarEfectivoMultiple(codTelefonos, cedula);
    }

    /** Compra de 1 a 3 teléfonos ("1,2,3") a crédito. Devuelve la factura. */
    public String comprarCreditoMultiple(String codTelefonos, String cedula, int plazoMeses) {
        return port.comprarCreditoMultiple(codTelefonos, cedula, plazoMeses);
    }

      public List<Factura> obtenerFactura(String cedula) {
          return port.obtenerFactura(cedula);
      }
       public List<Tabla> consultarTablaAmortizacion(String cedula) {
        return port.consultarTablaAmortizacion(cedula);
    }
}