package ec.edu.monster.vistas;

import ec.edu.monster.Principal;
import ec.edu.monster.controladores.TareaServicio;
import ec.edu.monster.modelos.DatosMovimiento;
import ec.edu.monster.modelos.RespuestaMovimientos;
import ec.edu.monster.utilidades.Formato;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Busqueda por cuenta, resumen de saldo, ingresos y salidas, e historial con colores. */
public class PanelMovimientos extends JPanel {

    private final VentanaPrincipal ventana;
    private final JTextField campoCuenta = Tema.campo("Numero de cuenta (8 digitos)");
    private final JPanel panelResultado = new JPanel();
    private final JLabel textoCuenta = Tema.etiqueta("", 13, Font.PLAIN, new Color(0xBFDBFE));
    private final JLabel textoSaldo = Tema.etiqueta("", 28, Font.BOLD, Color.WHITE);
    private final JLabel textoIngresos = PanelCuentas.valorIndicador();
    private final JLabel textoSalidas = PanelCuentas.valorIndicador();
    private final JLabel notaIngresos = PanelCuentas.notaIndicador("");
    private final JLabel notaSalidas = PanelCuentas.notaIndicador("");
    private final TablaEureka tabla = new TablaEureka(new String[]{"N°", "Fecha", "Descripcion", "Referencia", "Importe"});

    public PanelMovimientos(VentanaPrincipal ventana, String cuenta) {
        this.ventana = ventana;
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(tarjetaBusqueda());
        add(Box.createVerticalStrut(22));
        construirResultado();
        add(panelResultado);

        tabla.alinearDerecha(4);
        tabla.anchoColumna(0, 60);
        tabla.anchoColumna(1, 120);
        tabla.anchoColumna(3, 130);
        tabla.anchoColumna(4, 170);
        tabla.renderizar(2, valor -> {
            String[] partes = String.valueOf(valor).split("\\|", 2);
            boolean ingreso = "I".equals(partes[0]);
            JLabel etiqueta = Tema.etiqueta(partes[1], 13, Font.PLAIN, Tema.TEXTO);
            etiqueta.setIcon(Tema.icono(ingreso ? "arriba" : "abajo", 15, ingreso ? Tema.VERDE : Tema.ROJO));
            etiqueta.setIconTextGap(10);
            return etiqueta;
        });
        tabla.renderizar(4, valor -> {
            String texto = String.valueOf(valor);
            return Tema.etiqueta(texto, 13, Font.BOLD, texto.startsWith("+") ? Tema.VERDE : Tema.ROJO);
        });

        if (cuenta != null) {
            campoCuenta.setText(cuenta);
            consultar();
        }
    }

    private JPanel tarjetaBusqueda() {
        Tema.Tarjeta tarjeta = new Tema.Tarjeta();
        tarjeta.setLayout(new BorderLayout(12, 0));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(18, 20, 22, 20));
        JPanel campo = new JPanel();
        campo.setOpaque(false);
        campo.setLayout(new BoxLayout(campo, BoxLayout.Y_AXIS));
        campo.add(Tema.rotulo("Numero de cuenta"));
        campo.add(campoCuenta);
        tarjeta.add(campo, BorderLayout.CENTER);

        JButton consultar = Tema.botonPrimario("Consultar");
        consultar.setIcon(Tema.icono("buscar", 16, Color.WHITE));
        consultar.setIconTextGap(8);
        consultar.addActionListener(e -> consultar());
        campoCuenta.addActionListener(e -> consultar());
        JPanel derecha = new JPanel(new BorderLayout());
        derecha.setOpaque(false);
        derecha.add(consultar, BorderLayout.SOUTH);
        tarjeta.add(derecha, BorderLayout.EAST);
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        return tarjeta;
    }

    private void construirResultado() {
        panelResultado.setOpaque(false);
        panelResultado.setLayout(new BoxLayout(panelResultado, BoxLayout.Y_AXIS));

        JPanel resumen = new JPanel(new GridLayout(1, 3, 16, 0));
        resumen.setOpaque(false);
        JPanel destacada = new Tema.PanelDegradado(new Color(0x2563EB), Tema.PRIMARIO_OSCURO, 16);
        destacada.setLayout(new BoxLayout(destacada, BoxLayout.Y_AXIS));
        destacada.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        destacada.add(textoCuenta);
        destacada.add(Box.createVerticalStrut(6));
        destacada.add(textoSaldo);
        destacada.add(Tema.etiqueta("Saldo disponible", 12, Font.PLAIN, new Color(0xBFDBFE)));
        resumen.add(destacada);
        textoIngresos.setForeground(Tema.VERDE);
        textoSalidas.setForeground(Tema.ROJO);
        resumen.add(PanelCuentas.indicador("Total ingresos", textoIngresos, notaIngresos, null));
        resumen.add(PanelCuentas.indicador("Total salidas", textoSalidas, notaSalidas, null));
        resumen.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        panelResultado.add(resumen);
        panelResultado.add(Box.createVerticalStrut(22));

        Tema.Tarjeta tarjetaTabla = new Tema.Tarjeta();
        tarjetaTabla.setLayout(new BorderLayout());
        tarjetaTabla.setBorder(BorderFactory.createEmptyBorder(1, 1, 8, 1));
        tarjetaTabla.add(tabla.conDesplazamiento());
        tarjetaTabla.setPreferredSize(new Dimension(0, 420));
        panelResultado.add(tarjetaTabla);
        panelResultado.setVisible(false);
    }

    private void consultar() {
        String cuenta = campoCuenta.getText().trim();
        if (!Formato.esCuenta(cuenta)) {
            ventana.notificar("La cuenta debe tener 8 digitos.", true);
            return;
        }
        TareaServicio.ejecutar(() -> Principal.SERVICIO.consultarMovimientos(cuenta), this::mostrar, ventana::manejarError);
    }

    private void mostrar(RespuestaMovimientos datos) {
        String moneda = datos.getMoneda();
        textoCuenta.setText("Cuenta " + datos.getCuenta() + "  ·  " + moneda);
        textoSaldo.setText(Formato.dinero(moneda, datos.getSaldo()));

        BigDecimal ingresos = BigDecimal.ZERO;
        BigDecimal salidas = BigDecimal.ZERO;
        int cantidadIngresos = 0;
        List<Object[]> filas = new ArrayList<>();
        List<DatosMovimiento> movimientos = new ArrayList<>(datos.getMovimientos());
        java.util.Collections.reverse(movimientos);
        for (DatosMovimiento m : movimientos) {
            boolean ingreso = Formato.esIngreso(m);
            if (ingreso) {
                ingresos = ingresos.add(m.getImporte());
                cantidadIngresos++;
            } else {
                salidas = salidas.add(m.getImporte());
            }
            filas.add(new Object[]{m.getNumero(), m.getFecha(), (ingreso ? "I|" : "S|") + m.getDescripcion(),
                m.getCuentaReferencia() == null ? "-" : m.getCuentaReferencia(),
                (ingreso ? "+ " : "- ") + Formato.dinero(moneda, m.getImporte())});
        }
        textoIngresos.setText(Formato.dinero(moneda, ingresos));
        notaIngresos.setText(cantidadIngresos + " movimientos");
        textoSalidas.setText(Formato.dinero(moneda, salidas));
        notaSalidas.setText((movimientos.size() - cantidadIngresos) + " movimientos");
        tabla.cargar(filas);
        panelResultado.setVisible(true);
        revalidate();
    }
}
