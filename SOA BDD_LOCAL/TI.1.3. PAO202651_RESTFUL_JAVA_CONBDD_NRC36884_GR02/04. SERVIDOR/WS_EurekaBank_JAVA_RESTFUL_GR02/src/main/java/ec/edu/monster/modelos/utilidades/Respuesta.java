package ec.edu.monster.modelos.utilidades;

import jakarta.json.bind.annotation.JsonbPropertyOrder;

@JsonbPropertyOrder({"estado", "codigo", "mensaje"})
public class Respuesta {

    private String estado;
    private String codigo;
    private String mensaje;

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
}
