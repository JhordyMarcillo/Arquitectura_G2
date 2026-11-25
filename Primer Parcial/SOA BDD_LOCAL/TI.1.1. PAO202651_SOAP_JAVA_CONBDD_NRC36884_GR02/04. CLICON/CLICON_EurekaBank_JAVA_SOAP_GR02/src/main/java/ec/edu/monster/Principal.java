package ec.edu.monster;

import ec.edu.monster.controladores.ControladorConsola;
import ec.edu.monster.servicios.IServicioEurekaBank;
import ec.edu.monster.servicios.ServicioEurekaBankSoap;
import java.io.IOException;

/**
 * Cliente de consola EurekaBank (SOAP Java, GR02).
 * La URL del servicio puede venir como argumento:
 *   java -jar CLICON_EurekaBank_JAVA_SOAP_GR02.jar http://192.168.1.50:8080/WS_EurekaBank_JAVA_SOAP_GR02/WSEurekaBank
 */
public class Principal {

    public static void main(String[] args) throws IOException {
        IServicioEurekaBank servicio = new ServicioEurekaBankSoap();
        if (args.length > 0) {
            servicio.setUrlServidor(args[0]);
        }
        new ControladorConsola(servicio, "Cliente de consola  ·  SOAP Java  ·  GR02").ejecutar();
    }
}
