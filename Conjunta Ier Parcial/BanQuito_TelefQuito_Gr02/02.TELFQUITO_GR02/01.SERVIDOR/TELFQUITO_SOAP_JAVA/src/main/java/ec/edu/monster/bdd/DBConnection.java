
package ec.edu.monster.bdd;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // JDBC URL, username and password of MySQL server
    private static final String URL = "jdbc:mysql://localhost:3306/comercializadora?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    // Configurable: -DDB_PASSWORD / variable de entorno DB_PASSWORD (XAMPP por defecto: vacia)
    private static final String PASSWORD = System.getProperty("DB_PASSWORD", System.getenv().getOrDefault("DB_PASSWORD", ""));

    public static Connection getConnection() throws SQLException {
        try {
            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Open a connection
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected successfully!");
            return connection;
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found!");
            throw new SQLException("Driver not found", e);
        } catch (SQLException e) {
            System.out.println("Connection failed! Error: " + e.getMessage());
            throw e;
        }
    }

    // Example method to close the connection
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }

}
