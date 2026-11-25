using ec.edu.monster.controladores;
using ec.edu.monster.modelos;
using ec.edu.monster.utilidades;
using Microsoft.AspNetCore.Mvc;

namespace ec.edu.monster.vistas
{
    /// <summary>Deposito, retiro o transferencia segun la ruta /Operacion/{tipo}.</summary>
    public class PaginaOperacion : PaginaProtegida
    {
        public PaginaOperacion(IConfiguration configuracion) : base(configuracion)
        {
        }

        [BindProperty(SupportsGet = true)] public string Tipo { get; set; } = "deposito";
        [BindProperty] public string? Cuenta { get; set; }
        [BindProperty] public string? Clave { get; set; }
        [BindProperty] public string? CuentaDestino { get; set; }
        [BindProperty] public string? Importe { get; set; }

        public RespuestaOperacion? Resultado { get; private set; }
        public string? CodigoError { get; private set; }
        public string Moneda { get; private set; } = "Soles";
        public decimal ImporteRegistrado { get; private set; }

        public string Titulo => Tipo switch { "retiro" => "Retiro", "transferencia" => "Transferencia", _ => "Deposito" };
        public string Subtitulo => Tipo switch
        {
            "retiro" => "Retiro de efectivo validado con la clave de la cuenta.",
            "transferencia" => "Traspaso entre cuentas de la misma moneda.",
            _ => "Abono de efectivo a una cuenta activa."
        };
        public string Descripcion => Tipo switch
        {
            "retiro" => "Se valida la clave y el saldo disponible.",
            "transferencia" => "Ambas cuentas deben ser de la misma moneda.",
            _ => "El importe se abona a la cuenta."
        };

        public IActionResult OnGet()
        {
            if (Tipo is not ("deposito" or "retiro" or "transferencia"))
            {
                return NotFound();
            }
            MenuActivo = Tipo;
            return Page();
        }

        public async Task<IActionResult> OnPostAsync()
        {
            MenuActivo = Tipo;
            if (!Formato.IntentarLeerImporte(Importe ?? string.Empty, out decimal importe))
            {
                MensajeError = "Ingrese un importe mayor a cero (ej. 150.50).";
                return Page();
            }

            try
            {
                string cuenta = Cuenta?.Trim() ?? string.Empty;
                Resultado = Tipo switch
                {
                    "retiro" => await Servicio.RetirarAsync(cuenta, Clave ?? string.Empty, importe),
                    "transferencia" => await Servicio.TransferirAsync(cuenta, Clave ?? string.Empty, CuentaDestino?.Trim() ?? string.Empty, importe),
                    _ => await Servicio.DepositarAsync(cuenta, importe)
                };
                ImporteRegistrado = importe;
                Moneda = (await Servicio.ConsultarMovimientosAsync(Resultado.Cuenta)).Moneda;
                Importe = null;
            }
            catch (ExcepcionServicio e)
            {
                IActionResult? redireccion = ManejarError(e);
                if (redireccion is not null)
                {
                    return redireccion;
                }
                CodigoError = e.Codigo;
            }
            return Page();
        }
    }
}
