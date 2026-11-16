using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.modelos.utilidades.mapeadores;
using Microsoft.Data.SqlClient;

namespace ec.edu.monster.datos
{
    public class RepositorioMovimiento
    {
        private const string SQL_LISTAR = @"
            SELECT mv.int_movinumero, mv.dtt_movifecha, mv.chr_tipocodigo, t.vch_tipodescripcion,
                   t.vch_tipoaccion, mv.dec_moviimporte, mv.chr_cuenreferencia
            FROM Movimiento mv
            JOIN TipoMovimiento t ON t.chr_tipocodigo = mv.chr_tipocodigo
            WHERE mv.chr_cuencodigo = @cuenta
            ORDER BY mv.int_movinumero";

        private const string SQL_INSERTAR = @"
            INSERT INTO Movimiento (chr_cuencodigo, int_movinumero, dtt_movifecha, chr_emplcodigo,
                                    chr_tipocodigo, dec_moviimporte, chr_cuenreferencia)
            VALUES (@cuenta, @numero, CAST(GETDATE() AS DATE), @empleado, @tipo, @importe, @referencia)";

        public List<DatosMovimiento> Listar(string cuenta)
        {
            using SqlConnection conexion = AccesoDB.ObtenerConexion();
            using SqlCommand comando = new(SQL_LISTAR, conexion);
            comando.Parameters.AddWithValue("@cuenta", cuenta);
            using SqlDataReader lector = comando.ExecuteReader();

            List<DatosMovimiento> movimientos = new();
            while (lector.Read())
            {
                movimientos.Add(MovimientoMapper.ADatosMovimiento(lector));
            }
            return movimientos;
        }

        public void Insertar(SqlConnection conexion, SqlTransaction transaccion, string cuenta, int numero,
                string empleado, string tipo, decimal importe, string? referencia)
        {
            using SqlCommand comando = new(SQL_INSERTAR, conexion, transaccion);
            comando.Parameters.AddWithValue("@cuenta", cuenta);
            comando.Parameters.AddWithValue("@numero", numero);
            comando.Parameters.AddWithValue("@empleado", empleado);
            comando.Parameters.AddWithValue("@tipo", tipo);
            comando.Parameters.AddWithValue("@importe", importe);
            comando.Parameters.AddWithValue("@referencia", (object?)referencia ?? DBNull.Value);
            comando.ExecuteNonQuery();
        }
    }
}
