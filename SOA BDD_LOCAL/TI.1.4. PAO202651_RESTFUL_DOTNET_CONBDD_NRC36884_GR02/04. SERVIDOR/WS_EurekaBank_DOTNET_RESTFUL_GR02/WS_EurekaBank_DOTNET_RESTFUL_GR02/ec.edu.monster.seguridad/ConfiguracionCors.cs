namespace ec.edu.monster.seguridad
{
    public static class ConfiguracionCors
    {
        public const string POLITICA = "PoliticaCors";

        // Los clientes web del grupo corren en distintas maquinas de la red local
        private static readonly string[] METODOS = { "GET", "POST", "OPTIONS" };
        private static readonly string[] HEADERS = { "Origin", "Content-Type", "Accept", "Authorization", "X-Requested-With" };
        private const int MAX_AGE = 3600;

        public static IServiceCollection AgregarCors(this IServiceCollection servicios)
        {
            servicios.AddCors(opciones =>
                opciones.AddPolicy(POLITICA, politica => politica
                    .AllowAnyOrigin()
                    .WithMethods(METODOS)
                    .WithHeaders(HEADERS)
                    .SetPreflightMaxAge(TimeSpan.FromSeconds(MAX_AGE))));

            return servicios;
        }
    }
}
