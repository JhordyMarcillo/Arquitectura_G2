package ec.edu.monster.modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexion {

    private static boolean baseLista = false;

    private static String urlServidor() {
        return "jdbc:mysql://" + Config.get("db.host") + ":" + Config.get("db.port")
                + "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    private static String urlBase() {
        return "jdbc:mysql://" + Config.get("db.host") + ":" + Config.get("db.port")
                + "/" + Config.get("db.name")
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    /** Crea la base de datos y la tabla si todavia no existen. */
    private static synchronized void prepararBase() throws SQLException {
        if (baseLista) {
            return;
        }
        try (Connection con = DriverManager.getConnection(
                urlServidor(), Config.get("db.user"), Config.get("db.pass"));
             Statement st = con.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + Config.get("db.name"));
            st.executeUpdate("USE " + Config.get("db.name"));
            st.executeUpdate("CREATE TABLE IF NOT EXISTS pais("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "nombre VARCHAR(100) NOT NULL,"
                    + "capital VARCHAR(100),"
                    + "region VARCHAR(100),"
                    + "poblacion BIGINT,"
                    + "moneda VARCHAR(100),"
                    + "UNIQUE KEY uk_pais_nombre (nombre))");
        }
        baseLista = true;
    }

    /** Devuelve una conexion nueva (el que llama debe cerrarla). */
    public static Connection conectar() throws SQLException {
        prepararBase();
        return DriverManager.getConnection(
                urlBase(), Config.get("db.user"), Config.get("db.pass"));
    }
}
