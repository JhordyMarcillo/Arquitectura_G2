using ec.edu.monster.modelos;
using ec.edu.monster.servicios;
using ec.edu.monster.vistas;
using Spectre.Console;
using System.Globalization;

namespace ec.edu.monster.controladores
{
    /// <summary>Flujo del cliente de consola: conexion, inicio de sesion y menu de operaciones.</summary>
    public class ControladorConsola
    {
        private const string OPCION_CUENTAS = "Ver cuentas";
        private const string OPCION_MOVIMIENTOS = "Consultar movimientos";
        private const string OPCION_DEPOSITO = "Registrar deposito";
        private const string OPCION_RETIRO = "Registrar retiro";
        private const string OPCION_TRANSFERENCIA = "Registrar transferencia";
        private const string OPCION_CERRAR_SESION = "Cerrar sesion";
        private const string OPCION_SALIR = "Salir";

        private readonly IServicioEurekaBank servicio;
        private readonly string subtitulo;

        public ControladorConsola(IServicioEurekaBank servicio, string subtitulo)
        {
            this.servicio = servicio;
            this.subtitulo = subtitulo;
        }

        public async Task EjecutarAsync()
        {
            while (true)
            {
                if (!await IniciarSesionAsync())
                {
                    return;
                }
                if (!await MenuPrincipalAsync())
                {
                    return;
                }
            }
        }

        // ------------------------------------------------------------------ inicio de sesion

        private async Task<bool> IniciarSesionAsync()
        {
            while (true)
            {
                VistaConsola.Encabezado(subtitulo, servidor: servicio.UrlServidor);
                VistaConsola.Titulo("Inicio de sesion");

                servicio.UrlServidor = AnsiConsole.Prompt(new TextPrompt<string>("[grey]Servidor[/]")
                        .DefaultValue(servicio.UrlServidor).ShowDefaultValue(true));
                string usuario = AnsiConsole.Prompt(new TextPrompt<string>("Usuario"));
                string clave = AnsiConsole.Prompt(new TextPrompt<string>("Clave").Secret());

                try
                {
                    RespuestaAutenticacion sesion = await ConEsperaAsync("Verificando credenciales...",
                            () => servicio.IniciarSesionAsync(usuario, clave));
                    AnsiConsole.MarkupLine($"\n[green]✔[/] Bienvenido, [bold]{Markup.Escape(sesion.NombreEmpleado)}[/]");
                    await Task.Delay(700);
                    return true;
                }
                catch (ExcepcionServicio e)
                {
                    AnsiConsole.WriteLine();
                    VistaConsola.Error(e);
                    if (!AnsiConsole.Confirm("¿Intentar nuevamente?"))
                    {
                        return false;
                    }
                }
            }
        }

        // ------------------------------------------------------------------ menu

        /// <summary>Devuelve false si el usuario elige salir de la aplicacion.</summary>
        private async Task<bool> MenuPrincipalAsync()
        {
            while (servicio.Sesion is not null)
            {
                VistaConsola.Encabezado(subtitulo, servicio.Sesion);
                string opcion = AnsiConsole.Prompt(new SelectionPrompt<string>()
                        .Title("[bold]¿Que desea hacer?[/]")
                        .HighlightStyle(new Style(Color.White, new Color(29, 78, 216)))
                        .AddChoiceGroup("Consultas", OPCION_CUENTAS, OPCION_MOVIMIENTOS)
                        .AddChoiceGroup("Operaciones", OPCION_DEPOSITO, OPCION_RETIRO, OPCION_TRANSFERENCIA)
                        .AddChoiceGroup("Sesion", OPCION_CERRAR_SESION, OPCION_SALIR));

                if (opcion == OPCION_SALIR)
                {
                    return false;
                }
                if (opcion == OPCION_CERRAR_SESION)
                {
                    servicio.CerrarSesion();
                    return true;
                }

                VistaConsola.Encabezado(subtitulo, servicio.Sesion);
                VistaConsola.Titulo(opcion);
                try
                {
                    await (opcion switch
                    {
                        OPCION_CUENTAS => MostrarCuentasAsync(),
                        OPCION_MOVIMIENTOS => MostrarMovimientosAsync(),
                        OPCION_DEPOSITO => DepositarAsync(),
                        OPCION_RETIRO => RetirarAsync(),
                        _ => TransferirAsync()
                    });
                }
                catch (ExcepcionServicio e)
                {
                    VistaConsola.Error(e);
                    if (e.RequiereLogin)
                    {
                        VistaConsola.Informacion("Debe iniciar sesion nuevamente.");
                    }
                }
                VistaConsola.Pausa();
            }
            return true;
        }

