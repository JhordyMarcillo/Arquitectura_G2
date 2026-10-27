package ec.edu.monster.controlador;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ec.edu.monster.modelo.ConversionRequest;
import ec.edu.monster.modelo.ConversionResponse;
import ec.edu.monster.modelo.SesionUsuario;

public class ApiService {
    // URLs disponibles - puedes cambiar según tu ubicación
    // Para emulador (servidor local): http://10.0.2.2:5014/api/conversor
    // Para WiFi casa: http://192.168.10.104:5014/api/conversor
    // Para Universidad: http://10.40.17.85:5014/api/conversor
    // Para red compartida: http://192.168.137.1:5014/api/conversor

    private static final String BASE_URL = "http://192.168.137.1:5014/api/conversor"; // Red compartida
    private static final int TIMEOUT = 5000; // 5 segundos

    // Codigos que devuelve el servidor (tabla 4 de 04. SERVIDOR/CONTRATO_GR02.md)
    public static final String TOKEN_INVALIDO = "AUTH-001";
    public static final String SESION_EXPIRADA = "AUTH-002";
    public static final String CREDENCIALES_INCORRECTAS = "AUTH-003";
    public static final String UNIDAD_NO_SOPORTADA = "UNIT-001";
    public static final String DATOS_INCONSISTENTES = "DATA-001";
    public static final String ERROR_RED = "RED";

    private final ExecutorService executorService;
    private final Handler mainHandler;

    public ApiService() {
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public interface ApiCallback<T> {
        void onSuccess(T respuesta);
        void onError(String codigo, String mensaje);
    }

    // CU-01 Autenticarse: POST /login, devuelve el token
    public void login(String usuario, String contrasenia, ApiCallback<String> callback) {
        executorService.execute(() -> {
            try {
                JSONObject cuerpo = new JSONObject();
                cuerpo.put("usuario", usuario);
                cuerpo.put("contrasenia", contrasenia);

                JSONObject json = enviar("POST", "/login", cuerpo, false);
                if (esOk(json)) {
                    String token = json.optString("token", "");
                    mainHandler.post(() -> callback.onSuccess(token));
                } else {
                    notificarError(callback, json);
                }
            } catch (Exception e) {
                notificarExcepcion(callback, e);
            }
        });
    }

    // CU-02 Consultar catalogo: GET /categorias
    public void obtenerCategorias(ApiCallback<List<String>> callback) {
        executorService.execute(() -> {
            try {
                JSONObject json = enviar("GET", "/categorias", null, true);
                if (esOk(json)) {
                    List<String> categorias = leerLista(json.optJSONArray("categorias"));
                    mainHandler.post(() -> callback.onSuccess(categorias));
                } else {
                    notificarError(callback, json);
                }
            } catch (Exception e) {
                notificarExcepcion(callback, e);
            }
        });
    }

    // CU-03 Consultar unidades por categoria: GET /categorias/{categoria}/unidades
    public void obtenerUnidades(String categoria, ApiCallback<List<String>> callback) {
        executorService.execute(() -> {
            try {
                String ruta = "/categorias/" + URLEncoder.encode(categoria, "UTF-8") + "/unidades";
                JSONObject json = enviar("GET", ruta, null, true);
                if (esOk(json)) {
                    List<String> unidades = leerLista(json.optJSONArray("unidades"));
                    mainHandler.post(() -> callback.onSuccess(unidades));
                } else {
                    notificarError(callback, json);
                }
            } catch (Exception e) {
                notificarExcepcion(callback, e);
            }
        });
    }

    // CU-04 Realizar conversion: POST /convertir con cuerpo JSON
    public void realizarConversion(ConversionRequest request, ApiCallback<ConversionResponse> callback) {
        executorService.execute(() -> {
            try {
                JSONObject cuerpo = new JSONObject();
                cuerpo.put("categoria", request.getCategoria());
                cuerpo.put("unidadOrigen", request.getUnidadOrigen());
                cuerpo.put("unidadDestino", request.getUnidadDestino());
                cuerpo.put("valor", request.getValor());

                JSONObject json = enviar("POST", "/convertir", cuerpo, true);
                if (esOk(json)) {
                    ConversionResponse respuesta = new ConversionResponse();
                    respuesta.setEstado(json.optString("estado", ""));
                    respuesta.setCodigo(json.optString("codigo", ""));
                    respuesta.setMensaje(json.optString("mensaje", ""));
                    respuesta.setResultado(json.isNull("resultado") ? null : json.getDouble("resultado"));
                    respuesta.setUnidadOrigen(json.optString("unidadOrigen", request.getUnidadOrigen()));
                    respuesta.setUnidadDestino(json.optString("unidadDestino", request.getUnidadDestino()));
                    mainHandler.post(() -> callback.onSuccess(respuesta));
                } else {
                    notificarError(callback, json);
                }
            } catch (Exception e) {
                notificarExcepcion(callback, e);
            }
        });
    }

    // Envia la peticion y devuelve el JSON del cuerpo, tambien en 400/401/500
    private JSONObject enviar(String metodo, String ruta, JSONObject cuerpo, boolean conToken) throws Exception {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(BASE_URL + ruta);
            Log.d("ApiService", metodo + " " + url);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(metodo);
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);

            if (conToken && SesionUsuario.estaActiva()) {
                connection.setRequestProperty("Authorization", "Bearer " + SesionUsuario.getToken());
            }

            if (cuerpo != null) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                OutputStream os = connection.getOutputStream();
                os.write(cuerpo.toString().getBytes("UTF-8"));
                os.close();
            }

