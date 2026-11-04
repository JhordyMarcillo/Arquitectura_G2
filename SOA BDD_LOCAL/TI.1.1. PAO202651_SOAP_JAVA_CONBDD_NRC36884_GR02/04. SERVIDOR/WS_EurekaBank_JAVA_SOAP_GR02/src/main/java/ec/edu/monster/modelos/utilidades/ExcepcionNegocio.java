package ec.edu.monster.modelos.utilidades;

import ec.edu.monster.modelos.utilidades.enums.CodigoRespuesta;

public class ExcepcionNegocio extends RuntimeException {

    private final CodigoRespuesta codigo;

    public ExcepcionNegocio(CodigoRespuesta codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public CodigoRespuesta getCodigo() {
        return codigo;
    }
}
