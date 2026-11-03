package ec.edu.monster.models;

public class Compras {
     private int codCompra;
    private String formaPago;
    private String fecha;
    private int codTelefono;
    private int codcCliente;
    private Double descuento;
    private double preciofinal;

    public Compras() {
    }

    public Compras(int codCompra, String formaPago, String fecha, int codTelefono, int codcCliente, double preciofinal) {
        this.codCompra = codCompra;
        this.formaPago = formaPago;
        this.fecha = fecha;
        this.codTelefono = codTelefono;
        this.codcCliente = codcCliente;
        this.preciofinal = preciofinal;
    }

    public Compras(int codCompra, String formaPago, String fecha, int codTelefono, int codcCliente, Double descuento, double preciofinal) {
        this.codCompra = codCompra;
        this.formaPago = formaPago;
        this.fecha = fecha;
        this.codTelefono = codTelefono;
        this.codcCliente = codcCliente;
        this.descuento = descuento;
        this.preciofinal = preciofinal;
    }

    public double getPreciofinal() {
        return preciofinal;
    }

    public void setPreciofinal(double preciofinal) {
        this.preciofinal = preciofinal;
    }

   

    public int getCodCompra() {
        return codCompra;
    }

    public void setCodCompra(int codCompra) {
        this.codCompra = codCompra;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getCodTelefono() {
        return codTelefono;
    }

    public void setCodTelefono(int codTelefono) {
        this.codTelefono = codTelefono;
    }

    public int getCodcCliente() {
        return codcCliente;
    }

    public void setCodcCliente(int codcCliente) {
        this.codcCliente = codcCliente;
    }

    public Double getDescuento() {
        return descuento;
    }

    public void setDescuento(Double descuento) {
        this.descuento = descuento;
    }   
}