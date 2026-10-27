package ec.edu.monster.datos;

import ec.edu.monster.modelos.utilidades.DatosEmpleado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RepositorioUsuario {

    private static final String SQL_BUSCAR_ACTIVO = """
            SELECT u.chr_emplcodigo, CONCAT(e.vch_emplnombre, ' ', e.vch_emplpaterno) AS nombre
            FROM Usuario u
            JOIN Empleado e ON e.chr_emplcodigo = u.chr_emplcodigo
            WHERE u.vch_emplusuario = ? AND u.vch_emplclave = ? AND u.vch_emplestado = 'ACTIVO'
            """;

    public DatosEmpleado buscarActivo(String usuario, String claveCifrada) throws SQLException {
        try (Connection conexion = AccesoDB.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR_ACTIVO)) {
            sentencia.setString(1, usuario);
            sentencia.setString(2, claveCifrada);
            try (ResultSet fila = sentencia.executeQuery()) {
                return fila.next() ? new DatosEmpleado(fila.getString(1), fila.getString(2)) : null;
            }
        }
    }
}
