package ec.edu.monster.ws;

import ec.edu.monster.DAO.TelefonoDAO;
import ec.edu.monster.models.Telefonos;
import ec.edu.monster.servicio.TelefonoService;
import ec.edu.monster.bdd.DBConnection;

import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebService(serviceName = "WSTelefonos")
public class WSTelefonos {

    private TelefonoService telefonoService;

    public WSTelefonos() {
        try {
            Connection connection = DBConnection.getConnection();
            TelefonoDAO telefonoDAO = new TelefonoDAO(connection);
            this.telefonoService = new TelefonoService(telefonoDAO);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Web service operation to verify a customer
     */
    @WebMethod(operationName = "getTelefonoById")
    public Telefonos getTelefonoById(@WebParam(name = "id") int id) {
        try {
            return telefonoService.getTelefonoById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Web service operation to verify a customer
     */
    @WebMethod(operationName = "insertTelefono")
    public String insertTelefono(@WebParam(name = "telefono") Telefonos telefono) {
        try {
            return telefonoService.insertTelefono(telefono);
        } catch (SQLException e) {
            return "Error al insertar el teléfono: " + e.getMessage();
        }
    }
    /**
     * Web service operation to verify a customer
     */
    @WebMethod(operationName = "updateTelefono")
    public String updateTelefono(@WebParam(name = "telefono") Telefonos telefono) {
        try {
            return telefonoService.updateTelefono(telefono);
        } catch (SQLException e) {
            return "Error al actualizar el teléfono: " + e.getMessage();
        }
    }
    /**
     * Web service operation to verify a customer
     */
    @WebMethod(operationName = "getAllTelefonos")
    public List<Telefonos> getAllTelefonos() {
        try {
            return telefonoService.getAllTelefonos();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}