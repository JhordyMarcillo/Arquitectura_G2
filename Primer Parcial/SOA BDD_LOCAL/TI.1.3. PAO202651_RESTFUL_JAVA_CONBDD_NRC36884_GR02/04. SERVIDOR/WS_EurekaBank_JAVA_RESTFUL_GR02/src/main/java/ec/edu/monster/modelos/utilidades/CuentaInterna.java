package ec.edu.monster.modelos.utilidades;

import jakarta.json.bind.annotation.JsonbPropertyOrder;
import ec.edu.monster.modelos.utilidades.enums.EstadoCuenta;
import java.math.BigDecimal;

/** Cuenta leida para operar (incluye clave y costo); nunca se envia al cliente. */
@JsonbPropertyOrder({"codigo", "codigoMoneda", "moneda", "saldo", "estado", "contadorMovimientos", "clave", "costoMovimiento"})
public class CuentaInterna {

    private String codigo;
    private String codigoMoneda;
    private String moneda;
    private BigDecimal saldo;
    private EstadoCuenta estado;
    private int contadorMovimientos;
    private String clave;
    private BigDecimal costoMovimiento;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoMoneda() {
        return codigoMoneda;
    }

    public void setCodigoMoneda(String codigoMoneda) {
        this.codigoMoneda = codigoMoneda;
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

    public EstadoCuenta getEstado() {
        return estado;
    }

    public void setEstado(EstadoCuenta estado) {
        this.estado = estado;
    }

    public int getContadorMovimientos() {
        return contadorMovimientos;
    }

    public void setContadorMovimientos(int contadorMovimientos) {
        this.contadorMovimientos = contadorMovimientos;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public BigDecimal getCostoMovimiento() {
        return costoMovimiento;
    }

    public void setCostoMovimiento(BigDecimal costoMovimiento) {
        this.costoMovimiento = costoMovimiento;
    }
}
