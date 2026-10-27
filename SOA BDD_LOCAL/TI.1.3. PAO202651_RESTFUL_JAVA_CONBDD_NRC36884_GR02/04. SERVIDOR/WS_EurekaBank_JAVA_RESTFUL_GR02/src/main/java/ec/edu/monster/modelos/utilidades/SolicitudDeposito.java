package ec.edu.monster.modelos.utilidades;

import jakarta.json.bind.annotation.JsonbPropertyOrder;
import java.math.BigDecimal;

@JsonbPropertyOrder({"cuenta", "importe"})
public class SolicitudDeposito {

    private String cuenta;
    private BigDecimal importe;

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }
}
