package ec.edu.monster.modelos.utilidades;

import ec.edu.monster.modelos.utilidades.enums.CodigoRespuesta;
import ec.edu.monster.modelos.utilidades.enums.EstadoRespuesta;
import java.util.ArrayList;
import java.util.List;

public class RespuestaCatalogo {

    private String estado;
    private String codigo;
    private String mensaje;
    private List<String> categorias = new ArrayList<>();

    public RespuestaCatalogo() {}

    public static RespuestaCatalogo exito(List<String> categorias) {
        RespuestaCatalogo respuesta = new RespuestaCatalogo();
        respuesta.setEstado(EstadoRespuesta.OK.name());
        respuesta.setCodigo(CodigoRespuesta.OK.getCodigo());
        respuesta.setMensaje("Catalogo consultado correctamente");
        respuesta.setCategorias(new ArrayList<>(categorias));
        return respuesta;
    }

    public static RespuestaCatalogo error(CodigoRespuesta codigo, String mensaje) {
        RespuestaCatalogo respuesta = new RespuestaCatalogo();
        respuesta.setEstado(EstadoRespuesta.ERROR.name());
        respuesta.setCodigo(codigo.getCodigo());
        respuesta.setMensaje(mensaje);
        return respuesta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public List<String> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<String> categorias) {
        this.categorias = categorias;
    }
}
