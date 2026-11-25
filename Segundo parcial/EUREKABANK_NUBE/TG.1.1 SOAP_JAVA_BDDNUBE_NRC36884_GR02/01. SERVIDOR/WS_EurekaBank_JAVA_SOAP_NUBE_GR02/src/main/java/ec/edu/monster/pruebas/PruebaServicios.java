package ec.edu.monster.pruebas;

import ec.edu.monster.modelos.utilidades.DatosMovimiento;
import ec.edu.monster.modelos.utilidades.RespuestaAutenticacion;
import ec.edu.monster.modelos.utilidades.RespuestaMovimientos;
import ec.edu.monster.modelos.utilidades.SolicitudLogin;
import ec.edu.monster.servicios.ServicioAutenticacion;
import ec.edu.monster.servicios.ServicioCuentas;
import java.util.Scanner;

/** Prueba manual por consola de los servicios contra la base (sin servidor web). */
public class PruebaServicios {

    public static void main(String[] args) throws Exception {
        Scanner entrada = new Scanner(System.in);
        SolicitudLogin login = new SolicitudLogin();
        System.out.print("Usuario: ");
        login.setUsuario(entrada.nextLine());
        System.out.print("Clave: ");
        login.setClave(entrada.nextLine());

        RespuestaAutenticacion sesion = new ServicioAutenticacion().autenticar(login);
        System.out.println("Bienvenido " + sesion.getNombreEmpleado());

        System.out.print("Cuenta a consultar: ");
        RespuestaMovimientos movimientos = new ServicioCuentas().consultarMovimientos(entrada.nextLine());
        System.out.printf("Saldo: %s %s%n", movimientos.getSaldo(), movimientos.getMoneda());
        for (DatosMovimiento m : movimientos.getMovimientos()) {
            System.out.printf("%3d  %s  %-22s %-8s %10s%n", m.getNumero(), m.getFecha(), m.getDescripcion(),
                    m.getAccion(), m.getImporte());
        }
    }
}
