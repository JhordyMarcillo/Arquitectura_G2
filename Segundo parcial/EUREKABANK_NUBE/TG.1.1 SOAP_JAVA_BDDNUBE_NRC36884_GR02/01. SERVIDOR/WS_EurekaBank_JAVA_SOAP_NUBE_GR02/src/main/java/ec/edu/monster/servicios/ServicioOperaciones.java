package ec.edu.monster.servicios;

import ec.edu.monster.datos.AccesoDB;
import ec.edu.monster.datos.RepositorioCuenta;
import ec.edu.monster.datos.RepositorioMovimiento;
import ec.edu.monster.modelos.utilidades.CuentaInterna;
import ec.edu.monster.modelos.utilidades.ExcepcionNegocio;
import ec.edu.monster.modelos.utilidades.FabricaRespuesta;
import ec.edu.monster.modelos.utilidades.IServicioOperaciones;
import ec.edu.monster.modelos.utilidades.RespuestaOperacion;
import ec.edu.monster.modelos.utilidades.SolicitudDeposito;
import ec.edu.monster.modelos.utilidades.SolicitudRetiro;
import ec.edu.monster.modelos.utilidades.SolicitudTransferencia;
import ec.edu.monster.modelos.utilidades.enums.CodigoRespuesta;
import ec.edu.monster.modelos.utilidades.enums.EstadoCuenta;
import ec.edu.monster.modelos.utilidades.enums.TipoMovimiento;
import ec.edu.monster.modelos.utilidades.mapeadores.TipoMovimientoMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Deposito, retiro y transferencia con las mismas reglas que usp_deposito,
 * usp_egcc_retiro y usp_egcc_transferencia del docente. Cada operacion es una transaccion.
 */
public class ServicioOperaciones implements IServicioOperaciones {

    private final RepositorioCuenta repositorioCuenta = new RepositorioCuenta();
    private final RepositorioMovimiento repositorioMovimiento = new RepositorioMovimiento();

    @FunctionalInterface
    private interface Operacion {
        RespuestaOperacion ejecutar(Connection conexion) throws SQLException;
    }

    @Override
    public RespuestaOperacion depositar(SolicitudDeposito solicitud, String codigoEmpleado) {
        String cuenta = requerido(solicitud == null ? null : solicitud.getCuenta(), "La cuenta es obligatoria");
        validarImporte(solicitud.getImporte());

        return enTransaccion(conexion -> {
            CuentaInterna datos = cuentaActiva(conexion, cuenta);
            datos.setSaldo(datos.getSaldo().add(solicitud.getImporte()).subtract(datos.getCostoMovimiento()));
            registrarMovimientos(conexion, datos, codigoEmpleado, TipoMovimiento.DEPOSITO, solicitud.getImporte(), null);
            return exito("Deposito realizado correctamente", datos);
        });
    }

    @Override
    public RespuestaOperacion retirar(SolicitudRetiro solicitud, String codigoEmpleado) {
        String cuenta = requerido(solicitud == null ? null : solicitud.getCuenta(), "La cuenta es obligatoria");
        String clave = requerido(solicitud.getClave(), "La clave de la cuenta es obligatoria");
        validarImporte(solicitud.getImporte());

        return enTransaccion(conexion -> {
            CuentaInterna datos = cuentaActiva(conexion, cuenta);
            validarClave(datos, clave);
            validarSaldo(datos, solicitud.getImporte());
            datos.setSaldo(datos.getSaldo().subtract(solicitud.getImporte()).subtract(datos.getCostoMovimiento()));
            registrarMovimientos(conexion, datos, codigoEmpleado, TipoMovimiento.RETIRO, solicitud.getImporte(), null);
            return exito("Retiro realizado correctamente", datos);
        });
    }

    @Override
    public RespuestaOperacion transferir(SolicitudTransferencia solicitud, String codigoEmpleado) {
        String origen = requerido(solicitud == null ? null : solicitud.getCuentaOrigen(), "La cuenta de origen es obligatoria");
        String clave = requerido(solicitud.getClaveOrigen(), "La clave de la cuenta de origen es obligatoria");
        String destino = requerido(solicitud.getCuentaDestino(), "La cuenta de destino es obligatoria");
        validarImporte(solicitud.getImporte());
        if (origen.equals(destino)) {
            throw new ExcepcionNegocio(CodigoRespuesta.MISMA_CUENTA, "La cuenta de origen y la de destino son la misma");
        }

        return enTransaccion(conexion -> {
            // Se bloquean en orden para evitar interbloqueos entre transferencias cruzadas
            boolean origenPrimero = origen.compareTo(destino) < 0;
            CuentaInterna primera = cuentaActiva(conexion, origenPrimero ? origen : destino);
            CuentaInterna segunda = cuentaActiva(conexion, origenPrimero ? destino : origen);
            CuentaInterna datosOrigen = origenPrimero ? primera : segunda;
            CuentaInterna datosDestino = origenPrimero ? segunda : primera;

            validarClave(datosOrigen, clave);
            if (!datosOrigen.getCodigoMoneda().equals(datosDestino.getCodigoMoneda())) {
                throw new ExcepcionNegocio(CodigoRespuesta.MONEDAS_DISTINTAS, "Las cuentas deben ser de la misma moneda");
            }
            validarSaldo(datosOrigen, solicitud.getImporte());

            datosOrigen.setSaldo(datosOrigen.getSaldo().subtract(solicitud.getImporte()).subtract(datosOrigen.getCostoMovimiento()));
            registrarMovimientos(conexion, datosOrigen, codigoEmpleado, TipoMovimiento.TRANSFERENCIA_SALIDA,
                    solicitud.getImporte(), datosDestino.getCodigo());

            datosDestino.setSaldo(datosDestino.getSaldo().add(solicitud.getImporte()).subtract(datosDestino.getCostoMovimiento()));
            registrarMovimientos(conexion, datosDestino, codigoEmpleado, TipoMovimiento.TRANSFERENCIA_INGRESO,
                    solicitud.getImporte(), datosOrigen.getCodigo());

            return exito("Transferencia realizada correctamente", datosOrigen);
        });
    }

