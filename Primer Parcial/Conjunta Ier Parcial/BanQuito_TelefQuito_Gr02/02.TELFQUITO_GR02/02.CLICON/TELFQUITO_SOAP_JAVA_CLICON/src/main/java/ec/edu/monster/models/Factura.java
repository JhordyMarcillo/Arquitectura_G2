package ec.edu.monster.models;

public class Factura {
    private int codCompra;
    private String formaPago;
    private String fecha;
    private Double descuento;
    private Double precioFinal;
    private String nombreCliente;
    private String nombreTelefono;
    private String marcaTelefono;

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

    public Double getDescuento() {
        return descuento;
    }

    public void setDescuento(Double descuento) {
        this.descuento = descuento;
    }

    public Double getPreciofinal() {
        return precioFinal;
    }

    public void setPreciofinal(Double precioFinal) {
        this.precioFinal = precioFinal;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNombreTelefono() {
        return nombreTelefono;
    }

    public void setNombreTelefono(String nombreTelefono) {
        this.nombreTelefono = nombreTelefono;
    }

    public String getMarcaTelefono() {
        return marcaTelefono;
    }

    public void setMarcaTelefono(String marcaTelefono) {
        this.marcaTelefono = marcaTelefono;
    }
     @Override
    public String toString() {
        return String.format("CodCompra: %d, FormaPago: %s, Fecha: %s, Descuento: %.2f, PrecioFinal: %.2f, NombreCliente: %s, NombreTelefono: %s, MarcaTelefono: %s",
                codCompra, formaPago, fecha, descuento, precioFinal, nombreCliente, nombreTelefono, marcaTelefono);
    }
}