package ec.edu.monster.servicio;

import ec.edu.monster.DAO.ClienteComercializadoraDAO;
import ec.edu.monster.DAO.CompraDAO;
import ec.edu.monster.models.Compras;
import ec.edu.monster.banquito.SujetoCreditoService;
import ec.edu.monster.banquito.MontoMaximoService;
import ec.edu.monster.banquito.GenerarTablaService;
import ec.edu.monster.models.Telefonos;
import ec.edu.monster.DAO.TelefonoDAO;
import ec.edu.monster.models.Factura;
import ec.edu.monster.models.Tabla;
import java.util.List;

import java.time.LocalDate;

public class CompraService {
    private CompraDAO compraDAO;
    private TelefonoDAO telefonoDAO;
    private ClienteComercializadoraDAO clientecDAO;
    private SujetoCreditoService sujetoCreditoService;
    private MontoMaximoService montoMaximoService;
    private GenerarTablaService generarTablaService;

    public CompraService(CompraDAO compraDAO, TelefonoDAO telefonoDAO, ClienteComercializadoraDAO clientecDAO,
            SujetoCreditoService sujetoCreditoService, MontoMaximoService montoMaximoService,
            GenerarTablaService generarTablaService) {
        this.compraDAO = compraDAO;
        this.telefonoDAO = telefonoDAO;
        this.clientecDAO = clientecDAO;
        this.sujetoCreditoService = sujetoCreditoService;
        this.montoMaximoService = montoMaximoService;
        this.generarTablaService = generarTablaService;
    }

    public static final int MAX_PRODUCTOS = 3;

    public String realizarCompraEfectivo(int codTelefono, String codCedula, String formaPago) throws Exception {
        String r = realizarCompraEfectivoMultiple(String.valueOf(codTelefono), codCedula);
        // Los clientes de compra simple comparan este texto exacto
        return r.startsWith("FACTURA") ? "Compra en efectivo exitosa!!" : r;
    }

    public String realizarCompraCredito(int codTelefono, String cedula, String formaPago, int plazoMeses)
            throws Exception {
        String r = realizarCompraCreditoMultiple(String.valueOf(codTelefono), cedula, plazoMeses);
        return r.startsWith("FACTURA") ? "Compra exitosa a credito!!" : r;
    }

