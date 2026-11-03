package ec.edu.monster.models;

public class Telefonos {
    private int codTelefono;
    private String nombre;
    private double precio;
    private String marca;
    private int disponible;

    public Telefonos() {
    }

    public Telefonos(int codTelefono, String nombre, double precio, String marca, int disponible) {
        this.codTelefono = codTelefono;
        this.nombre = nombre;
        this.precio = precio;
        this.marca = marca;
        this.disponible = disponible;
    }

    public Telefonos(String nombre, double precio, String marca, int disponible) {
        this.nombre = nombre;
        this.precio = precio;
        this.marca = marca;
        this.disponible = disponible;
    }
    
    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public int getDisponible() {
        return disponible;
    }

    public void setDisponible(int disponible) {
        this.disponible = disponible;
    }

    public int getCodTelefono() {
        return codTelefono;
    }

    public void setCodTelefono(int codTelefono) {
        this.codTelefono = codTelefono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return String.format("CodTelefono: %d, Nombre: %s, Precio: %.2f, Marca: %s, Disponible: %d",
                codTelefono, nombre, precio, marca, disponible);
    }
}