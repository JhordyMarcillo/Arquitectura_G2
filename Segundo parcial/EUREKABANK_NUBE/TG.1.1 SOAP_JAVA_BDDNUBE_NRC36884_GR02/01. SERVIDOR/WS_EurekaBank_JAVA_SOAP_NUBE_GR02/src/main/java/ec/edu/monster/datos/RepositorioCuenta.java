package ec.edu.monster.datos;

import ec.edu.monster.modelos.utilidades.CuentaInterna;
import ec.edu.monster.modelos.utilidades.DatosCuenta;
import ec.edu.monster.modelos.utilidades.mapeadores.CuentaMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepositorioCuenta {

    private static final String SQL_LISTAR = """
            SELECT c.chr_cuencodigo,
                   CONCAT(cl.vch_clienombre, ' ', cl.vch_cliepaterno, ' ', cl.vch_cliematerno) AS cliente,
                   m.vch_monedescripcion, c.dec_cuensaldo, c.vch_cuenestado
            FROM Cuenta c
            JOIN Cliente cl ON cl.chr_cliecodigo = c.chr_cliecodigo
            JOIN Moneda m ON m.chr_monecodigo = c.chr_monecodigo
            ORDER BY c.chr_cuencodigo
            """;

    private static final String SQL_BUSCAR = """
            SELECT c.chr_cuencodigo, c.chr_monecodigo, m.vch_monedescripcion, c.dec_cuensaldo,
                   c.vch_cuenestado, c.int_cuencontmov, c.chr_cuenclave, co.dec_costimporte
            FROM Cuenta c
            JOIN Moneda m ON m.chr_monecodigo = c.chr_monecodigo
            JOIN CostoMovimiento co ON co.chr_monecodigo = c.chr_monecodigo
            WHERE c.chr_cuencodigo = ?
            """;

    private static final String SQL_ACTUALIZAR = """
            UPDATE Cuenta SET dec_cuensaldo = ?, int_cuencontmov = ? WHERE chr_cuencodigo = ?
            """;

    public List<DatosCuenta> listar() throws SQLException {
        try (Connection conexion = AccesoDB.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_LISTAR);
                ResultSet fila = sentencia.executeQuery()) {
            List<DatosCuenta> cuentas = new ArrayList<>();
            while (fila.next()) {
                cuentas.add(CuentaMapper.aDatosCuenta(fila));
            }
            return cuentas;
        }
    }

    public CuentaInterna buscar(String cuenta) throws SQLException {
        try (Connection conexion = AccesoDB.obtenerConexion()) {
            return buscar(conexion, cuenta, false);
        }
    }

    /** Lee la cuenta y la bloquea (FOR UPDATE) hasta que termine la transaccion. */
    public CuentaInterna buscarParaActualizar(Connection conexion, String cuenta) throws SQLException {
        return buscar(conexion, cuenta, true);
    }

    public void actualizar(Connection conexion, CuentaInterna cuenta) throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {
            sentencia.setBigDecimal(1, cuenta.getSaldo());
            sentencia.setInt(2, cuenta.getContadorMovimientos());
            sentencia.setString(3, cuenta.getCodigo());
            sentencia.executeUpdate();
        }
    }

    private CuentaInterna buscar(Connection conexion, String cuenta, boolean bloquear) throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR + (bloquear ? " FOR UPDATE" : ""))) {
            sentencia.setString(1, cuenta);
            try (ResultSet fila = sentencia.executeQuery()) {
                return fila.next() ? CuentaMapper.aCuentaInterna(fila) : null;
            }
        }
    }
}
