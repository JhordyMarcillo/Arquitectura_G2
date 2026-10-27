namespace ec.edu.monster.modelos.utilidades
{
    public class RespuestaMovimientos : Respuesta
    {
        public string Cuenta { get; set; } = string.Empty;
        public string Moneda { get; set; } = string.Empty;
        public decimal Saldo { get; set; }
        public List<DatosMovimiento> Movimientos { get; set; } = new();
    }
}
