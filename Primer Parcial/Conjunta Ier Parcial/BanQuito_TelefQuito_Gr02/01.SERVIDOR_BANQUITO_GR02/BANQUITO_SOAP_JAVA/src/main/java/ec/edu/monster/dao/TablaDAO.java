package ec.edu.monster.dao;

import ec.edu.monster.bdd.DBConnection;
import ec.edu.monster.model.Tabla;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TablaDAO {
    public void createAmortizacion(Tabla amortizacion) throws SQLException {
        String sql = "INSERT INTO amortizacion (COD_CREDITO, CUOTA, VALOR_CUOTA, INTERES_PAGADO, CAPITAL_PAGADO, SALDO) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, amortizacion.getCodCredito());
            statement.setInt(2, amortizacion.getCuota());
            statement.setDouble(3, amortizacion.getValorCuota());
            statement.setDouble(4, amortizacion.getInteresPagado());
            statement.setDouble(5, amortizacion.getCapitalPagado());
            statement.setDouble(6, amortizacion.getSaldo());
            statement.executeUpdate();
        }
    }

    public List<Tabla> getAmortizacionByCedula(String cedula) throws SQLException {
        String sql = "SELECT a.* FROM amortizacion a " +
                "INNER JOIN credito cr ON a.COD_CREDITO = cr.COD_CREDITO " +
                "INNER JOIN cliente cl ON cr.COD_CLIENTE = cl.COD_CLIENTE " +
                "WHERE cl.CEDULA = ?";
        List<Tabla> amortizaciones = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cedula);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Tabla amortizacion = new Tabla();
                amortizacion.setCodCredito(resultSet.getInt("COD_CREDITO"));
                amortizacion.setCuota(resultSet.getInt("CUOTA"));
                amortizacion.setValorCuota(resultSet.getDouble("VALOR_CUOTA"));
                amortizacion.setInteresPagado(resultSet.getDouble("INTERES_PAGADO"));
                amortizacion.setCapitalPagado(resultSet.getDouble("CAPITAL_PAGADO"));
                amortizacion.setSaldo(resultSet.getDouble("SALDO"));
                amortizaciones.add(amortizacion);
            }
        }
        return amortizaciones;
    }
}