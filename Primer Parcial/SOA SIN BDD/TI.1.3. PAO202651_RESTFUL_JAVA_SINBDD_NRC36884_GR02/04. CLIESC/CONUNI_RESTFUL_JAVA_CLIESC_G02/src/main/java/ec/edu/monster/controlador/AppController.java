package ec.edu.monster.controlador;

import ec.edu.monster.vista.VentanaLogin;
import ec.edu.monster.vista.VentanaPrincipal;

import javax.swing.*;

public class AppController {

    public void iniciar() {
        SwingUtilities.invokeLater(() -> {
            new VentanaLogin().setVisible(true);
        });
    }
}


