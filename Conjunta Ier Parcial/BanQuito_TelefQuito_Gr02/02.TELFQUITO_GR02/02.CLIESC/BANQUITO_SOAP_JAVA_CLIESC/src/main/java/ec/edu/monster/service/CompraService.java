package ec.edu.monster.service;

import ec.edu.monster.ws.Factura;
import ec.edu.monster.ws.Tabla;
import ec.edu.monster.ws.WSCompra;
import ec.edu.monster.ws.WSCompra_Service;
import java.util.List;

public class CompraService {
    private final WSCompra port;

    public CompraService() {
        // Inicializar el cliente del web service
        WSCompra_Service servicio = new WSCompra_Service();
        this.port = servicio.getWSCompraPort();
    }
    
    /**
     * Realiza una compra en efectivo de un teléfono.
     * @param codTelefono Código del teléfono a comprar
     * @param codcCedula Cédula del comprador
     * @return Mensaje de confirmación o error
     */
    public String comprarEfectivo(int codTelefono, String codcCedula) {
        return port.comprarEfectivo(codTelefono, codcCedula);
    }

    /**
     * Realiza una compra a crédito de un teléfono.
     * @param codTelefono Código del teléfono a comprar
     * @param cedula Cédula del comprador
     * @param plazoMeses Plazo en meses para la compra a crédito
     * @return Mensaje de confirmación o error
     */
    public String comprarCredito(int codTelefono, String cedula, int plazoMeses) {
        return port.comprarCredito(codTelefono, cedula, plazoMeses);
    }

    /**
     * Obtiene la lista de facturas de un usuario por su cédula.
     * @param cedula Cédula del usuario
     * @return Lista de objetos Factura
     */
    public List<Factura> obtenerFactura(String cedula) {
        return port.obtenerFactura(cedula);
    }

    /**
     * Obtiene la tabla de amortización para un usuario por su cédula.
     * @param cedula Cédula del usuario
     * @return Lista de objetos Tabla
     */
    public List<Tabla> consultarTablaAmortizacion(String cedula) {
        return port.consultarTablaAmortizacion(cedula);
    }
    
}
