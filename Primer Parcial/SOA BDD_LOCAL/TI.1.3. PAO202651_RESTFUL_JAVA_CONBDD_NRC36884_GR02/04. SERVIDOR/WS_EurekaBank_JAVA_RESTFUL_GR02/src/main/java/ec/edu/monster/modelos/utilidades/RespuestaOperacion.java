package ec.edu.monster.modelos.utilidades;

import jakarta.json.bind.annotation.JsonbPropertyOrder;
import java.math.BigDecimal;

@JsonbPropertyOrder({"cuenta", "saldo"})
public class RespuestaOperacion extends Respuesta {

    private String cuenta;
    private BigDecimal saldo;

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}
