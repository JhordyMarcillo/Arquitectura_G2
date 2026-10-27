package ec.edu.monster.modelos.utilidades;

import ec.edu.monster.modelos.utilidades.enums.CodigoRespuesta;
import ec.edu.monster.modelos.utilidades.enums.EstadoRespuesta;
import java.util.function.Supplier;

public final class FabricaRespuesta {

    private FabricaRespuesta() {
    }

    public static <T extends Respuesta> T exito(Supplier<T> constructor, String mensaje) {
        T respuesta = constructor.get();
        respuesta.setEstado(EstadoRespuesta.OK.name());
        respuesta.setCodigo(CodigoRespuesta.EXITO.getCodigo());
        respuesta.setMensaje(mensaje);
        return respuesta;
    }

    public static Respuesta error(CodigoRespuesta codigo, String mensaje) {
        Respuesta respuesta = new Respuesta();
        respuesta.setEstado(EstadoRespuesta.ERROR.name());
        respuesta.setCodigo(codigo.getCodigo());
        respuesta.setMensaje(mensaje);
        return respuesta;
    }
}
