package ec.edu.monster.models;

public class Credito {
    private int codCredito;
    private int codCliente;
    private double monto;
    private int plazoMeses;
    private double tasaInteres;
    private String fechaInicio;
    private int activo;

    public Credito(int codCredito, int codCliente, double monto, int plazoMeses, double tasaInteres, String fechaInicio, int activo) {
        this.codCredito = codCredito;
        this.codCliente = codCliente;
        this.monto = monto;
        this.plazoMeses = plazoMeses;
        this.tasaInteres = tasaInteres;
        this.fechaInicio = fechaInicio;
        this.activo = activo;
    }

    public Credito() {
    }

    // Getters y setters
    public int getCodCredito() {
        return codCredito;
    }

    public void setCodCredito(int codCredito) {
        this.codCredito = codCredito;
    }

    public int getCodCliente() {
        return codCliente;
    }

    public void setCodCliente(int codCliente) {
        this.codCliente = codCliente;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public int getPlazoMeses() {
        return plazoMeses;
    }

    public void setPlazoMeses(int plazoMeses) {
        this.plazoMeses = plazoMeses;
    }

    public double getTasaInteres() {
        return tasaInteres;
    }

    public void setTasaInteres(double tasaInteres) {
        this.tasaInteres = tasaInteres;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public int getActivo() {
        return activo;
    }

    public void setActivo(int activo) {
        this.activo = activo;
    }
}