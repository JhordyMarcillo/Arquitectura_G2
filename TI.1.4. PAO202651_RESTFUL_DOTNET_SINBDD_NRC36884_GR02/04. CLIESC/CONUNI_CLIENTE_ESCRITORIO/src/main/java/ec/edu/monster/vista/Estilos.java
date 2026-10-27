package ec.edu.monster.vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicButtonUI;

// Colores y fuentes del cliente de escritorio original (Tkinter)
public final class Estilos {

    // Login
    public static final Color FONDO_LOGIN = new Color(0xE8F4F8);
    public static final Color TITULO_LOGIN = new Color(0x1A5490);
    public static final Color BOTON_LOGIN = new Color(0x4A90D9);
    public static final Color BOTON_LOGIN_HOVER = new Color(0x3A7AC0);
    public static final Color BOTON_CANCELAR = new Color(0x87CEEB);
    public static final Color BOTON_CANCELAR_HOVER = new Color(0x6FB9D9);
    public static final Color TEXTO_CAMPO = new Color(0x333333);

    // Calculadora
    public static final Color ENCABEZADO = new Color(0x1976D2);
    public static final Color TITULO_SECCION = new Color(0x64B5F6);
    public static final Color ETIQUETA = new Color(0x1565C0);
    public static final Color SEPARADOR = new Color(0x42A5F5);
    public static final Color BOTON_CONVERTIR = new Color(0x1976D2);
    public static final Color BOTON_CONVERTIR_HOVER = new Color(0x1565C0);
    public static final Color BOTON_LIMPIAR = new Color(0xFF9800);
    public static final Color BOTON_LIMPIAR_HOVER = new Color(0xF57C00);
    public static final Color BOTON_SESION = new Color(0x455A64);
    public static final Color BOTON_SESION_HOVER = new Color(0x37474F);
    public static final Color FONDO_RESULTADO = new Color(0xC8E6C9);
    public static final Color BORDE_RESULTADO = new Color(0x81C784);
    public static final Color TITULO_RESULTADO = new Color(0x2E7D32);
    public static final Color VALOR_RESULTADO = new Color(0x1B5E20);

    // Barra de estado
    public static final Color ESTADO_INFO = new Color(0x2196F3);
    public static final Color ESTADO_EXITO = new Color(0x4CAF50);
    public static final Color ESTADO_ERROR = new Color(0xF44336);
    public static final Color ESTADO_AVISO = new Color(0xFF9800);

    public static final String FUENTE = "Segoe UI";

    private Estilos() {
    }

    public static Font fuente(int estilo, int tamanio) {
        return new Font(FUENTE, estilo, tamanio);
    }

    public static JLabel etiqueta(String texto, int estilo, int tamanio, Color color) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(fuente(estilo, tamanio));
        etiqueta.setForeground(color);
        return etiqueta;
    }

    // Boton plano con color propio y efecto hover (BasicButtonUI respeta el color de fondo)
    public static JButton boton(String texto, Color fondo, Color hover, int tamanio, Dimension medida) {
        JButton boton = new JButton(texto);
        boton.setUI(new BasicButtonUI());
        boton.setFont(fuente(Font.BOLD, tamanio));
        boton.setForeground(Color.WHITE);
        boton.setBackground(fondo);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (medida != null) {
            boton.setPreferredSize(medida);
            boton.setMaximumSize(medida);
        }
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(fondo);
            }
        });
        return boton;
    }

    // Imagen de src/main/resources/imagenes (null si no existe)
    public static Image imagen(String nombre) {
        URL url = Estilos.class.getResource("/imagenes/" + nombre);
        return url == null ? null : new ImageIcon(url).getImage();
    }
}
