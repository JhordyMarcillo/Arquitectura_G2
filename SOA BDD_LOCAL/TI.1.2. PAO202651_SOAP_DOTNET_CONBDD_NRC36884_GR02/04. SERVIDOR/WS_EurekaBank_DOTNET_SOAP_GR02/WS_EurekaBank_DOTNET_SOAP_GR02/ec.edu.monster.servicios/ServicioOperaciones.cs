using ec.edu.monster.datos;
using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.modelos.utilidades.enums;
using ec.edu.monster.modelos.utilidades.mapeadores;
using Microsoft.Data.SqlClient;
using System.Globalization;

namespace ec.edu.monster.servicios
{
    /// <summary>
    /// Deposito, retiro y transferencia con las mismas reglas que usp_deposito,
    /// usp_egcc_retiro y usp_egcc_transferencia del docente. Cada operacion es una transaccion.
    /// </summary>
    public class ServicioOperaciones : IServicioOperaciones
    {
        private readonly RepositorioCuenta repositorioCuenta = new();
        private readonly RepositorioMovimiento repositorioMovimiento = new();

        public RespuestaOperacion Depositar(SolicitudDeposito solicitud, string codigoEmpleado)
        {
            string cuenta = Requerido(solicitud.Cuenta, "La cuenta es obligatoria");
            ValidarImporte(solicitud.Importe);

            return EnTransaccion((conexion, transaccion) =>
            {
                CuentaInterna datos = CuentaActiva(conexion, transaccion, cuenta);

                datos.Saldo += solicitud.Importe - datos.CostoMovimiento;
                RegistrarMovimientos(conexion, transaccion, datos, codigoEmpleado,
                        (TipoMovimiento.DEPOSITO, solicitud.Importe, null));

                return Exito("Deposito realizado correctamente", datos);
            });
        }

        public RespuestaOperacion Retirar(SolicitudRetiro solicitud, string codigoEmpleado)
        {
            string cuenta = Requerido(solicitud.Cuenta, "La cuenta es obligatoria");
            string clave = Requerido(solicitud.Clave, "La clave de la cuenta es obligatoria");
            ValidarImporte(solicitud.Importe);

            return EnTransaccion((conexion, transaccion) =>
            {
                CuentaInterna datos = CuentaActiva(conexion, transaccion, cuenta);
                ValidarClave(datos, clave);
                ValidarSaldo(datos, solicitud.Importe);

                datos.Saldo -= solicitud.Importe + datos.CostoMovimiento;
                RegistrarMovimientos(conexion, transaccion, datos, codigoEmpleado,
                        (TipoMovimiento.RETIRO, solicitud.Importe, null));

                return Exito("Retiro realizado correctamente", datos);
            });
        }

        public RespuestaOperacion Transferir(SolicitudTransferencia solicitud, string codigoEmpleado)
        {
            string origen = Requerido(solicitud.CuentaOrigen, "La cuenta de origen es obligatoria");
            string clave = Requerido(solicitud.ClaveOrigen, "La clave de la cuenta de origen es obligatoria");
            string destino = Requerido(solicitud.CuentaDestino, "La cuenta de destino es obligatoria");
            ValidarImporte(solicitud.Importe);
            if (origen == destino)
            {
                throw new ExcepcionNegocio(CodigosRespuesta.MISMA_CUENTA, "La cuenta de origen y la de destino son la misma");
            }

            return EnTransaccion((conexion, transaccion) =>
            {
                // Se bloquean en orden para evitar interbloqueos entre transferencias cruzadas
                bool origenPrimero = string.CompareOrdinal(origen, destino) < 0;
                CuentaInterna primera = CuentaActiva(conexion, transaccion, origenPrimero ? origen : destino);
                CuentaInterna segunda = CuentaActiva(conexion, transaccion, origenPrimero ? destino : origen);
                CuentaInterna datosOrigen = origenPrimero ? primera : segunda;
                CuentaInterna datosDestino = origenPrimero ? segunda : primera;

                ValidarClave(datosOrigen, clave);
                if (datosOrigen.CodigoMoneda != datosDestino.CodigoMoneda)
                {
                    throw new ExcepcionNegocio(CodigosRespuesta.MONEDAS_DISTINTAS, "Las cuentas deben ser de la misma moneda",
                            StatusCodes.Status409Conflict);
                }
                ValidarSaldo(datosOrigen, solicitud.Importe);

                datosOrigen.Saldo -= solicitud.Importe + datosOrigen.CostoMovimiento;
                RegistrarMovimientos(conexion, transaccion, datosOrigen, codigoEmpleado,
                        (TipoMovimiento.TRANSFERENCIA_SALIDA, solicitud.Importe, datosDestino.Codigo));

                datosDestino.Saldo += solicitud.Importe - datosDestino.CostoMovimiento;
                RegistrarMovimientos(conexion, transaccion, datosDestino, codigoEmpleado,
                        (TipoMovimiento.TRANSFERENCIA_INGRESO, solicitud.Importe, datosOrigen.Codigo));

                return Exito("Transferencia realizada correctamente", datosOrigen);
            });
        }

