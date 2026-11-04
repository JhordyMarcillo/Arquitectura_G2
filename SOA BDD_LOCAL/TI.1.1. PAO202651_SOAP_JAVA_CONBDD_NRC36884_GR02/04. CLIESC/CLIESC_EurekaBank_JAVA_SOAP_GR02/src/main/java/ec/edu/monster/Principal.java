package ec.edu.monster;

import com.formdev.flatlaf.FlatLightLaf;
import ec.edu.monster.servicios.IServicioEurekaBank;
import ec.edu.monster.servicios.ServicioEurekaBankSoap;
import ec.edu.monster.vistas.VentanaLogin;
import java.awt.Color;
import java.util.prefs.Preferences;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Cliente de escritorio EurekaBank (SOAP Java, GR02) con Swing y FlatLaf.
 * La URL del servicio puede venir como argumento (la usa LEVANTAR_TI_1.1.bat):
 *   java -jar CLIESC_EurekaBank_JAVA_SOAP_GR02.jar http://192.168.1.50:8080/WS_EurekaBank_JAVA_SOAP_GR02/WSEurekaBank
 */
public class Principal {

    public static final String SUBTITULO = "SOAP Java  ·  GR02";

    /** Servicio compartido por todas las ventanas (una sola sesion por aplicacion). */
    public static final IServicioEurekaBank SERVICIO = new ServicioEurekaBankSoap();

    private static final Preferences PREFERENCIAS = Preferences.userRoot().node("ec/edu/monster/eurekabank/cliesc-java-soap");
    private static final String CLAVE_URL = "urlServidor";

    public static void main(String[] args) {
        SERVICIO.setUrlServidor(PREFERENCIAS.get(CLAVE_URL, SERVICIO.getUrlServidor()));
        if (args.length > 0) {
            SERVICIO.setUrlServidor(args[0]);
            guardarUrlServidor();
        }

        FlatLightLaf.setup();
        UIManager.put("defaultFont", new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        UIManager.put("Component.arc", 12);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.focusColor", new Color(0x1D4ED8));
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.borderColor", new Color(0xCBD5E1));
        UIManager.put("Table.selectionBackground", new Color(0xDBEAFE));
        UIManager.put("Table.selectionForeground", new Color(0x0F172A));
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 10);

        SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
    }

    public static void guardarUrlServidor() {
        PREFERENCIAS.put(CLAVE_URL, SERVICIO.getUrlServidor());
    }
}
