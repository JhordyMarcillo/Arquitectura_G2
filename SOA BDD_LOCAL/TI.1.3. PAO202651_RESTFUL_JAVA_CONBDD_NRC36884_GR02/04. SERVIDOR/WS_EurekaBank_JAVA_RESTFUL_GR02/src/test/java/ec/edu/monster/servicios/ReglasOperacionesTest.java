package ec.edu.monster.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ec.edu.monster.modelos.utilidades.CuentaInterna;
import ec.edu.monster.modelos.utilidades.ExcepcionNegocio;
import ec.edu.monster.modelos.utilidades.enums.CodigoRespuesta;
import ec.edu.monster.modelos.utilidades.enums.EstadoCuenta;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ReglasOperacionesTest {

    private static CuentaInterna cuenta(String saldo) {
        CuentaInterna cuenta = new CuentaInterna();
        cuenta.setCodigo("00100001");
        cuenta.setSaldo(new BigDecimal(saldo));
        cuenta.setClave("123456");
        cuenta.setCostoMovimiento(new BigDecimal("2.00"));
        cuenta.setEstado(EstadoCuenta.ACTIVO);
        return cuenta;
    }

    @Test
    void importeInvalidoLanzaData001() {
        for (String importe : new String[]{"0", "-5", "10.555"}) {
            ExcepcionNegocio e = assertThrows(ExcepcionNegocio.class,
                    () -> ServicioOperaciones.validarImporte(new BigDecimal(importe)));
            assertEquals(CodigoRespuesta.DATOS_INCONSISTENTES, e.getCodigo());
        }
    }

    @Test
    void saldoDebeCubrirImporteMasCosto() {
        ServicioOperaciones.validarSaldo(cuenta("102.00"), new BigDecimal("100.00"));
        ExcepcionNegocio e = assertThrows(ExcepcionNegocio.class,
                () -> ServicioOperaciones.validarSaldo(cuenta("101.99"), new BigDecimal("100.00")));
        assertEquals(CodigoRespuesta.SALDO_INSUFICIENTE, e.getCodigo());
    }

    @Test
    void claveIncorrectaLanzaCta003() {
        ExcepcionNegocio e = assertThrows(ExcepcionNegocio.class,
                () -> ServicioOperaciones.validarClave(cuenta("10"), "000000"));
        assertEquals(CodigoRespuesta.CLAVE_CUENTA_INCORRECTA, e.getCodigo());
    }
}
