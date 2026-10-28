package ec.edu.monster.modelo;

/**
 * Modelo para representar una conversión en el cliente web
 * @author ACER NITRO V15
 */
public class Conversion {
    
    private double valorOriginal;
    private String unidadOriginal;
    private double valorConvertido;
    private String unidadConvertida;
    private String operacion;
    private boolean exitosa;
    private String mensajeError;
    private String tipoConversion;
    
    // Constructores
    public Conversion() {
        this.exitosa = false;
        this.mensajeError = "";
    }
    
    public Conversion(double valorOriginal, String unidadOriginal, String operacion, String tipoConversion) {
        this.valorOriginal = valorOriginal;
        this.unidadOriginal = unidadOriginal;
        this.operacion = operacion;
        this.tipoConversion = tipoConversion;
        this.exitosa = false;
        this.mensajeError = "";
    }
    
    // Getters y Setters
    public double getValorOriginal() {
        return valorOriginal;
    }
    
    public void setValorOriginal(double valorOriginal) {
        this.valorOriginal = valorOriginal;
    }
    
    public String getUnidadOriginal() {
        return unidadOriginal;
    }
    
    public void setUnidadOriginal(String unidadOriginal) {
        this.unidadOriginal = unidadOriginal;
    }
    
    public double getValorConvertido() {
        return valorConvertido;
    }
    
    public void setValorConvertido(double valorConvertido) {
        this.valorConvertido = valorConvertido;
    }
    
    public String getUnidadConvertida() {
        return unidadConvertida;
    }
    
    public void setUnidadConvertida(String unidadConvertida) {
        this.unidadConvertida = unidadConvertida;
    }
    
    public String getOperacion() {
        return operacion;
    }
    
    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }
    
    public boolean isExitosa() {
        return exitosa;
    }
    
    public void setExitosa(boolean exitosa) {
        this.exitosa = exitosa;
    }
    
    public String getMensajeError() {
        return mensajeError;
    }
    
    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }
    
    public String getTipoConversion() {
        return tipoConversion;
    }
    
    public void setTipoConversion(String tipoConversion) {
        this.tipoConversion = tipoConversion;
    }
    
    /**
     * Obtiene el resultado formateado para mostrar
     * @return String con el resultado formateado
     */
    public String getResultadoFormateado() {
        if (exitosa) {
            return String.format("%.2f %s = %.2f %s", 
                valorOriginal, unidadOriginal, valorConvertido, unidadConvertida);
        } else {
            return "ERROR: " + mensajeError;
        }
    }
    
    /**
     * Convierte el objeto a JSON
     * @return String con el JSON
     */
    public String toJSON() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"exitosa\":").append(exitosa).append(",");
        json.append("\"exitoso\":").append(exitosa).append(","); // Compatibilidad con login
        json.append("\"valorOriginal\":").append(valorOriginal).append(",");
        json.append("\"unidadOriginal\":\"").append(escaparJSON(unidadOriginal != null ? unidadOriginal : "")).append("\",");
        json.append("\"valorConvertido\":").append(valorConvertido).append(",");
        json.append("\"unidadConvertida\":\"").append(escaparJSON(unidadConvertida != null ? unidadConvertida : "")).append("\",");
        json.append("\"operacion\":\"").append(escaparJSON(operacion != null ? operacion : "")).append("\",");
        json.append("\"tipoConversion\":\"").append(escaparJSON(tipoConversion != null ? tipoConversion : "")).append("\",");
        json.append("\"mensajeError\":\"").append(escaparJSON(mensajeError != null ? mensajeError : "")).append("\",");
        json.append("\"mensaje\":\"").append(escaparJSON(mensajeError != null ? mensajeError : "")).append("\","); // Compatibilidad con login
        json.append("\"resultadoFormateado\":\"").append(escaparJSON(getResultadoFormateado())).append("\"");
        json.append("}");
        return json.toString();
    }
    
    /**
     * Escapa caracteres especiales para JSON
     * @param texto - Texto a escapar
     * @return String con caracteres escapados
     */
    private String escaparJSON(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    @Override
    public String toString() {
        return getResultadoFormateado();
    }
}


