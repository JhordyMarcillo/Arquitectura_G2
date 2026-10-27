using ec.edu.monster.controladores;
using ec.edu.monster.modelos;
using ec.edu.monster.servicios;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;

namespace ec.edu.monster.vistas
{
    public class PaginaLogin : PageModel
    {
        private readonly IConfiguration configuracion;

        public PaginaLogin(IConfiguration configuracion)
        {
            this.configuracion = configuracion;
        }

        [BindProperty] public string Usuario { get; set; } = string.Empty;
        [BindProperty] public string Clave { get; set; } = string.Empty;
        [BindProperty] public string UrlServidor { get; set; } = string.Empty;
        public string? MensajeError { get; private set; }
        public string? Aviso { get; private set; }

        public IActionResult OnGet()
        {
            if (SesionWeb.Sesion(HttpContext) is not null)
            {
                return RedirectToPage("/Index");
            }
            UrlServidor = SesionWeb.UrlServidor(HttpContext, configuracion);
            Aviso = SesionWeb.TomarAviso(HttpContext);
            return Page();
        }

        public async Task<IActionResult> OnPostAsync()
        {
            IServicioEurekaBank servicio = SesionWeb.CrearServicio(HttpContext, configuracion);
            servicio.UrlServidor = UrlServidor;
            try
            {
                RespuestaAutenticacion sesion = await servicio.IniciarSesionAsync(Usuario.Trim(), Clave);
                SesionWeb.Guardar(HttpContext, servicio.UrlServidor, sesion);
                return RedirectToPage("/Index");
            }
            catch (ExcepcionServicio e)
            {
                MensajeError = e.Message;
                return Page();
            }
        }

        public IActionResult OnPostSalir()
        {
            SesionWeb.Cerrar(HttpContext);
            return RedirectToPage("/Login");
        }
    }
}
