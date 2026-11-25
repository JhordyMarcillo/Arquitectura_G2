package ec.edu.monster.vista;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {

    private JTextField usuarioField;
    private JPasswordField contrasenaField;
    private boolean autenticado;

    public LoginDialog(Frame owner) {
        super(owner, "MONSTERS INC. - Autenticación", true);
        construirUI();
    }

    private void construirUI() {
        setLayout(new BorderLayout(10, 10));
        JPanel panel = new JPanel(new GridLayout(0, 1, 6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Banner ASCII al estilo del cliente SOAP
        JLabel banner = new JLabel(
                "<html><pre style='font-family:Consolas,Monospace;font-size:10px;color:#00CCFF;'>" +
                "███╗   ███╗ ██████╗ ███╗   ██╗███████╗████████╗███████╗██████╗     ██████╗ ██████╗ ██████╗<br/>" +
                "████╗ ████║██╔═══██╗████╗  ██║██╔════╝╚══██╔══╝██╔════╝██╔══██╗   ██╔════╝ ██╔══██╗╚═══██╗<br/>" +
                "██╔████╔██║██║   ██║██╔██╗ ██║███████╗   ██║   █████╗  ██████╔╝   ██║  ███╗██████╔╝  ███╔╝<br/>" +
                "██║╚██╔╝██║██║   ██║██║╚██╗██║╚════██║   ██║   ██╔══╝  ██╔══██╗   ██║   ██║██╔══██╗ ███╔╝<br/>" +
                "██║ ╚═╝ ██║╚██████╔╝██║ ╚████║███████║   ██║   ███████╗██║  ██║   ╚██████╔╝██║  ██║██████╗<br/>" +
                "╚═╝     ╚═╝ ╚═════╝ ╚═╝  ╚═══╝╚══════╝   ╚═╝   ╚══════╝╚═╝  ╚═╝    ╚═════╝ ╚═╝  ╚═╝╚═════╝" +
                "</pre></html>");
        banner.setHorizontalAlignment(SwingConstants.CENTER);
        add(banner, BorderLayout.NORTH);

        usuarioField = new JTextField();
        contrasenaField = new JPasswordField();

        panel.add(new JLabel("Usuario:"));
        panel.add(usuarioField);
        panel.add(new JLabel("Contraseña:"));
        panel.add(contrasenaField);

        add(panel, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton entrar = new JButton("Entrar");
        JButton cancelar = new JButton("Cancelar");
        botones.add(cancelar);
        botones.add(entrar);
        add(botones, BorderLayout.SOUTH);

        entrar.addActionListener(e -> autenticar());
        cancelar.addActionListener(e -> {
            autenticado = false;
            dispose();
        });

        getRootPane().setDefaultButton(entrar);
        setSize(420, 220);
        setLocationRelativeTo(getOwner());
    }

    private void autenticar() {
        String usuario = usuarioField.getText();
        String contrasena = new String(contrasenaField.getPassword());

        if ("MONSTER".equals(usuario) && "MONSTER9".equals(contrasena)) {
            autenticado = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Credenciales inválidas", "Error", JOptionPane.ERROR_MESSAGE);
            contrasenaField.setText("");
            contrasenaField.requestFocusInWindow();
        }
    }

    public boolean isAutenticado() {
        return autenticado;
    }
}


