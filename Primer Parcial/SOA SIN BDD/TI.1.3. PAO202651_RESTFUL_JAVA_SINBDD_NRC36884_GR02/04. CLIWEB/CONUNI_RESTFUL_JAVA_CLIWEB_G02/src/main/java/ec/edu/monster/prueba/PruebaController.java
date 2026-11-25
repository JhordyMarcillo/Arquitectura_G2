package ec.edu.monster.prueba;

import ec.edu.monster.servicio.ConfiguracionCliente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "PruebaController", urlPatterns = {"/pruebas/ping", "/pruebas/info"})
public class PruebaController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            String baseUrl = ConfiguracionCliente.getBaseUrl();
            String endpoint = baseUrl + ("/info");
            if (!baseUrl.endsWith("/api/conversion")) {
                endpoint = baseUrl.replace("/convertir", "") + "/info";
            }
            URL url = new URL(endpoint);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            int code = con.getResponseCode();
            out.printf("{\"ok\":true,\"status\":%d,\"endpoint\":\"%s\"}", code, endpoint);
        } catch (Exception e) {
            resp.setStatus(500);
            try (PrintWriter out = resp.getWriter()) {
                String msg = e.getMessage() == null ? "error" : e.getMessage();
                out.printf("{\"ok\":false,\"error\":\"%s\"}", escape(msg));
            }
        }
    }

    private String escape(String s){
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}




