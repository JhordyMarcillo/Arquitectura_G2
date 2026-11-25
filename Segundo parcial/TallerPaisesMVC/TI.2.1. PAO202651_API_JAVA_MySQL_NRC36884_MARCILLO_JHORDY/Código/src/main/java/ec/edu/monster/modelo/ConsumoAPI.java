package ec.edu.monster.modelo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Consumo de la API REST Countries v5 (requiere API key en el header Authorization: Bearer).
 * Cada respuesta tiene la forma: { "data": { "objects": [ ... ] }, "meta": { "total": N } }
 */
public class ConsumoAPI {

    /** Solo pedimos los campos que necesita el taller. */
    private static final String CAMPOS =
            "names.common,capitals,region,population,currencies";

    /** Maximo de registros por pagina en el plan gratuito. */
    public static final int LIMITE = 100;

    private static final HttpClient CLIENTE = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /** Lista paginada de paises. Devuelve el JSON completo de la respuesta. */
    public static JsonObject obtenerPagina(int limit, int offset) throws ApiException {
        return llamar("?limit=" + limit + "&offset=" + offset
                + "&response_fields=" + CAMPOS);
    }

    /** Busqueda por nombre (Actividad 1). */
    public static JsonObject buscarPorNombre(String nombre) throws ApiException {
        String q = URLEncoder.encode(nombre.trim(), StandardCharsets.UTF_8);
        return llamar("?q=" + q + "&limit=" + LIMITE + "&response_fields=" + CAMPOS);
    }

    private static JsonObject llamar(String consulta) throws ApiException {
        String key = Config.getApiKey();
        if (key.isEmpty()) {
            throw new ApiException("Falta la API key de REST Countries. "
                    + "Consiguela en https://restcountries.com/sign-up y colocala en "
                    + "config.properties (api.key) o con el boton \"API Key\".");
        }
        String base = Config.get("api.url");
        HttpRequest req = HttpRequest.newBuilder(URI.create(base + consulta))
                .timeout(Duration.ofSeconds(20))
                .header("Authorization", "Bearer " + key)
                .header("Accept", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> resp =
                    CLIENTE.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            int codigo = resp.statusCode();
            if (codigo == 401 || codigo == 403) {
                throw new ApiException("La API rechazo la key (HTTP " + codigo
                        + "). Revisa que la API key sea valida.");
            }
            if (codigo == 429) {
                throw new ApiException("Se supero el limite de solicitudes (HTTP 429). "
                        + "Espera un momento e intenta de nuevo.");
            }
            if (codigo < 200 || codigo >= 300) {
                throw new ApiException("La API respondio con error HTTP " + codigo
                        + ": " + resp.body());
            }
            return JsonParser.parseString(resp.body()).getAsJsonObject();
        } catch (IOException e) {
            throw new ApiException("No se pudo conectar con la API: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Consulta interrumpida.", e);
        }
    }
}
