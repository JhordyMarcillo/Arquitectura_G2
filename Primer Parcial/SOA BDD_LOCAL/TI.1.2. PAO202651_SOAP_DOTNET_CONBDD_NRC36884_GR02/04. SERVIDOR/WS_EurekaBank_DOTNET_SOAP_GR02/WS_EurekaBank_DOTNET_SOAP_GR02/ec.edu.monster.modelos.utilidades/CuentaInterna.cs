using ec.edu.monster.modelos.utilidades.enums;

namespace ec.edu.monster.modelos.utilidades
{
    /// <summary>Cuenta leida para operar (incluye clave y costo); nunca se envia al cliente.</summary>
    public class CuentaInterna
    {
        public string Codigo { get; set; } = string.Empty;
        public string CodigoMoneda { get; set; } = string.Empty;
        public string Moneda { get; set; } = string.Empty;
        public decimal Saldo { get; set; }
        public EstadoCuenta Estado { get; set; }
        public int ContadorMovimientos { get; set; }
        public string Clave { get; set; } = string.Empty;
        public decimal CostoMovimiento { get; set; }
    }
}
