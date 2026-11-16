package ec.edu.monster.test;

import ec.edu.monster.dao.ClienteDAO;
import ec.edu.monster.dao.CreditoDAO;
import ec.edu.monster.dao.MovimientoDAO;
import java.sql.SQLException;

public class TestVerificaciones {
    public static void main(String[] args) {
        ClienteDAO clienteDAO = new ClienteDAO();
        MovimientoDAO movimientoDAO = new MovimientoDAO();
        CreditoDAO creditoDAO = new CreditoDAO();

        String cedula = "1234567890"; // Cédula de ejemplo

        try {
            if (!clienteDAO.esCliente(cedula)) {
                System.out.println("El solicitante no es cliente del banco.");
                return;
            }

            if (!movimientoDAO.tieneDepositoReciente(cedula)) {
                System.out.println("El cliente no tiene depósitos en el último mes.");
                return;
            }

            if (!clienteDAO.cumpleRequisitoEdad(cedula)) {
                System.out.println("El cliente no cumple con el requisito de edad.");
                return;
            }

            if (creditoDAO.tieneCreditoActivo(cedula)) {
                System.out.println("El cliente tiene un crédito activo.");
                return;
            }

            System.out.println("El cliente cumple con todos los requisitos.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}