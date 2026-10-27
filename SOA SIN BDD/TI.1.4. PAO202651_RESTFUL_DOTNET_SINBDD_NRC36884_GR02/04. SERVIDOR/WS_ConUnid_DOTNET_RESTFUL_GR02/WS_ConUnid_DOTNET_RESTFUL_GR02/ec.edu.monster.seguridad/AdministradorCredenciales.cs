namespace ec.edu.monster.seguridad
{
    public static class AdministradorCredenciales
    {
        private const string USUARIO = "MONSTER";
        private const string CONTRASENIA = "Monster9";

        public static bool ValidarCredenciales(string usuario, string contrasenia)
        {
            return USUARIO == usuario && CONTRASENIA == contrasenia;
        }
    }
}
