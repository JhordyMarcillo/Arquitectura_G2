package ec.edu.monster.dao;

import ec.edu.monster.bdd.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
    
public class MovimientoDAO {
    public boolean tieneDepositoReciente(String cedula) throws SQLException {
        String sql = " SELECT COUNT(*) FROM movimiento m INNER JOIN cuenta c ON m.NUM_CUENTA = c.NUM_CUENTA INNER JOIN cliente cl ON c.COD_CLIENTE = cl.COD_CLIENTE WHERE cl.CEDULA = ? AND m.TIPO = 'DEP' AND m.FECHA >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH) ";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cedula);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        }
    }
     public double obtenerPromedioDepositos(int cedula) throws SQLException {
        String sql = "SELECT AVG(m.VALOR) FROM movimiento m INNER JOIN cuenta c ON m.NUM_CUENTA = c.NUM_CUENTA INNER JOIN cliente cl ON c.COD_CLIENTE = cl.COD_CLIENTE WHERE cl.COD_CLIENTE = ? AND m.TIPO = 'DEP' AND m.FECHA >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH) ";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, Integer.toString(cedula));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble(1); 
            }
        }
        return 0.0;
    }
       public double obtenerPromedioRetiros(int cedula) throws SQLException {
        String sql = " SELECT AVG(m.VALOR) FROM movimiento m  INNER JOIN cuenta c ON m.NUM_CUENTA = c.NUM_CUENTA INNER JOIN cliente cl ON c.COD_CLIENTE = cl.COD_CLIENTE WHERE cl.COD_CLIENTE = ? AND m.TIPO = 'RET' AND m.FECHA >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH) ";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, Integer.toString(cedula));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble(1);
            }
        }
        return 0.0;
    }
}