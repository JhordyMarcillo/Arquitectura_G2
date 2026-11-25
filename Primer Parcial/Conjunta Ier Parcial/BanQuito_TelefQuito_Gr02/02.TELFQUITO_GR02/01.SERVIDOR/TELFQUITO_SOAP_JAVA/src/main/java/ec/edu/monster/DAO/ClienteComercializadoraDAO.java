package ec.edu.monster.DAO;

import ec.edu.monster.models.ClienteComercializadora;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteComercializadoraDAO {
     private Connection connection;

    public ClienteComercializadoraDAO(Connection connection) {
        this.connection = connection;
    }

    public ClienteComercializadora getClienteById(int id) throws SQLException {
        String query = "SELECT * FROM cliente_comercializadora WHERE codc_cliente = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ClienteComercializadora cliente = new ClienteComercializadora();
                cliente.setCodcCliente(rs.getInt("codc_cliente"));
                cliente.setCedula(rs.getString("cedula"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setGenero(rs.getString("genero"));
                cliente.setFechaNacimiento(("fecha_nacimiento"));
                return cliente;
            }
        }
        return null;
    }
    public int getCodClienteByCedula(String cedula) throws SQLException {
        String query = "SELECT codc_cliente FROM cliente_comercializadora WHERE cedula = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cedula);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("codc_cliente");
            }
        }
        return -1;
    }
}