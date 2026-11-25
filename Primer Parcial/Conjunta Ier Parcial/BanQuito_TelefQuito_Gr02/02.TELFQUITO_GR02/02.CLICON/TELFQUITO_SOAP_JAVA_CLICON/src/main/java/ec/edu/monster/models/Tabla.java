package ec.edu.monster.models;

public class Tabla {
    private int codCredito;
    private int cuota;
    private double valorCuota;
    private double interesPagado;
    private double capitalPagado;
    private double saldo;

    public Tabla(int codCredito, int cuota, double valorCuota, double interesPagado, double capitalPagado, double saldo) {
        this.codCredito = codCredito;
        this.cuota = cuota;
        this.valorCuota = valorCuota;
        this.interesPagado = interesPagado;
        this.capitalPagado = capitalPagado;
        this.saldo = saldo;
    }

    public Tabla() {
    }

    public int getCodCredito() {
        return codCredito;
    }

    public void setCodCredito(int codCredito) {
        this.codCredito = codCredito;
    }
    
    public int getCuota() {
        return cuota;
    }

    public void setCuota(int cuota) {
        this.cuota = cuota;
    }

    public double getValorCuota() {
        return valorCuota;
    }

    public void setValorCuota(double valorCuota) {
        this.valorCuota = valorCuota;
    }

    public double getInteresPagado() {
        return interesPagado;
    }

    public void setInteresPagado(double interesPagado) {
        this.interesPagado = interesPagado;
    }

    public double getCapitalPagado() {
        return capitalPagado;
    }

    public void setCapitalPagado(double capitalPagado) {
        this.capitalPagado = capitalPagado;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
     @Override
    public String toString() {
        return String.format("CodCredito: %d, Cuota: %d, ValorCuota: %.2f, InteresPagado: %.2f, CapitalPagado: %.2f, Saldo: %.2f",
                codCredito, cuota, valorCuota, interesPagado, capitalPagado, saldo);
    }
}