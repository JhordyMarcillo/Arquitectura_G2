package ec.edu.monster.DAO;

import ec.edu.monster.models.Factura;
import ec.edu.monster.models.Telefonos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Una compra es una FACTURA (tabla compras) con una o varias lineas (tabla detalle_compra).
 */
public class CompraDAO {

    private Connection connection;

    public CompraDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Registra la factura y sus lineas en una sola transaccion.
     *
     * @param descuentoPorcentaje 0.42 para efectivo, 0 para credito directo
     * @return numero de factura (cod_compra) o -1 si fallo
     */
    public int crearFactura(String formaPago, String fecha, int codcCliente, List<Telefonos> items,
            double descuentoPorcentaje) {
        String sqlCab = "INSERT INTO compras (forma_pago, fecha, codc_cliente, subtotal, descuento, precio_final) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlDet = "INSERT INTO detalle_compra (cod_compra, cod_telefono, precio, descuento, precio_final) VALUES (?, ?, ?, ?, ?)";
        try {
            connection.setAutoCommit(false);
            double subtotal = 0;
            for (Telefonos t : items) {
                subtotal += t.getPrecio();
            }
            double descuentoTotal = redondear(subtotal * descuentoPorcentaje);
            int codCompra;
            try (PreparedStatement cab = connection.prepareStatement(sqlCab, Statement.RETURN_GENERATED_KEYS)) {
                cab.setString(1, formaPago);
                cab.setString(2, fecha);
                cab.setInt(3, codcCliente);
                cab.setDouble(4, redondear(subtotal));
                cab.setDouble(5, descuentoTotal);
                cab.setDouble(6, redondear(subtotal - descuentoTotal));
                cab.executeUpdate();
                try (ResultSet keys = cab.getGeneratedKeys()) {
                    keys.next();
                    codCompra = keys.getInt(1);
                }
            }
            try (PreparedStatement det = connection.prepareStatement(sqlDet)) {
                for (Telefonos t : items) {
                    double desc = redondear(t.getPrecio() * descuentoPorcentaje);
                    det.setInt(1, codCompra);
                    det.setInt(2, t.getCodTelefono());
                    det.setDouble(3, t.getPrecio());
                    det.setDouble(4, desc);
                    det.setDouble(5, redondear(t.getPrecio() - desc));
                    det.addBatch();
                }
                det.executeBatch();
            }
            connection.commit();
            return codCompra;
        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ignored) {
            }
            return -1;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }
    }

    /** Devuelve una fila por cada linea de cada factura del cliente (misma codCompra = misma factura). */
    public List<Factura> obtenerFactura(String cedula) {
        String query = "SELECT c.cod_compra, c.forma_pago, c.fecha, cl.nombre AS nombre_cliente, "
                + "t.nombre AS nombre_telefono, t.marca AS marca_telefono, d.descuento, d.precio_final "
                + "FROM compras c "
                + "JOIN cliente_comercializadora cl ON c.codc_cliente = cl.codc_cliente "
                + "JOIN detalle_compra d ON d.cod_compra = c.cod_compra "
                + "JOIN telefonos t ON d.cod_telefono = t.cod_telefono "
                + "WHERE cl.cedula = ? ORDER BY c.cod_compra, d.cod_detalle";
        List<Factura> lineas = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cedula);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Factura f = new Factura();
                f.setCodCompra(rs.getInt("cod_compra"));
                f.setFormaPago(rs.getString("forma_pago"));
                f.setFecha(rs.getString("fecha"));
                f.setNombreCliente(rs.getString("nombre_cliente"));
                f.setNombreTelefono(rs.getString("nombre_telefono"));
                f.setMarcaTelefono(rs.getString("marca_telefono"));
                f.setDescuento(rs.getDouble("descuento"));
                f.setPreciofinal(rs.getDouble("precio_final"));
                lineas.add(f);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lineas;
    }

    private static double redondear(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
