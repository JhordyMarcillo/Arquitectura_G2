package ec.edu.monster.vistas;

import ec.edu.monster.Principal;
import ec.edu.monster.controladores.TareaServicio;
import ec.edu.monster.modelos.DatosCuenta;
import ec.edu.monster.utilidades.Formato;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/** Indicadores de la cartera y listado de cuentas (doble clic abre sus movimientos). */
public class PanelCuentas extends JPanel {

    private final VentanaPrincipal ventana;
    private final JLabel textoActivas = valorIndicador();
    private final JLabel textoTotal = notaIndicador("");
    private final JLabel textoSoles = valorIndicador();
    private final JLabel textoDolares = valorIndicador();
    private final TablaEureka tabla = new TablaEureka(new String[]{"Cuenta", "Cliente", "Moneda", "Saldo", "Estado"});

    public PanelCuentas(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel indicadores = new JPanel(new GridLayout(1, 3, 16, 0));
        indicadores.setOpaque(false);
        indicadores.add(indicador("Cuentas activas", textoActivas, textoTotal, cajaIcono(Tema.icono("banco", 20, Tema.PRIMARIO), Tema.PRIMARIO_SUAVE)));
        indicadores.add(indicador("Saldo total en soles", textoSoles, notaIndicador("Cuentas activas en S/"), cajaTexto("S/", Tema.VERDE_SUAVE, Tema.VERDE)));
        indicadores.add(indicador("Saldo total en dolares", textoDolares, notaIndicador("Cuentas activas en US$"), cajaTexto("US$", Tema.AMBAR_SUAVE, Tema.AMBAR)));
        indicadores.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        add(indicadores);
        add(Box.createVerticalStrut(22));
        add(tarjetaTabla());

        tabla.alinearDerecha(3);
        tabla.anchoColumna(0, 120);
        tabla.anchoColumna(2, 110);
        tabla.anchoColumna(3, 150);
        tabla.anchoColumna(4, 140);
        tabla.renderizar(0, valor -> Tema.etiqueta(String.valueOf(valor), 13, Font.BOLD, Tema.TEXTO));
        tabla.renderizar(3, valor -> Tema.etiqueta(String.valueOf(valor), 13, Font.BOLD, Tema.TEXTO));
        tabla.renderizar(4, valor -> "ACTIVO".equals(valor)
                ? Tema.pastilla("ACTIVO", Tema.VERDE_SUAVE, Tema.VERDE)
                : Tema.pastilla(String.valueOf(valor), Tema.FONDO, Tema.TEXTO_SECUNDARIO));
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabla.getSelectedRow() >= 0) {
                    ventana.mostrarMovimientos(String.valueOf(tabla.getValueAt(tabla.getSelectedRow(), 0)));
                }
            }
        });
        cargar();
    }

    private JPanel tarjetaTabla() {
        Tema.Tarjeta tarjeta = new Tema.Tarjeta();
        tarjeta.setLayout(new BorderLayout());
        tarjeta.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JPanel titulo = new JPanel(new BorderLayout());
        titulo.setOpaque(false);
        titulo.setBorder(BorderFactory.createEmptyBorder(18, 24, 14, 24));
        JLabel textos = Tema.etiqueta("<html><b style='font-size:12px'>Listado de cuentas</b><br><span style='color:#64748B;font-size:9px'>"
                + "Doble clic en una cuenta para ver sus movimientos.</span></html>", 13, Font.PLAIN, Tema.TEXTO);
        titulo.add(textos, BorderLayout.WEST);
        JButton actualizar = Tema.botonSecundario("Actualizar", Tema.icono("actualizar", 15, Tema.PRIMARIO_OSCURO));
        actualizar.addActionListener(e -> {
            cargar();
            ventana.notificar("Cuentas actualizadas", false);
        });
        titulo.add(actualizar, BorderLayout.EAST);

        tarjeta.add(titulo, BorderLayout.NORTH);
        JPanel cuerpo = new JPanel(new BorderLayout());
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
        cuerpo.add(tabla.conDesplazamiento());
        cuerpo.setPreferredSize(new Dimension(0, 300));
        tarjeta.add(cuerpo, BorderLayout.CENTER);
        return tarjeta;
    }

    private void cargar() {
        TareaServicio.ejecutar(() -> Principal.SERVICIO.listarCuentas().getCuentas(), cuentas -> {
            List<Object[]> filas = new ArrayList<>();
            BigDecimal soles = BigDecimal.ZERO;
            BigDecimal dolares = BigDecimal.ZERO;
            int activas = 0;
            for (DatosCuenta c : cuentas) {
                filas.add(new Object[]{c.getCodigo(), c.getCliente(), c.getMoneda(), Formato.dinero(c.getMoneda(), c.getSaldo()), c.getEstado()});
                if (Formato.esActiva(c)) {
                    activas++;
                    if ("S/".equals(Formato.simbolo(c.getMoneda()))) {
                        soles = soles.add(c.getSaldo());
                    } else {
                        dolares = dolares.add(c.getSaldo());
                    }
                }
            }
            tabla.cargar(filas);
            textoActivas.setText(String.valueOf(activas));
            textoTotal.setText("de " + cuentas.size() + " cuentas registradas");
            textoSoles.setText(Formato.dinero("Soles", soles));
            textoDolares.setText(Formato.dinero("Dolares", dolares));
        }, ventana::manejarError);
    }

    // ------------------------------------------------------------------ piezas reutilizables

    static JPanel indicador(String etiqueta, JLabel valor, JLabel nota, JLabel caja) {
        Tema.Tarjeta tarjeta = new Tema.Tarjeta();
        tarjeta.setLayout(new BorderLayout());
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(Tema.etiqueta(etiqueta, 13, Font.PLAIN, Tema.TEXTO_SECUNDARIO));
        textos.add(Box.createVerticalStrut(6));
        textos.add(valor);
        textos.add(nota);
        tarjeta.add(textos, BorderLayout.CENTER);
        if (caja != null) {
            JPanel derecha = new JPanel(new BorderLayout());
            derecha.setOpaque(false);
            derecha.add(caja, BorderLayout.NORTH);
            tarjeta.add(derecha, BorderLayout.EAST);
        }
        return tarjeta;
    }

    static JLabel valorIndicador() {
        return Tema.etiqueta("-", 26, Font.BOLD, Tema.TEXTO);
    }

    static JLabel notaIndicador(String texto) {
        return Tema.etiqueta(texto, 12, Font.PLAIN, Tema.TEXTO_SECUNDARIO);
    }

    static JLabel cajaIcono(javax.swing.Icon icono, Color fondo) {
        JLabel caja = Tema.pastilla("", fondo, Tema.TEXTO);
        caja.setIcon(icono);
        caja.setPreferredSize(new Dimension(44, 44));
        caja.setHorizontalAlignment(SwingConstants.CENTER);
        return redondear(caja, fondo);
    }

    static JLabel cajaTexto(String texto, Color fondo, Color color) {
        JLabel caja = Tema.etiqueta(texto, 13, Font.BOLD, color);
        caja.setPreferredSize(new Dimension(44, 44));
        caja.setHorizontalAlignment(SwingConstants.CENTER);
        return redondear(caja, fondo);
    }

    private static JLabel redondear(JLabel caja, Color fondo) {
        caja.setUI(new javax.swing.plaf.basic.BasicLabelUI() {
            @Override
            public void paint(java.awt.Graphics g, javax.swing.JComponent c) {
                java.awt.Graphics2D g2 = Tema.suavizado(g);
                g2.setColor(fondo);
                g2.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, c.getWidth(), c.getHeight(), 14, 14));
                g2.dispose();
                super.paint(g, c);
            }
        });
        caja.setBorder(BorderFactory.createEmptyBorder());
        return caja;
    }
}
