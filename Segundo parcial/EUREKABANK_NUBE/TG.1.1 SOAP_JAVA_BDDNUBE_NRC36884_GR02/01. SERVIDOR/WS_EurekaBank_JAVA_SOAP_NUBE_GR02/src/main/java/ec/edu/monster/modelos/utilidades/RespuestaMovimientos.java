package ec.edu.monster.modelos.utilidades;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@XmlType(name = "RespuestaMovimientos", propOrder = {"cuenta", "moneda", "saldo", "movimientos"})
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

    /** Lista envuelta como en WCF: <movimientos><DatosMovimiento>...</DatosMovimiento></movimientos>. */
    @XmlElementWrapper(name = "movimientos")
    @XmlElement(name = "DatosMovimiento")
    public List<DatosMovimiento> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<DatosMovimiento> movimientos) {
        this.movimientos = movimientos;
    }
}
