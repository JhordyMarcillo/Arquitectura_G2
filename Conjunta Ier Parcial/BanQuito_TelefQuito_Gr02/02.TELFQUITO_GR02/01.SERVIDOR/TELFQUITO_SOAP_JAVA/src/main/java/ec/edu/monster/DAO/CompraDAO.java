package ec.edu.monster.DAO;

import ec.edu.monster.models.Compras;
import ec.edu.monster.models.Factura;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CompraDAO {
    private Connection connection;

    public CompraDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean createCompraEfectivo(Compras compra) {
        String query = "INSERT INTO compras (forma_pago, fecha, cod_telefono, codc_cliente, descuento, precio_final) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, compra.getFormaPago());
            stmt.setString(2, compra.getFecha());
            stmt.setInt(3, compra.getCodTelefono());
            stmt.setInt(4, compra.getCodcCliente());
            stmt.setDouble(5, compra.getDescuento());
            stmt.setDouble(6, compra.getPreciofinal());
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean createCompraCredito(Compras compra) {
        String query = "INSERT INTO compras (forma_pago, fecha, cod_telefono, codc_cliente, precio_final) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, compra.getFormaPago());
            stmt.setString(2, compra.getFecha());
            stmt.setInt(3, compra.getCodTelefono());
            stmt.setInt(4, compra.getCodcCliente());
            stmt.setDouble(5, compra.getPreciofinal());
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public List<Factura> obtenerFactura(String cedula) {
        String query = "SELECT c.cod_compra, c.forma_pago, c.fecha, cl.nombre AS nombre_cliente, t.nombre AS nombre_telefono, t.marca AS marca_telefono, c.descuento, c.precio_final " +
                       "FROM compras c " +
                       "JOIN cliente_comercializadora cl ON c.codc_cliente = cl.codc_cliente " +
                       "JOIN telefonos t ON c.cod_telefono = t.cod_telefono " +
                       "WHERE cl.cedula = ?";
        List<Factura> comprasList = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cedula);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Factura compra = new Factura();
                compra.setCodCompra(rs.getInt("cod_compra"));
                compra.setFormaPago(rs.getString("forma_pago"));
                compra.setFecha(rs.getString("fecha"));
                compra.setNombreCliente(rs.getString("nombre_cliente"));
                compra.setNombreTelefono(rs.getString("nombre_telefono"));
                compra.setMarcaTelefono(rs.getString("marca_telefono"));
                compra.setDescuento(rs.getDouble("descuento"));
                compra.setPreciofinal(rs.getDouble("precio_final"));
                comprasList.add(compra);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return comprasList;
    }
}