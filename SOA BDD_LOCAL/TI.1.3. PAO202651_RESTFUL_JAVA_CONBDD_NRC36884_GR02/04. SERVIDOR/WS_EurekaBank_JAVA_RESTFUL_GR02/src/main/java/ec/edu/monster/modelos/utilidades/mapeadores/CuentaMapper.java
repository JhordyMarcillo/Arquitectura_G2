package ec.edu.monster.modelos.utilidades.mapeadores;

import ec.edu.monster.modelos.utilidades.CuentaInterna;
import ec.edu.monster.modelos.utilidades.DatosCuenta;
import ec.edu.monster.modelos.utilidades.enums.EstadoCuenta;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class CuentaMapper {

    private CuentaMapper() {
    }

    public static DatosCuenta aDatosCuenta(ResultSet fila) throws SQLException {
        DatosCuenta cuenta = new DatosCuenta();
        cuenta.setCodigo(fila.getString(1));
        cuenta.setCliente(fila.getString(2));
        cuenta.setMoneda(fila.getString(3));
        cuenta.setSaldo(fila.getBigDecimal(4));
        cuenta.setEstado(fila.getString(5));
        return cuenta;
    }

    public static CuentaInterna aCuentaInterna(ResultSet fila) throws SQLException {
        CuentaInterna cuenta = new CuentaInterna();
        cuenta.setCodigo(fila.getString(1));
        cuenta.setCodigoMoneda(fila.getString(2));
        cuenta.setMoneda(fila.getString(3));
        cuenta.setSaldo(fila.getBigDecimal(4));
        cuenta.setEstado(EstadoCuenta.valueOf(fila.getString(5)));
        cuenta.setContadorMovimientos(fila.getInt(6));
        cuenta.setClave(fila.getString(7));
        cuenta.setCostoMovimiento(fila.getBigDecimal(8));
        return cuenta;
    }
}
