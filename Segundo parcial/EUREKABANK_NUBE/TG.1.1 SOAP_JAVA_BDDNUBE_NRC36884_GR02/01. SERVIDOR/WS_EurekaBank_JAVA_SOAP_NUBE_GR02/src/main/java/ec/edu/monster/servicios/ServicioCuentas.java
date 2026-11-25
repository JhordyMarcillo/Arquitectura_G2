package ec.edu.monster.servicios;

import ec.edu.monster.datos.RepositorioCuenta;
import ec.edu.monster.datos.RepositorioMovimiento;
import ec.edu.monster.modelos.utilidades.CuentaInterna;
import ec.edu.monster.modelos.utilidades.ExcepcionNegocio;
import ec.edu.monster.modelos.utilidades.FabricaRespuesta;
import ec.edu.monster.modelos.utilidades.RespuestaCuentas;
import ec.edu.monster.modelos.utilidades.RespuestaMovimientos;
import ec.edu.monster.modelos.utilidades.enums.CodigoRespuesta;
import java.sql.SQLException;

public class ServicioCuentas {

    private final RepositorioCuenta repositorioCuenta = new RepositorioCuenta();
    private final RepositorioMovimiento repositorioMovimiento = new RepositorioMovimiento();

    public RespuestaCuentas listarCuentas() throws SQLException {
        RespuestaCuentas respuesta = FabricaRespuesta.exito(RespuestaCuentas::new, "Cuentas consultadas correctamente");
        respuesta.setCuentas(repositorioCuenta.listar());
        return respuesta;
    }

    public RespuestaMovimientos consultarMovimientos(String cuenta) throws SQLException {
        if (cuenta == null || cuenta.isBlank()) {
            throw new ExcepcionNegocio(CodigoRespuesta.DATOS_INCONSISTENTES, "La cuenta es obligatoria");
        }
        CuentaInterna datos = repositorioCuenta.buscar(cuenta.trim());
        if (datos == null) {
            throw new ExcepcionNegocio(CodigoRespuesta.CUENTA_NO_EXISTE, "La cuenta " + cuenta + " no existe");
        }

        RespuestaMovimientos respuesta = FabricaRespuesta.exito(RespuestaMovimientos::new, "Movimientos consultados correctamente");
        respuesta.setCuenta(datos.getCodigo());
        respuesta.setMoneda(datos.getMoneda());
        respuesta.setSaldo(datos.getSaldo());
        respuesta.setMovimientos(repositorioMovimiento.listar(datos.getCodigo()));
        return respuesta;
    }
}
