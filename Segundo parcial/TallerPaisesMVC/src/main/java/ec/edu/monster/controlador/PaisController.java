package ec.edu.monster.controlador;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import ec.edu.monster.modelo.ApiException;
import ec.edu.monster.modelo.ConsumoAPI;
import ec.edu.monster.modelo.Pais;
import ec.edu.monster.modelo.PaisDAO;

public class PaisController {

    /** Regiones que muestra la vista (en espanol) -> valor que usa la API (en ingles). */
    public static final Map<String, String> REGIONES = new LinkedHashMap<>();
    static {
        REGIONES.put("América", "Americas");
        REGIONES.put("Europa", "Europe");
        REGIONES.put("Asia", "Asia");
        REGIONES.put("África", "Africa");
        REGIONES.put("Oceanía", "Oceania");
    }

    private final PaisDAO dao = new PaisDAO();

    // =============== Consumo de la API ===============

    /**
     * Importa todos los paises de la API a MySQL, pagina por pagina.
     * @param progreso recibe (importados, total) tras cada pagina; puede ser null
     * @return cantidad de paises guardados
     */
    public int importarPaises(BiConsumer<Integer, Integer> progreso)
            throws ApiException, SQLException {
        int offset = 0;
        int guardados = 0;
        int total = Integer.MAX_VALUE;
        while (offset < total) {
            JsonObject respuesta = ConsumoAPI.obtenerPagina(ConsumoAPI.LIMITE, offset);
            total = totalDe(respuesta);
            List<Pais> pagina = parsear(respuesta);
            if (pagina.isEmpty()) {
                break;
            }
            guardados += dao.guardarLote(pagina);
            offset += ConsumoAPI.LIMITE;
            if (progreso != null) {
                progreso.accept(Math.min(offset, total), total);
            }
        }
        return guardados;
    }

    /** Actividad 1: consulta paises por nombre directamente en la API (sin guardar). */
    public List<Pais> buscarEnApi(String nombre) throws ApiException {
        if (nombre == null || nombre.isBlank()) {
            throw new ApiException("Escribe el nombre del pais a buscar.");
        }
        return parsear(ConsumoAPI.buscarPorNombre(nombre));
    }

    public int guardarEnBD(List<Pais> paises) throws SQLException {
        return dao.guardarLote(paises);
    }

    // =============== JSON -> objetos Java ===============

    /** Total de paises que existen en la API. En v5 viene en data.meta.total. */
    public static int totalDe(JsonObject respuesta) {
        JsonObject meta = null;
        if (respuesta.has("data") && respuesta.get("data").isJsonObject()
                && respuesta.getAsJsonObject("data").has("meta")) {
            meta = respuesta.getAsJsonObject("data").getAsJsonObject("meta");
        } else if (respuesta.has("meta")) {
            meta = respuesta.getAsJsonObject("meta");
        }
        return meta != null && meta.has("total") ? meta.get("total").getAsInt() : 0;
    }

    /** Convierte la respuesta de la API (data.objects[]) en una lista de Pais. */
    public static List<Pais> parsear(JsonObject respuesta) {
        List<Pais> lista = new ArrayList<>();
        if (!respuesta.has("data") || respuesta.get("data").isJsonNull()) {
            return lista;
        }
        JsonObject data = respuesta.getAsJsonObject("data");
        if (!data.has("objects")) {
            return lista;
        }
        JsonArray objetos = data.getAsJsonArray("objects");
        for (JsonElement e : objetos) {
            lista.add(aPais(e.getAsJsonObject()));
        }
        return lista;
    }

    /** Un objeto pais de la API v5 -> Pais del modelo. Tolera campos ausentes. */
    public static Pais aPais(JsonObject obj) {
        String nombre = "";
        if (obj.has("names") && obj.getAsJsonObject("names").has("common")) {
            nombre = obj.getAsJsonObject("names").get("common").getAsString();
        }

        // capital: la marcada como primary; si no hay, la primera
        String capital = "";
        if (obj.has("capitals") && obj.get("capitals").isJsonArray()) {
            JsonArray caps = obj.getAsJsonArray("capitals");
            for (JsonElement c : caps) {
                JsonObject co = c.getAsJsonObject();
                String n = co.has("name") ? co.get("name").getAsString() : "";
                boolean primary = co.has("attributes")
                        && co.getAsJsonObject("attributes").has("primary")
                        && co.getAsJsonObject("attributes").get("primary").getAsBoolean();
                if (capital.isEmpty() || primary) {
                    capital = n;
                }
                if (primary) {
                    break;
                }
            }
        }

        String region = obj.has("region") && !obj.get("region").isJsonNull()
                ? obj.get("region").getAsString() : "";
        long poblacion = obj.has("population") && !obj.get("population").isJsonNull()
                ? obj.get("population").getAsLong() : 0L;

        // moneda: "Canadian dollar (CAD)"; varias separadas por coma
        List<String> monedas = new ArrayList<>();
        if (obj.has("currencies") && obj.get("currencies").isJsonArray()) {
            for (JsonElement m : obj.getAsJsonArray("currencies")) {
                JsonObject mo = m.getAsJsonObject();
                String n = mo.has("name") ? mo.get("name").getAsString() : "";
                String c = mo.has("code") ? mo.get("code").getAsString() : "";
                monedas.add(c.isEmpty() ? n : n + " (" + c + ")");
            }
        }
        return new Pais(nombre, capital, region, poblacion, String.join(", ", monedas));
    }

    // =============== CRUD (Actividad 2) ===============

    public void crear(Pais p) throws SQLException {
        validar(p);
        dao.guardar(p);
    }

    public List<Pais> listar() throws SQLException {
        return dao.listar();
    }

    public void actualizar(Pais p) throws SQLException {
        validar(p);
        if (!dao.actualizar(p)) {
            throw new SQLException("No existe un pais con id " + p.getId());
        }
    }

    public void eliminar(int id) throws SQLException {
        if (!dao.eliminar(id)) {
            throw new SQLException("No existe un pais con id " + id);
        }
    }

    private void validar(Pais p) {
        if (p.getNombre() == null || p.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (p.getPoblacion() < 0) {
            throw new IllegalArgumentException("La poblacion no puede ser negativa.");
        }
    }

    // =============== Busquedas (Actividad 4) ===============

    public List<Pais> buscarPorNombreBD(String texto) throws SQLException {
        return dao.buscarPorNombre(texto);
    }

    /** @param regionVista nombre en espanol ("América") o el valor ya en ingles ("Americas") */
    public List<Pais> buscarPorRegion(String regionVista) throws SQLException {
        String region = REGIONES.getOrDefault(regionVista, regionVista);
        return dao.buscarPorRegion(region);
    }

    // =============== Reportes (Actividad 5) ===============

    public static class Reporte {
        public Map<String, Integer> paisesPorRegion;
        public Pais masPoblado;
        public String regionConMasPaises;
        public int totalPaises;
    }

    public Reporte generarReporte() throws SQLException {
        Reporte r = new Reporte();
        r.paisesPorRegion = dao.contarPorRegion(); // ya viene ordenado de mayor a menor
        r.masPoblado = dao.paisMasPoblado();
        r.totalPaises = r.paisesPorRegion.values().stream().mapToInt(Integer::intValue).sum();
        r.regionConMasPaises = r.paisesPorRegion.isEmpty()
                ? null : r.paisesPorRegion.keySet().iterator().next();
        return r;
    }
}
