package ec.edu.monster.modelo;

public class ExcepcionApi extends Exception {

    private final String codigo;

    public ExcepcionApi(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    // CU-08 token invalido o CU-09 sesion expirada: hay que volver a autenticarse
    public boolean esErrorDeSesion() {
        return "AUTH-001".equals(codigo) || "AUTH-002".equals(codigo);
    }

    // El servidor no respondio (apagado, IP incorrecta, timeout)
    public boolean esErrorDeRed() {
        return "RED".equals(codigo);
    }
}
