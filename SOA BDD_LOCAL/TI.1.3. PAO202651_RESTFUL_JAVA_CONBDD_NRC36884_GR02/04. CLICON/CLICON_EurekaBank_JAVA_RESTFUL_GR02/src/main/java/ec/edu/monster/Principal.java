package ec.edu.monster;

import ec.edu.monster.controladores.ControladorConsola;
import ec.edu.monster.servicios.IServicioEurekaBank;
import ec.edu.monster.servicios.ServicioEurekaBankRest;
import java.io.IOException;

/**
 * Cliente de consola EurekaBank (RESTful Java, GR02).
 * La URL del servidor puede venir como argumento:
 *   java -jar CLICON_EurekaBank_JAVA_RESTFUL_GR02.jar http://192.168.1.50:8080/WS_EurekaBank_JAVA_RESTFUL_GR02/api/eurekabank
 */
public class Principal {

    public static void main(String[] args) throws IOException {
        IServicioEurekaBank servicio = new ServicioEurekaBankRest();
        if (args.length > 0) {
            servicio.setUrlServidor(args[0]);
        }
        new ControladorConsola(servicio, "Cliente de consola  ·  RESTful Java  ·  GR02").ejecutar();
    }
}
