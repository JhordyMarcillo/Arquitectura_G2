package ec.edu.monster.modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** DAO: unico lugar que habla SQL con la tabla pais. Los errores se propagan como SQLException. */
public class PaisDAO {

    private static final String SELECT =
            "SELECT id,nombre,capital,region,poblacion,moneda FROM pais ";

    // ---------- CREATE ----------

    /** Inserta el pais; si el nombre ya existe, actualiza sus datos (importar varias veces no duplica). */
    public void guardar(Pais pais) throws SQLException {
        String sql = "INSERT INTO pais(nombre,capital,region,poblacion,moneda) "
                + "VALUES(?,?,?,?,?) "
                + "ON DUPLICATE KEY UPDATE capital=VALUES(capital), region=VALUES(region), "
                + "poblacion=VALUES(poblacion), moneda=VALUES(moneda)";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            rellenar(ps, pais);
            ps.executeUpdate();
        }
    }

    /** Guarda muchos paises en un solo lote. Devuelve cuantos se procesaron. */
    public int guardarLote(List<Pais> paises) throws SQLException {
        String sql = "INSERT INTO pais(nombre,capital,region,poblacion,moneda) "
                + "VALUES(?,?,?,?,?) "
                + "ON DUPLICATE KEY UPDATE capital=VALUES(capital), region=VALUES(region), "
                + "poblacion=VALUES(poblacion), moneda=VALUES(moneda)";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            con.setAutoCommit(false);
            for (Pais p : paises) {
                rellenar(ps, p);
                ps.addBatch();
            }
            ps.executeBatch();
            con.commit();
            return paises.size();
        }
    }

    // ---------- READ ----------

    public List<Pais> listar() throws SQLException {
        return consultar(SELECT + "ORDER BY nombre", null);
    }

    public List<Pais> buscarPorNombre(String texto) throws SQLException {
        return consultar(SELECT + "WHERE nombre LIKE ? ORDER BY nombre", "%" + texto + "%");
    }

    public List<Pais> buscarPorRegion(String region) throws SQLException {
        return consultar(SELECT + "WHERE region = ? ORDER BY nombre", region);
    }

    private List<Pais> consultar(String sql, String parametro) throws SQLException {
        List<Pais> lista = new ArrayList<>();
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (parametro != null) {
                ps.setString(1, parametro);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    // ---------- UPDATE ----------

    public boolean actualizar(Pais pais) throws SQLException {
        String sql = "UPDATE pais SET nombre=?, capital=?, region=?, poblacion=?, moneda=? "
                + "WHERE id=?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            rellenar(ps, pais);
            ps.setInt(6, pais.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // ---------- DELETE ----------

    public boolean eliminar(int id) throws SQLException {
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement("DELETE FROM pais WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ---------- REPORTES (Actividad 5) ----------

    /** Numero de paises por region, de mayor a menor. */
    public Map<String, Integer> contarPorRegion() throws SQLException {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        String sql = "SELECT region, COUNT(*) total FROM pais "
                + "GROUP BY region ORDER BY total DESC, region";
        try (Connection con = Conexion.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String region = rs.getString("region");
                mapa.put(region == null || region.isBlank() ? "(sin region)" : region,
                        rs.getInt("total"));
            }
        }
        return mapa;
    }

    /** Pais con mayor poblacion, o null si la tabla esta vacia. */
    public Pais paisMasPoblado() throws SQLException {
        List<Pais> l = consultar(SELECT + "ORDER BY poblacion DESC LIMIT 1", null);
        return l.isEmpty() ? null : l.get(0);
    }

    // ---------- utilidades ----------

    private void rellenar(PreparedStatement ps, Pais p) throws SQLException {
        ps.setString(1, p.getNombre());
        ps.setString(2, p.getCapital());
        ps.setString(3, p.getRegion());
        ps.setLong(4, p.getPoblacion());
        ps.setString(5, p.getMoneda());
    }

    private Pais mapear(ResultSet rs) throws SQLException {
        Pais p = new Pais(rs.getString("nombre"), rs.getString("capital"),
                rs.getString("region"), rs.getLong("poblacion"), rs.getString("moneda"));
        p.setId(rs.getInt("id"));
        return p;
    }
}
