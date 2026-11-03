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

    public String realizarCompraEfectivo(int codTelefono, String codCedula, String formaPago) throws Exception {
        Telefonos telefono = telefonoDAO.getTelefonoById(codTelefono);
        if (telefono == null) {
            return "No se encontró el teléfono";
        }
        double precioOriginal = telefono.getPrecio();
        double descuento = precioOriginal * 0.42;
        double precioFinal = precioOriginal - descuento;
        int idCliente = clientecDAO.getCodClienteByCedula(codCedula);
        if (idCliente == -1) {
            return "No se encontró el cliente";
        }

        Compras compra = new Compras();
        compra.setCodTelefono(codTelefono);
        compra.setCodcCliente(idCliente);
        compra.setFormaPago(formaPago);
        compra.setFecha(LocalDate.now().toString());
        compra.setDescuento(descuento);
        compra.setPreciofinal(precioFinal);

        if (compraDAO.createCompraEfectivo(compra)) {
            return ("Compra en efectivo exitosa!!");
        } else {
            return ("Compra en efectivo fallida");
        }
    }

    public String realizarCompraCredito(int codTelefono, String cedula, String formaPago, int plazoMeses)
            throws Exception {
        Telefonos telefono = telefonoDAO.getTelefonoById(codTelefono);
        double precioTelefono = telefono.getPrecio();

        int esSujetoCredito = sujetoCreditoService.verifica(cedula);
        if (esSujetoCredito == 0) {
            return ("El cliente no es sujeto de crédito.");
        }

        double montoMaximo = montoMaximoService.montoMaximo(esSujetoCredito);
        System.out.println(montoMaximo);
        if (precioTelefono > montoMaximo) {
            return ("El precio del teléfono excede el monto máximo de crédito aprobado.");
        }

        String resultado = generarTablaService.crearCreditoYTablaAmortizacion(esSujetoCredito, precioTelefono,
                plazoMeses);
        if (!resultado.equals("Crédito y tabla de amortización creados exitosamente.")) {
            return ("Error al crear la tabla de amortización.");
        }

        Compras compra = new Compras();
        compra.setCodTelefono(codTelefono);
        compra.setCodcCliente(clientecDAO.getCodClienteByCedula(cedula));
        compra.setFormaPago(formaPago);
        compra.setFecha(LocalDate.now().toString());
        compra.setPreciofinal(precioTelefono);

        if (compraDAO.createCompraCredito(compra)) {
            return ("Compra exitosa a credito!!");
        } else {
            return ("Compra fallida!!");
        }
    }

    public List<Factura> obtenerFactura(String cedula) {
        return compraDAO.obtenerFactura(cedula);
    }

    public List<ec.edu.monster.banquito.Tabla> consultarTablaAmortizacion(String cedula) {
        return generarTablaService.consultarTablaAmortizacion(cedula);
    }
}