package ec.edu.monster.modelos.utilidades;

import ec.edu.monster.modelos.utilidades.enums.CodigoRespuesta;
import ec.edu.monster.modelos.utilidades.enums.EstadoRespuesta;
import java.util.ArrayList;
import java.util.List;

public class RespuestaUnidades {

    private String estado;
    private String codigo;
    private String mensaje;
    private String categoria;
    private List<String> unidades = new ArrayList<>();

    public RespuestaUnidades() {}

    public static RespuestaUnidades exito(String categoria, List<String> unidades) {
        RespuestaUnidades respuesta = new RespuestaUnidades();
        respuesta.setEstado(EstadoRespuesta.OK.name());
        respuesta.setCodigo(CodigoRespuesta.OK.getCodigo());
        respuesta.setMensaje("Unidades consultadas correctamente");
        respuesta.setCategoria(categoria);
        respuesta.setUnidades(new ArrayList<>(unidades));
        return respuesta;
    }

    public static RespuestaUnidades error(CodigoRespuesta codigo, String mensaje, String categoria) {
        RespuestaUnidades respuesta = new RespuestaUnidades();
        respuesta.setEstado(EstadoRespuesta.ERROR.name());
        respuesta.setCodigo(codigo.getCodigo());
        respuesta.setMensaje(mensaje);
        respuesta.setCategoria(categoria);
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public List<String> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<String> unidades) {
        this.unidades = unidades;
    }
}
