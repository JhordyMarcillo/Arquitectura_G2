using ec.edu.monster.modelos;
using ec.edu.monster.servicios;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;
using Microsoft.AspNetCore.Mvc.RazorPages;

namespace ec.edu.monster.controladores
{
    /// <summary>Base de las paginas que exigen sesion: si no hay sesion, redirige al login.</summary>
    public abstract class PaginaProtegida : PageModel
    {
        private readonly IConfiguration configuracion;

        protected PaginaProtegida(IConfiguration configuracion)
        {
            this.configuracion = configuracion;
        }

        protected IServicioEurekaBank Servicio { get; private set; } = null!;
        public RespuestaAutenticacion Usuario { get; private set; } = null!;
        public string MenuActivo { get; protected set; } = string.Empty;
        public string? MensajeError { get; protected set; }

        public override async Task OnPageHandlerExecutionAsync(PageHandlerExecutingContext contexto, PageHandlerExecutionDelegate siguiente)
        {
            RespuestaAutenticacion? sesion = SesionWeb.Sesion(HttpContext);
            if (sesion is null)
            {
                contexto.Result = RedirectToPage("/Login");
                return;
            }
            Usuario = sesion;
            Servicio = SesionWeb.CrearServicio(HttpContext, configuracion);
            await siguiente();
        }

        /// <summary>Si la sesion expiro vuelve al login; en otro caso muestra el error en la pagina.</summary>
        protected IActionResult? ManejarError(ExcepcionServicio error)
        {
            if (error.RequiereLogin)
            {
                SesionWeb.Cerrar(HttpContext);
                SesionWeb.Avisar(HttpContext, error.Message + ". Inicie sesion nuevamente.");
                return RedirectToPage("/Login");
            }
            MensajeError = error.Message;
            return null;
        }
    }
}
