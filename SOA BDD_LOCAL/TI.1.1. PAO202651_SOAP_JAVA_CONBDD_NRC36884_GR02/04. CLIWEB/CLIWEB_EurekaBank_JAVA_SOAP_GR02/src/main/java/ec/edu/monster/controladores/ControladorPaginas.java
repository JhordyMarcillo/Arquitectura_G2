package ec.edu.monster.controladores;

import ec.edu.monster.modelos.DatosCuenta;
import ec.edu.monster.modelos.DatosMovimiento;
import ec.edu.monster.modelos.ExcepcionServicio;
import ec.edu.monster.modelos.RespuestaMovimientos;
import ec.edu.monster.modelos.RespuestaOperacion;
import ec.edu.monster.servicios.IServicioEurekaBank;
import ec.edu.monster.utilidades.Formato;
import ec.edu.monster.vistas.FilaCuenta;
import ec.edu.monster.vistas.FilaMovimiento;
import ec.edu.monster.vistas.LineaComprobante;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Paginas del panel: cuentas, movimientos y operaciones (deposito, retiro, transferencia). */
@WebServlet({"", "/cuentas", "/movimientos", "/operacion"})
public class ControladorPaginas extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta) throws ServletException, IOException {
        try {
            switch (peticion.getServletPath()) {
                case "/movimientos" -> movimientos(peticion);
                case "/operacion" -> prepararOperacion(peticion);
                default -> cuentas(peticion);
            }
        } catch (ExcepcionServicio e) {
            if (manejarError(peticion, respuesta, e)) {
                return;
            }
        }
        mostrar(peticion, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta) throws ServletException, IOException {
        prepararOperacion(peticion);
        try {
            registrarOperacion(peticion);
        } catch (ExcepcionServicio e) {
            if (manejarError(peticion, respuesta, e)) {
                return;
            }
            peticion.setAttribute("codigoError", e.getCodigo());
        }
        mostrar(peticion, respuesta);
    }

    // ------------------------------------------------------------------ cuentas

    private void cuentas(HttpServletRequest peticion) throws ExcepcionServicio {
        peticion.setAttribute("menu", "cuentas");
        peticion.setAttribute("titulo", "Cuentas");
        peticion.setAttribute("subtitulo", "Cartera de cuentas de ahorro y su estado actual.");
        List<DatosCuenta> cuentas = SesionWeb.servicio(peticion).listarCuentas().getCuentas();
        List<FilaCuenta> filas = new ArrayList<>();
        BigDecimal soles = BigDecimal.ZERO;
        BigDecimal dolares = BigDecimal.ZERO;
        int activas = 0;
        for (DatosCuenta c : cuentas) {
            FilaCuenta fila = new FilaCuenta();
            fila.setCodigo(c.getCodigo());
            fila.setCliente(c.getCliente());
            fila.setMoneda(c.getMoneda());
            fila.setSaldo(Formato.dinero(c.getMoneda(), c.getSaldo()));
            fila.setEstado(c.getEstado());
            fila.setActiva(Formato.esActiva(c));
            filas.add(fila);
            if (Formato.esActiva(c)) {
                activas++;
                if ("S/".equals(Formato.simbolo(c.getMoneda()))) {
                    soles = soles.add(c.getSaldo());
                } else {
                    dolares = dolares.add(c.getSaldo());
                }
            }
        }
        peticion.setAttribute("cuentas", filas);
        peticion.setAttribute("activas", activas);
        peticion.setAttribute("totalSoles", Formato.dinero("Soles", soles));
        peticion.setAttribute("totalDolares", Formato.dinero("Dolares", dolares));
    }

    // ------------------------------------------------------------------ movimientos

    private void movimientos(HttpServletRequest peticion) throws ExcepcionServicio {
        peticion.setAttribute("menu", "movimientos");
        peticion.setAttribute("titulo", "Movimientos");
        peticion.setAttribute("subtitulo", "Historial de movimientos y saldo de una cuenta.");
        String cuenta = ControladorLogin.valor(peticion, "cuenta");
        peticion.setAttribute("cuenta", cuenta);
        if (cuenta.isEmpty()) {
            return;
        }
        RespuestaMovimientos datos = SesionWeb.servicio(peticion).consultarMovimientos(cuenta);
        String moneda = datos.getMoneda();
        List<DatosMovimiento> lista = new ArrayList<>(datos.getMovimientos());
        Collections.reverse(lista);
        List<FilaMovimiento> filas = new ArrayList<>();
        BigDecimal ingresos = BigDecimal.ZERO;
        BigDecimal salidas = BigDecimal.ZERO;
        int cantidadIngresos = 0;
        for (DatosMovimiento m : lista) {
            boolean ingreso = Formato.esIngreso(m);
            if (ingreso) {
                ingresos = ingresos.add(m.getImporte());
                cantidadIngresos++;
            } else {
                salidas = salidas.add(m.getImporte());
            }
            FilaMovimiento fila = new FilaMovimiento();
            fila.setNumero(String.valueOf(m.getNumero()));
            fila.setFecha(m.getFecha());
            fila.setDescripcion(m.getDescripcion());
            fila.setReferencia(m.getCuentaReferencia() == null ? "-" : m.getCuentaReferencia());
            fila.setImporte((ingreso ? "+ " : "- ") + Formato.dinero(moneda, m.getImporte()));
            fila.setIngreso(ingreso);
            filas.add(fila);
        }
        peticion.setAttribute("datos", datos);
        peticion.setAttribute("saldo", Formato.dinero(moneda, datos.getSaldo()));
        peticion.setAttribute("ingresos", Formato.dinero(moneda, ingresos));
        peticion.setAttribute("salidas", Formato.dinero(moneda, salidas));
        peticion.setAttribute("cantidadIngresos", cantidadIngresos);
        peticion.setAttribute("cantidadSalidas", lista.size() - cantidadIngresos);
        peticion.setAttribute("movimientos", filas);
    }

