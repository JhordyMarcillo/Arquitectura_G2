using ec.edu.monster.modelos.utilidades.enums;
using System.Data;

namespace ec.edu.monster.modelos.utilidades.mapeadores
{
    public static class CuentaMapper
    {
        public static DatosCuenta ADatosCuenta(IDataRecord fila)
        {
            return new DatosCuenta
            {
                Codigo = fila.GetString(0),
                Cliente = fila.GetString(1),
                Moneda = fila.GetString(2),
                Saldo = fila.GetDecimal(3),
                Estado = fila.GetString(4)
            };
        }

        public static CuentaInterna ACuentaInterna(IDataRecord fila)
        {
            return new CuentaInterna
            {
                Codigo = fila.GetString(0),
                CodigoMoneda = fila.GetString(1),
                Moneda = fila.GetString(2),
                Saldo = fila.GetDecimal(3),
                Estado = Enum.Parse<EstadoCuenta>(fila.GetString(4)),
                ContadorMovimientos = fila.GetInt32(5),
                Clave = fila.GetString(6),
                CostoMovimiento = fila.GetDecimal(7)
            };
        }
    }
}
