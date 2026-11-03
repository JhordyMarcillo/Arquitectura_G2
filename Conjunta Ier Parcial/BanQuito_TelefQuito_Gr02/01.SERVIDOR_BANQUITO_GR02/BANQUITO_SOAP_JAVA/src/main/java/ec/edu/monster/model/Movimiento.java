
package ec.edu.monster.model;

public class Movimiento {
    private int codMovimiento;
    private String numCuenta;
    private String tipo;
    private double valor;
    private java.sql.Date fecha;

    // Getters y setters
    public int getCodMovimiento() { return codMovimiento; }
    public void setCodMovimiento(int codMovimiento) { this.codMovimiento = codMovimiento; }

    public String getNumCuenta() { return numCuenta; }
    public void setNumCuenta(String numCuenta) { this.numCuenta = numCuenta; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public java.sql.Date getFecha() { return fecha; }
    public void setFecha(java.sql.Date fecha) { this.fecha = fecha; }
}
