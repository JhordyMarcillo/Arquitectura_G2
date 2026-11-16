package ec.edu.monster.models;

public class ClienteComercializadora {
    private int codcCliente;
    private String cedula;
    private String nombre;
    private String genero;
    private String fechaNacimiento;

    public ClienteComercializadora(int codcCliente, String cedula, String nombre, String genero, String fechaNacimiento) {
        this.codcCliente = codcCliente;
        this.cedula = cedula;
        this.nombre = nombre;
        this.genero = genero;
        this.fechaNacimiento = fechaNacimiento;
    }
    
        public ClienteComercializadora() {
    }

    public int getCodcCliente() {
        return codcCliente;
    }

    public void setCodcCliente(int codcCliente) {
        this.codcCliente = codcCliente;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
}