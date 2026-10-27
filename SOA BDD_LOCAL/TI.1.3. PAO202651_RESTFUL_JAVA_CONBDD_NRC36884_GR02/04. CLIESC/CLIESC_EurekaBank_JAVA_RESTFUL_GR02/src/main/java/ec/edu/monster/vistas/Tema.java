package ec.edu.monster.vistas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/** Identidad visual EurekaBank para Swing: paleta, tipografia, componentes e iconos vectoriales. */
public final class Tema {

    public static final Color MARINO = new Color(0x0F172A);
    public static final Color MARINO_CLARO = new Color(0x1E293B);
    public static final Color PRIMARIO = new Color(0x1D4ED8);
    public static final Color PRIMARIO_OSCURO = new Color(0x1E3A8A);
    public static final Color PRIMARIO_SUAVE = new Color(0xDBEAFE);
    public static final Color FONDO = new Color(0xF1F5F9);
    public static final Color BORDE = new Color(0xE2E8F0);
    public static final Color TEXTO = new Color(0x0F172A);
    public static final Color TEXTO_SECUNDARIO = new Color(0x64748B);
    public static final Color TEXTO_CLARO = new Color(0xCBD5E1);
    public static final Color VERDE = new Color(0x059669);
    public static final Color VERDE_SUAVE = new Color(0xD1FAE5);
    public static final Color ROJO = new Color(0xDC2626);
    public static final Color ROJO_SUAVE = new Color(0xFEE2E2);
    public static final Color AMBAR = new Color(0xD97706);
    public static final Color AMBAR_SUAVE = new Color(0xFEF3C7);

    private static final String FUENTE = "Segoe UI";

    private Tema() {
    }

    public static Font fuente(int estilo, float tamano) {
        return new Font(FUENTE, estilo, Math.round(tamano));
    }

    // ------------------------------------------------------------------ componentes

