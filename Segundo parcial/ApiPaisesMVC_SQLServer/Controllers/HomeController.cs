using System.Diagnostics;
using Microsoft.AspNetCore.Mvc;
using ApiPaisesMVC_SQLServer.Models;

namespace ApiPaisesMVC_SQLServer.Controllers;

// Solo maneja la pagina de error; la pagina principal es Pais/Index.
public class HomeController : Controller
{
    [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
    public IActionResult Error()
    {
        return View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
    }
}
