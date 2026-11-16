package ec.edu.monster.controller;

import ec.edu.monster.service.CompraService;
import ec.edu.monster.view.FacturaView;
import ec.edu.monster.ws.Factura;
import ec.edu.monster.ws.Telefonos;
import java.util.List;

public class FacturaController {

    private CompraService service;

    FacturaController() {
        service = new CompraService();
    }

    public void mostrarFactura(double precio, String cedula, FacturaView facturaView) {
        List<Factura> facturas = service.obtenerFactura(cedula);
        Factura ultimaFactura = facturas.get(facturas.size() - 1);
        
        facturaView.lblNomCliente.setText(ultimaFactura.getNombreCliente());
        facturaView.lblCedula.setText(cedula);
        
        facturaView.lblTipoPago.setText(ultimaFactura.getFormaPago());
        

        facturaView.lblConcepto.setText(ultimaFactura.getNombreTelefono());
        facturaView.lblMarca.setText(ultimaFactura.getMarcaTelefono());
        facturaView.lblPrecio.setText(String.valueOf(precio));
        facturaView.lblSubtotal.setText(String.valueOf(precio));
        facturaView.lblTitDescuento.setText("Descuento 0%");
        facturaView.lblDescuento.setText(String.valueOf(ultimaFactura.getDescuento()));
        facturaView.lblTotal.setText(String.valueOf(ultimaFactura.getPreciofinal())); 
        
        if("Efectivo".equals(ultimaFactura.getFormaPago())){
            facturaView.lblBanco.setVisible(false);
            facturaView.lblDescuento.setText("0");
            facturaView.lblTitDescuento.setText("Descuento 42%");
        }
        facturaView.setVisible(true);
    }

}