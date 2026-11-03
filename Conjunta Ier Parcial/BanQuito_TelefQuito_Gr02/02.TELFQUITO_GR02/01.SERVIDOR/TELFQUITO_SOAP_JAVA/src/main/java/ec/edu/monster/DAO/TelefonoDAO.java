package ec.edu.monster.DAO;

import ec.edu.monster.models.Telefonos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TelefonoDAO {
    private Connection connection;

    public TelefonoDAO(Connection connection) {
        this.connection = connection;
    }

    public Telefonos getTelefonoById(int id) throws SQLException {
        String query = "SELECT * FROM telefonos WHERE cod_telefono = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Telefonos telefono = new Telefonos();
                telefono.setCodTelefono(rs.getInt("cod_telefono"));
                telefono.setNombre(rs.getString("nombre"));
                telefono.setPrecio(rs.getDouble("precio"));
                telefono.setMarca(rs.getString("marca"));
                telefono.setDisponible(rs.getInt("disponible"));
                return telefono;
            }
        }
        return null;
    }
     public void insertTelefono(Telefonos telefono) throws SQLException {
        String query = "INSERT INTO telefonos (nombre, precio, marca, disponible) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, telefono.getNombre());
            stmt.setDouble(2, telefono.getPrecio());
            stmt.setString(3, telefono.getMarca());
            stmt.setInt(4, telefono.getDisponible());
            stmt.executeUpdate();
        }
    }

    public void updateTelefono(Telefonos telefono) throws SQLException {
        String query = "UPDATE telefonos SET nombre = ?, precio = ?, marca = ?, disponible = ? WHERE cod_telefono = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, telefono.getNombre());
            stmt.setDouble(2, telefono.getPrecio());
            stmt.setString(3, telefono.getMarca());
            stmt.setInt(4, telefono.getDisponible());
            stmt.setInt(5, telefono.getCodTelefono());
            stmt.executeUpdate();
        }
    }
    public List<Telefonos> getAllTelefonos() throws SQLException {
        String query = "SELECT * FROM telefonos";
        List<Telefonos> telefonosList = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Telefonos telefono = new Telefonos();
                telefono.setCodTelefono(rs.getInt("cod_telefono"));
                telefono.setNombre(rs.getString("nombre"));
                telefono.setPrecio(rs.getDouble("precio"));
                telefono.setMarca(rs.getString("marca"));
                telefono.setDisponible(rs.getInt("disponible"));
                telefonosList.add(telefono);
            }
        }
        return telefonosList;
    }
}