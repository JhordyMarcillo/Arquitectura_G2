package ec.edu.monster.controladores.manejadores;

import ec.edu.monster.modelos.utilidades.ExcepcionNegocio;
import ec.edu.monster.modelos.utilidades.FabricaRespuesta;
import ec.edu.monster.modelos.utilidades.enums.CodigoRespuesta;
import jakarta.json.bind.JsonbException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Convierte cualquier error en la respuesta uniforme {estado, codigo, mensaje}. */
@Provider
public class ManejadorErrores implements ExceptionMapper<Throwable> {

    private static final Logger REGISTRO = Logger.getLogger(ManejadorErrores.class.getName());

    @Override
    public Response toResponse(Throwable error) {
        if (error instanceof ExcepcionNegocio negocio) {
            return responder(negocio.getCodigo(), negocio.getMessage());
        }
        if (error instanceof JsonbException || error instanceof ProcessingException
                || error.getCause() instanceof JsonbException) {
            return responder(CodigoRespuesta.DATOS_INCONSISTENTES, "Datos inconsistentes: la solicitud no tiene el formato esperado");
        }
        if (error instanceof WebApplicationException web) {
            if (web.getResponse().getStatus() == 400) {
                return responder(CodigoRespuesta.DATOS_INCONSISTENTES, "Datos inconsistentes: la solicitud no tiene el formato esperado");
            }
            return web.getResponse();
        }
        if (error instanceof SQLException || error.getCause() instanceof SQLException) {
            REGISTRO.log(Level.SEVERE, "Error de base de datos", error);
            return responder(CodigoRespuesta.ERROR_SERVIDOR, "No se pudo acceder a la base de datos");
        }
        REGISTRO.log(Level.SEVERE, "Error no controlado", error);
        return responder(CodigoRespuesta.ERROR_SERVIDOR, "Error interno del servidor");
    }

    private static Response responder(CodigoRespuesta codigo, String mensaje) {
        return Response.status(codigo.getEstadoHttp())
                .type(MediaType.APPLICATION_JSON)
                .entity(FabricaRespuesta.error(codigo, mensaje))
                .build();
    }
}
