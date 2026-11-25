using Microsoft.Data.SqlClient;

namespace ec.edu.monster.datos
{
    public static class AccesoDB
    {
        public static SqlConnection ObtenerConexion()
        {
            SqlConnection conexion = new(ConfiguracionBaseDatos.CadenaConexion);
            conexion.Open();
            return conexion;
        }
    }
}
