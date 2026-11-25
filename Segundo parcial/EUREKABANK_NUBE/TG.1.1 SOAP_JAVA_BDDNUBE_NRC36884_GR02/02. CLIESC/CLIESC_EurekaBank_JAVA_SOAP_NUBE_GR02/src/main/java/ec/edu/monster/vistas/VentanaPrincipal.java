package ec.edu.monster.vistas;

import ec.edu.monster.Principal;
import ec.edu.monster.modelos.ExcepcionServicio;
import ec.edu.monster.modelos.RespuestaAutenticacion;
import ec.edu.monster.utilidades.Formato;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;

/** Ventana principal: barra lateral de navegacion, encabezado de pagina y area de contenido. */
public class VentanaPrincipal extends JFrame {

    public enum Operacion { DEPOSITO, RETIRO, TRANSFERENCIA }

    private final Map<String, JButton> menu = new LinkedHashMap<>();
    private final JLabel textoTitulo = Tema.etiqueta("", 24, Font.BOLD, Tema.TEXTO);
    private final JLabel textoSubtitulo = Tema.etiqueta("", 13, Font.PLAIN, Tema.TEXTO_SECUNDARIO);
    private final JPanel contenedor = new PanelAjustado();
    private final JLabel notificacion = new JLabel();
    private final Timer temporizador = new Timer(4000, e -> notificacion.setVisible(false));

    public VentanaPrincipal() {
        super("EurekaBank");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImage(VentanaLogin.imagenLogo());
        setSize(1240, 780);
        setMinimumSize(new Dimension(1080, 680));
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(barraLateral(), BorderLayout.WEST);
        raiz.add(areaContenido(), BorderLayout.CENTER);
        setContentPane(raiz);
        prepararNotificacion();
        temporizador.setRepeats(false);

        mostrarCuentas();
    }

    /** Contenedor que sigue el ancho visible: el contenido se adapta y solo hay desplazamiento vertical. */
    private static class PanelAjustado extends JPanel implements javax.swing.Scrollable {
        PanelAjustado() {
            super(new BorderLayout());
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(java.awt.Rectangle visible, int orientacion, int direccion) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(java.awt.Rectangle visible, int orientacion, int direccion) {
            return visible.height;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    // ------------------------------------------------------------------ estructura

    private JPanel barraLateral() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Tema.MARINO);
        barra.setPreferredSize(new Dimension(248, 0));

        JPanel arriba = new JPanel();
        arriba.setOpaque(false);
        arriba.setLayout(new BoxLayout(arriba, BoxLayout.Y_AXIS));
        arriba.setBorder(BorderFactory.createEmptyBorder(26, 12, 0, 12));
        JLabel marca = Tema.etiqueta("<html><b style='font-size:13px'>EurekaBank</b><br>"
                + "<span style='color:#94A3B8;font-size:8px'>Panel de operaciones</span></html>", 13, Font.PLAIN, Color.WHITE);
        marca.setIcon(Tema.logo(36));
        marca.setIconTextGap(12);
        marca.setBorder(BorderFactory.createEmptyBorder(0, 12, 26, 0));
        arriba.add(marca);

        arriba.add(seccion("CONSULTAS"));
        arriba.add(boton("cuentas", "Cuentas", "banco", this::mostrarCuentas));
        arriba.add(boton("movimientos", "Movimientos", "historial", () -> mostrarMovimientos(null)));
        arriba.add(Box.createVerticalStrut(14));
        arriba.add(seccion("OPERACIONES"));
        arriba.add(boton("deposito", "Deposito", "deposito", () -> mostrarOperacion(Operacion.DEPOSITO)));
        arriba.add(boton("retiro", "Retiro", "retiro", () -> mostrarOperacion(Operacion.RETIRO)));
        arriba.add(boton("transferencia", "Transferencia", "transferencia", () -> mostrarOperacion(Operacion.TRANSFERENCIA)));
        barra.add(arriba, BorderLayout.NORTH);
        barra.add(tarjetaUsuario(), BorderLayout.SOUTH);
        return barra;
    }