    /** Convierte "1,2,3" en los telefonos del catalogo (maximo 3, deben existir y estar a la venta). */
    private List<Telefonos> resolverItems(String codsTelefono) throws Exception {
        List<Telefonos> items = new java.util.ArrayList<>();
        if (codsTelefono == null || codsTelefono.trim().isEmpty()) {
            throw new IllegalArgumentException("Seleccione al menos un teléfono.");
        }
        for (String c : codsTelefono.split(",")) {
            if (c.trim().isEmpty()) {
                continue;
            }
            Telefonos t = telefonoDAO.getTelefonoById(Integer.parseInt(c.trim()));
            if (t == null) {
                throw new IllegalArgumentException("No se encontró el teléfono " + c.trim() + ".");
            }
            if (t.getDisponible() != 1) {
                throw new IllegalArgumentException("El teléfono " + t.getNombre() + " no está a la venta.");
            }
            items.add(t);
        }
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Seleccione al menos un teléfono.");
        }
        if (items.size() > MAX_PRODUCTOS) {
            throw new IllegalArgumentException("Una factura admite máximo " + MAX_PRODUCTOS + " productos.");
        }
        return items;
    }

    public String realizarCompraEfectivoMultiple(String codsTelefono, String cedula) throws Exception {
        List<Telefonos> items;
        try {
            items = resolverItems(codsTelefono);
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
        int idCliente = clientecDAO.getCodClienteByCedula(cedula);
        if (idCliente == -1) {
            return "No se encontró el cliente";
        }
        int factura = compraDAO.crearFactura("Efectivo", LocalDate.now().toString(), idCliente, items, 0.42);
        if (factura > 0) {
            return armarFactura(factura, cedula, 0);
        }
        return "Compra en efectivo fallida";
    }

    public String realizarCompraCreditoMultiple(String codsTelefono, String cedula, int plazoMeses) throws Exception {
        List<Telefonos> items;
        try {
            items = resolverItems(codsTelefono);
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
        double total = 0;
        for (Telefonos t : items) {
            total += t.getPrecio();
        }
        int esSujetoCredito = sujetoCreditoService.verifica(cedula);
        if (esSujetoCredito == 0) {
            return ("El cliente no es sujeto de crédito.");
        }
        double montoMaximo = montoMaximoService.montoMaximo(esSujetoCredito);
        if (total > montoMaximo) {
            return String.format(java.util.Locale.US,
                    "El total de la compra ($%.2f) excede el monto máximo de crédito aprobado ($%.2f).", total, montoMaximo);
        }
        String resultado = generarTablaService.crearCreditoYTablaAmortizacion(esSujetoCredito, total, plazoMeses);
        if (!resultado.equals("Crédito y tabla de amortización creados exitosamente.")) {
            return ("Error al crear la tabla de amortización: " + resultado);
        }
        int factura = compraDAO.crearFactura("Crédito Directo", LocalDate.now().toString(),
                clientecDAO.getCodClienteByCedula(cedula), items, 0);
        if (factura > 0) {
            return armarFactura(factura, cedula, plazoMeses);
        }
        return "Compra fallida!!";
    }

    private static String dinero(double v) {
        return String.format(java.util.Locale.US, "$%,.2f", v);
    }

    /** Factura en texto: encabezado, lineas, subtotal, descuento y total (y cuota si es a credito). */
    private String armarFactura(int codCompra, String cedula, int plazoMeses) {
        List<Factura> lineas = new java.util.ArrayList<>();
        for (Factura f : compraDAO.obtenerFactura(cedula)) {
            if (f.getCodCompra() == codCompra) {
                lineas.add(f);
            }
        }
        if (lineas.isEmpty()) {
            return "FACTURA No. " + codCompra;
        }
        Factura c = lineas.get(0);
        boolean efectivo = "Efectivo".equalsIgnoreCase(c.getFormaPago());
        double subtotal = 0, descuento = 0, total = 0;
        for (Factura f : lineas) {
            descuento += f.getDescuento();
            total += f.getPreciofinal();
            subtotal += f.getPreciofinal() + f.getDescuento();
        }
        String barra = "------------------------------------------------------------\n";
        StringBuilder sb = new StringBuilder();
        sb.append("FACTURA No. ").append(codCompra).append("  -  TELFQUITO\n");
        sb.append(barra);
        sb.append("Fecha         : ").append(c.getFecha()).append('\n');
        sb.append("Cliente       : ").append(c.getNombreCliente()).append('\n');
        sb.append("Cédula        : ").append(cedula).append('\n');
        sb.append("Forma de pago : ").append(c.getFormaPago()).append('\n');
        sb.append(barra);
        sb.append(String.format("%-3s %-30s %10s %10s%n", "#", "Producto", "Precio", "Total"));
        int n = 1;
        for (Factura f : lineas) {
            String prod = f.getNombreTelefono() + " (" + f.getMarcaTelefono() + ")";
            if (prod.length() > 30) {
                prod = prod.substring(0, 29) + "…";
            }
            sb.append(String.format("%-3d %-30s %10s %10s%n", n++, prod,
                    dinero(f.getPreciofinal() + f.getDescuento()), dinero(f.getPreciofinal())));
        }
        sb.append(barra);
        sb.append(String.format("%-20s %s%n", "Subtotal", dinero(subtotal)));
        if (efectivo) {
            sb.append(String.format("%-20s -%s%n", "Descuento (42%)", dinero(descuento)));
        }
        sb.append(String.format("%-20s %s%n", "TOTAL A PAGAR", dinero(total)));
        if (!efectivo && plazoMeses > 0) {
            double r = 0.165 / 12;
            double cuota = total * r / (1 - Math.pow(1 + r, -plazoMeses));
            sb.append(barra);
            sb.append(String.format("Crédito directo: %d cuotas mensuales de %s (16.5%% anual)%n", plazoMeses, dinero(cuota)));
        }
        return sb.toString().trim();
    }

    public List<Factura> obtenerFactura(String cedula) {
        return compraDAO.obtenerFactura(cedula);
    }

    public List<ec.edu.monster.banquito.Tabla> consultarTablaAmortizacion(String cedula) {
        return generarTablaService.consultarTablaAmortizacion(cedula);
    }
}