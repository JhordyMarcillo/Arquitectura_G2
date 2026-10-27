using ec.edu.monster.controladores;
using ec.edu.monster.modelos;
using Microsoft.AspNetCore.Mvc;

namespace ec.edu.monster.vistas
{
    public class PaginaMovimientos : PaginaProtegida
    {
        public PaginaMovimientos(IConfiguration configuracion) : base(configuracion)
        {
            MenuActivo = "movimientos";
        }

        [BindProperty(SupportsGet = true)]
        public string? Cuenta { get; set; }

        public RespuestaMovimientos? Datos { get; private set; }

        public async Task<IActionResult> OnGetAsync()
        {
            if (string.IsNullOrWhiteSpace(Cuenta))
            {
                return Page();
            }
            try
            {
                Datos = await Servicio.ConsultarMovimientosAsync(Cuenta);
            }
            catch (ExcepcionServicio e)
            {
                return ManejarError(e) ?? Page();
            }
            return Page();
        }
    }
}
