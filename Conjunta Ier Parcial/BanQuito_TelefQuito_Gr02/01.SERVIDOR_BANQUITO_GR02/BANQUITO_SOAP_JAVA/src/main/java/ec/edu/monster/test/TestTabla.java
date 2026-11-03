package ec.edu.monster.test;

import ec.edu.monster.model.Credito;
import ec.edu.monster.servicio.GenerarTablaService;
import java.sql.SQLException;

public class TestTabla {
     public static void main(String[] args) {
        GenerarTablaService generarTablaService = new GenerarTablaService();

        Credito credito = new Credito();
        credito.setCodCliente(1);
        credito.setMonto(5000.00);
        credito.setPlazoMeses(12);
        credito.setTasaInteres(16.5);
        credito.setFechaInicio(("2024-01-01"));

        try {
            generarTablaService.crearCreditoYTablaAmortizacion(credito);
            System.out.println("Crédito y tabla de amortización creados exitosamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}