    private static JLabel seccion(String texto) {
        JLabel etiqueta = Tema.etiqueta(texto, 11, Font.BOLD, Tema.TEXTO_SECUNDARIO);
        etiqueta.setBorder(BorderFactory.createEmptyBorder(0, 16, 8, 0));
        return etiqueta;
    }

    private JButton boton(String clave, String texto, String icono, Runnable accion) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                boolean activo = Boolean.TRUE.equals(getClientProperty("activo"));
                if (activo || getModel().isRollover()) {
                    Graphics2D g2 = Tema.suavizado(g);
                    g2.setColor(activo ? Tema.PRIMARIO : Tema.MARINO_CLARO);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        boton.setIcon(Tema.icono(icono, 18, Tema.TEXTO_CLARO));
        boton.setIconTextGap(12);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setFont(Tema.fuente(Font.PLAIN, 14));
        boton.setForeground(Tema.TEXTO_CLARO);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setRolloverEnabled(true);
        boton.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        boton.setPreferredSize(new Dimension(224, 44));
        boton.setAlignmentX(LEFT_ALIGNMENT);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.addActionListener(e -> accion.run());
        menu.put(clave, boton);
        return boton;
    }

    private JPanel tarjetaUsuario() {
        RespuestaAutenticacion sesion = Principal.SERVICIO.getSesion();
        JPanel tarjeta = new Tema.Tarjeta(Tema.MARINO_CLARO);
        tarjeta.setLayout(new BorderLayout(10, 0));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(12, 12, 14, 8));

        JLabel avatar = new JLabel(Formato.iniciales(sesion.getNombreEmpleado()), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = Tema.suavizado(g);
                g2.setPaint(new java.awt.GradientPaint(0, 0, new Color(0x2563EB), getWidth(), getHeight(), Tema.PRIMARIO_OSCURO));
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setPreferredSize(new Dimension(38, 38));
        avatar.setForeground(Color.WHITE);
        avatar.setFont(Tema.fuente(Font.BOLD, 13));

        JLabel datos = Tema.etiqueta("<html><b>" + sesion.getNombreEmpleado() + "</b><br><span style='color:#94A3B8;font-size:8px'>Empleado "
                + sesion.getCodigoEmpleado() + "</span></html>", 13, Font.PLAIN, Color.WHITE);

        JButton salir = new JButton(Tema.icono("salir", 17, new Color(0x94A3B8)));
        salir.setToolTipText("Cerrar sesion");
        salir.setContentAreaFilled(false);
        salir.setBorderPainted(false);
        salir.setFocusPainted(false);
        salir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        salir.addActionListener(e -> volverAlLogin());

        tarjeta.add(avatar, BorderLayout.WEST);
        tarjeta.add(datos, BorderLayout.CENTER);
        tarjeta.add(salir, BorderLayout.EAST);

        JPanel margen = new JPanel(new BorderLayout());
        margen.setOpaque(false);
        margen.setBorder(BorderFactory.createEmptyBorder(0, 12, 18, 12));
        margen.add(tarjeta);
        return margen;
    }

