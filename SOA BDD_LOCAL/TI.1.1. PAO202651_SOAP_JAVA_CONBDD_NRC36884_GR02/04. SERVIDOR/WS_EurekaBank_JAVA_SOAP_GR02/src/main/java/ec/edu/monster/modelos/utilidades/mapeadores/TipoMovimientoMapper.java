package ec.edu.monster.modelos.utilidades.mapeadores;

import ec.edu.monster.modelos.utilidades.enums.TipoMovimiento;

public final class TipoMovimientoMapper {

    private TipoMovimientoMapper() {
    }

    public static String aCodigo(TipoMovimiento tipo) {
        return switch (tipo) {
            case DEPOSITO -> "003";
            case RETIRO -> "004";
            case TRANSFERENCIA_INGRESO -> "008";
            case TRANSFERENCIA_SALIDA -> "009";
            case COSTO_MOVIMIENTO -> "010";
        };
    }
}
