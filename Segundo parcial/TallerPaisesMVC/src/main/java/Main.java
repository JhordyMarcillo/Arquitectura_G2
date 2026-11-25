import javax.swing.SwingUtilities;
import ec.edu.monster.vista.FrmPais;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmPais().setVisible(true));
    }
}
