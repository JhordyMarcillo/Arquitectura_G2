using ec.edu.monster.datos;
using ec.edu.monster.modelos.utilidades;

namespace ec.edu.monster.servicios
{
    public class ServicioCuentas
    {
        private readonly RepositorioCuenta repositorioCuenta = new();
        private readonly RepositorioMovimiento repositorioMovimiento = new();

        public RespuestaCuentas ListarCuentas()
        {
            RespuestaCuentas respuesta = Respuesta.Exito<RespuestaCuentas>("Cuentas consultadas correctamente");
            respuesta.Cuentas = repositorioCuenta.Listar();
            return respuesta;
        }

        public RespuestaMovimientos ConsultarMovimientos(string cuenta)
        {
            if (string.IsNullOrWhiteSpace(cuenta))
            {
                throw new ExcepcionNegocio(CodigosRespuesta.DATOS_INCONSISTENTES, "La cuenta es obligatoria");
            }

            CuentaInterna datos = repositorioCuenta.Buscar(cuenta.Trim())
                    ?? throw new ExcepcionNegocio(CodigosRespuesta.CUENTA_NO_EXISTE, "La cuenta " + cuenta + " no existe",
                            StatusCodes.Status404NotFound);

            RespuestaMovimientos respuesta = Respuesta.Exito<RespuestaMovimientos>("Movimientos consultados correctamente");
            respuesta.Cuenta = datos.Codigo;
            respuesta.Moneda = datos.Moneda;
            respuesta.Saldo = datos.Saldo;
            respuesta.Movimientos = repositorioMovimiento.Listar(datos.Codigo);
            return respuesta;
        }
    }
}
