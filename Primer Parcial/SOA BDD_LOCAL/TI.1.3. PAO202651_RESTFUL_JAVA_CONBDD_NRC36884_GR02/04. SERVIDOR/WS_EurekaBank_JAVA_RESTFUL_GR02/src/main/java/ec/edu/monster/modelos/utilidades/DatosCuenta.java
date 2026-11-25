package ec.edu.monster.modelos.utilidades;

import jakarta.json.bind.annotation.JsonbPropertyOrder;
import java.math.BigDecimal;

@JsonbPropertyOrder({"codigo", "cliente", "moneda", "saldo", "estado"})
public class DatosCuenta {

    private String codigo;
    private String cliente;
    private String moneda;
    private BigDecimal saldo;
    private String estado;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
