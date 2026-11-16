package ec.edu.monster.servicio;

import ec.edu.monster.DAO.TelefonoDAO;
import ec.edu.monster.models.Telefonos;
import java.sql.SQLException;
import java.util.List;

public class TelefonoService {
    private TelefonoDAO telefonoDAO;

    public TelefonoService(TelefonoDAO telefonoDAO) {
        this.telefonoDAO = telefonoDAO;
    }

    public Telefonos getTelefonoById(int id) throws SQLException {
        return telefonoDAO.getTelefonoById(id);
    }

    public String insertTelefono(Telefonos telefono) throws SQLException {
        try {
            telefonoDAO.insertTelefono(telefono);
            return "Teléfono insertado con éxito.";
        } catch (SQLException e) {
            return "Error al insertar el teléfono: " + e.getMessage();
        }
    }

    public String updateTelefono(Telefonos telefono) throws SQLException {
        try {
            telefonoDAO.updateTelefono(telefono);
            return "Teléfono actualizado con éxito.";
        } catch (SQLException e) {
            return "Error al actualizar el teléfono: " + e.getMessage();
        }
    }

    public List<Telefonos> getAllTelefonos() throws SQLException {
        return telefonoDAO.getAllTelefonos();
    }
}