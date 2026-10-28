package ec.edu.monster.controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * Servlet de autenticación.
 * <p>
 * Las credenciales NO se almacenan en el cliente. La validación
 * se delega al servidor RESTful a través del endpoint
 * {@code POST /api/auth/login}.
 * </p>
 *
 * @author GR02 - Arquitectura de Software
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    /** URL del endpoint de autenticación en el servidor RESTful. */
    private static final String AUTH_URL =
            ec.edu.monster.servicio.ConfiguracionCliente.getBaseUrl() + "/auth/login";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String usuario   = request.getParameter("username");
        String contrasena = request.getParameter("password");

        if (validarCredencialesEnServidor(usuario, contrasena)) {
            HttpSession session = request.getSession(true);
            session.setAttribute("usuario", usuario);
            response.sendRedirect(request.getContextPath() + "/app");
        } else {
            request.setAttribute("error", "Credenciales incorrectas");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    /**
     * Llama al endpoint REST del servidor para validar las credenciales.
     * El cliente NUNCA almacena ni conoce las credenciales correctas.
     *
     * @param usuario   nombre de usuario ingresado
     * @param contrasena contraseña ingresada
     * @return {@code true} si el servidor responde 200 OK
     */
    private boolean validarCredencialesEnServidor(String usuario, String contrasena) {
        try {
            URI uri = URI.create(AUTH_URL);
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            String body = "{\"usuario\":\"" + usuario + "\",\"contrasena\":\"" + contrasena + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            return conn.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            getServletContext().log("Error al autenticar en servidor: " + e.getMessage());
            return false;
        }
    }
}
