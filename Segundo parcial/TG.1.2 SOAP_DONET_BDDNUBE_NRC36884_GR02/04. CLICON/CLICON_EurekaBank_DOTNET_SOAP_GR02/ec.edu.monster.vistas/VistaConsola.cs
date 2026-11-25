using ec.edu.monster.modelos;
using Spectre.Console;
using System.Globalization;

namespace ec.edu.monster.vistas
{
    /// <summary>Todo lo que se dibuja en pantalla: encabezados, tablas, paneles y mensajes.</summary>
    public static class VistaConsola
    {
        private static readonly CultureInfo CULTURA = CultureInfo.InvariantCulture;
        private static readonly Color AZUL = new(29, 78, 216);
        private static readonly Color MARINO = new(15, 23, 42);
        private static readonly Color VERDE = new(5, 150, 105);
        private static readonly Color ROJO = new(220, 38, 38);
        private static readonly Color GRIS = new(100, 116, 139);

        public static void Encabezado(string subtitulo, RespuestaAutenticacion? sesion = null, string? servidor = null)
        {
            if (!Console.IsOutputRedirected)
            {
                AnsiConsole.Clear();
            }
            AnsiConsole.Write(new FigletText("EurekaBank").Color(AZUL));
            Grid datos = new Grid().Expand().AddColumn().AddColumn(new GridColumn().RightAligned());
            string derecha = sesion is null
                    ? $"[grey]{Markup.Escape(servidor ?? string.Empty)}[/]"
                    : $"[bold]{Markup.Escape(sesion.NombreEmpleado)}[/] [grey]· empleado {sesion.CodigoEmpleado}[/]";
            datos.AddRow(new Markup($"[grey]{Markup.Escape(subtitulo)}[/]"), new Markup(derecha));
            AnsiConsole.Write(datos);
            AnsiConsole.Write(new Rule().RuleStyle(new Style(GRIS)));
            AnsiConsole.WriteLine();
        }

        public static void Titulo(string texto)
        {
            AnsiConsole.Write(new Rule($"[bold]{Markup.Escape(texto)}[/]").LeftJustified().RuleStyle(new Style(AZUL)));
            AnsiConsole.WriteLine();
        }

        public static void Cuentas(IEnumerable<DatosCuenta> cuentas)
        {
            Table tabla = TablaBase()
                    .AddColumn("[bold]Cuenta[/]")
                    .AddColumn("[bold]Cliente[/]")
                    .AddColumn("[bold]Moneda[/]")
                    .AddColumn(new TableColumn("[bold]Saldo[/]").RightAligned())
                    .AddColumn(new TableColumn("[bold]Estado[/]").Centered());

            foreach (DatosCuenta c in cuentas)
            {
                string estado = c.EstaActiva ? "[green]● ACTIVO[/]" : $"[grey]○ {c.Estado}[/]";
                tabla.AddRow(new Markup($"[bold]{c.Codigo}[/]"), new Text(c.Cliente), new Text(c.Moneda),
                        new Markup($"[bold]{Dinero(c.SimboloMoneda, c.Saldo)}[/]"), new Markup(estado));
            }
            AnsiConsole.Write(tabla);
            AnsiConsole.WriteLine();
        }

        public static void Movimientos(RespuestaMovimientos datos)
        {
            string simbolo = datos.Moneda.StartsWith("Dol", StringComparison.OrdinalIgnoreCase) ? "US$" : "S/";
            int ingresos = datos.Movimientos.Count(m => m.EsIngreso);

            Grid resumen = new Grid().AddColumns(3);
            resumen.AddRow(
                    Tarjeta("Cuenta", datos.Cuenta, AZUL),
                    Tarjeta("Saldo disponible", Dinero(simbolo, datos.Saldo), VERDE),
                    Tarjeta("Movimientos", $"{datos.Movimientos.Count}  ({ingresos} ingresos)", MARINO));
            AnsiConsole.Write(resumen);

            Table tabla = TablaBase()
                    .AddColumn(new TableColumn("[bold]N°[/]").RightAligned())
                    .AddColumn("[bold]Fecha[/]")
                    .AddColumn("[bold]Tipo[/]")
                    .AddColumn("[bold]Referencia[/]")
                    .AddColumn(new TableColumn("[bold]Importe[/]").RightAligned());

            foreach (DatosMovimiento m in datos.Movimientos)
            {
                string color = m.EsIngreso ? "green" : "red";
                string signo = m.EsIngreso ? "+" : "-";
                tabla.AddRow(new Text(m.Numero.ToString(CULTURA)), new Text(m.Fecha),
                        new Markup($"[{color}]{(m.EsIngreso ? "▲" : "▼")}[/] {Markup.Escape(m.Descripcion)}"),
                        new Markup($"[grey]{m.CuentaReferencia ?? "-"}[/]"),
                        new Markup($"[{color}]{signo} {Dinero(simbolo, m.Importe)}[/]"));
            }
            AnsiConsole.Write(tabla);
        }

        public static void Exito(string titulo, RespuestaOperacion respuesta, string simbolo)
        {
            Panel panel = new Panel(new Rows(
                    new Markup($"[bold]{Markup.Escape(respuesta.Mensaje)}[/]"),
                    new Markup($"Cuenta [bold]{respuesta.Cuenta}[/]  ·  Nuevo saldo [bold green]{Dinero(simbolo, respuesta.Saldo)}[/]")))
                    .Header($" [green]✔[/] {Markup.Escape(titulo)} ")
                    .Border(BoxBorder.Rounded)
                    .BorderColor(VERDE)
                    .Padding(2, 1);
            AnsiConsole.Write(panel);
        }

        public static void Error(ExcepcionServicio error)
        {
            Panel panel = new Panel(new Markup($"{Markup.Escape(error.Message)}\n[grey]Codigo: {error.Codigo}[/]"))
                    .Header(" [red]✖[/] No se pudo completar la operacion ")
                    .Border(BoxBorder.Rounded)
                    .BorderColor(ROJO)
                    .Padding(2, 1);
            AnsiConsole.Write(panel);
        }

        public static void Informacion(string mensaje) => AnsiConsole.MarkupLine($"[grey]{Markup.Escape(mensaje)}[/]");

        public static void Pausa()
        {
            AnsiConsole.WriteLine();
            AnsiConsole.Markup("[grey]Presione cualquier tecla para continuar...[/]");
            Console.ReadKey(true);
        }

        public static string Dinero(string simbolo, decimal valor) => $"{simbolo} {valor.ToString("#,##0.00", CULTURA)}";

        private static Table TablaBase() => new Table().Border(TableBorder.Rounded).BorderColor(GRIS).Expand();

        private static Panel Tarjeta(string etiqueta, string valor, Color color) =>
                new Panel(new Rows(new Markup($"[grey]{etiqueta}[/]"), new Markup($"[bold]{Markup.Escape(valor)}[/]")))
                        .Border(BoxBorder.Rounded).BorderColor(color).Expand();
    }
}
