using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.modelos.utilidades.mapeadores;
using Microsoft.Data.SqlClient;

namespace ec.edu.monster.datos
{
    public class RepositorioCuenta
    {
        private const string SQL_LISTAR = @"
            SELECT c.chr_cuencodigo,
                   cl.vch_clienombre + ' ' + cl.vch_cliepaterno + ' ' + cl.vch_cliematerno AS cliente,
                   m.vch_monedescripcion, c.dec_cuensaldo, c.vch_cuenestado
            FROM Cuenta c
            JOIN Cliente cl ON cl.chr_cliecodigo = c.chr_cliecodigo
            JOIN Moneda m ON m.chr_monecodigo = c.chr_monecodigo
            ORDER BY c.chr_cuencodigo";

        private const string SQL_BUSCAR_BLOQUEANDO = @"
            SELECT c.chr_cuencodigo, c.chr_monecodigo, m.vch_monedescripcion, c.dec_cuensaldo,
                   c.vch_cuenestado, c.int_cuencontmov, c.chr_cuenclave, co.dec_costimporte
            FROM Cuenta c WITH (UPDLOCK, ROWLOCK)
            JOIN Moneda m ON m.chr_monecodigo = c.chr_monecodigo
            JOIN CostoMovimiento co ON co.chr_monecodigo = c.chr_monecodigo
            WHERE c.chr_cuencodigo = @cuenta";

        private const string SQL_BUSCAR = @"
            SELECT c.chr_cuencodigo, c.chr_monecodigo, m.vch_monedescripcion, c.dec_cuensaldo,
                   c.vch_cuenestado, c.int_cuencontmov, c.chr_cuenclave, co.dec_costimporte
            FROM Cuenta c
            JOIN Moneda m ON m.chr_monecodigo = c.chr_monecodigo
            JOIN CostoMovimiento co ON co.chr_monecodigo = c.chr_monecodigo
            WHERE c.chr_cuencodigo = @cuenta";

        private const string SQL_ACTUALIZAR = @"
            UPDATE Cuenta
            SET dec_cuensaldo = @saldo, int_cuencontmov = @contador
            WHERE chr_cuencodigo = @cuenta";

        public List<DatosCuenta> Listar()
        {
            using SqlConnection conexion = AccesoDB.ObtenerConexion();
            using SqlCommand comando = new(SQL_LISTAR, conexion);
            using SqlDataReader lector = comando.ExecuteReader();

            List<DatosCuenta> cuentas = new();
            while (lector.Read())
            {
                cuentas.Add(CuentaMapper.ADatosCuenta(lector));
            }
            return cuentas;
        }

        public CuentaInterna? Buscar(string cuenta)
        {
            using SqlConnection conexion = AccesoDB.ObtenerConexion();
            using SqlCommand comando = new(SQL_BUSCAR, conexion);
            comando.Parameters.AddWithValue("@cuenta", cuenta);
            using SqlDataReader lector = comando.ExecuteReader();
            return lector.Read() ? CuentaMapper.ACuentaInterna(lector) : null;
        }

        /// <summary>Lee la cuenta y la bloquea hasta que termine la transaccion.</summary>
        public CuentaInterna? BuscarParaActualizar(SqlConnection conexion, SqlTransaction transaccion, string cuenta)
        {
            using SqlCommand comando = new(SQL_BUSCAR_BLOQUEANDO, conexion, transaccion);
            comando.Parameters.AddWithValue("@cuenta", cuenta);
            using SqlDataReader lector = comando.ExecuteReader();
            return lector.Read() ? CuentaMapper.ACuentaInterna(lector) : null;
        }

        public void Actualizar(SqlConnection conexion, SqlTransaction transaccion, CuentaInterna cuenta)
        {
            using SqlCommand comando = new(SQL_ACTUALIZAR, conexion, transaccion);
            comando.Parameters.AddWithValue("@saldo", cuenta.Saldo);
            comando.Parameters.AddWithValue("@contador", cuenta.ContadorMovimientos);
            comando.Parameters.AddWithValue("@cuenta", cuenta.Codigo);
            comando.ExecuteNonQuery();
        }
    }
}
