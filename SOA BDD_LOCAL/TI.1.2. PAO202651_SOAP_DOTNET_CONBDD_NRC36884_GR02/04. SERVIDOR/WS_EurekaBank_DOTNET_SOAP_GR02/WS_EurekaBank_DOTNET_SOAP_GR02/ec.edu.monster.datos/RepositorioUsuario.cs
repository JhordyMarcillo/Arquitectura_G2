using ec.edu.monster.modelos.utilidades;
using Microsoft.Data.SqlClient;

namespace ec.edu.monster.datos
{
    public class RepositorioUsuario
    {
        private const string SQL_BUSCAR_ACTIVO = @"
            SELECT u.chr_emplcodigo,
                   e.vch_emplnombre + ' ' + e.vch_emplpaterno AS nombre
            FROM Usuario u
            JOIN Empleado e ON e.chr_emplcodigo = u.chr_emplcodigo
            WHERE u.vch_emplusuario = @usuario
              AND u.vch_emplclave = @clave
              AND u.vch_emplestado = 'ACTIVO'";

        public DatosEmpleado? BuscarActivo(string usuario, string claveCifrada)
        {
            using SqlConnection conexion = AccesoDB.ObtenerConexion();
            using SqlCommand comando = new(SQL_BUSCAR_ACTIVO, conexion);
            comando.Parameters.AddWithValue("@usuario", usuario);
            comando.Parameters.AddWithValue("@clave", claveCifrada);

            using SqlDataReader lector = comando.ExecuteReader();
            if (!lector.Read())
            {
                return null;
            }
            return new DatosEmpleado(lector.GetString(0), lector.GetString(1));
        }
    }
}
