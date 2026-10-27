namespace ec.edu.monster.seguridad
{
    public static class ConfiguracionCors
    {
        public const string POLITICA = "PoliticaCors";

        private static readonly string[] ORIGENES = { "http://localhost:4200", "http://localhost:5173", "http://localhost:3000", "http://localhost:5141" };
        private static readonly string[] METODOS = { "GET", "POST", "PUT", "DELETE", "OPTIONS" };
        private static readonly string[] HEADERS = { "Origin", "Content-Type", "Accept", "Authorization", "X-Requested-With" };
        private const int MAX_AGE = 3600;

        public static IServiceCollection AgregarCors(this IServiceCollection servicios)
        {
            servicios.AddCors(opciones =>
                opciones.AddPolicy(POLITICA, politica => politica
                    .WithOrigins(ORIGENES)
                    .WithMethods(METODOS)
                    .WithHeaders(HEADERS)
                    .SetPreflightMaxAge(TimeSpan.FromSeconds(MAX_AGE))));

            return servicios;
        }
    }
}
