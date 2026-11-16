using System.Globalization;

namespace ec.edu.monster.utilidades
{
    public static class Formato
    {
        private static readonly CultureInfo CULTURA = CultureInfo.InvariantCulture;

        public static string Simbolo(string moneda) =>
                moneda.StartsWith("Dol", StringComparison.OrdinalIgnoreCase) ? "US$" : "S/";

        public static string Dinero(string moneda, decimal valor) => $"{Simbolo(moneda)} {valor.ToString("#,##0.00", CULTURA)}";

        /// <summary>Acepta "150.50" o "150,50".</summary>
        public static bool IntentarLeerImporte(string texto, out decimal importe) =>
                decimal.TryParse(texto.Trim().Replace(',', '.'), NumberStyles.Number, CULTURA, out importe) && importe > 0;

        public static string Iniciales(string nombre)
        {
            string[] partes = nombre.Split(' ', StringSplitOptions.RemoveEmptyEntries);
            return string.Concat(partes.Take(2).Select(p => char.ToUpperInvariant(p[0])));
        }
    }
}
