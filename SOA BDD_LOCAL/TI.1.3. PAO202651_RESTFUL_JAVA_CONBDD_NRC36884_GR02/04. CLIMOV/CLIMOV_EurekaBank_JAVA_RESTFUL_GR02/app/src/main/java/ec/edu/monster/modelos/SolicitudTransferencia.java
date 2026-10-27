package ec.edu.monster.modelos;

import java.math.BigDecimal;

public class SolicitudTransferencia {

    private String cuentaOrigen;
    private String claveOrigen;
    private String cuentaDestino;
    private BigDecimal importe;

    public String getCuentaOrigen() {
        return cuentaOrigen;
    }

    public void setCuentaOrigen(String cuentaOrigen) {
        this.cuentaOrigen = cuentaOrigen;
    }

    public String getClaveOrigen() {
        return claveOrigen;
    }

    public void setClaveOrigen(String claveOrigen) {
        this.claveOrigen = claveOrigen;
    }

    public String getCuentaDestino() {
        return cuentaDestino;
    }

    public void setCuentaDestino(String cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }
}
