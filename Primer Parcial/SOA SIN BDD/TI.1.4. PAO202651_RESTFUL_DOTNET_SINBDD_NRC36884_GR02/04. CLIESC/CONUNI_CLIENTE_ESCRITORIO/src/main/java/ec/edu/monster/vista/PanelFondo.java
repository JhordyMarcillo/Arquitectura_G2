package ec.edu.monster.vista;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import javax.swing.JPanel;

// Panel que dibuja una imagen estirada como fondo (fondoes.jpg)
public class PanelFondo extends JPanel {

    private final Image fondo;

    public PanelFondo(Image fondo, LayoutManager layout) {
        super(layout);
        this.fondo = fondo;
        setBackground(new Color(0xF0F0F0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fondo != null) {
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
