package ec.edu.monster.modelo;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Modelo para las respuestas de conversión
 * @author ACER NITRO V15
 */
@XmlRootElement
public class ConversionResponse {
    
    private double valorOriginal;
    private double valorConvertido;
    private String unidadOrigen;
    private String unidadDestino;
    private String categoria;
    private boolean exito;
    private String mensaje;
    private long timestamp;
    
    // Constructores
    public ConversionResponse() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public ConversionResponse(double valorOriginal, double valorConvertido, 
                            String unidadOrigen, String unidadDestino, 
                            String categoria, boolean exito, String mensaje) {
        this.valorOriginal = valorOriginal;
        this.valorConvertido = valorConvertido;
        this.unidadOrigen = unidadOrigen;
        this.unidadDestino = unidadDestino;
        this.categoria = categoria;
        this.exito = exito;
        this.mensaje = mensaje;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Método estático para crear respuesta exitosa
    public static ConversionResponse crearExito(double valorOriginal, double valorConvertido,
                                               String unidadOrigen, String unidadDestino, String categoria) {
        return new ConversionResponse(valorOriginal, valorConvertido, unidadOrigen, 
                                    unidadDestino, categoria, true, "Conversión exitosa");
    }
    
    // Método estático para crear respuesta de error
    public static ConversionResponse crearError(String mensaje) {
        ConversionResponse response = new ConversionResponse();
        response.setExito(false);
        response.setMensaje(mensaje);
        return response;
    }
    
    // Getters y Setters
    public double getValorOriginal() {
        return valorOriginal;
    }
    
    public void setValorOriginal(double valorOriginal) {
        this.valorOriginal = valorOriginal;
    }
    
    public double getValorConvertido() {
        return valorConvertido;
    }
    
    public void setValorConvertido(double valorConvertido) {
        this.valorConvertido = valorConvertido;
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
    
    public boolean isExito() {
        return exito;
    }
    
    public void setExito(boolean exito) {
        this.exito = exito;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "ConversionResponse{" +
                "valorOriginal=" + valorOriginal +
                ", valorConvertido=" + valorConvertido +
                ", unidadOrigen='" + unidadOrigen + '\'' +
                ", unidadDestino='" + unidadDestino + '\'' +
                ", categoria='" + categoria + '\'' +
                ", exito=" + exito +
                ", mensaje='" + mensaje + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}


