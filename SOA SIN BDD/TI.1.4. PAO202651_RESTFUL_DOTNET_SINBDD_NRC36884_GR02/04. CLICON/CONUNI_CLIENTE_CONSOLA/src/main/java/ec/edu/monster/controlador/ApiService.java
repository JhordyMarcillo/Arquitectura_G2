package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ConversionRequest;
import ec.edu.monster.modelo.ConversionResponse;
import ec.edu.monster.modelo.ExcepcionApi;
import ec.edu.monster.modelo.SesionUsuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ApiService {

    // Codigos que devuelve el servidor (tabla 4 de 04. SERVIDOR/CONTRATO_GR02.md)
    public static final String TOKEN_INVALIDO = "AUTH-001";
    public static final String SESION_EXPIRADA = "AUTH-002";
    public static final String CREDENCIALES_INCORRECTAS = "AUTH-003";
    public static final String UNIDAD_NO_SOPORTADA = "UNIT-001";
    public static final String DATOS_INCONSISTENTES = "DATA-001";
    public static final String ERROR_RED = "RED";

    // CU-01 Autenticarse: POST /login, guarda el token en SesionUsuario
    public String login(String usuario, String contrasenia) throws ExcepcionApi {
        JSONObject cuerpo = new JSONObject();
        cuerpo.put("usuario", usuario);
        cuerpo.put("contrasenia", contrasenia);

        JSONObject json = enviar("POST", "/login", cuerpo, false);
        String token = json.optString("token", "");
        SesionUsuario.iniciar(usuario, token);
        return token;
    }

    // CU-02 Consultar catalogo: GET /categorias
    public List<String> obtenerCategorias() throws ExcepcionApi {
        JSONObject json = enviar("GET", "/categorias", null, true);
        return leerLista(json.optJSONArray("categorias"));
    }

    // CU-03 Consultar unidades por categoria: GET /categorias/{categoria}/unidades
    public List<String> obtenerUnidades(String categoria) throws ExcepcionApi {
        String ruta = "/categorias/" + URLEncoder.encode(categoria, StandardCharsets.UTF_8) + "/unidades";
        JSONObject json = enviar("GET", ruta, null, true);
        return leerLista(json.optJSONArray("unidades"));
    }

    // CU-04 Realizar conversion: POST /convertir con cuerpo JSON
    public ConversionResponse realizarConversion(ConversionRequest request) throws ExcepcionApi {
        JSONObject cuerpo = new JSONObject();
        cuerpo.put("categoria", request.getCategoria());
        cuerpo.put("unidadOrigen", request.getUnidadOrigen());
        cuerpo.put("unidadDestino", request.getUnidadDestino());
        cuerpo.put("valor", request.getValor());

        JSONObject json = enviar("POST", "/convertir", cuerpo, true);

        ConversionResponse respuesta = new ConversionResponse();
        respuesta.setEstado(json.optString("estado", ""));
        respuesta.setCodigo(json.optString("codigo", ""));
        respuesta.setMensaje(json.optString("mensaje", ""));
        respuesta.setResultado(json.isNull("resultado") ? null : json.getDouble("resultado"));
        respuesta.setUnidadOrigen(json.optString("unidadOrigen", request.getUnidadOrigen()));
        respuesta.setUnidadDestino(json.optString("unidadDestino", request.getUnidadDestino()));
        return respuesta;
    }

    // Sin token el servidor responde 401 con JSON: cualquier respuesta HTTP indica que esta vivo
    public boolean verificarConexion() {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) URI.create(Configuracion.URL_BASE + "/categorias").toURL().openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.getResponseCode();
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // Envia la peticion; devuelve el JSON si estado = OK, si no lanza ExcepcionApi con codigo y mensaje
    private JSONObject enviar(String metodo, String ruta, JSONObject cuerpo, boolean conToken) throws ExcepcionApi {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) URI.create(Configuracion.URL_BASE + ruta).toURL().openConnection();
            connection.setRequestMethod(metodo);
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(Configuracion.TIMEOUT_MS);
            connection.setReadTimeout(Configuracion.TIMEOUT_MS);

            if (conToken && SesionUsuario.estaActiva()) {
                connection.setRequestProperty("Authorization", "Bearer " + SesionUsuario.getToken());
            }

            if (cuerpo != null) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(cuerpo.toString().getBytes(StandardCharsets.UTF_8));
                }
            }

            // En 400/401/500 el JSON de error viene por getErrorStream()
            int responseCode = connection.getResponseCode();
            InputStream stream = responseCode >= 400 ? connection.getErrorStream() : connection.getInputStream();
            String texto = stream == null ? "" : new String(stream.readAllBytes(), StandardCharsets.UTF_8);

            JSONObject json;
            try {
                json = new JSONObject(texto);
            } catch (JSONException e) {
                throw new ExcepcionApi(String.valueOf(responseCode),
                        "Servidor no responde correctamente (código: " + responseCode + ")");
            }

            if (!"OK".equals(json.optString("estado", ""))) {
                ExcepcionApi error = new ExcepcionApi(json.optString("codigo", String.valueOf(responseCode)),
                        json.optString("mensaje", "Error desconocido"));
                if (error.esErrorDeSesion()) {
                    SesionUsuario.cerrar();
                }
                throw error;
            }
            return json;

        } catch (SocketTimeoutException e) {
            throw new ExcepcionApi(ERROR_RED, "Tiempo de espera agotado. Verifique que el servidor esté ejecutándose.");
        } catch (ConnectException e) {
            throw new ExcepcionApi(ERROR_RED, "No se puede conectar con el servidor en " + Configuracion.URL_BASE);
        } catch (UnknownHostException e) {
            throw new ExcepcionApi(ERROR_RED, "No se encuentra el servidor en " + Configuracion.URL_BASE);
        } catch (IOException e) {
            throw new ExcepcionApi(ERROR_RED, "Error de comunicación: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private List<String> leerLista(JSONArray arreglo) {
        List<String> lista = new ArrayList<>();
        if (arreglo != null) {
            for (int i = 0; i < arreglo.length(); i++) {
                lista.add(arreglo.getString(i));
            }
        }
        return lista;
    }
}
