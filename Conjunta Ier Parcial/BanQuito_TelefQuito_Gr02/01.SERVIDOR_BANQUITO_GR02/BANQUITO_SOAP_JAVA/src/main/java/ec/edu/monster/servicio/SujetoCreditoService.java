package ec.edu.monster.servicio;

import ec.edu.monster.dao.ClienteDAO;
import ec.edu.monster.dao.CreditoDAO;
import ec.edu.monster.dao.MovimientoDAO;
import java.sql.SQLException;

public class SujetoCreditoService {
    ClienteDAO clienteDAO = new ClienteDAO();
    MovimientoDAO movimientoDAO = new MovimientoDAO();
    CreditoDAO creditoDAO = new CreditoDAO();

        
    public int verificar( String cedula ){
        int cod = this.obtenerCodClientePorCedula(cedula);
         try {
            if (!clienteDAO.esCliente(cedula)) {
                System.out.println("El solicitante no es cliente del banco.");
                return 0;
            }

            if (!movimientoDAO.tieneDepositoReciente(cedula)) {
                System.out.println("El cliente no tiene depósitos en el último mes.");
                return 0;
            }

            if (!clienteDAO.cumpleRequisitoEdad(cedula)) {
                System.out.println("El cliente no cumple con el requisito de edad.");
                return 0;
            }

            if (creditoDAO.tieneCreditoActivo(cedula)) {
                System.out.println("El cliente tiene un crédito activo.");
                return 0;
            }
            

            System.out.println("El cliente cumple con todos los requisitos.");
            return cod;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    public int obtenerCodClientePorCedula(String cedula) {
        try {
            return clienteDAO.getCodClienteByCedula(cedula);
            
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}