        // ------------------------------------------------------------------ opciones

        private async Task MostrarCuentasAsync()
        {
            RespuestaCuentas respuesta = await ConEsperaAsync("Consultando cuentas...", servicio.ListarCuentasAsync);
            VistaConsola.Cuentas(respuesta.Cuentas);
        }

        private async Task MostrarMovimientosAsync()
        {
            string cuenta = PedirCuenta("Numero de cuenta");
            RespuestaMovimientos respuesta = await ConEsperaAsync("Consultando movimientos...",
                    () => servicio.ConsultarMovimientosAsync(cuenta));
            VistaConsola.Movimientos(respuesta);
        }

        private async Task DepositarAsync()
        {
            string cuenta = PedirCuenta("Cuenta a depositar");
            decimal importe = PedirImporte();
            if (!Confirmar($"Depositar {importe.ToString("0.00", CultureInfo.InvariantCulture)} en la cuenta {cuenta}"))
            {
                return;
            }
            RespuestaOperacion respuesta = await ConEsperaAsync("Registrando deposito...",
                    () => servicio.DepositarAsync(cuenta, importe));
            VistaConsola.Exito("Deposito registrado", respuesta, await SimboloAsync(cuenta));
        }

        private async Task RetirarAsync()
        {
            string cuenta = PedirCuenta("Cuenta a retirar");
            string clave = AnsiConsole.Prompt(new TextPrompt<string>("Clave de la cuenta").Secret());
            decimal importe = PedirImporte();
            if (!Confirmar($"Retirar {importe.ToString("0.00", CultureInfo.InvariantCulture)} de la cuenta {cuenta}"))
            {
                return;
            }
            RespuestaOperacion respuesta = await ConEsperaAsync("Registrando retiro...",
                    () => servicio.RetirarAsync(cuenta, clave, importe));
            VistaConsola.Exito("Retiro registrado", respuesta, await SimboloAsync(cuenta));
        }

        private async Task TransferirAsync()
        {
            string origen = PedirCuenta("Cuenta de origen");
            string clave = AnsiConsole.Prompt(new TextPrompt<string>("Clave de la cuenta de origen").Secret());
            string destino = PedirCuenta("Cuenta de destino");
            decimal importe = PedirImporte();
            if (!Confirmar($"Transferir {importe.ToString("0.00", CultureInfo.InvariantCulture)} de {origen} a {destino}"))
            {
                return;
            }
            RespuestaOperacion respuesta = await ConEsperaAsync("Registrando transferencia...",
                    () => servicio.TransferirAsync(origen, clave, destino, importe));
            VistaConsola.Exito("Transferencia registrada", respuesta, await SimboloAsync(origen));
        }

        // ------------------------------------------------------------------ utilidades

        private static string PedirCuenta(string etiqueta) =>
                AnsiConsole.Prompt(new TextPrompt<string>(etiqueta)
                        .Validate(c => c.Trim().Length == 8 && c.Trim().All(char.IsDigit)
                                ? ValidationResult.Success()
                                : ValidationResult.Error("[red]La cuenta tiene 8 digitos[/]"))).Trim();

        private static decimal PedirImporte()
        {
            string texto = AnsiConsole.Prompt(new TextPrompt<string>("Importe")
                    .Validate(t => decimal.TryParse(t.Replace(',', '.'), NumberStyles.Number, CultureInfo.InvariantCulture, out decimal v) && v > 0
                            ? ValidationResult.Success()
                            : ValidationResult.Error("[red]Ingrese un importe mayor a cero (ej. 150.50)[/]")));
            return decimal.Parse(texto.Replace(',', '.'), CultureInfo.InvariantCulture);
        }

        private static bool Confirmar(string accion)
        {
            AnsiConsole.WriteLine();
            return AnsiConsole.Confirm($"¿Confirma? [bold]{Markup.Escape(accion)}[/]");
        }

        /// <summary>Simbolo de la moneda de la cuenta, para mostrar el nuevo saldo.</summary>
        private async Task<string> SimboloAsync(string cuenta)
        {
            try
            {
                RespuestaCuentas cuentas = await servicio.ListarCuentasAsync();
                return cuentas.Cuentas.FirstOrDefault(c => c.Codigo == cuenta)?.SimboloMoneda ?? "S/";
            }
            catch (ExcepcionServicio)
            {
                return "S/";
            }
        }

        private static Task<T> ConEsperaAsync<T>(string mensaje, Func<Task<T>> accion) =>
                AnsiConsole.Status().Spinner(Spinner.Known.Dots).SpinnerStyle(new Style(new Color(29, 78, 216)))
                        .StartAsync(mensaje, _ => accion());
    }
}
