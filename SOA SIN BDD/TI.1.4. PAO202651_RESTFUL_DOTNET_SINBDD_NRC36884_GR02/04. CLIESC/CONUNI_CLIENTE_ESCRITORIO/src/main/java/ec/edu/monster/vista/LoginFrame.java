package ec.edu.monster.vista;

import ec.edu.monster.controlador.ApiService;
import ec.edu.monster.modelo.ExcepcionApi;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class LoginFrame extends JFrame {

    private final ApiService apiService = new ApiService();

    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnLogin;

    public LoginFrame() {
        this(null);
    }

    // mensaje: aviso a mostrar al abrir (por ejemplo, sesion expirada)
    public LoginFrame(String mensaje) {
        setTitle("Login - Sistema de Conversión de Unidades");
        setSize(480, 580);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        crearInterfaz();

        if (mensaje != null) {
            addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowOpened(java.awt.event.WindowEvent e) {
                    JOptionPane.showMessageDialog(LoginFrame.this, mensaje,
                            "Sesión expirada", JOptionPane.WARNING_MESSAGE);
                }
            });
        }
    }

    private void crearInterfaz() {
        JPanel principal = new JPanel();
        principal.setLayout(new BoxLayout(principal, BoxLayout.Y_AXIS));
        principal.setBackground(Estilos.FONDO_LOGIN);
        principal.setBorder(BorderFactory.createEmptyBorder(25, 20, 20, 20));

        // Logo circular
        Image logo = Estilos.imagen("logo_login.png");
        if (logo != null) {
            agregarCentrado(principal, new JLabel(new ImageIcon(crearImagenCircular(logo, 150))));
            principal.add(Box.createVerticalStrut(15));
        }

        agregarCentrado(principal, Estilos.etiqueta("Sistema de Conversión de Unidades", Font.BOLD, 16, Estilos.TITULO_LOGIN));
        principal.add(Box.createVerticalStrut(10));

        JPanel separador = new JPanel();
        separador.setBackground(Estilos.BOTON_LOGIN);
        separador.setMaximumSize(new Dimension(200, 3));
        separador.setPreferredSize(new Dimension(200, 3));
        agregarCentrado(principal, separador);
        principal.add(Box.createVerticalStrut(12));

        agregarCentrado(principal, Estilos.etiqueta("Iniciar Sesión", Font.BOLD, 14, Estilos.TITULO_LOGIN));
        principal.add(Box.createVerticalStrut(15));

        txtUsuario = new JTextField();
        agregarCampo(principal, "Usuario:", txtUsuario);
        principal.add(Box.createVerticalStrut(10));

        txtContrasena = new JPasswordField();
        txtContrasena.setEchoChar('●');
        agregarCampo(principal, "Contraseña:", txtContrasena);
        principal.add(Box.createVerticalStrut(20));

        Dimension medidaBoton = new Dimension(300, 45);
        btnLogin = Estilos.boton("Iniciar Sesión", Estilos.BOTON_LOGIN, Estilos.BOTON_LOGIN_HOVER, 14, medidaBoton);
        btnLogin.addActionListener(e -> validarLogin());
        agregarCentrado(principal, btnLogin);
        principal.add(Box.createVerticalStrut(8));

        JButton btnCancelar = Estilos.boton("Cancelar", Estilos.BOTON_CANCELAR, Estilos.BOTON_CANCELAR_HOVER, 12, medidaBoton);
        btnCancelar.addActionListener(e -> System.exit(0));
        agregarCentrado(principal, btnCancelar);

        // Enter en cualquier campo = Iniciar Sesion
        getRootPane().setDefaultButton(btnLogin);

        setContentPane(principal);
    }

    private void agregarCampo(JPanel panel, String texto, JTextField campo) {
        JLabel etiqueta = Estilos.etiqueta(texto, Font.BOLD, 11, Estilos.TITULO_LOGIN);
        etiqueta.setAlignmentX(Component.CENTER_ALIGNMENT);
        etiqueta.setMaximumSize(new Dimension(300, 20));
        panel.add(etiqueta);
        panel.add(Box.createVerticalStrut(4));

        campo.setFont(Estilos.fuente(Font.PLAIN, 11));
        campo.setForeground(Estilos.TEXTO_CAMPO);
        campo.setMaximumSize(new Dimension(300, 30));
        campo.setPreferredSize(new Dimension(300, 30));
        campo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(campo);
    }

    private void agregarCentrado(JPanel panel, javax.swing.JComponent componente) {
        componente.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(componente);
    }

    private Image crearImagenCircular(Image original, int tamanio) {
        BufferedImage circular = new BufferedImage(tamanio, tamanio, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = circular.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setClip(new Ellipse2D.Float(0, 0, tamanio, tamanio));
        g.drawImage(original, 0, 0, tamanio, tamanio, null);
        g.setClip(null);
        g.setColor(Estilos.BOTON_LOGIN);
        g.setStroke(new BasicStroke(3));
        g.draw(new Ellipse2D.Float(1.5f, 1.5f, tamanio - 3, tamanio - 3));
        g.dispose();
        return circular;
    }

    // CU-01 Autenticarse: el servidor valida y entrega el token
    private void validarLogin() {
        String usuario = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword()).trim();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese usuario y contraseña",
                    "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnLogin.setEnabled(false);
        TareaApi.ejecutar(this,
                () -> apiService.login(usuario, contrasena),
                token -> {
                    JOptionPane.showMessageDialog(this, "¡Bienvenido " + usuario + "!",
                            "Login Exitoso", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new ConversorFrame(usuario).setVisible(true);
                },
                this::mostrarError);
    }

    private void mostrarError(ExcepcionApi e) {
        btnLogin.setEnabled(true);
        if (ApiService.CREDENCIALES_INCORRECTAS.equals(e.getCodigo())) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
            txtContrasena.setText("");
            txtUsuario.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    }
}
