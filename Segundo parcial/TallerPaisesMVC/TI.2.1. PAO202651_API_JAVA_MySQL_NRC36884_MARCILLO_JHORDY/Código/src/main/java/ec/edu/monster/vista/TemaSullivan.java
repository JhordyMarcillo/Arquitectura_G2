package ec.edu.monster.vista;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Tema visual "Sullivan" (Monsters, Inc.): azul de su pelaje, manchas moradas,
 * cuernos crema y ojos amarillos. Solo afecta al aspecto, no a la logica.
 */
public final class TemaSullivan {

    public static final Color AZUL = new Color(0x5FA8D3);
    public static final Color AZUL_OSCURO = new Color(0x1D5F8A);
    public static final Color NOCHE = new Color(0x0F2A47);
    public static final Color MANCHA = new Color(0x7B4FA3);
    public static final Color MANCHA_CLARO = new Color(0xE9DDF5);
    public static final Color CUERNO = new Color(0xF4E7B7);
    public static final Color OJO = new Color(0xFFD43B);
    public static final Color FONDO = new Color(0xEAF4FB);

    /** Duracion del ciclo completo de la animacion (se esconde, se levanta, mira, se esconde). */
    private static final int CICLO_MS = 8000;

    private TemaSullivan() {
    }

    /** 0 = escondido, 1 = levantado, segun la fase (0..1) del ciclo. */
    static double alturaAsomado(double f) {
        if (f < 0.08) return 0;
        if (f < 0.22) return suave((f - 0.08) / 0.14);
        if (f < 0.76) return 1;
        if (f < 0.90) return 1 - suave((f - 0.76) / 0.14);
        return 0;
    }

    private static double suave(double t) {
        return t * t * (3 - 2 * t);
    }

    /** Instala los colores del tema. Debe llamarse antes de crear la ventana. */
    public static void instalar() {
        try {
            UIManager.put("nimbusBase", AZUL_OSCURO);
            UIManager.put("nimbusBlueGrey", new Color(0x6E93AD));
            UIManager.put("control", FONDO);
            UIManager.put("nimbusFocus", OJO);
            UIManager.put("nimbusSelectionBackground", MANCHA);
            UIManager.put("nimbusSelectedText", Color.WHITE);
            UIManager.put("text", new Color(0x17324D));
            UIManager.put("Table.alternateRowColor", MANCHA_CLARO);
            UIManager.put("Table.background", Color.WHITE);
            UIManager.put("Table.showGrid", Boolean.FALSE);
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // si Nimbus no esta disponible se usa el aspecto por defecto
        }
    }

