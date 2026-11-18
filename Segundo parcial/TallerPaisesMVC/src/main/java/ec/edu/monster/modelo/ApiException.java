package ec.edu.monster.modelo;

public class ApiException extends Exception {

    public ApiException(String mensaje) {
        super(mensaje);
    }

    public ApiException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
