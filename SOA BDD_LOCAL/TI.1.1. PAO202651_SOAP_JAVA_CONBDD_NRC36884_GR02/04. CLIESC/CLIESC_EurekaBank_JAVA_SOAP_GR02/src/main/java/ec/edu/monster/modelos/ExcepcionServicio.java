package ec.edu.monster.modelos;

/** Error devuelto por el servicio (estado ERROR) o de comunicacion con el servidor. */
public class ExcepcionServicio extends Exception {

    public static final String SIN_CONEXION = "RED-001";

    private final String codigo;

    public ExcepcionServicio(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public ExcepcionServicio(String codigo, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    /** La sesion ya no es valida y hay que volver a iniciar sesion. */
    public boolean requiereLogin() {
        return "AUTH-001".equals(codigo) || "AUTH-002".equals(codigo);
    }
}