        // ---------------------------------------------------------------- reglas comunes

        internal static string Requerido(string? valor, string mensaje)
        {
            if (string.IsNullOrWhiteSpace(valor))
            {
                throw new ExcepcionNegocio(CodigosRespuesta.DATOS_INCONSISTENTES, mensaje);
            }
            return valor.Trim();
        }

        internal static void ValidarImporte(decimal importe)
        {
            if (importe <= 0)
            {
                throw new ExcepcionNegocio(CodigosRespuesta.DATOS_INCONSISTENTES, "El importe debe ser mayor a cero");
            }
            if (decimal.Round(importe, 2) != importe)
            {
                throw new ExcepcionNegocio(CodigosRespuesta.DATOS_INCONSISTENTES, "El importe admite maximo dos decimales");
            }
        }

        internal static void ValidarClave(CuentaInterna cuenta, string clave)
        {
            if (cuenta.Clave != clave)
            {
                throw new ExcepcionNegocio(CodigosRespuesta.CLAVE_CUENTA_INCORRECTA, "Clave de la cuenta incorrecta",
                        StatusCodes.Status401Unauthorized);
            }
        }

        internal static void ValidarSaldo(CuentaInterna cuenta, decimal importe)
        {
            if (cuenta.Saldo < importe + cuenta.CostoMovimiento)
            {
                throw new ExcepcionNegocio(CodigosRespuesta.SALDO_INSUFICIENTE, string.Create(CultureInfo.InvariantCulture,
                        $"Saldo insuficiente: disponible {cuenta.Saldo:0.00}, requerido {importe + cuenta.CostoMovimiento:0.00} (incluye costo de {cuenta.CostoMovimiento:0.00})"),
                        StatusCodes.Status409Conflict);
            }
        }

        private CuentaInterna CuentaActiva(SqlConnection conexion, SqlTransaction transaccion, string codigo)
        {
            CuentaInterna cuenta = repositorioCuenta.BuscarParaActualizar(conexion, transaccion, codigo)
                    ?? throw new ExcepcionNegocio(CodigosRespuesta.CUENTA_NO_EXISTE, "La cuenta " + codigo + " no existe",
                            StatusCodes.Status404NotFound);
            if (cuenta.Estado != EstadoCuenta.ACTIVO)
            {
                throw new ExcepcionNegocio(CodigosRespuesta.CUENTA_NO_ACTIVA,
                        "La cuenta " + codigo + " no esta activa (" + cuenta.Estado + ")", StatusCodes.Status409Conflict);
            }
            return cuenta;
        }

        /// <summary>Registra el movimiento principal y el costo (010), y actualiza la cuenta.</summary>
        private void RegistrarMovimientos(SqlConnection conexion, SqlTransaction transaccion, CuentaInterna cuenta,
                string codigoEmpleado, (TipoMovimiento Tipo, decimal Importe, string? Referencia) principal)
        {
            repositorioMovimiento.Insertar(conexion, transaccion, cuenta.Codigo, cuenta.ContadorMovimientos + 1,
                    codigoEmpleado, TipoMovimientoMapper.ACodigo(principal.Tipo), principal.Importe, principal.Referencia);
            repositorioMovimiento.Insertar(conexion, transaccion, cuenta.Codigo, cuenta.ContadorMovimientos + 2,
                    codigoEmpleado, TipoMovimientoMapper.ACodigo(TipoMovimiento.COSTO_MOVIMIENTO), cuenta.CostoMovimiento, null);

            cuenta.ContadorMovimientos += 2;
            repositorioCuenta.Actualizar(conexion, transaccion, cuenta);
        }

        private static RespuestaOperacion Exito(string mensaje, CuentaInterna cuenta)
        {
            RespuestaOperacion respuesta = Respuesta.Exito<RespuestaOperacion>(mensaje);
            respuesta.Cuenta = cuenta.Codigo;
            respuesta.Saldo = decimal.Round(cuenta.Saldo, 2);
            return respuesta;
        }

        private static RespuestaOperacion EnTransaccion(Func<SqlConnection, SqlTransaction, RespuestaOperacion> operacion)
        {
            using SqlConnection conexion = AccesoDB.ObtenerConexion();
            using SqlTransaction transaccion = conexion.BeginTransaction();
            try
            {
                RespuestaOperacion respuesta = operacion(conexion, transaccion);
                transaccion.Commit();
                return respuesta;
            }
            catch
            {
                transaccion.Rollback();
                throw;
            }
        }
    }
}