            int responseCode = connection.getResponseCode();
            InputStream stream = responseCode >= 400 ? connection.getErrorStream() : connection.getInputStream();
            String texto = leerTexto(stream);
            Log.d("ApiService", "HTTP " + responseCode + " " + texto);

            if (texto.isEmpty()) {
                JSONObject vacio = new JSONObject();
                vacio.put("estado", "ERROR");
                vacio.put("codigo", String.valueOf(responseCode));
                vacio.put("mensaje", "Servidor no responde correctamente (código: " + responseCode + ")");
                return vacio;
            }
            return new JSONObject(texto);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String leerTexto(InputStream stream) throws Exception {
        if (stream == null) {
            return "";
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuilder texto = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            texto.append(line);
        }
        br.close();
        return texto.toString();
    }

    private boolean esOk(JSONObject json) {
        return "OK".equals(json.optString("estado", ""));
    }

    private List<String> leerLista(JSONArray arreglo) throws Exception {
        List<String> lista = new ArrayList<>();
        if (arreglo != null) {
            for (int i = 0; i < arreglo.length(); i++) {
                lista.add(arreglo.getString(i));
            }
        }
        return lista;
    }

    private <T> void notificarError(ApiCallback<T> callback, JSONObject json) {
        String codigo = json.optString("codigo", "");
        String mensaje = json.optString("mensaje", "Error desconocido");
        mainHandler.post(() -> callback.onError(codigo, mensaje));
    }

    private <T> void notificarExcepcion(ApiCallback<T> callback, Exception e) {
        String mensaje;
        if (e instanceof java.net.SocketTimeoutException) {
            mensaje = "Tiempo de espera agotado. Verifica que el servidor esté ejecutándose.";
        } else if (e instanceof java.net.ConnectException) {
            mensaje = "No se puede conectar al servidor. Verifica:\n1. Servidor ejecutándose\n2. Misma red WiFi\n3. Firewall desactivado";
        } else if (e instanceof java.net.UnknownHostException) {
            mensaje = "No se encuentra el servidor en " + BASE_URL;
        } else {
            mensaje = "Error: " + e.getMessage();
        }
        mainHandler.post(() -> callback.onError(ERROR_RED, mensaje));
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