    /** Banner superior con pelaje azul, manchas moradas y la cara de Sullivan. */
    public static JPanel crearBanner(String titulo, String subtitulo) {
        final long t0 = System.currentTimeMillis();
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g.setPaint(new GradientPaint(0, 0, AZUL_OSCURO, w, 0, AZUL));
                g.fillRect(0, 0, w, h);

                // manchas moradas del pelaje (posiciones relativas al ancho)
                g.setColor(new Color(0x7B, 0x4F, 0xA3, 140));
                double[][] m = {{0.04, 0.35, 34}, {0.19, 0.95, 26}, {0.36, 0.10, 30},
                    {0.52, 0.85, 36}, {0.68, 0.20, 28}, {0.80, 0.90, 30}};
                for (double[] s : m) {
                    g.fill(new Ellipse2D.Double(w * s[0] - s[2] / 2, h * s[1] - s[2] / 2, s[2], s[2]));
                }

                // textos
                g.setColor(CUERNO);
                g.setFont(getFont().deriveFont(Font.BOLD, 24f));
                g.drawString(titulo, 18, 34);
                g.setColor(Color.WHITE);
                g.setFont(getFont().deriveFont(Font.PLAIN, 13f));
                g.drawString(subtitulo, 20, 56);

                // Sullivan se levanta desde el borde inferior, mira a los lados y se esconde
                double fase = ((System.currentTimeMillis() - t0) % CICLO_MS) / (double) CICLO_MS;
                int size = 78;
                double sube = alturaAsomado(fase);          // 0 = escondido, 1 = levantado
                int yBase = 4;
                int y = (int) (yBase + (1 - sube) * (h - yBase + 6));
                double mira = (fase > 0.24 && fase < 0.72) ? Math.sin((fase - 0.24) / 0.48 * Math.PI * 3) * 2.4 : 0;
                dibujarSullivan(g, w - size - 20, y, size, mira, true);

                // franja morada inferior
                g.setColor(MANCHA);
                g.fillRect(0, h - 4, w, 4);
                g.dispose();
            }
        };
        p.setPreferredSize(new Dimension(100, 76));
        p.setOpaque(true);
        new javax.swing.Timer(40, e -> p.repaint()).start();   // animacion (25 cuadros por segundo)
        return p;
    }

    /** Cabeza estilizada de monstruo peludo azul con cuernos y manchas moradas. */
    public static void dibujarSullivan(Graphics2D g0, int x, int y, int size) {
        dibujarSullivan(g0, x, y, size, 0, false);
    }

    /**
     * @param pupilaDx desplazamiento horizontal de las pupilas (para mirar a los lados)
     * @param cuerpo   si es true dibuja tambien el torso debajo de la cabeza
     */
    public static void dibujarSullivan(Graphics2D g0, int x, int y, int size, double pupilaDx, boolean cuerpo) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.translate(x, y);
        double s = size / 100.0;
        g.scale(s, s);
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // torso (cuando se asoma)
        if (cuerpo) {
            Ellipse2D torso = new Ellipse2D.Double(6, 80, 88, 76);
            g.setColor(AZUL);
            g.fill(torso);
            g.setStroke(new BasicStroke(2.5f));
            g.setColor(AZUL_OSCURO);
            g.draw(torso);
            g.setColor(MANCHA);
            g.fill(new Ellipse2D.Double(22, 102, 12, 12));
            g.fill(new Ellipse2D.Double(65, 108, 14, 14));
            g.fill(new Ellipse2D.Double(45, 124, 10, 10));
            g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        }

        // cuernos
        Path2D izq = new Path2D.Double();
        izq.moveTo(27, 24); izq.lineTo(20, 3); izq.lineTo(42, 17); izq.closePath();
        Path2D der = new Path2D.Double();
        der.moveTo(73, 24); der.lineTo(80, 3); der.lineTo(58, 17); der.closePath();
        for (Path2D c : new Path2D[]{izq, der}) {
            g.setColor(CUERNO);
            g.fill(c);
            g.setColor(new Color(0xB79B4A));
            g.draw(c);
        }

        // cabeza
        Ellipse2D cabeza = new Ellipse2D.Double(10, 19, 80, 74);
        g.setColor(AZUL);
        g.fill(cabeza);
        g.setStroke(new BasicStroke(2.5f));
        g.setColor(AZUL_OSCURO);
        g.draw(cabeza);

        // manchas
        g.setColor(MANCHA);
        double[][] spots = {{27, 34, 7}, {50, 22, 5}, {74, 34, 7}, {17, 62, 5}, {84, 62, 5}};
        for (double[] sp : spots) {
            g.fill(new Ellipse2D.Double(sp[0] - sp[2], sp[1] - sp[2], sp[2] * 2, sp[2] * 2));
        }

        // ojos
        g.setStroke(new BasicStroke(1.5f));
        for (int cx : new int[]{37, 63}) {
            Ellipse2D ojo = new Ellipse2D.Double(cx - 8, 39, 16, 18);
            g.setColor(Color.WHITE);
            g.fill(ojo);
            g.setColor(new Color(0x1D3F5C));
            g.draw(ojo);
            g.fill(new Ellipse2D.Double(cx - 4 + (cx < 50 ? 1.5 : -1.5) + pupilaDx, 46, 8, 8));
        }

        // nariz y boca con colmillos
        g.setColor(new Color(0x1D3F5C));
        g.fill(new Ellipse2D.Double(42, 54.5, 16, 11));
        Path2D boca = new Path2D.Double();
        boca.moveTo(29, 71); boca.quadTo(50, 92, 71, 71); boca.quadTo(50, 77, 29, 71);
        g.setColor(new Color(0x3B1F4D));
        g.fill(boca);
        Path2D dientes = new Path2D.Double();
        dientes.moveTo(40, 75); dientes.lineTo(43, 81); dientes.lineTo(46, 77); dientes.closePath();
        dientes.moveTo(54, 77); dientes.lineTo(57, 81); dientes.lineTo(60, 75); dientes.closePath();
        g.setColor(Color.WHITE);
        g.fill(dientes);
        g.dispose();
    }

    /** Icono de la ventana. */
    public static Image icono() {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        dibujarSullivan(g, 0, 0, 64);
        g.dispose();
        return img;
    }

    /** Encabezado morado y filas alternadas con la paleta del tema. */
    public static void estilizarTabla(JTable tabla) {
        tabla.setRowHeight(24);
        tabla.setGridColor(MANCHA_CLARO);
        tabla.setSelectionBackground(MANCHA);
        tabla.setSelectionForeground(Color.WHITE);
        JTableHeader cab = tabla.getTableHeader();
        cab.setReorderingAllowed(false);
        cab.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                    boolean foco, int fila, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, false, false, fila, col);
                l.setBackground(MANCHA);
                l.setForeground(Color.WHITE);
                l.setFont(l.getFont().deriveFont(Font.BOLD));
                l.setOpaque(true);
                l.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return l;
            }
        });
    }
}
