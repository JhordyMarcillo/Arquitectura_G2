using System.Data;

namespace ec.edu.monster.modelos.utilidades.mapeadores
{
    public static class MovimientoMapper
    {
        public static DatosMovimiento ADatosMovimiento(IDataRecord fila)
        {
            return new DatosMovimiento
            {
                Numero = fila.GetInt32(0),
                Fecha = fila.GetDateTime(1).ToString("yyyy-MM-dd"),
                Tipo = fila.GetString(2),
                Descripcion = fila.GetString(3),
                Accion = fila.GetString(4),
                Importe = fila.GetDecimal(5),
                CuentaReferencia = fila.IsDBNull(6) ? null : fila.GetString(6)
            };
        }
    }
}
