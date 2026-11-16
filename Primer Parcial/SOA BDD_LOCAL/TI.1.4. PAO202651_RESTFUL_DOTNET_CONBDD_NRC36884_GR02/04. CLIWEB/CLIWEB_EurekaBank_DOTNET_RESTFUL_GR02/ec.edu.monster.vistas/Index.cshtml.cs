using ec.edu.monster.controladores;
using ec.edu.monster.modelos;
using Microsoft.AspNetCore.Mvc;

namespace ec.edu.monster.vistas
{
    public class PaginaCuentas : PaginaProtegida
    {
        public PaginaCuentas(IConfiguration configuracion) : base(configuracion)
        {
            MenuActivo = "cuentas";
        }

        public List<DatosCuenta> Cuentas { get; private set; } = new();
        public decimal TotalSoles => Cuentas.Where(c => c.EstaActiva && c.SimboloMoneda == "S/").Sum(c => c.Saldo);
        public decimal TotalDolares => Cuentas.Where(c => c.EstaActiva && c.SimboloMoneda == "US$").Sum(c => c.Saldo);

        public async Task<IActionResult> OnGetAsync()
        {
            try
            {
                Cuentas = (await Servicio.ListarCuentasAsync()).Cuentas;
            }
            catch (ExcepcionServicio e)
            {
                return ManejarError(e) ?? Page();
            }
            return Page();
        }
    }
}
