namespace ec.edu.monster.modelos.utilidades
{
    public class DatosCuenta
    {
        public string Codigo { get; set; } = string.Empty;
        public string Cliente { get; set; } = string.Empty;
        public string Moneda { get; set; } = string.Empty;
        public decimal Saldo { get; set; }
        public string Estado { get; set; } = string.Empty;
    }
}
