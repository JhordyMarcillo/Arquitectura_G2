package ec.edu.monster.controladores;

import ec.edu.monster.servicios.IServicioEurekaBank;
import ec.edu.monster.servicios.ServicioEurekaBankRest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/** Cada sesion del navegador tiene su propio servicio EurekaBank (con su URL y su token). */
public final class SesionWeb {

    private static final String CLAVE_SERVICIO = "servicioEurekaBank";
    private static final String CLAVE_AVISO = "aviso";

    private SesionWeb() {
    }

    public static IServicioEurekaBank servicio(HttpServletRequest peticion) {
        HttpSession sesion = peticion.getSession();
        IServicioEurekaBank servicio = (IServicioEurekaBank) sesion.getAttribute(CLAVE_SERVICIO);
        if (servicio == null) {
            servicio = new ServicioEurekaBankRest();
            String url = peticion.getServletContext().getInitParameter("urlServidor");
            if (url != null && !url.isBlank()) {
                servicio.setUrlServidor(url);
            }
            sesion.setAttribute(CLAVE_SERVICIO, servicio);
        }
        return servicio;
    }

    public static boolean conSesion(HttpServletRequest peticion) {
        return servicio(peticion).getSesion() != null;
    }

    /** Mensaje que se muestra una sola vez en el login (por ejemplo, sesion expirada). */
    public static void avisar(HttpServletRequest peticion, String mensaje) {
        peticion.getSession().setAttribute(CLAVE_AVISO, mensaje);
    }

    public static String tomarAviso(HttpServletRequest peticion) {
        HttpSession sesion = peticion.getSession();
        String aviso = (String) sesion.getAttribute(CLAVE_AVISO);
        sesion.removeAttribute(CLAVE_AVISO);
        return aviso;
    }
}
