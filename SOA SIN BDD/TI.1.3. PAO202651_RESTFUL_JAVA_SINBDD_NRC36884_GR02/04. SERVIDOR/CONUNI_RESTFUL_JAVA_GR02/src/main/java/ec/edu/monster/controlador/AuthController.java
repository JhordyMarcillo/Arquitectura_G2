package ec.edu.monster.controlador;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Controlador REST para la autenticación de usuarios.
 * <p>
 * Las credenciales residen ÚNICAMENTE en el servidor. Los clientes
 * envían usuario y contraseña vía POST y el servidor responde si son válidas.
 * </p>
 *
 * <p>Endpoint: {@code POST /api/auth/login}</p>
 * <p>Body JSON: {@code { "usuario": "...", "contrasena": "..." }}</p>
 * <p>Respuesta: {@code { "autenticado": true/false, "mensaje": "..." }}</p>
 *
 * @author GR09 - Arquitectura de Software
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    // Credenciales almacenadas SOLO en el servidor
    private static final String USUARIO_VALIDO    = "MONSTER";
    private static final String CONTRASENA_VALIDA = "MONSTER9";

    /**
     * Endpoint de autenticación.
     * Recibe credenciales del cliente y retorna si son válidas.
     *
     * @param credenciales objeto JSON con "usuario" y "contrasena"
     * @return 200 OK con {@code { "autenticado": true }}  o
     *         401 Unauthorized con {@code { "autenticado": false, "mensaje": "Credenciales incorrectas" }}
     */
    @POST
    @Path("/login")
    public Response login(LoginRequest credenciales) {
        if (credenciales == null
                || credenciales.getUsuario() == null
                || credenciales.getContrasena() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new LoginResponse(false, "Credenciales no proporcionadas"))
                    .build();
        }

        boolean esValido = USUARIO_VALIDO.equals(credenciales.getUsuario().trim().toUpperCase())
                        && CONTRASENA_VALIDA.equals(credenciales.getContrasena().trim());

        System.out.println("DEBUG: login – usuario='" + credenciales.getUsuario()
                + "' autenticado=" + esValido);

        if (esValido) {
            return Response.ok(new LoginResponse(true, "Autenticación exitosa")).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new LoginResponse(false, "Credenciales incorrectas"))
                    .build();
        }
    }

    // ====== DTOs internos al controlador ======

    /**
     * DTO para recibir las credenciales del cliente.
     */
    public static class LoginRequest {
        private String usuario;
        private String contrasena;

        public LoginRequest() {}

        public String getUsuario()    { return usuario; }
        public void setUsuario(String u) { this.usuario = u; }

        public String getContrasena()    { return contrasena; }
        public void setContrasena(String c) { this.contrasena = c; }
    }

    /**
     * DTO para devolver el resultado de la autenticación.
     */
    public static class LoginResponse {
        private boolean autenticado;
        private String  mensaje;

        public LoginResponse() {}
        public LoginResponse(boolean autenticado, String mensaje) {
            this.autenticado = autenticado;
            this.mensaje     = mensaje;
        }

        public boolean isAutenticado()   { return autenticado; }
        public void setAutenticado(boolean a) { this.autenticado = a; }

        public String getMensaje()   { return mensaje; }
        public void setMensaje(String m) { this.mensaje = m; }
    }
}
