namespace ec.edu.monster.vistas
{
    /// <summary>Cuenta preparada para mostrarse en la lista.</summary>
    public class FilaCuenta
    {
        public string Codigo { get; init; } = string.Empty;
        public string Cliente { get; init; } = string.Empty;
        public string Moneda { get; init; } = string.Empty;
        public string Saldo { get; init; } = string.Empty;
        public string Estado { get; init; } = string.Empty;
        public bool EstaActiva { get; init; }
        public Color ColorEstado => EstaActiva ? Color.FromArgb("#059669") : Color.FromArgb("#64748B");
        public Color FondoEstado => EstaActiva ? Color.FromArgb("#D1FAE5") : Color.FromArgb("#F1F5F9");
    }

    /// <summary>Movimiento preparado para mostrarse en la lista.</summary>
    public class FilaMovimiento
    {
        public string Descripcion { get; init; } = string.Empty;
        public string Detalle { get; init; } = string.Empty;
        public string Importe { get; init; } = string.Empty;
        public bool EsIngreso { get; init; }
        public string Icono => EsIngreso ? "icono_arriba.png" : "icono_abajo.png";
        public Color ColorImporte => EsIngreso ? Color.FromArgb("#059669") : Color.FromArgb("#DC2626");
        public Color FondoIcono => EsIngreso ? Color.FromArgb("#D1FAE5") : Color.FromArgb("#FEE2E2");
    }

    /// <summary>Linea del comprobante de una operacion.</summary>
    public class LineaComprobante
    {
        public string Etiqueta { get; init; } = string.Empty;
        public string Valor { get; init; } = string.Empty;
        public bool Destacado { get; init; }
        public double TamanoValor => Destacado ? 18 : 14;
        public Color ColorValor => Destacado ? Color.FromArgb("#1D4ED8") : Color.FromArgb("#0F172A");
    }
}
