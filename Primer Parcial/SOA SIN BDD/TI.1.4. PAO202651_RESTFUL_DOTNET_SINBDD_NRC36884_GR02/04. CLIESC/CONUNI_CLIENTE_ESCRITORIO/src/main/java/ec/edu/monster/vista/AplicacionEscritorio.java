package ec.edu.monster.vista;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class AplicacionEscritorio {

    public static void main(String[] args) {
        try {
            // Apariencia de Windows para campos y combos; los botones de color usan su propio estilo
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("No se pudo aplicar el look and feel del sistema: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
