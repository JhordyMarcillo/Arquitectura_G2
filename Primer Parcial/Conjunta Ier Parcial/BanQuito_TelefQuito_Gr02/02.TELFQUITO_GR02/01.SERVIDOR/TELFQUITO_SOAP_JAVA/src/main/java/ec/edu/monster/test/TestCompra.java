package ec.edu.monster.test;

import ec.edu.monster.DAO.ClienteComercializadoraDAO;
import ec.edu.monster.DAO.CompraDAO;
import ec.edu.monster.DAO.TelefonoDAO;
import ec.edu.monster.servicio.CompraService;
import ec.edu.monster.banquito.SujetoCreditoService;
import ec.edu.monster.banquito.MontoMaximoService;
import ec.edu.monster.banquito.GenerarTablaService;
import ec.edu.monster.bdd.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class TestCompra {
    public static void main(String[] args) {
  

        try (Connection connection = DBConnection.getConnection()) {
            CompraDAO comprasDAO = new CompraDAO(connection);
            TelefonoDAO telefonoDAO = new TelefonoDAO(connection);
            ClienteComercializadoraDAO clientecDAO = new  ClienteComercializadoraDAO(connection);
            SujetoCreditoService sujetoCreditoService = new SujetoCreditoService();
            MontoMaximoService montoMaximoService = new MontoMaximoService();
            GenerarTablaService generarTablaService = new GenerarTablaService();

            CompraService compraService = new CompraService(comprasDAO, telefonoDAO,clientecDAO, sujetoCreditoService, montoMaximoService, generarTablaService);

            int codTelefono = 5;
            String codcCliente = "0506070809";
            String formaPagoEfectivo = "Efectivo";
            String formaPagoCredito = "Crédito Directo";
            int plazoMeses = 12;

            try {
                compraService.realizarCompraEfectivo(codTelefono, codcCliente, formaPagoEfectivo);
                System.out.println("Compra en efectivo realizada con éxito.");
            } catch (Exception e) {
                System.err.println("Error al realizar compra en efectivo: " + e.getMessage());
            }

            try {
                compraService.realizarCompraCredito(codTelefono, codcCliente, formaPagoCredito, plazoMeses);
                System.out.println("Compra con crédito directo realizada con éxito.");
            } catch (Exception e) {
                System.err.println("Error al realizar compra con crédito directo: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
    }
}