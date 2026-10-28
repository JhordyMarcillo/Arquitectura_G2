package ec.edu.monster.modelo;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Modelo para las peticiones de conversión
 * @author ACER NITRO V15
 */
@XmlRootElement
public class ConversionRequest {
    
    private double valor;
    private String unidadOrigen;
    private String unidadDestino;
    private String categoria;
    
    // Constructores
    public ConversionRequest() {
    }
    
    public ConversionRequest(double valor, String unidadOrigen, String unidadDestino, String categoria) {
        this.valor = valor;
        this.unidadOrigen = unidadOrigen;
        this.unidadDestino = unidadDestino;
        this.categoria = categoria;
    }
    
    // Getters y Setters
    public double getValor() {
        return valor;
    }
    
    public void setValor(double valor) {
        this.valor = valor;
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
    
    public String getCategoria() {
        return categoria;
    }
    
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
    @Override
    public String toString() {
        return "ConversionRequest{" +
                "valor=" + valor +
                ", unidadOrigen='" + unidadOrigen + '\'' +
                ", unidadDestino='" + unidadDestino + '\'' +
                ", categoria='" + categoria + '\'' +
                '}';
    }
}


