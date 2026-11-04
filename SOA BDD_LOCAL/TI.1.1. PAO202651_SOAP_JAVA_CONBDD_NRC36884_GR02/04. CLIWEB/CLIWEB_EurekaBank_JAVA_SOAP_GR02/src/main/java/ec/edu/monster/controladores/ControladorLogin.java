package ec.edu.monster.controladores;

import ec.edu.monster.modelos.ExcepcionServicio;
import ec.edu.monster.servicios.IServicioEurekaBank;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Inicio y cierre de sesion. */
@WebServlet({"/login", "/salir"})
public class ControladorLogin extends HttpServlet {

    private static final String VISTA = "/WEB-INF/vistas/login.jsp";

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta) throws ServletException, IOException {
        if ("/salir".equals(peticion.getServletPath())) {
            doPost(peticion, respuesta);
            return;
        }
        if (SesionWeb.conSesion(peticion)) {
            respuesta.sendRedirect(peticion.getContextPath() + "/cuentas");
            return;
        }
        peticion.setAttribute("urlServidor", SesionWeb.servicio(peticion).getUrlServidor());
        peticion.setAttribute("aviso", SesionWeb.tomarAviso(peticion));
        peticion.getRequestDispatcher(VISTA).forward(peticion, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta) throws ServletException, IOException {
        IServicioEurekaBank servicio = SesionWeb.servicio(peticion);
        if ("/salir".equals(peticion.getServletPath())) {
            servicio.cerrarSesion();
            respuesta.sendRedirect(peticion.getContextPath() + "/login");
            return;
        }

        String usuario = valor(peticion, "usuario");
        servicio.setUrlServidor(valor(peticion, "urlServidor"));
        try {
            servicio.iniciarSesion(usuario, valor(peticion, "clave"));
            respuesta.sendRedirect(peticion.getContextPath() + "/cuentas");
        } catch (ExcepcionServicio e) {
            peticion.setAttribute("mensajeError", e.getMessage());
            peticion.setAttribute("usuarioIngresado", usuario);
            peticion.setAttribute("urlServidor", servicio.getUrlServidor());
            peticion.getRequestDispatcher(VISTA).forward(peticion, respuesta);
        }
    }

    static String valor(HttpServletRequest peticion, String nombre) {
        String valor = peticion.getParameter(nombre);
        return valor == null ? "" : valor.trim();
    }
}
