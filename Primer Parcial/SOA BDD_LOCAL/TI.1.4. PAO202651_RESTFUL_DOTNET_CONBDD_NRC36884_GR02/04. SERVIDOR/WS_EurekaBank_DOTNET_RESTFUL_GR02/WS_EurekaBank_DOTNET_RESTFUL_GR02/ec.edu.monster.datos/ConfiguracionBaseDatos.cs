namespace ec.edu.monster.datos
{
    /// <summary>
    /// Resuelve la cadena de conexion a SQL Server, en este orden:
    /// 1. Variable de entorno DB_CONNECTION_STRING.
    /// 2. Archivo .env de la carpeta "03. BDD" del TI (el mismo del docker-compose).
    /// 3. ConnectionStrings:EurekaBank de appsettings.json.
    /// </summary>
    public static class ConfiguracionBaseDatos
    {
        private const string VARIABLE = "DB_CONNECTION_STRING";
        private const string CARPETA_BDD = "03. BDD";

        public static string CadenaConexion { get; private set; } = string.Empty;
        public static string Origen { get; private set; } = string.Empty;

        public static void Inicializar(IConfiguration configuracion)
        {
            string? deEntorno = Environment.GetEnvironmentVariable(VARIABLE);
            if (!string.IsNullOrWhiteSpace(deEntorno))
            {
                Establecer(deEntorno, "variable de entorno " + VARIABLE);
                return;
            }

            string? archivoEnv = BuscarArchivoEnv();
            string? deArchivo = archivoEnv is null ? null : LeerVariable(archivoEnv, VARIABLE);
            if (!string.IsNullOrWhiteSpace(deArchivo))
            {
                Establecer(deArchivo, archivoEnv!);
                return;
            }

            string? deConfiguracion = configuracion.GetConnectionString("EurekaBank");
            if (string.IsNullOrWhiteSpace(deConfiguracion))
            {
                throw new InvalidOperationException("No se encontro la cadena de conexion a la base de datos");
            }
            Establecer(deConfiguracion, "appsettings.json");
        }

        private static void Establecer(string cadena, string origen)
        {
            CadenaConexion = cadena;
            Origen = origen;
        }

        private static string? BuscarArchivoEnv()
        {
            foreach (string inicio in new[] { Directory.GetCurrentDirectory(), AppContext.BaseDirectory })
            {
                DirectoryInfo? directorio = new(inicio);
                while (directorio is not null)
                {
                    string candidato = Path.Combine(directorio.FullName, CARPETA_BDD, ".env");
                    if (File.Exists(candidato))
                    {
                        return candidato;
                    }
                    directorio = directorio.Parent;
                }
            }
            return null;
        }

        private static string? LeerVariable(string archivo, string nombre)
        {
            foreach (string linea in File.ReadAllLines(archivo))
            {
                string limpia = linea.Trim();
                if (limpia.StartsWith('#') || !limpia.StartsWith(nombre + "="))
                {
                    continue;
                }
                return limpia[(nombre.Length + 1)..].Trim();
            }
            return null;
        }
    }
}
