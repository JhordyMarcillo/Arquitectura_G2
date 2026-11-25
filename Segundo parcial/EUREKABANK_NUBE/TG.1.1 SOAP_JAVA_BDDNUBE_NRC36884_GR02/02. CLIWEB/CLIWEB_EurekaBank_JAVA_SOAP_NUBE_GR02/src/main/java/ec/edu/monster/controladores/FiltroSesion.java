package ec.edu.monster.controladores;

import ec.edu.monster.utilidades.Formato;
import ec.edu.monster.utilidades.Iconos;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Exige sesion en todas las paginas salvo el login y los recursos estaticos; publica los iconos para las JSP. */
@WebFilter("/*")
public class FiltroSesion extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest peticion, HttpServletResponse respuesta, FilterChain cadena)
            throws IOException, ServletException {
        peticion.setCharacterEncoding("UTF-8");
        String ruta = peticion.getServletPath();
        boolean publica = ruta.equals("/login") || ruta.startsWith("/recursos/");
        if (!publica && !SesionWeb.conSesion(peticion)) {
            respuesta.sendRedirect(peticion.getContextPath() + "/login");
            return;
        }
        peticion.setAttribute("iconos", Iconos.TODOS);
        if (SesionWeb.conSesion(peticion)) {
            peticion.setAttribute("usuario", SesionWeb.servicio(peticion).getSesion());
            peticion.setAttribute("iniciales", Formato.iniciales(SesionWeb.servicio(peticion).getSesion().getNombreEmpleado()));
            peticion.setAttribute("servidor", SesionWeb.servicio(peticion).getUrlServidor());
        }
        cadena.doFilter(peticion, respuesta);
    }
}
