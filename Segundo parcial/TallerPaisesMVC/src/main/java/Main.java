import javax.swing.SwingUtilities;
import ec.edu.monster.vista.FrmPais;
import ec.edu.monster.vista.TemaSullivan;

public class Main {

    public static void main(String[] args) {
        TemaSullivan.instalar();   // tema visual de Sullivan (Monsters, Inc.)
        SwingUtilities.invokeLater(() -> new FrmPais().setVisible(true));
    }
}
