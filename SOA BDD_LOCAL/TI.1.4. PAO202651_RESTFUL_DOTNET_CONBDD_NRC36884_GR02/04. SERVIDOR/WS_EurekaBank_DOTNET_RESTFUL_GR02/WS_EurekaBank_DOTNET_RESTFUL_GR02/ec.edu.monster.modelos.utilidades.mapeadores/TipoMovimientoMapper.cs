using ec.edu.monster.modelos.utilidades.enums;

namespace ec.edu.monster.modelos.utilidades.mapeadores
{
    public static class TipoMovimientoMapper
    {
        public static string ACodigo(TipoMovimiento tipo) => tipo switch
        {
            TipoMovimiento.DEPOSITO => "003",
            TipoMovimiento.RETIRO => "004",
            TipoMovimiento.TRANSFERENCIA_INGRESO => "008",
            TipoMovimiento.TRANSFERENCIA_SALIDA => "009",
            TipoMovimiento.COSTO_MOVIMIENTO => "010",
            _ => throw new ArgumentOutOfRangeException(nameof(tipo))
        };
    }
}
