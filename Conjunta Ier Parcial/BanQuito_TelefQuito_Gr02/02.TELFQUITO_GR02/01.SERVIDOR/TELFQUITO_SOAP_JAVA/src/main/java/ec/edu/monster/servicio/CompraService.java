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
        return realizarCompraEfectivoMultiple(String.valueOf(codTelefono), codCedula);
    }

    public String realizarCompraCredito(int codTelefono, String cedula, String formaPago, int plazoMeses)
            throws Exception {
        return realizarCompraCreditoMultiple(String.valueOf(codTelefono), cedula, plazoMeses);
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
            return "Compra en efectivo exitosa!! Factura No. " + factura;
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
            return "Compra exitosa a credito!! Factura No. " + factura;
        }
        return "Compra fallida!!";
    }

    public List<Factura> obtenerFactura(String cedula) {
        return compraDAO.obtenerFactura(cedula);
    }

    public List<ec.edu.monster.banquito.Tabla> consultarTablaAmortizacion(String cedula) {
        return generarTablaService.consultarTablaAmortizacion(cedula);
    }
}