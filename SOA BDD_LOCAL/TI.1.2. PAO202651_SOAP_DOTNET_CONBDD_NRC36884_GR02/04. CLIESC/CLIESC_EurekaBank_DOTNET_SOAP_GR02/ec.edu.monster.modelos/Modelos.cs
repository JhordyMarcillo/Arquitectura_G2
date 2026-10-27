namespace ec.edu.monster.modelos
{
    /// <summary>Campos comunes de toda respuesta del servicio EurekaBank.</summary>
    public class Respuesta
    {
        public string Estado { get; set; } = string.Empty;
        public string Codigo { get; set; } = string.Empty;
        public string Mensaje { get; set; } = string.Empty;

        public bool EsExito => Estado == "OK";
    }

    public class RespuestaAutenticacion : Respuesta
    {
        public string Token { get; set; } = string.Empty;
        public string Expiracion { get; set; } = string.Empty;
        public string CodigoEmpleado { get; set; } = string.Empty;
        public string NombreEmpleado { get; set; } = string.Empty;
    }

    public class RespuestaCuentas : Respuesta
    {
        public List<DatosCuenta> Cuentas { get; set; } = new();
    }

    public class RespuestaMovimientos : Respuesta
    {
        public string Cuenta { get; set; } = string.Empty;
        public string Moneda { get; set; } = string.Empty;
        public decimal Saldo { get; set; }
        public List<DatosMovimiento> Movimientos { get; set; } = new();
    }

    public class RespuestaOperacion : Respuesta
    {
        public string Cuenta { get; set; } = string.Empty;
        public decimal Saldo { get; set; }
    }

    public class DatosCuenta
    {
        public string Codigo { get; set; } = string.Empty;
        public string Cliente { get; set; } = string.Empty;
        public string Moneda { get; set; } = string.Empty;
        public decimal Saldo { get; set; }
        public string Estado { get; set; } = string.Empty;

        public bool EstaActiva => Estado == "ACTIVO";
        public string SimboloMoneda => Moneda.StartsWith("Dol", StringComparison.OrdinalIgnoreCase) ? "US$" : "S/";
    }

    public class DatosMovimiento
    {
        public int Numero { get; set; }
        public string Fecha { get; set; } = string.Empty;
        public string Tipo { get; set; } = string.Empty;
        public string Descripcion { get; set; } = string.Empty;
        public string Accion { get; set; } = string.Empty;
        public decimal Importe { get; set; }
        public string? CuentaReferencia { get; set; }

        public bool EsIngreso => Accion == "INGRESO";
        /// <summary>Importe con signo: positivo si es ingreso, negativo si es salida.</summary>
        public decimal ImporteConSigno => EsIngreso ? Importe : -Importe;
    }

    /// <summary>Error devuelto por el servicio (estado ERROR) o de comunicacion con el servidor.</summary>
    public class ExcepcionServicio : Exception
    {
        public const string SIN_CONEXION = "RED-001";

        public string Codigo { get; }

        public ExcepcionServicio(string codigo, string mensaje, Exception? causa = null) : base(mensaje, causa)
        {
            Codigo = codigo;
        }

        /// <summary>La sesion ya no es valida y hay que volver a iniciar sesion.</summary>
        public bool RequiereLogin => Codigo is "AUTH-001" or "AUTH-002";
    }
}
