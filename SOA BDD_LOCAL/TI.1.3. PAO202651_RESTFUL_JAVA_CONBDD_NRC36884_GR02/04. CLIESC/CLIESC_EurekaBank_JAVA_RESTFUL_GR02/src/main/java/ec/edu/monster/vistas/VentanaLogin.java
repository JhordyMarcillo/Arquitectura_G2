package ec.edu.monster.vistas;

import ec.edu.monster.Principal;
import ec.edu.monster.controladores.TareaServicio;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/** Ventana de inicio de sesion: panel institucional a la izquierda y formulario en tarjeta a la derecha. */
public class VentanaLogin extends JFrame {

    private final JTextField campoUsuario = Tema.campo("Usuario");
    private final JPasswordField campoClave = Tema.campoClave("Clave");
    private final JTextField campoServidor = Tema.campo("URL del servidor");
    private final JLabel textoError = new JLabel();
    private final JPanel panelError = new Tema.Tarjeta(Tema.ROJO_SUAVE);
    private final JButton botonIngresar = Tema.botonPrimario("Ingresar");

    public VentanaLogin() {
        super("EurekaBank - Iniciar sesion");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImage(imagenLogo());
        setSize(980, 620);
        setMinimumSize(new Dimension(860, 560));
        setLocationRelativeTo(null);

        JPanel contenido = new JPanel(new GridLayout(1, 2));
        contenido.add(panelInstitucional());
        contenido.add(panelFormulario());
        setContentPane(contenido);

        campoServidor.setText(Principal.SERVICIO.getUrlServidor());
        campoServidor.setFont(Tema.fuente(Font.PLAIN, 12));
        campoServidor.setForeground(Tema.TEXTO_SECUNDARIO);
        getRootPane().setDefaultButton(botonIngresar);
        botonIngresar.addActionListener(e -> ingresar());
    }

    private JPanel panelInstitucional() {
        JPanel panel = new Tema.PanelDegradado(Tema.MARINO, Tema.PRIMARIO_OSCURO, 0);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(44, 44, 32, 44));

        JLabel marca = Tema.etiqueta("<html><b style='font-size:15px'>EurekaBank</b><br>"
                + "<span style='color:#CBD5E1;font-size:9px'>Sistema de cuentas de ahorro</span></html>", 15, Font.PLAIN, Color.WHITE);
        marca.setIcon(Tema.logo(44));
        marca.setIconTextGap(14);
        panel.add(marca);
        panel.add(Box.createVerticalGlue());
        panel.add(Tema.etiqueta("<html>Banca segura,<br>siempre a la mano.</html>", 32, Font.BOLD, Color.WHITE));
        panel.add(Box.createVerticalStrut(16));
        panel.add(Tema.etiqueta("<html><div style='width:300px'>Consulte cuentas y movimientos, y registre depositos, "
                + "retiros y transferencias en tiempo real.</div></html>", 14, Font.PLAIN, Tema.TEXTO_CLARO));
        panel.add(Box.createVerticalStrut(24));
        for (String punto : new String[]{"Operaciones transaccionales con validacion de saldo",
            "Sesiones protegidas con token", "Compatible con cualquier servidor del grupo"}) {
            JLabel item = Tema.etiqueta(punto, 13, Font.PLAIN, Tema.TEXTO_CLARO);
            item.setIcon(Tema.icono("check", 15, new Color(0x93C5FD)));
            item.setIconTextGap(10);
            item.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            panel.add(item);
        }
        panel.add(Box.createVerticalGlue());
        panel.add(Tema.etiqueta(Principal.SUBTITULO, 12, Font.PLAIN, Tema.TEXTO_SECUNDARIO));
        return panel;
    }

    private JPanel panelFormulario() {
        JPanel fondo = new JPanel(new GridBagLayout());
        fondo.setBackground(Tema.FONDO);

        Tema.Tarjeta tarjeta = new Tema.Tarjeta();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(34, 36, 34, 36));
        tarjeta.setPreferredSize(new Dimension(400, 500));

        tarjeta.add(Tema.etiqueta("Bienvenido", 24, Font.BOLD, Tema.TEXTO));
        tarjeta.add(Tema.etiqueta("Ingrese con su usuario de empleado.", 13, Font.PLAIN, Tema.TEXTO_SECUNDARIO));
        tarjeta.add(Box.createVerticalStrut(24));
        agregarCampo(tarjeta, "Usuario", campoUsuario);
        agregarCampo(tarjeta, "Clave", campoClave);
        agregarCampo(tarjeta, "Servidor", campoServidor);

        panelError.setLayout(new BorderLayout());
        panelError.setBorder(BorderFactory.createEmptyBorder(8, 12, 10, 12));
        textoError.setForeground(new Color(0x991B1B));
        textoError.setFont(Tema.fuente(Font.PLAIN, 13));
        panelError.add(textoError);
        panelError.setAlignmentX(LEFT_ALIGNMENT);
        panelError.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        panelError.setVisible(false);
        tarjeta.add(panelError);
        tarjeta.add(Box.createVerticalStrut(14));

        tarjeta.add(botonIngresar);
        tarjeta.add(Box.createVerticalStrut(14));
        JLabel prueba = Tema.etiqueta("Usuario de prueba: MONSTER / Monster9", 12, Font.PLAIN, Tema.TEXTO_SECUNDARIO);
        prueba.setAlignmentX(LEFT_ALIGNMENT);
        tarjeta.add(prueba);

        fondo.add(tarjeta);
        return fondo;
    }

    private static void agregarCampo(JPanel panel, String rotulo, JTextField campo) {
        panel.add(Tema.rotulo(rotulo));
        panel.add(campo);
        panel.add(Box.createVerticalStrut(14));
    }

    private void ingresar() {
        String usuario = campoUsuario.getText().trim();
        String clave = new String(campoClave.getPassword());
        if (usuario.isEmpty() || clave.isEmpty()) {
            mostrarError("Ingrese el usuario y la clave.");
            return;
        }
        botonIngresar.setEnabled(false);
        botonIngresar.setText("Verificando...");
        panelError.setVisible(false);
        Principal.SERVICIO.setUrlServidor(campoServidor.getText());

        TareaServicio.ejecutar(() -> Principal.SERVICIO.iniciarSesion(usuario, clave),
                sesion -> {
                    Principal.guardarUrlServidor();
                    new VentanaPrincipal().setVisible(true);
                    dispose();
                },
                error -> {
                    mostrarError(error.getMessage());
                    campoClave.setText("");
                    botonIngresar.setEnabled(true);
                    botonIngresar.setText("Ingresar");
                });
    }

    private void mostrarError(String mensaje) {
        textoError.setText(mensaje);
        panelError.setVisible(true);
        revalidate();
    }

    public static BufferedImage imagenLogo() {
        BufferedImage imagen = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Tema.logo(64).paintIcon(null, imagen.getGraphics(), 0, 0);
        return imagen;
    }
}
