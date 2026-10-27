package ec.edu.monster.modelos.utilidades;

import jakarta.json.bind.annotation.JsonbPropertyOrder;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@JsonbPropertyOrder({"cuenta", "moneda", "saldo", "movimientos"})
public class RespuestaMovimientos extends Respuesta {

    private String cuenta;
    private String moneda;
    private BigDecimal saldo;
    private List<DatosMovimiento> movimientos = new ArrayList<>();

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public List<DatosMovimiento> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<DatosMovimiento> movimientos) {
        this.movimientos = movimientos;
    }
}
