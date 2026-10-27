package ec.edu.monster.controladores;

import ec.edu.monster.modelos.DatosCuenta;
import ec.edu.monster.modelos.ExcepcionServicio;
import ec.edu.monster.modelos.RespuestaAutenticacion;
import ec.edu.monster.modelos.RespuestaOperacion;
import ec.edu.monster.servicios.IServicioEurekaBank;
import ec.edu.monster.utilidades.Formato;
import ec.edu.monster.vistas.VistaConsola;
import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

/** Flujo del cliente de consola: conexion, inicio de sesion y menu de operaciones. */
public class ControladorConsola {

    private final IServicioEurekaBank servicio;
    private final String subtitulo;
    private final BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

    public ControladorConsola(IServicioEurekaBank servicio, String subtitulo) {
        this.servicio = servicio;
        this.subtitulo = subtitulo;
    }

    public void ejecutar() throws IOException {
        while (iniciarSesion()) {
            if (!menuPrincipal()) {
                return;
            }
        }
    }

    // ------------------------------------------------------------------ inicio de sesion

    private boolean iniciarSesion() throws IOException {
        while (true) {
            VistaConsola.encabezado(subtitulo, null);
            VistaConsola.titulo("Inicio de sesion");
            String servidor = leer("Servidor [" + servicio.getUrlServidor() + "]:");
            if (!servidor.isBlank()) {
                servicio.setUrlServidor(servidor);
            }
            String usuario = leer("Usuario:");
            String clave = leerClave("Clave:");
            try {
                RespuestaAutenticacion sesion = servicio.iniciarSesion(usuario, clave);
                VistaConsola.lineaEnBlanco();
                VistaConsola.exitoSimple("Bienvenido, " + sesion.getNombreEmpleado());
                pausa(700);
                return true;
            } catch (ExcepcionServicio e) {
                VistaConsola.lineaEnBlanco();
                VistaConsola.error(e);
                if (!leer("¿Intentar nuevamente? (s/n):").equalsIgnoreCase("s")) {
                    return false;
                }
            }
        }
    }

    // ------------------------------------------------------------------ menu

    /** Devuelve false si el usuario elige salir de la aplicacion. */
    private boolean menuPrincipal() throws IOException {
        while (servicio.getSesion() != null) {
            VistaConsola.encabezado(subtitulo, servicio.getSesion());
            VistaConsola.grupo("CONSULTAS");
            VistaConsola.opcion(1, "Ver cuentas");
            VistaConsola.opcion(2, "Consultar movimientos");
            VistaConsola.grupo("OPERACIONES");
            VistaConsola.opcion(3, "Registrar deposito");
            VistaConsola.opcion(4, "Registrar retiro");
            VistaConsola.opcion(5, "Registrar transferencia");
            VistaConsola.grupo("SESION");
            VistaConsola.opcion(6, "Cerrar sesion");
            VistaConsola.opcion(0, "Salir");
            VistaConsola.lineaEnBlanco();

            String opcion = leer("¿Que desea hacer?");
            if (opcion.equals("0")) {
                return false;
            }
            if (opcion.equals("6")) {
                servicio.cerrarSesion();
                return true;
            }
            String[] titulos = {"", "Ver cuentas", "Consultar movimientos", "Registrar deposito", "Registrar retiro", "Registrar transferencia"};
            if (!opcion.matches("[1-5]")) {
                continue;
            }
            VistaConsola.encabezado(subtitulo, servicio.getSesion());
            VistaConsola.titulo(titulos[Integer.parseInt(opcion)]);
            try {
                switch (opcion) {
                    case "1" -> VistaConsola.cuentas(servicio.listarCuentas().getCuentas());
                    case "2" -> VistaConsola.movimientos(servicio.consultarMovimientos(pedirCuenta("Numero de cuenta:")));
                    case "3" -> depositar();
                    case "4" -> retirar();
                    default -> transferir();
                }
            } catch (ExcepcionServicio e) {
                VistaConsola.error(e);
                if (e.requiereLogin()) {
                    VistaConsola.informacion("Debe iniciar sesion nuevamente.");
                }
            }
            leer("\nPresione Enter para continuar...");
        }
        return true;
    }

    // ------------------------------------------------------------------ operaciones

    private void depositar() throws IOException, ExcepcionServicio {
        String cuenta = pedirCuenta("Cuenta a depositar:");
        BigDecimal importe = pedirImporte();
        if (confirmar("Depositar " + importe + " en la cuenta " + cuenta)) {
            mostrarExito("Deposito registrado", servicio.depositar(cuenta, importe));
        }
    }

    private void retirar() throws IOException, ExcepcionServicio {
        String cuenta = pedirCuenta("Cuenta a retirar:");
        String clave = leerClave("Clave de la cuenta:");
        BigDecimal importe = pedirImporte();
        if (confirmar("Retirar " + importe + " de la cuenta " + cuenta)) {
            mostrarExito("Retiro registrado", servicio.retirar(cuenta, clave, importe));
        }
    }

    private void transferir() throws IOException, ExcepcionServicio {
        String origen = pedirCuenta("Cuenta de origen:");
        String clave = leerClave("Clave de la cuenta de origen:");
        String destino = pedirCuenta("Cuenta de destino:");
        BigDecimal importe = pedirImporte();
        if (confirmar("Transferir " + importe + " de " + origen + " a " + destino)) {
            mostrarExito("Transferencia registrada", servicio.transferir(origen, clave, destino, importe));
        }
    }

    private void mostrarExito(String titulo, RespuestaOperacion respuesta) throws ExcepcionServicio {
        String moneda = "Soles";
        for (DatosCuenta c : servicio.listarCuentas().getCuentas()) {
            if (c.getCodigo().equals(respuesta.getCuenta())) {
                moneda = c.getMoneda();
            }
        }
        VistaConsola.exito(titulo, respuesta, moneda);
    }

    // ------------------------------------------------------------------ lectura

    private String pedirCuenta(String etiqueta) throws IOException {
        while (true) {
            String cuenta = leer(etiqueta);
            if (Formato.esCuenta(cuenta)) {
                return cuenta.trim();
            }
            VistaConsola.informacion("  La cuenta tiene 8 digitos.");
        }
    }

    private BigDecimal pedirImporte() throws IOException {
        while (true) {
            BigDecimal importe = Formato.leerImporte(leer("Importe:"));
            if (importe != null) {
                return importe;
            }
            VistaConsola.informacion("  Ingrese un importe mayor a cero (ej. 150.50).");
        }
    }

    private boolean confirmar(String accion) throws IOException {
        VistaConsola.lineaEnBlanco();
        return leer("¿Confirma? " + accion + " (s/n):").equalsIgnoreCase("s");
    }

    private String leer(String etiqueta) throws IOException {
        VistaConsola.pregunta(etiqueta);
        String linea = entrada.readLine();
        if (linea == null) {
            throw new IOException("Entrada cerrada");
        }
        // Se descarta la marca BOM que algunas terminales anteponen a la entrada
        return linea.replace("\uFEFF", "").trim();
    }

    private String leerClave(String etiqueta) throws IOException {
        Console consola = System.console();
        if (consola == null) {
            return leer(etiqueta);
        }
        VistaConsola.pregunta(etiqueta);
        char[] clave = consola.readPassword();
        return clave == null ? "" : new String(clave);
    }

    private static void pausa(int milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
