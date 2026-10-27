package ec.edu.monster.datos;

import ec.edu.monster.modelos.utilidades.DatosMovimiento;
import ec.edu.monster.modelos.utilidades.mapeadores.MovimientoMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class RepositorioMovimiento {

    private static final String SQL_LISTAR = """
            SELECT mv.int_movinumero, mv.dtt_movifecha, mv.chr_tipocodigo, t.vch_tipodescripcion,
                   t.vch_tipoaccion, mv.dec_moviimporte, mv.chr_cuenreferencia
            FROM Movimiento mv
            JOIN TipoMovimiento t ON t.chr_tipocodigo = mv.chr_tipocodigo
            WHERE mv.chr_cuencodigo = ?
            ORDER BY mv.int_movinumero
            """;

    private static final String SQL_INSERTAR = """
            INSERT INTO Movimiento (chr_cuencodigo, int_movinumero, dtt_movifecha, chr_emplcodigo,
                                    chr_tipocodigo, dec_moviimporte, chr_cuenreferencia)
            VALUES (?, ?, CURRENT_DATE, ?, ?, ?, ?)
            """;

    public List<DatosMovimiento> listar(String cuenta) throws SQLException {
        try (Connection conexion = AccesoDB.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_LISTAR)) {
            sentencia.setString(1, cuenta);
            try (ResultSet fila = sentencia.executeQuery()) {
                List<DatosMovimiento> movimientos = new ArrayList<>();
                while (fila.next()) {
                    movimientos.add(MovimientoMapper.aDatosMovimiento(fila));
                }
                return movimientos;
            }
        }
    }

    public void insertar(Connection conexion, String cuenta, int numero, String empleado, String tipo,
            BigDecimal importe, String referencia) throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR)) {
            sentencia.setString(1, cuenta);
            sentencia.setInt(2, numero);
            sentencia.setString(3, empleado);
            sentencia.setString(4, tipo);
            sentencia.setBigDecimal(5, importe);
            if (referencia == null) {
                sentencia.setNull(6, Types.CHAR);
            } else {
                sentencia.setString(6, referencia);
            }
            sentencia.executeUpdate();
        }
    }
}