    private JPanel areaContenido() {
        JPanel area = new JPanel(new BorderLayout());
        area.setBackground(Tema.FONDO);

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(Color.WHITE);
        cabecera.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.BORDE),
                BorderFactory.createEmptyBorder(20, 36, 20, 36)));
        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));
        titulos.add(textoTitulo);
        titulos.add(textoSubtitulo);
        cabecera.add(titulos, BorderLayout.WEST);
        JLabel insignia = Tema.pastilla("●  " + Principal.SUBTITULO, Tema.FONDO, Tema.TEXTO_SECUNDARIO);
        insignia.setFont(Tema.fuente(Font.PLAIN, 12));
        insignia.setToolTipText(Principal.SERVICIO.getUrlServidor());
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
        derecha.setOpaque(false);
        derecha.add(insignia);
        cabecera.add(derecha, BorderLayout.EAST);

        contenedor.setOpaque(false);
        contenedor.setBorder(BorderFactory.createEmptyBorder(26, 36, 26, 36));
        JScrollPane desplazamiento = new JScrollPane(contenedor);
        desplazamiento.setBorder(BorderFactory.createEmptyBorder());
        desplazamiento.getViewport().setBackground(Tema.FONDO);
        desplazamiento.getVerticalScrollBar().setUnitIncrement(16);

        area.add(cabecera, BorderLayout.NORTH);
        area.add(desplazamiento, BorderLayout.CENTER);
        return area;
    }

    private void prepararNotificacion() {
        notificacion.setOpaque(false);
        notificacion.setForeground(Color.WHITE);
        notificacion.setFont(Tema.fuente(Font.PLAIN, 13));
        notificacion.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        notificacion.setIconTextGap(12);
        notificacion.setVisible(false);
        notificacion.setUI(new javax.swing.plaf.basic.BasicLabelUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = Tema.suavizado(g);
                g2.setColor(Tema.MARINO);
                g2.fill(new RoundRectangle2D.Float(0, 0, c.getWidth(), c.getHeight(), 16, 16));
                g2.dispose();
                super.paint(g, c);
            }
        });
        getLayeredPane().add(notificacion, JLayeredPane.POPUP_LAYER);
    }

    // ------------------------------------------------------------------ navegacion

    public void mostrarCuentas() {
        navegar("cuentas", "Cuentas", "Cartera de cuentas de ahorro y su estado actual.", new PanelCuentas(this));
    }

    public void mostrarMovimientos(String cuenta) {
        navegar("movimientos", "Movimientos", "Historial de movimientos y saldo de una cuenta.", new PanelMovimientos(this, cuenta));
    }

    public void mostrarOperacion(Operacion tipo) {
        switch (tipo) {
            case DEPOSITO -> navegar("deposito", "Deposito", "Abono de efectivo a una cuenta activa.", new PanelOperacion(this, tipo));
            case RETIRO -> navegar("retiro", "Retiro", "Retiro de efectivo validado con la clave de la cuenta.", new PanelOperacion(this, tipo));
            default -> navegar("transferencia", "Transferencia", "Traspaso entre cuentas de la misma moneda.", new PanelOperacion(this, tipo));
        }
    }

    private void navegar(String clave, String titulo, String subtitulo, JPanel pagina) {
        menu.forEach((nombre, boton) -> {
            boolean activo = nombre.equals(clave);
            boton.putClientProperty("activo", activo);
            boton.setForeground(activo ? Color.WHITE : Tema.TEXTO_CLARO);
            boton.repaint();
        });
        textoTitulo.setText(titulo);
        textoSubtitulo.setText(subtitulo);
        contenedor.removeAll();
        contenedor.add(pagina, BorderLayout.NORTH);
        contenedor.revalidate();
        contenedor.repaint();
    }

    // ------------------------------------------------------------------ mensajes

    public void notificar(String mensaje, boolean esError) {
        notificacion.setText(mensaje);
        notificacion.setIcon(Tema.icono(esError ? "x" : "check", 16, esError ? new Color(0xF87171) : new Color(0x34D399)));
        Dimension tamano = notificacion.getPreferredSize();
        notificacion.setBounds(getLayeredPane().getWidth() - tamano.width - 32, getLayeredPane().getHeight() - tamano.height - 28,
                tamano.width, tamano.height);
        notificacion.setVisible(true);
        temporizador.restart();
    }

    /** Muestra el error; si la sesion expiro vuelve al inicio de sesion. */
    public void manejarError(ExcepcionServicio error) {
        if (error.requiereLogin()) {
            JOptionPane.showMessageDialog(this, error.getMessage() + "\n\nInicie sesion nuevamente.", "EurekaBank",
                    JOptionPane.INFORMATION_MESSAGE);
            volverAlLogin();
            return;
        }
        notificar(error.getMessage(), true);
    }

    private void volverAlLogin() {
        Principal.SERVICIO.cerrarSesion();
        new VentanaLogin().setVisible(true);
        dispose();
    }
}
