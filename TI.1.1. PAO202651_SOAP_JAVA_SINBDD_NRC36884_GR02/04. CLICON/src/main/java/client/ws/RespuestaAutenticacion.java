package client.ws;

public class RespuestaAutenticacion {

    private String estado;
    private String codigo;
    private String mensaje;
    private String token;
    private long minutosExpiracion;

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
