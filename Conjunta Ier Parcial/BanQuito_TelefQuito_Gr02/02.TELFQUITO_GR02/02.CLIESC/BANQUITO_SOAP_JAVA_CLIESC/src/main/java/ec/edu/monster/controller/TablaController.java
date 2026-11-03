package ec.edu.monster.controller;

import ec.edu.monster.service.CompraService;
import ec.edu.monster.view.TablaView;
import ec.edu.monster.ws.Tabla;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class TablaController {
     private CompraService compraService;

    public TablaController() {
        this.compraService = new CompraService();
    }

    /**
     * Método para cargar y mostrar los tablas en la vista proporcionada.
     *
     * @param tablaView
     */
    public void cargarTablas(String cedula, TablaView tablaView) {
        try {
            List<Tabla> tablas = compraService.consultarTablaAmortizacion(cedula);

            JPanel panelResultados = new JPanel();
            panelResultados.setLayout(new BoxLayout(panelResultados, BoxLayout.Y_AXIS));
            panelResultados.setBackground(Color.WHITE);

            if (tablas == null || tablas.isEmpty()) {
                JLabel noResultados = new JLabel("No se encontraron teléfonos registrados: ");
                noResultados.setFont(new Font("Arial", Font.BOLD, 16));
                noResultados.setForeground(Color.RED);
                panelResultados.add(noResultados);
            } else {
                // Crear celdas para cada movimiento
                for (Tabla tabla : tablas) {
                    JPanel celda = tablaView.crearCelda( String.valueOf(tabla.getCuota()),
                            String.valueOf(tabla.getValorCuota()),
                            String.valueOf(tabla.getInteresPagado()),
                            String.valueOf(tabla.getCapitalPagado()),
                            String.valueOf(tabla.getSaldo())
                    );
                    panelResultados.add(celda);
                }

            }

            tablaView.getjScrollPane1().setViewportView(panelResultados);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(tablaView,
                    "Error al obtener las tablas de amortización : " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}