package ec.edu.monster.controladores.api;

import ec.edu.monster.controladores.manejadores.ManejadorAutenticacion;
import ec.edu.monster.controladores.manejadores.PermitirAnonimo;
import ec.edu.monster.modelos.utilidades.IServicioOperaciones;
import ec.edu.monster.modelos.utilidades.RespuestaAutenticacion;
import ec.edu.monster.modelos.utilidades.RespuestaCuentas;
import ec.edu.monster.modelos.utilidades.RespuestaMovimientos;
import ec.edu.monster.modelos.utilidades.RespuestaOperacion;
import ec.edu.monster.modelos.utilidades.SolicitudDeposito;
import ec.edu.monster.modelos.utilidades.SolicitudLogin;
import ec.edu.monster.modelos.utilidades.SolicitudRetiro;
import ec.edu.monster.modelos.utilidades.SolicitudTransferencia;
import ec.edu.monster.seguridad.SesionEmpleado;
import ec.edu.monster.servicios.ServicioAutenticacion;
import ec.edu.monster.servicios.ServicioCuentas;
import ec.edu.monster.servicios.ServicioOperaciones;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import java.sql.SQLException;

@Path("eurekabank")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ControladorEurekaBank {

    private final ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion();
    private final ServicioCuentas servicioCuentas = new ServicioCuentas();
    private final IServicioOperaciones servicioOperaciones = new ServicioOperaciones();

    @Context
    private ContainerRequestContext peticion;

    @POST
    @Path("login")
    @PermitirAnonimo
    public RespuestaAutenticacion login(SolicitudLogin solicitud) throws SQLException {
        return servicioAutenticacion.autenticar(solicitud);
    }

    @GET
    @Path("cuentas")
    public RespuestaCuentas listarCuentas() throws SQLException {
        return servicioCuentas.listarCuentas();
    }

    @GET
    @Path("cuentas/{cuenta}/movimientos")
    public RespuestaMovimientos consultarMovimientos(@PathParam("cuenta") String cuenta) throws SQLException {
        return servicioCuentas.consultarMovimientos(cuenta);
    }

    @POST
    @Path("depositos")
    public RespuestaOperacion depositar(SolicitudDeposito solicitud) {
        return servicioOperaciones.depositar(solicitud, sesion().codigoEmpleado());
    }

    @POST
    @Path("retiros")
    public RespuestaOperacion retirar(SolicitudRetiro solicitud) {
        return servicioOperaciones.retirar(solicitud, sesion().codigoEmpleado());
    }

    @POST
    @Path("transferencias")
    public RespuestaOperacion transferir(SolicitudTransferencia solicitud) {
        return servicioOperaciones.transferir(solicitud, sesion().codigoEmpleado());
    }

    private SesionEmpleado sesion() {
        return (SesionEmpleado) peticion.getProperty(ManejadorAutenticacion.CLAVE_SESION);
    }
}
