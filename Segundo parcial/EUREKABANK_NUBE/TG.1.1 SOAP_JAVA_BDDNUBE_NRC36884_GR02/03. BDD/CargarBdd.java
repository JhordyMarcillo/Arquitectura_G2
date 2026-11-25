import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Carga Crea_BD.sql y Carga_Datos.sql en MySQL (Amazon RDS) y crea el usuario de la aplicacion.
 * Las claves llegan por variables de entorno (RDS_ADMIN_PASSWORD y DB_PASSWORD), nunca en el codigo.
 * Argumentos: host usuarioAdmin usuarioApp carpetaScripts [recrear]
 */
public class CargarBdd {

    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://" + args[0] + ":3306/?sslMode=REQUIRED&allowMultiQueries=true&connectTimeout=15000&characterEncoding=UTF-8";
        boolean recrear = args.length > 4 && "recrear".equals(args[4]);
        String claveApp = System.getenv("DB_PASSWORD");
        if (claveApp == null || !claveApp.matches("[A-Za-z0-9]{16,}")) {
            throw new IllegalArgumentException("DB_PASSWORD debe tener al menos 16 letras o numeros");
        }
        try (Connection con = DriverManager.getConnection(url, args[1], System.getenv("RDS_ADMIN_PASSWORD"));
             Statement st = con.createStatement()) {
            try (ResultSet rs = st.executeQuery("SHOW DATABASES LIKE 'eurekabank'")) {
                if (rs.next() && !recrear) {
                    System.out.println("La base eurekabank ya existe. Use -Recrear para volver a cargarla (se pierden los datos).");
                    System.exit(2);
                }
            }
            st.execute(Files.readString(Path.of(args[3], "Crea_BD.sql")));
            while (st.getMoreResults() || st.getUpdateCount() != -1) {
            }
            System.out.println("Crea_BD.sql aplicado");
            st.execute(Files.readString(Path.of(args[3], "Carga_Datos.sql")));
            while (st.getMoreResults() || st.getUpdateCount() != -1) {
            }
            System.out.println("Carga_Datos.sql aplicado");
            st.execute("CREATE USER IF NOT EXISTS '" + args[2] + "'@'%' IDENTIFIED BY '" + claveApp + "' REQUIRE SSL");
            st.execute("ALTER USER '" + args[2] + "'@'%' IDENTIFIED BY '" + claveApp + "' REQUIRE SSL");
            st.execute("GRANT SELECT, INSERT, UPDATE, DELETE ON eurekabank.* TO '" + args[2] + "'@'%'");
            System.out.println("Usuario " + args[2] + " listo (solo lectura y escritura de datos, con SSL)");
            try (ResultSet rs = st.executeQuery("SELECT (SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='eurekabank'),"
                    + "(SELECT COUNT(*) FROM eurekabank.Cuenta),(SELECT COUNT(*) FROM eurekabank.Cliente),(SELECT COUNT(*) FROM eurekabank.Movimiento), @@version")) {
                rs.next();
                System.out.println("Tablas: " + rs.getInt(1) + " | cuentas: " + rs.getInt(2) + " | clientes: " + rs.getInt(3)
                        + " | movimientos: " + rs.getInt(4) + " | MySQL " + rs.getString(5));
            }
        }
    }
}