    // ------------------------------------------------------------------ operaciones

    private void prepararOperacion(HttpServletRequest peticion) {
        String tipo = ControladorLogin.valor(peticion, "tipo");
        if (!tipo.equals("retiro") && !tipo.equals("transferencia")) {
            tipo = "deposito";
        }
        String titulo = switch (tipo) {
            case "retiro" -> "Retiro";
            case "transferencia" -> "Transferencia";
            default -> "Deposito";
        };
        peticion.setAttribute("menu", tipo);
        peticion.setAttribute("tipo", tipo);
        peticion.setAttribute("titulo", titulo);
        peticion.setAttribute("esTransferencia", tipo.equals("transferencia"));
        peticion.setAttribute("descripcion", switch (tipo) {
            case "retiro" -> "Se valida la clave y el saldo disponible.";
            case "transferencia" -> "Ambas cuentas deben ser de la misma moneda.";
            default -> "El importe se abona a la cuenta.";
        });
        peticion.setAttribute("subtitulo", switch (tipo) {
            case "retiro" -> "Retiro de efectivo validado con la clave de la cuenta.";
            case "transferencia" -> "Traspaso entre cuentas de la misma moneda.";
            default -> "Abono de efectivo a una cuenta activa.";
        });
    }

    private void registrarOperacion(HttpServletRequest peticion) throws ExcepcionServicio {
        String tipo = (String) peticion.getAttribute("tipo");
        String cuenta = ControladorLogin.valor(peticion, "cuenta");
        String clave = ControladorLogin.valor(peticion, "clave");
        String destino = ControladorLogin.valor(peticion, "cuentaDestino");
        BigDecimal importe = Formato.leerImporte(ControladorLogin.valor(peticion, "importe"));
        peticion.setAttribute("cuenta", cuenta);
        peticion.setAttribute("cuentaDestino", destino);
        if (importe == null) {
            peticion.setAttribute("mensajeError", "Ingrese un importe mayor a cero (ej. 150.50).");
            return;
        }

        IServicioEurekaBank servicio = SesionWeb.servicio(peticion);
        RespuestaOperacion r = switch (tipo) {
            case "retiro" -> servicio.retirar(cuenta, clave, importe);
            case "transferencia" -> servicio.transferir(cuenta, clave, destino, importe);
            default -> servicio.depositar(cuenta, importe);
        };
        String moneda = servicio.consultarMovimientos(r.getCuenta()).getMoneda();

        List<LineaComprobante> detalle = new ArrayList<>();
        detalle.add(linea("Operacion", ((String) peticion.getAttribute("titulo")).toUpperCase(), false));
        detalle.add(linea(tipo.equals("transferencia") ? "Cuenta de origen" : "Cuenta", r.getCuenta(), false));
        if (tipo.equals("transferencia")) {
            detalle.add(linea("Cuenta de destino", destino, false));
        }
        detalle.add(linea("Importe", Formato.dinero(moneda, importe), false));
        detalle.add(linea("Nuevo saldo", Formato.dinero(moneda, r.getSaldo()), true));
        detalle.add(linea("Empleado", servicio.getSesion().getNombreEmpleado(), false));
        detalle.add(linea("Fecha y hora", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), false));
        peticion.setAttribute("resultado", r);
        peticion.setAttribute("detalle", detalle);
    }

    private static LineaComprobante linea(String etiqueta, String valor, boolean destacada) {
        LineaComprobante linea = new LineaComprobante();
        linea.setEtiqueta(etiqueta);
        linea.setValor(valor);
        linea.setDestacada(destacada);
        return linea;
    }

    // ------------------------------------------------------------------ comunes

    /** Si la sesion expiro redirige al login (devuelve true); si no, deja el mensaje para la pagina. */
    private static boolean manejarError(HttpServletRequest peticion, HttpServletResponse respuesta, ExcepcionServicio e)
            throws IOException {
        if (e.requiereLogin()) {
            SesionWeb.servicio(peticion).cerrarSesion();
            SesionWeb.avisar(peticion, e.getMessage() + ". Inicie sesion nuevamente.");
            respuesta.sendRedirect(peticion.getContextPath() + "/login");
            return true;
        }
        peticion.setAttribute("mensajeError", e.getMessage());
        return false;
    }

    private static void mostrar(HttpServletRequest peticion, HttpServletResponse respuesta) throws ServletException, IOException {
        String vista = switch (peticion.getServletPath()) {
            case "/movimientos" -> "movimientos";
            case "/operacion" -> "operacion";
            default -> "cuentas";
        };
        peticion.getRequestDispatcher("/WEB-INF/vistas/" + vista + ".jsp").forward(peticion, respuesta);
    }
}
