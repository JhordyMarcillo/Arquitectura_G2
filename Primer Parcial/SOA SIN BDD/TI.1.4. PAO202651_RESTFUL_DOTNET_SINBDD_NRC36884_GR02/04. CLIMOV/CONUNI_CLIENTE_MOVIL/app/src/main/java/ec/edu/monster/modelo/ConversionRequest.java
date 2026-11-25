package ec.edu.monster.modelo;

public class ConversionRequest {
    private String categoria;
    private String unidadOrigen;
    private String unidadDestino;
    private double valor;

    public ConversionRequest() {
    }

    public ConversionRequest(String categoria, String unidadOrigen, String unidadDestino, double valor) {
        this.categoria = categoria;
        this.unidadOrigen = unidadOrigen;
        this.unidadDestino = unidadDestino;
        this.valor = valor;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getUnidadOrigen() {
        return unidadOrigen;
    }

    public void setUnidadOrigen(String unidadOrigen) {
        this.unidadOrigen = unidadOrigen;
    }

    public String getUnidadDestino() {
        return unidadDestino;
    }

    public void setUnidadDestino(String unidadDestino) {
        this.unidadDestino = unidadDestino;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
