package ec.edu.monster.dao;

import ec.edu.monster.bdd.DBConnection;
import ec.edu.monster.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public void createCliente(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (CEDULA, NOMBRE, GENERO, FECHA_NACIMIENTO) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cliente.getCedula());
            statement.setString(2, cliente.getNombre());
            statement.setString(3, cliente.getGenero());
            statement.setDate(4, cliente.getFechaNacimiento());
            statement.executeUpdate();
            System.out.println("Cliente creado exitosamente.");
        }
    }

    public Cliente getClienteById(int id) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE COD_CLIENTE = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Cliente cliente = new Cliente();
                cliente.setCodCliente(resultSet.getInt("COD_CLIENTE"));
                cliente.setCedula(resultSet.getString("CEDULA"));
                cliente.setNombre(resultSet.getString("NOMBRE"));
                cliente.setGenero(resultSet.getString("GENERO"));
                cliente.setFechaNacimiento(resultSet.getDate("FECHA_NACIMIENTO"));
                return cliente;
            }
        }
        return null;
    }

    public List<Cliente> getAllClientes() throws SQLException {
        String sql = "SELECT * FROM cliente";
        List<Cliente> clientes = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Cliente cliente = new Cliente();
                cliente.setCodCliente(resultSet.getInt("COD_CLIENTE"));
                cliente.setCedula(resultSet.getString("CEDULA"));
                cliente.setNombre(resultSet.getString("NOMBRE"));
                cliente.setGenero(resultSet.getString("GENERO"));
                cliente.setFechaNacimiento(resultSet.getDate("FECHA_NACIMIENTO"));
                clientes.add(cliente);
            }
        }
        return clientes;
    }

    public void updateCliente(Cliente cliente) throws SQLException {
        String sql = "UPDATE cliente SET CEDULA = ?, NOMBRE = ?, GENERO = ?, FECHA_NACIMIENTO = ? WHERE COD_CLIENTE = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cliente.getCedula());
            statement.setString(2, cliente.getNombre());
            statement.setString(3, cliente.getGenero());
            statement.setDate(4, cliente.getFechaNacimiento());
            statement.setInt(5, cliente.getCodCliente());
            statement.executeUpdate();
            System.out.println("Cliente actualizado exitosamente.");
        }
    }

    public void deleteCliente(int id) throws SQLException {
        String sql = "DELETE FROM cliente WHERE COD_CLIENTE = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("Cliente eliminado exitosamente.");
        }
    }
    public boolean esCliente(String cedula) throws SQLException {
    String sql = "SELECT COUNT(*) FROM cliente WHERE CEDULA = ?";
    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, cedula);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1) > 0;
        }
    }
    return false;
    }
    public boolean tieneDepositoReciente(String cedula) throws SQLException {
    String sql = "SELECT COUNT(*) FROM movimiento m INNER JOIN cuenta c ON m.NUM_CUENTA = c.NUM_CUENTA INNER JOIN cliente cl ON c.COD_CLIENTE = cl.COD_CLIENTE WHERE cl.CEDULA = ? AND m.TIPO = 'DEP' AND m.FECHA >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)";
    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, cedula);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1) > 0;
        }
    }
    return false;
}
    public boolean cumpleRequisitoEdad(String cedula) throws SQLException {
    String sql = "SELECT TIMESTAMPDIFF(YEAR, FECHA_NACIMIENTO, CURDATE()) AS edad, GENERO FROM cliente WHERE CEDULA = ?";
    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, cedula);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            int edad = resultSet.getInt("edad");
            String genero = resultSet.getString("GENERO");
            return !("M".equals(genero) && edad < 25);
        }
    }
    return false;
}
    public boolean tieneCreditoActivo(String cedula) throws SQLException {
    String sql = "SELECT COUNT(*) FROM credito cr INNER JOIN cliente cl ON cr.COD_CLIENTE = cl.COD_CLIENTE WHERE cl.CEDULA = ? AND NOT EXISTS ( SELECT 1 FROM amortizacion WHERE COD_CREDITO = cr.COD_CREDITO AND SALDO > 0)";
    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, cedula);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1) > 0;
        }
    }
    return false;
}
     public int getCodClienteByCedula(String cedula) throws SQLException {
        String sql = "SELECT COD_CLIENTE FROM cliente WHERE CEDULA = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cedula);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("COD_CLIENTE");
            }
        }
        return -1;
    }
}