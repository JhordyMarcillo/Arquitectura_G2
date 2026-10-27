package ec.edu.monster.modelos.utilidades;

import ec.edu.monster.modelos.utilidades.enums.CodigoRespuesta;

public class ExcepcionValidacion extends RuntimeException {

    private final CodigoRespuesta codigo;

    public ExcepcionValidacion(CodigoRespuesta codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public CodigoRespuesta getCodigo() {
        return codigo;
    }
}
