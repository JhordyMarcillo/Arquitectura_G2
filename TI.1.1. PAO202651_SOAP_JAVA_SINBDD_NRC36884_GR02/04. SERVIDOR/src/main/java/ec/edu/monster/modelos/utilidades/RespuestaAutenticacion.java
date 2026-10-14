package ec.edu.monster.modelos.utilidades;

import ec.edu.monster.modelos.utilidades.enums.CodigoRespuesta;
import ec.edu.monster.modelos.utilidades.enums.EstadoRespuesta;

public class RespuestaAutenticacion {

    private String estado;
    private String codigo;
    private String mensaje;
    private String token;
    private long minutosExpiracion;

    public RespuestaAutenticacion() {}

    public static RespuestaAutenticacion exito(String token, long minutosExpiracion) {
        RespuestaAutenticacion respuesta = new RespuestaAutenticacion();
        respuesta.setEstado(EstadoRespuesta.OK.name());
        respuesta.setCodigo(CodigoRespuesta.OK.getCodigo());
        respuesta.setMensaje("Autenticacion exitosa");
        respuesta.setToken(token);
        respuesta.setMinutosExpiracion(minutosExpiracion);
        return respuesta;
    }

    public static RespuestaAutenticacion error(CodigoRespuesta codigo, String mensaje) {
        RespuestaAutenticacion respuesta = new RespuestaAutenticacion();
        respuesta.setEstado(EstadoRespuesta.ERROR.name());
        respuesta.setCodigo(codigo.getCodigo());
        respuesta.setMensaje(mensaje);
        respuesta.setToken(null);
        respuesta.setMinutosExpiracion(0);
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getMinutosExpiracion() {
        return minutosExpiracion;
    }

    public void setMinutosExpiracion(long minutosExpiracion) {
        this.minutosExpiracion = minutosExpiracion;
    }
}
