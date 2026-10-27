package ec.edu.monster.modelos.utilidades;

import jakarta.json.bind.annotation.JsonbPropertyOrder;

@JsonbPropertyOrder({"usuario", "clave"})
public class SolicitudLogin {

    private String usuario;
    private String clave;

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
}
