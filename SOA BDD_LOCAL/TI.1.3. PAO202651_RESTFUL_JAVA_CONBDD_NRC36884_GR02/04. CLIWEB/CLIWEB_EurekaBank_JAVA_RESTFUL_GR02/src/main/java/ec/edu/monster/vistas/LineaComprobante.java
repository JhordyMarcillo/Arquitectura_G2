package ec.edu.monster.vistas;

/** Linea (etiqueta y valor) del comprobante de una operacion. */
public class LineaComprobante {

    private String etiqueta;
    private String valor;
    private boolean destacada;

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public boolean isDestacada() {
        return destacada;
    }

    public void setDestacada(boolean destacada) {
        this.destacada = destacada;
    }
}
