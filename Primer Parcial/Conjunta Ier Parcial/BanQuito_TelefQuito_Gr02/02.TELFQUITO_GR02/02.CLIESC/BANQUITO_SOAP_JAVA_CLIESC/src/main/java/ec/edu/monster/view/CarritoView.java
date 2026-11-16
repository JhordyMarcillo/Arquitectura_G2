package ec.edu.monster.view;

import ec.edu.monster.service.CompraService;
import ec.edu.monster.service.TelefonoService;
import ec.edu.monster.ws.Telefonos;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/** Compra de 1 a 3 teléfonos en una sola factura (efectivo o crédito). */
public class CarritoView extends JFrame {

    private static final int MAX = 3;

    private final CompraService compraService = new CompraService();
    private final List<JCheckBox> checks = new ArrayList<>();
    private final List<Telefonos> telefonos = new ArrayList<>();
    private final JLabel lblResumen = new JLabel("Seleccionados: 0 / " + MAX);
    private final JComboBox<String> cmbPago = new JComboBox<>(new String[]{"Efectivo (42% de descuento)", "Crédito"});
    private final JTextField txtCedula = new JTextField(12);
    private final JSpinner spnPlazo = new JSpinner(new SpinnerNumberModel(12, 3, 18, 1));
    private final JTextArea txtFactura = new JTextArea(12, 60);

    public CarritoView() {
        super("Comprar hasta 3 productos");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBorder(BorderFactory.createTitledBorder("Teléfonos disponibles (elija de 1 a 3)"));
        try {
            for (Telefonos t : new TelefonoService().obtenerTelefonos()) {
                if (t.getDisponible() != 1) {
                    continue;
                }
                JCheckBox c = new JCheckBox(String.format("%s (%s) - $%.2f", t.getNombre(), t.getMarca(), t.getPrecio()));
                c.addActionListener(e -> alCambiarSeleccion(c));
                checks.add(c);
                telefonos.add(t);
                lista.add(c);
            }
        } catch (Exception ex) {
            lista.add(new JLabel("No se pudo cargar el listado: " + ex.getMessage()));
        }
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setPreferredSize(new Dimension(520, 200));
        add(scroll, BorderLayout.NORTH);

        JPanel datos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datos.add(lblResumen);
        datos.add(new JLabel("  Pago:"));
        datos.add(cmbPago);
        datos.add(new JLabel("Cédula:"));
        datos.add(txtCedula);
        datos.add(new JLabel("Plazo (meses):"));
        datos.add(spnPlazo);
        JButton btnComprar = new JButton("Comprar y generar factura");
        btnComprar.addActionListener(e -> comprar());
        datos.add(btnComprar);
        add(datos, BorderLayout.CENTER);

        txtFactura.setEditable(false);
        txtFactura.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollFactura = new JScrollPane(txtFactura);
        scrollFactura.setBorder(BorderFactory.createTitledBorder("Factura"));
        add(scrollFactura, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private int contarMarcados() {
        int n = 0;
        for (JCheckBox c : checks) {
            if (c.isSelected()) {
                n++;
            }
        }
        return n;
    }

    private void alCambiarSeleccion(JCheckBox c) {
        if (contarMarcados() > MAX) {
            c.setSelected(false);
            JOptionPane.showMessageDialog(this, "Solo se pueden comprar hasta " + MAX + " productos.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
        lblResumen.setText("Seleccionados: " + contarMarcados() + " / " + MAX);
    }

    private void comprar() {
        StringBuilder codigos = new StringBuilder();
        for (int i = 0; i < checks.size(); i++) {
            if (checks.get(i).isSelected()) {
                if (codigos.length() > 0) {
                    codigos.append(',');
                }
                codigos.append(telefonos.get(i).getCodTelefono());
            }
        }
        if (codigos.length() == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione al menos un teléfono.", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String cedula = txtCedula.getText().trim();
        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la cédula.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String factura = cmbPago.getSelectedIndex() == 1
                    ? compraService.comprarCreditoMultiple(codigos.toString(), cedula, (Integer) spnPlazo.getValue())
                    : compraService.comprarEfectivoMultiple(codigos.toString(), cedula);
            txtFactura.setText(factura);
            txtFactura.setCaretPosition(0);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al comprar: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
