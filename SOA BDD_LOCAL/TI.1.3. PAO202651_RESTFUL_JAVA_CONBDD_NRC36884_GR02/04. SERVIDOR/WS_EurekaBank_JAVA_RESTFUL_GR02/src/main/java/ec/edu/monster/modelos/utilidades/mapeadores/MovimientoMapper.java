package ec.edu.monster.modelos.utilidades.mapeadores;

import ec.edu.monster.modelos.utilidades.DatosMovimiento;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class MovimientoMapper {

    private MovimientoMapper() {
    }

    public static DatosMovimiento aDatosMovimiento(ResultSet fila) throws SQLException {
        DatosMovimiento movimiento = new DatosMovimiento();
        movimiento.setNumero(fila.getInt(1));
        movimiento.setFecha(fila.getDate(2).toLocalDate().toString());
        movimiento.setTipo(fila.getString(3));
        movimiento.setDescripcion(fila.getString(4));
        movimiento.setAccion(fila.getString(5));
        movimiento.setImporte(fila.getBigDecimal(6));
        movimiento.setCuentaReferencia(fila.getString(7));
        return movimiento;
    }
}
