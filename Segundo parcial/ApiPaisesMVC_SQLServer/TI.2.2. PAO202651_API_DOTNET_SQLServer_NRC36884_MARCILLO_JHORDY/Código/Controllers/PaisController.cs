using ApiPaisesMVC_SQLServer.Models;
using ApiPaisesMVC_SQLServer.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace ApiPaisesMVC_SQLServer.Controllers
{
    public class PaisController : Controller
    {
        // Regiones que muestra la vista (espanol) -> valor que entrega la API (ingles)
        public static readonly Dictionary<string, string> Regiones = new()
        {
            ["América"] = "Americas",
            ["Europa"] = "Europe",
            ["Asia"] = "Asia",
            ["África"] = "Africa",
            ["Oceanía"] = "Oceania",
        };

        private readonly AppDbContext _context;
        private readonly PaisApiService _api;

        public PaisController(AppDbContext context, PaisApiService api)
        {
            _context = context;
            _api = api;
        }

        // ---------------- Pagina principal ----------------
        public async Task<IActionResult> Index()
        {
            ViewBag.Total = await _context.Paises.CountAsync();
            return View();
        }

        // ---------------- Importar desde la API ----------------
        [HttpPost, ValidateAntiForgeryToken]
        public async Task<IActionResult> Importar()
        {
            try
            {
                var paises = await _api.ObtenerPaises();
                int n = await GuardarPaises(paises);
                TempData["Mensaje"] = $"Importación finalizada: {n} países procesados.";
                return RedirectToAction(nameof(Lista));
            }
            catch (ApiException ex)
            {
                TempData["Error"] = ex.Message;
                return RedirectToAction(nameof(Index));
            }
        }

        /// <summary>Inserta los nuevos y actualiza los existentes (por nombre).</summary>
        private async Task<int> GuardarPaises(List<Pais> paises)
        {
            var existentes = await _context.Paises.ToDictionaryAsync(p => p.Nombre);
            foreach (var p in paises.Where(x => !string.IsNullOrWhiteSpace(x.Nombre)))
            {
                if (existentes.TryGetValue(p.Nombre, out var actual))
                {
                    actual.Capital = p.Capital;
                    actual.Region = p.Region;
                    actual.SubRegion = p.SubRegion;
                    actual.Poblacion = p.Poblacion;
                    actual.Area = p.Area;
                    actual.Moneda = p.Moneda;
                }
                else
                {
                    _context.Paises.Add(p);
                    existentes[p.Nombre] = p;
                }
            }
            await _context.SaveChangesAsync();
            return paises.Count;
        }

        // ---------------- Actividad 1: consultar un pais en la API ----------------
        public async Task<IActionResult> Consultar(string nombre)
        {
            ViewBag.Nombre = nombre;
            if (string.IsNullOrWhiteSpace(nombre))
                return View(new List<Pais>());
            try
            {
                return View(await _api.BuscarPorNombre(nombre));
            }
            catch (ApiException ex)
            {
                ViewBag.Error = ex.Message;
                return View(new List<Pais>());
            }
        }

        [HttpPost, ValidateAntiForgeryToken]
        public async Task<IActionResult> GuardarConsulta(string nombre)
        {
            try
            {
                int n = await GuardarPaises(await _api.BuscarPorNombre(nombre));
                TempData["Mensaje"] = $"{n} país(es) guardado(s) en SQL Server.";
                return RedirectToAction(nameof(Lista), new { nombre });
            }
            catch (ApiException ex)
            {
                TempData["Error"] = ex.Message;
                return RedirectToAction(nameof(Consultar), new { nombre });
            }
        }

        // ---------------- Listado + Actividades 3 y 4 (busquedas y filtros) ----------------
        public async Task<IActionResult> Lista(string nombre, string capital, string region)
        {
            var consulta = _context.Paises.AsQueryable();
            if (!string.IsNullOrWhiteSpace(nombre))
                consulta = consulta.Where(p => p.Nombre.Contains(nombre));
            if (!string.IsNullOrWhiteSpace(capital))
                consulta = consulta.Where(p => p.Capital.Contains(capital));
            if (!string.IsNullOrWhiteSpace(region))
            {
                string valor = Regiones.TryGetValue(region, out var v) ? v : region;
                consulta = consulta.Where(p => p.Region == valor);
            }

            ViewBag.Nombre = nombre;
            ViewBag.Capital = capital;
            ViewBag.Region = region;
            ViewBag.Regiones = Regiones.Keys;
            return View(await consulta.OrderBy(p => p.Nombre).ToListAsync());
        }

        // ---------------- Actividad 2: CRUD ----------------
        public IActionResult Create() => View(new Pais());

        [HttpPost, ValidateAntiForgeryToken]
        public async Task<IActionResult> Create(Pais pais)
        {
            if (await _context.Paises.AnyAsync(p => p.Nombre == pais.Nombre))
                ModelState.AddModelError(nameof(Pais.Nombre), "Ya existe un país con ese nombre.");
            if (!ModelState.IsValid) return View(pais);

            _context.Paises.Add(pais);
            await _context.SaveChangesAsync();
            TempData["Mensaje"] = "País creado.";
            return RedirectToAction(nameof(Lista));
        }

        public async Task<IActionResult> Edit(int id)
        {
            var pais = await _context.Paises.FindAsync(id);
            return pais == null ? NotFound() : View(pais);
        }

        [HttpPost, ValidateAntiForgeryToken]
        public async Task<IActionResult> Edit(int id, Pais pais)
        {
            if (id != pais.Id) return NotFound();
            if (await _context.Paises.AnyAsync(p => p.Nombre == pais.Nombre && p.Id != id))
                ModelState.AddModelError(nameof(Pais.Nombre), "Ya existe otro país con ese nombre.");
            if (!ModelState.IsValid) return View(pais);

            _context.Update(pais);
            await _context.SaveChangesAsync();
            TempData["Mensaje"] = "País actualizado.";
            return RedirectToAction(nameof(Lista));
        }

        public async Task<IActionResult> Delete(int id)
        {
            var pais = await _context.Paises.FindAsync(id);
            return pais == null ? NotFound() : View(pais);
        }

        [HttpPost, ActionName("Delete"), ValidateAntiForgeryToken]
        public async Task<IActionResult> DeleteConfirmed(int id)
        {
            var pais = await _context.Paises.FindAsync(id);
            if (pais != null)
            {
                _context.Paises.Remove(pais);
                await _context.SaveChangesAsync();
                TempData["Mensaje"] = "País eliminado.";
            }
            return RedirectToAction(nameof(Lista));
        }

        // ---------------- Actividad 5: reportes ----------------
        public async Task<IActionResult> Reportes()
        {
            // Reporte 1: cantidad de paises por region
            ViewBag.PorRegion = await _context.Paises
                .GroupBy(p => p.Region)
                .Select(g => new { Region = g.Key, Total = g.Count() })
                .OrderByDescending(x => x.Total)
                .ToListAsync();

            // Reporte 2: pais mas poblado
            ViewBag.MasPoblado = await _context.Paises
                .OrderByDescending(p => p.Poblacion)
                .FirstOrDefaultAsync();

            // Reporte 3: promedio poblacional por region
            ViewBag.Promedio = await _context.Paises
                .GroupBy(p => p.Region)
                .Select(g => new { Region = g.Key, Promedio = g.Average(p => (double)p.Poblacion) })
                .OrderByDescending(x => x.Promedio)
                .ToListAsync();

            return View();
        }
    }
}
