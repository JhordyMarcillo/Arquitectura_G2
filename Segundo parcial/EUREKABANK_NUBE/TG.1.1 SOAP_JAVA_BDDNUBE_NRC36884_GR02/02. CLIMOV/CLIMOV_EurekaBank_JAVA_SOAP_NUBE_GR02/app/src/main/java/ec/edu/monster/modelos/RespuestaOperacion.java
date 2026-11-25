package ec.edu.monster.modelos;

import java.math.BigDecimal;

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
