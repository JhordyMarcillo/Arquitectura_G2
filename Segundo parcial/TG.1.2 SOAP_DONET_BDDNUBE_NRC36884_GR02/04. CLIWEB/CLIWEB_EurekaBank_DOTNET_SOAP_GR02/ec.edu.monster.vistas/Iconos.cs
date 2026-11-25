using Microsoft.AspNetCore.Html;

namespace ec.edu.monster.vistas
{
    /// <summary>Iconos SVG de trazo (estilo Lucide) incluidos en la propia pagina, sin dependencias externas.</summary>
    public static class Iconos
    {
        private static readonly Dictionary<string, string> TRAZOS = new()
        {
            ["banco"] = "<path d='M3 21h18M5 21V10M19 21V10M9 21v-6M15 21v-6M2 10l10-6 10 6'/>",
            ["historial"] = "<path d='M3 12a9 9 0 1 0 3-6.7L3 8'/><path d='M3 3v5h5M12 7v5l3 2'/>",
            ["deposito"] = "<path d='M12 3v12M7 10l5 5 5-5M5 21h14'/>",
            ["retiro"] = "<path d='M12 21V9M7 14l5-5 5 5M5 3h14'/>",
            ["transferencia"] = "<path d='M7 7h13l-4-4M17 17H4l4 4'/>",
            ["salir"] = "<path d='M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9'/>",
            ["actualizar"] = "<path d='M21 12a9 9 0 1 1-3-6.7L21 8'/><path d='M21 3v5h-5'/>",
            ["buscar"] = "<circle cx='11' cy='11' r='7'/><path d='m21 21-4.3-4.3'/>",
            ["check"] = "<path d='M20 6 9 17l-5-5'/>",
            ["x"] = "<path d='M18 6 6 18M6 6l12 12'/>",
            ["info"] = "<circle cx='12' cy='12' r='10'/><path d='M12 16v-4M12 8h.01'/>",
            ["alerta"] = "<circle cx='12' cy='12' r='10'/><path d='M12 8v4M12 16h.01'/>",
            ["arriba"] = "<path d='M12 19V5M5 12l7-7 7 7'/>",
            ["abajo"] = "<path d='M12 5v14M19 12l-7 7-7-7'/>",
            ["recibo"] = "<path d='M4 2v20l3-2 3 2 3-2 3 2 3-2 1 1V2l-1 1-3-2-3 2-3-2-3 2-3-2Z'/><path d='M8 8h8M8 12h8M8 16h5'/>"
        };

        public static IHtmlContent Svg(string nombre) =>
                new HtmlString($"<svg class='icono' viewBox='0 0 24 24' aria-hidden='true'>{TRAZOS[nombre]}</svg>");
    }
}
