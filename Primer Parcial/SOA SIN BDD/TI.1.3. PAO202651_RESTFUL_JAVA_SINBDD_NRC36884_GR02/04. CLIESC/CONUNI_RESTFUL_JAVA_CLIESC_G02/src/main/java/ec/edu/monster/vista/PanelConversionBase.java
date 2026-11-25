package ec.edu.monster.vista;

import ec.edu.monster.modelo.ConversionResponse;
import ec.edu.monster.servicio.ConversionApiClient;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Panel base de conversión (tema Monsters Inc.): tarjeta centrada con valor, tipo de conversión,
 * botón invertir, botón convertir y resultado destacado. La llamada REST corre en segundo plano
 * para no congelar la ventana. Las conversiones van en pares (ida/vuelta) para poder invertir.
 */
public abstract class PanelConversionBase extends JPanel {

    protected static final Color AZUL = new Color(33, 169, 218);
    protected static final Color ROJO = new Color(246, 105, 113);
    protected static final Color PURPURA = new Color(140, 107, 205);
    protected static final Color AZUL_CLARO = new Color(159, 220, 250);
    protected static final Color GRIS_OSCURO = new Color(44, 62, 80);
    protected static final Color GRIS_TEXTO = new Color(127, 140, 141);
    protected static final Color FONDO = new Color(236, 240, 241);
    private static final Color VERDE_FONDO = new Color(232, 245, 232);
    private static final Color ROJO_FONDO = new Color(255, 240, 240);
    private static final DecimalFormat FORMATO =
            new DecimalFormat("#,##0.####", DecimalFormatSymbols.getInstance(new Locale("es", "EC")));

    private final Color acento;
    private final String[] etiquetas;
    private final String[] operaciones;
    protected final ConversionApiClient clienteREST = new ConversionApiClient();

    private final JTextField txtValor = new JTextField();
    private final JComboBox<String> cmbConversion;
    private final JButton btnConvertir = new JButton("CONVERTIR");
    private final JLabel lblEtiqueta = new JLabel("El resultado aparecerá aquí", SwingConstants.CENTER);
    private final JLabel lblResultado = new JLabel(" ", SwingConstants.CENTER);
    private final JButton btnCopiar = new JButton("Copiar");
    private final JPanel panelResultado = new JPanel(new BorderLayout(0, 6));
    private String ultimoResultado;

    /** Ejecuta la conversión contra el servicio REST (se llama fuera del hilo de la interfaz). */
    protected abstract ConversionResponse ejecutar(String operacion, double valor);

    /** @param conversiones pares {etiqueta, operación REST}, en orden ida/vuelta */
    protected PanelConversionBase(String titulo, Color acento, String[][] conversiones) {
        this.acento = acento;
        etiquetas = new String[conversiones.length];
        operaciones = new String[conversiones.length];
        for (int i = 0; i < conversiones.length; i++) {
            etiquetas[i] = conversiones[i][0];
            operaciones[i] = conversiones[i][1];
        }
        cmbConversion = new JComboBox<>(etiquetas);
        construir(titulo);
    }

    private void construir(String titulo) {
        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());

