package ec.edu.monster.datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class AccesoDB {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private AccesoDB() {
    }

    public static Connection obtenerConexion() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encuentra el driver JDBC de MySQL", e);
        }
        return DriverManager.getConnection(ConfiguracionBaseDatos.url(),
                ConfiguracionBaseDatos.usuario(), ConfiguracionBaseDatos.clave());
    }
}