    public static JLabel etiqueta(String texto, float tamano, int estilo, Color color) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(fuente(estilo, tamano));
        etiqueta.setForeground(color);
        return etiqueta;
    }

    /** Etiqueta pequena en mayusculas sobre un campo. */
    public static JLabel rotulo(String texto) {
        JLabel rotulo = etiqueta(texto.toUpperCase(), 11, Font.BOLD, TEXTO_SECUNDARIO);
        rotulo.setBorder(BorderFactory.createEmptyBorder(0, 2, 6, 0));
        rotulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return rotulo;
    }

    public static JTextField campo(String marcador) {
        JTextField campo = new JTextField();
        prepararCampo(campo, marcador);
        return campo;
    }

    public static JPasswordField campoClave(String marcador) {
        JPasswordField campo = new JPasswordField();
        prepararCampo(campo, marcador);
        return campo;
    }

    private static void prepararCampo(JTextField campo, String marcador) {
        campo.setFont(fuente(Font.PLAIN, 14));
        campo.putClientProperty("JTextField.placeholderText", marcador);
        campo.putClientProperty("JComponent.roundRect", true);
        campo.setPreferredSize(new Dimension(200, 42));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    public static JButton botonPrimario(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(fuente(Font.BOLD, 14));
        boton.setBackground(PRIMARIO);
        boton.setForeground(Color.WHITE);
        boton.putClientProperty("JButton.buttonType", "roundRect");
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(160, 44));
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        return boton;
    }

    public static JButton botonSecundario(String texto, Icon icono) {
        JButton boton = botonPrimario(texto);
        boton.setIcon(icono);
        boton.setIconTextGap(8);
        boton.setBackground(PRIMARIO_SUAVE);
        boton.setForeground(PRIMARIO_OSCURO);
        boton.setFont(fuente(Font.BOLD, 13));
        boton.setPreferredSize(new Dimension(140, 36));
        boton.setMaximumSize(new Dimension(160, 36));
        return boton;
    }

    /** Panel blanco con esquinas redondeadas, borde suave y sombra ligera. */
    public static class Tarjeta extends JPanel {
        private final Color fondo;

        public Tarjeta() {
            this(Color.WHITE);
        }

        public Tarjeta(Color fondo) {
            this.fondo = fondo;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = suavizado(g);
            for (int i = 4; i >= 1; i--) {
                g2.setColor(new Color(15, 23, 42, 4 * (5 - i)));
                g2.fill(new RoundRectangle2D.Float(i - 1, i + 1, getWidth() - 2 * (i - 1) - 1, getHeight() - i - 2, 18, 18));
            }
            g2.setColor(fondo);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 4, 16, 16));
            if (fondo.equals(Color.WHITE)) {
                g2.setColor(BORDE);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 4, 16, 16));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Panel con degradado de marca (azul a azul marino). */
    public static class PanelDegradado extends JPanel {
        private final Color inicio;
        private final Color fin;
        private final int radio;

        public PanelDegradado(Color inicio, Color fin, int radio) {
            this.inicio = inicio;
            this.fin = fin;
            this.radio = radio;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = suavizado(g);
            g2.setPaint(new GradientPaint(0, 0, inicio, getWidth() * 0.6f, getHeight(), fin));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radio, radio));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Etiqueta con fondo redondeado (estado ACTIVO / CANCELADO). */
    public static JLabel pastilla(String texto, Color fondo, Color color) {
        JLabel etiqueta = new JLabel(texto, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = suavizado(g);
                g2.setColor(fondo);
                g2.fill(new RoundRectangle2D.Float(0, 2, getWidth(), getHeight() - 4, getHeight() - 4, getHeight() - 4));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        etiqueta.setFont(fuente(Font.BOLD, 11));
        etiqueta.setForeground(color);
        etiqueta.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        return etiqueta;
    }

    public static Graphics2D suavizado(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        return g2;
    }

    // ------------------------------------------------------------------ logo e iconos vectoriales

    /** Logo EurekaBank: cuadro redondeado con degradado y una "E". */
    public static Icon logo(int tamano) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = suavizado(g);
                g2.translate(x, y);
                float k = tamano / 64f;
                g2.scale(k, k);
                g2.setPaint(new GradientPaint(0, 0, new Color(0x2563EB), 64, 64, PRIMARIO_OSCURO));
                g2.fill(new RoundRectangle2D.Float(0, 0, 64, 64, 24, 24));
                g2.setColor(Color.WHITE);
                Path2D e = new Path2D.Float();
                e.moveTo(18, 16); e.lineTo(46, 16); e.lineTo(46, 23); e.lineTo(26, 23); e.lineTo(26, 29); e.lineTo(42, 29);
                e.lineTo(42, 35); e.lineTo(26, 35); e.lineTo(26, 41); e.lineTo(46, 41); e.lineTo(46, 48); e.lineTo(18, 48); e.closePath();
                g2.fill(e);
                g2.setColor(new Color(0x93C5FD));
                g2.fillRect(48, 16, 4, 32);
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return tamano;
            }

            @Override
            public int getIconHeight() {
                return tamano;
            }
        };
    }

    /** Iconos de trazo (estilo Lucide) dibujados en una cuadricula de 24x24. */
    public static Icon icono(String nombre, int tamano, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = suavizado(g);
                g2.translate(x, y);
                float k = tamano / 24f;
                g2.scale(k, k);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                Path2D p = new Path2D.Float();
                switch (nombre) {
                    case "banco" -> {
                        linea(p, 3, 21, 21, 21); linea(p, 5, 21, 5, 10); linea(p, 19, 21, 19, 10);
                        linea(p, 9, 21, 9, 15); linea(p, 15, 21, 15, 15);
                        p.moveTo(2, 10); p.lineTo(12, 4); p.lineTo(22, 10);
                    }
                    case "historial" -> {
                        p.append(new java.awt.geom.Arc2D.Float(3, 3, 18, 18, 140, 310, java.awt.geom.Arc2D.OPEN), false);
                        p.moveTo(3, 3); p.lineTo(3, 8); p.lineTo(8, 8);
                        p.moveTo(12, 7); p.lineTo(12, 12); p.lineTo(15, 14);
                    }
                    case "deposito" -> {
                        linea(p, 12, 3, 12, 15); p.moveTo(7, 10); p.lineTo(12, 15); p.lineTo(17, 10); linea(p, 5, 21, 19, 21);
                    }
                    case "retiro" -> {
                        linea(p, 12, 21, 12, 9); p.moveTo(7, 14); p.lineTo(12, 9); p.lineTo(17, 14); linea(p, 5, 3, 19, 3);
                    }
                    case "transferencia" -> {
                        p.moveTo(7, 7); p.lineTo(20, 7); p.lineTo(16, 3); p.moveTo(17, 17); p.lineTo(4, 17); p.lineTo(8, 21);
                    }
                    case "salir" -> {
                        p.moveTo(9, 21); p.lineTo(5, 21); p.quadTo(3, 21, 3, 19); p.lineTo(3, 5); p.quadTo(3, 3, 5, 3); p.lineTo(9, 3);
                        p.moveTo(16, 17); p.lineTo(21, 12); p.lineTo(16, 7); linea(p, 21, 12, 9, 12);
                    }
                    case "buscar" -> {
                        p.append(new java.awt.geom.Ellipse2D.Float(4, 4, 14, 14), false); linea(p, 21, 21, 16.5f, 16.5f);
                    }
                    case "actualizar" -> {
                        p.append(new java.awt.geom.Arc2D.Float(3, 3, 18, 18, 40, -310, java.awt.geom.Arc2D.OPEN), false);
                        p.moveTo(21, 3); p.lineTo(21, 8); p.lineTo(16, 8);
                    }
                    case "check" -> {
                        p.moveTo(20, 6); p.lineTo(9, 17); p.lineTo(4, 12);
                    }
                    case "x" -> {
                        linea(p, 18, 6, 6, 18); linea(p, 6, 6, 18, 18);
                    }
                    case "arriba" -> {
                        linea(p, 12, 19, 12, 5); p.moveTo(5, 12); p.lineTo(12, 5); p.lineTo(19, 12);
                    }
                    case "abajo" -> {
                        linea(p, 12, 5, 12, 19); p.moveTo(19, 12); p.lineTo(12, 19); p.lineTo(5, 12);
                    }
                    default -> {
                        p.append(new java.awt.geom.Ellipse2D.Float(2, 2, 20, 20), false); linea(p, 12, 16, 12, 12); linea(p, 12, 8, 12.01f, 8);
                    }
                }
                g2.draw(p);
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return tamano;
            }

            @Override
            public int getIconHeight() {
                return tamano;
            }
        };
    }

    private static void linea(Path2D p, float x1, float y1, float x2, float y2) {
        p.moveTo(x1, y1);
        p.lineTo(x2, y2);
    }
}
