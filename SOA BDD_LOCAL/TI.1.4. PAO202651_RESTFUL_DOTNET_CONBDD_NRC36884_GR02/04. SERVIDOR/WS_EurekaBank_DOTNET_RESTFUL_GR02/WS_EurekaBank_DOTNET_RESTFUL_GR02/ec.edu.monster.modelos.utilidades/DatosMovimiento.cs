namespace ec.edu.monster.modelos.utilidades
{
    public class DatosMovimiento
    {
        public int Numero { get; set; }
        public string Fecha { get; set; } = string.Empty;
        public string Tipo { get; set; } = string.Empty;
        public string Descripcion { get; set; } = string.Empty;
        public string Accion { get; set; } = string.Empty;
        public decimal Importe { get; set; }
        public string? CuentaReferencia { get; set; }
    }
}
