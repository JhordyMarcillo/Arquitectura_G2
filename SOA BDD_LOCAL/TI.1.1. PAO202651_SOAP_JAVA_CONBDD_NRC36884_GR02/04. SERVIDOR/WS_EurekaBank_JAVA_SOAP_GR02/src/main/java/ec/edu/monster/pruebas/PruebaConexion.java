package ec.edu.monster.pruebas;

import ec.edu.monster.datos.AccesoDB;
import ec.edu.monster.datos.ConfiguracionBaseDatos;
import java.sql.Connection;

/** Prueba manual: ejecutar con "Run File" en NetBeans para verificar la conexion a MySQL. */
public class PruebaConexion {

    public static void main(String[] args) throws Exception {
        System.out.println("URL: " + ConfiguracionBaseDatos.url());
        try (Connection conexion = AccesoDB.obtenerConexion()) {
            System.out.println("Conexion correcta a " + conexion.getMetaData().getDatabaseProductName()
                    + " " + conexion.getMetaData().getDatabaseProductVersion());
        }
    }
}
