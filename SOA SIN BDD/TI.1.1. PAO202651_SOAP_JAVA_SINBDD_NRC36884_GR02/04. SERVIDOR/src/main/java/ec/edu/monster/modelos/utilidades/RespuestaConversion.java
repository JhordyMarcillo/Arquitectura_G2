package ec.edu.monster.modelos.utilidades;

import ec.edu.monster.modelos.utilidades.enums.CodigoRespuesta;
import ec.edu.monster.modelos.utilidades.enums.EstadoRespuesta;

public class RespuestaConversion {

    private String estado;
    private String codigo;
    private String mensaje;
    private Double resultado;
    private String unidadOrigen;
    private String unidadDestino;

    public RespuestaConversion() {}

    public static RespuestaConversion exito(double resultado, String unidadOrigen, String unidadDestino) {
        RespuestaConversion respuesta = new RespuestaConversion();
        respuesta.setEstado(EstadoRespuesta.OK.name());
        respuesta.setCodigo(CodigoRespuesta.OK.getCodigo());
        respuesta.setMensaje("Conversion realizada correctamente");
        respuesta.setResultado(resultado);
        respuesta.setUnidadOrigen(unidadOrigen);
        respuesta.setUnidadDestino(unidadDestino);
        return respuesta;
    }

    public static RespuestaConversion error(CodigoRespuesta codigo, String mensaje,
            String unidadOrigen, String unidadDestino) {
        RespuestaConversion respuesta = new RespuestaConversion();
        respuesta.setEstado(EstadoRespuesta.ERROR.name());
        respuesta.setCodigo(codigo.getCodigo());
        respuesta.setMensaje(mensaje);
        respuesta.setResultado(null);
        respuesta.setUnidadOrigen(unidadOrigen);
        respuesta.setUnidadDestino(unidadDestino);
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

    public Double getResultado() {
        return resultado;
    }

    public void setResultado(Double resultado) {
        this.resultado = resultado;
    }

    public String getUnidadOrigen() {
        return unidadOrigen;
    }

    public void setUnidadOrigen(String unidadOrigen) {
        this.unidadOrigen = unidadOrigen;
    }

    public String getUnidadDestino() {
        return unidadDestino;
    }

    public void setUnidadDestino(String unidadDestino) {
        this.unidadDestino = unidadDestino;
    }
}
