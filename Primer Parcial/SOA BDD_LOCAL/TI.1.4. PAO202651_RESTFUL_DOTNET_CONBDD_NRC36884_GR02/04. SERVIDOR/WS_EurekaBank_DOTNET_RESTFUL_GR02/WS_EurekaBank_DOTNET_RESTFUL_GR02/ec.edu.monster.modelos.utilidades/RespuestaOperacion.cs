namespace ec.edu.monster.modelos.utilidades
{
    public class RespuestaOperacion : Respuesta
    {
        public string Cuenta { get; set; } = string.Empty;
        public decimal Saldo { get; set; }
    }
}