    // ------------------------------------------------------------------ reglas comunes

    static String requerido(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new ExcepcionNegocio(CodigoRespuesta.DATOS_INCONSISTENTES, mensaje);
        }
        return valor.trim();
    }

    static void validarImporte(BigDecimal importe) {
        if (importe == null || importe.signum() <= 0) {
            throw new ExcepcionNegocio(CodigoRespuesta.DATOS_INCONSISTENTES, "El importe debe ser mayor a cero");
        }
        if (importe.stripTrailingZeros().scale() > 2) {
            throw new ExcepcionNegocio(CodigoRespuesta.DATOS_INCONSISTENTES, "El importe admite maximo dos decimales");
        }
    }

    static void validarClave(CuentaInterna cuenta, String clave) {
        if (!cuenta.getClave().equals(clave)) {
            throw new ExcepcionNegocio(CodigoRespuesta.CLAVE_CUENTA_INCORRECTA, "Clave de la cuenta incorrecta");
        }
    }

    static void validarSaldo(CuentaInterna cuenta, BigDecimal importe) {
        BigDecimal requerido = importe.add(cuenta.getCostoMovimiento());
        if (cuenta.getSaldo().compareTo(requerido) < 0) {
            throw new ExcepcionNegocio(CodigoRespuesta.SALDO_INSUFICIENTE, String.format(java.util.Locale.ROOT,
                    "Saldo insuficiente: disponible %.2f, requerido %.2f (incluye costo de %.2f)",
                    cuenta.getSaldo(), requerido, cuenta.getCostoMovimiento()));
        }
    }

    private CuentaInterna cuentaActiva(Connection conexion, String codigo) throws SQLException {
        CuentaInterna cuenta = repositorioCuenta.buscarParaActualizar(conexion, codigo);
        if (cuenta == null) {
            throw new ExcepcionNegocio(CodigoRespuesta.CUENTA_NO_EXISTE, "La cuenta " + codigo + " no existe");
        }
        if (cuenta.getEstado() != EstadoCuenta.ACTIVO) {
            throw new ExcepcionNegocio(CodigoRespuesta.CUENTA_NO_ACTIVA,
                    "La cuenta " + codigo + " no esta activa (" + cuenta.getEstado() + ")");
        }
        return cuenta;
    }

    /** Registra el movimiento principal y el costo (010), y actualiza la cuenta. */
    private void registrarMovimientos(Connection conexion, CuentaInterna cuenta, String codigoEmpleado,
            TipoMovimiento tipo, BigDecimal importe, String referencia) throws SQLException {
        int numero = cuenta.getContadorMovimientos();
        repositorioMovimiento.insertar(conexion, cuenta.getCodigo(), numero + 1, codigoEmpleado,
                TipoMovimientoMapper.aCodigo(tipo), importe, referencia);
        repositorioMovimiento.insertar(conexion, cuenta.getCodigo(), numero + 2, codigoEmpleado,
                TipoMovimientoMapper.aCodigo(TipoMovimiento.COSTO_MOVIMIENTO), cuenta.getCostoMovimiento(), null);
        cuenta.setContadorMovimientos(numero + 2);
        repositorioCuenta.actualizar(conexion, cuenta);
    }

    private static RespuestaOperacion exito(String mensaje, CuentaInterna cuenta) {
        RespuestaOperacion respuesta = FabricaRespuesta.exito(RespuestaOperacion::new, mensaje);
        respuesta.setCuenta(cuenta.getCodigo());
        respuesta.setSaldo(cuenta.getSaldo().setScale(2, RoundingMode.HALF_UP));
        return respuesta;
    }

    private static RespuestaOperacion enTransaccion(Operacion operacion) {
        try (Connection conexion = AccesoDB.obtenerConexion()) {
            conexion.setAutoCommit(false);
            try {
                RespuestaOperacion respuesta = operacion.ejecutar(conexion);
                conexion.commit();
                return respuesta;
            } catch (RuntimeException | SQLException e) {
                conexion.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo acceder a la base de datos", e);
        }
    }
}
