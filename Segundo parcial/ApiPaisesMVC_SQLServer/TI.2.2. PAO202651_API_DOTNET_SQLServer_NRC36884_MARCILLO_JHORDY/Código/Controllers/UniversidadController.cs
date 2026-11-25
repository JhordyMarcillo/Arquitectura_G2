using ApiPaisesMVC_SQLServer.Models;
using ApiPaisesMVC_SQLServer.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace ApiPaisesMVC_SQLServer.Controllers
{
    // Actividad avanzada: consumir otra API publica y guardarla en SQL Server
    public class UniversidadController : Controller
    {
        private readonly AppDbContext _context;
        private readonly UniversidadApiService _api;

        public UniversidadController(AppDbContext context, UniversidadApiService api)
        {
            _context = context;
            _api = api;
        }

        public async Task<IActionResult> Lista(string pais, string buscar)
        {
            var consulta = _context.Universidades.AsQueryable();
            if (!string.IsNullOrWhiteSpace(pais))
                consulta = consulta.Where(u => u.Pais == pais);
            if (!string.IsNullOrWhiteSpace(buscar))
                consulta = consulta.Where(u => u.Nombre.Contains(buscar));

            ViewBag.Pais = pais;
            ViewBag.Buscar = buscar;
            ViewBag.Paises = await _context.Universidades
                .Select(u => u.Pais).Distinct().OrderBy(p => p).ToListAsync();
            return View(await consulta.OrderBy(u => u.Nombre).ToListAsync());
        }

        [HttpPost, ValidateAntiForgeryToken]
        public async Task<IActionResult> Importar(string pais)
        {
            pais = string.IsNullOrWhiteSpace(pais) ? "Ecuador" : pais.Trim();
            try
            {
                var lista = await _api.ObtenerPorPais(pais);
                var existentes = (await _context.Universidades
                    .Where(u => u.Pais == pais).Select(u => u.Nombre).ToListAsync()).ToHashSet();

                int nuevas = 0;
                foreach (var u in lista)
                {
                    if (string.IsNullOrWhiteSpace(u.Nombre) || !existentes.Add(u.Nombre)) continue;
                    u.Pais = pais;
                    _context.Universidades.Add(u);
                    nuevas++;
                }
                await _context.SaveChangesAsync();
                TempData["Mensaje"] = $"{lista.Count} universidades recibidas de la API; {nuevas} nuevas guardadas.";
            }
            catch (ApiException ex)
            {
                TempData["Error"] = ex.Message;
            }
            return RedirectToAction(nameof(Lista), new { pais });
        }
    }
}
