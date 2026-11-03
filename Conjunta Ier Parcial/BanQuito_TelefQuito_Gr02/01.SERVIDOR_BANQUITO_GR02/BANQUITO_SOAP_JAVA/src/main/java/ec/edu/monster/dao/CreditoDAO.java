package ec.edu.monster.dao;

import ec.edu.monster.bdd.DBConnection;
import ec.edu.monster.model.Credito;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


    public class CreditoDAO {
    public boolean tieneCreditoActivo(String cedula) throws SQLException {
        String sql = " SELECT COUNT(*) FROM credito cr INNER JOIN cliente cl ON cr.COD_CLIENTE = cl.COD_CLIENTE WHERE cl.CEDULA = ? AND cr.ACTIVO = 1; ";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cedula);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        }
    }
    public int createCredito(Credito credito) throws SQLException {
        String sql = "INSERT INTO credito (COD_CLIENTE, MONTO, PLAZO_MESES, TASA_INTERES, FECHA_INICIO, ACTIVO) VALUES (?, ?, ?, ?, ?, 1)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, credito.getCodCliente());
            statement.setDouble(2, credito.getMonto());
            statement.setInt(3, credito.getPlazoMeses());
            statement.setDouble(4, credito.getTasaInteres());
            statement.setString(5, credito.getFechaInicio());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating credito failed, no ID obtained.");
            }
        }
    }
    public List<Credito> getCreditosByCedula(String cedula) throws SQLException {
        String sql = "SELECT cr.* FROM credito cr INNER JOIN cliente cl ON cr.COD_CLIENTE = cl.COD_CLIENTE WHERE cl.CEDULA = ?";
        List<Credito> creditos = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cedula);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Credito credito = new Credito();
                credito.setCodCredito(resultSet.getInt("COD_CREDITO"));
                credito.setCodCliente(resultSet.getInt("COD_CLIENTE"));
                credito.setMonto(resultSet.getDouble("MONTO"));
                credito.setPlazoMeses(resultSet.getInt("PLAZO_MESES"));
                credito.setTasaInteres(resultSet.getDouble("TASA_INTERES"));
                credito.setFechaInicio(resultSet.getDate("FECHA_INICIO").toString());
                credito.setActivo(resultSet.getInt("ACTIVO"));
                creditos.add(credito);
            }
        }
        return creditos;
    }
}