        JPanel tarjeta = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(acento);
                g.fillRect(0, 0, getWidth(), 8);
            }
        };
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 230), 2),
                new EmptyBorder(28, 36, 28, 36)));
        tarjeta.setPreferredSize(new Dimension(640, 470));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(GRIS_OSCURO);

        txtValor.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtValor.setHorizontalAlignment(SwingConstants.CENTER);
        txtValor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 206, 210), 1), new EmptyBorder(6, 10, 6, 10)));
        txtValor.addActionListener(e -> convertir());   // Enter convierte

        cmbConversion.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cmbConversion.setBackground(Color.WHITE);

        JButton btnInvertir = new JButton("⇄");
        btnInvertir.setToolTipText("Invertir conversión");
        btnInvertir.setFont(new Font("Segoe UI Symbol", Font.BOLD, 18));
        btnInvertir.setForeground(PURPURA);
        btnInvertir.setBackground(Color.WHITE);
        btnInvertir.setFocusPainted(false);
        btnInvertir.setBorder(BorderFactory.createLineBorder(PURPURA, 2));
        btnInvertir.setPreferredSize(new Dimension(48, 38));
        btnInvertir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnInvertir.addActionListener(e -> cmbConversion.setSelectedIndex(cmbConversion.getSelectedIndex() ^ 1));

        estiloBoton(btnConvertir, acento, Color.WHITE, 17);
        btnConvertir.setPreferredSize(new Dimension(560, 52));
        btnConvertir.addActionListener(e -> convertir());
        btnConvertir.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { if (btnConvertir.isEnabled()) btnConvertir.setBackground(acento.darker()); }
            @Override public void mouseExited(MouseEvent e) { if (btnConvertir.isEnabled()) btnConvertir.setBackground(acento); }
        });

        lblEtiqueta.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblEtiqueta.setForeground(GRIS_TEXTO);
        lblResultado.setFont(new Font("Segoe UI", Font.BOLD, 34));
        lblResultado.setForeground(AZUL);
        estiloBoton(btnCopiar, Color.WHITE, new Color(46, 125, 50), 12);
        btnCopiar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(76, 175, 80), 1), new EmptyBorder(4, 16, 4, 16)));
        btnCopiar.setBorderPainted(true);
        btnCopiar.setVisible(false);
        btnCopiar.addActionListener(e -> copiar());
        JPanel filaCopiar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        filaCopiar.setOpaque(false);
        filaCopiar.add(btnCopiar);

        panelResultado.setBackground(FONDO);
        panelResultado.setBorder(new EmptyBorder(16, 16, 16, 16));
        panelResultado.setPreferredSize(new Dimension(560, 160));
        panelResultado.add(lblEtiqueta, BorderLayout.NORTH);
        panelResultado.add(lblResultado, BorderLayout.CENTER);
        panelResultado.add(filaCopiar, BorderLayout.SOUTH);

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridwidth = 3; g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(0, 0, 20, 0);
        g.gridy = 0; tarjeta.add(lblTitulo, g);

        g.gridwidth = 1; g.insets = new Insets(0, 0, 6, 12); g.weightx = 1;
        g.gridy = 1; g.gridx = 0; tarjeta.add(etiqueta("Valor a convertir"), g);
        g.gridx = 1; g.gridwidth = 2; g.insets = new Insets(0, 0, 6, 0); tarjeta.add(etiqueta("Tipo de conversión"), g);

        g.gridy = 2; g.gridx = 0; g.gridwidth = 1; g.insets = new Insets(0, 0, 22, 12); g.ipady = 6; tarjeta.add(txtValor, g);
        g.gridx = 1; g.insets = new Insets(0, 0, 22, 8); tarjeta.add(cmbConversion, g);
        g.gridx = 2; g.weightx = 0; g.fill = GridBagConstraints.NONE; g.insets = new Insets(0, 0, 22, 0); g.ipady = 0;
        tarjeta.add(btnInvertir, g);

        g.gridy = 3; g.gridx = 0; g.gridwidth = 3; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1;
        g.insets = new Insets(0, 0, 20, 0); tarjeta.add(btnConvertir, g);
        g.gridy = 4; g.insets = new Insets(0, 0, 0, 0); tarjeta.add(panelResultado, g);

        add(tarjeta);
    }

    private static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(GRIS_OSCURO);
        return l;
    }

    private static void estiloBoton(JButton b, Color fondo, Color texto, int tam) {
        b.setBackground(fondo);
        b.setForeground(texto);
        b.setFont(new Font("Segoe UI", Font.BOLD, tam));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void convertir() {
        final double valor;
        try {
            valor = Double.parseDouble(txtValor.getText().trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            mostrarError("Ingrese un valor numérico válido");
            return;
        }
        final String operacion = operaciones[cmbConversion.getSelectedIndex()];

        btnConvertir.setEnabled(false);
        btnConvertir.setBackground(AZUL_CLARO);
        btnConvertir.setText("CONVIRTIENDO...");
        panelResultado.setBackground(FONDO);
        lblEtiqueta.setForeground(GRIS_TEXTO);
        lblEtiqueta.setText("Procesando conversión...");
        lblResultado.setText(" ");
        btnCopiar.setBorderPainted(true);
        btnCopiar.setVisible(false);

        new SwingWorker<ConversionResponse, Void>() {
            @Override protected ConversionResponse doInBackground() { return ejecutar(operacion, valor); }

            @Override protected void done() {
                try {
                    ConversionResponse r = get();
                    if (r != null && r.isExito()) mostrarResultado(r);
                    else mostrarError(r != null ? r.getMensaje() : "Error desconocido");
                } catch (Exception ex) {
                    mostrarError("Error al enviar la solicitud: " + ex.getMessage());
                }
                btnConvertir.setEnabled(true);
                btnConvertir.setBackground(acento);
                btnConvertir.setText("CONVERTIR");
            }
        }.execute();
    }

    private void mostrarResultado(ConversionResponse r) {
        ultimoResultado = String.valueOf(Math.round(r.getValorConvertido() * 10000.0) / 10000.0);
        panelResultado.setBackground(VERDE_FONDO);
        lblEtiqueta.setForeground(GRIS_TEXTO);
        lblEtiqueta.setText(FORMATO.format(r.getValorOriginal()) + " " + r.getUnidadOrigen() + " equivale a");
        lblResultado.setFont(new Font("Segoe UI", Font.BOLD, 34));
        lblResultado.setForeground(AZUL);
        lblResultado.setText(FORMATO.format(r.getValorConvertido()) + " " + r.getUnidadDestino());
        btnCopiar.setText("Copiar");
        btnCopiar.setVisible(true);
    }

    public void mostrarError(String error) {
        panelResultado.setBackground(ROJO_FONDO);
        lblEtiqueta.setForeground(ROJO);
        lblEtiqueta.setText("No se pudo convertir");
        lblResultado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblResultado.setForeground(ROJO);
        lblResultado.setText("<html><div style='text-align:center'>" + error.replace("\n", "<br>") + "</div></html>");
        btnCopiar.setBorderPainted(true);
        btnCopiar.setVisible(false);
    }

    private void copiar() {
        if (ultimoResultado == null) return;
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(ultimoResultado), null);
        btnCopiar.setText("Copiado ✓");
        Timer t = new Timer(1500, e -> btnCopiar.setText("Copiar"));
        t.setRepeats(false);
        t.start();
    }
}
