package ec.edu.monster.modelo;

/**
 * DTO para encapsular la respuesta de una conversión
 * @author ACER NITRO V15
 */
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

    // Métodos estáticos para crear respuestas
    public static ConversionResponse crearExito(double valorOriginal, double valorConvertido, 
                                              String unidadOrigen, String unidadDestino, String categoria) {
        return new ConversionResponse(valorOriginal, valorConvertido, unidadOrigen, unidadDestino, 
                                    categoria, true, "Conversion exitosa");
    }

    public static ConversionResponse crearError(String mensaje) {
        return new ConversionResponse(0, 0, "", "", "", false, mensaje);
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
