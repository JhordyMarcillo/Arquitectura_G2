using ec.edu.monster.modelos;
using ec.edu.monster.utilidades;

namespace ec.edu.monster.vistas
{
    public enum TipoOperacion
    {
        Deposito,
        Retiro,
        Transferencia
    }

    /// <summary>Deposito, retiro y transferencia con selector segmentado y comprobante.</summary>
    public partial class PaginaOperaciones : ContentPage
    {
        private TipoOperacion tipo = TipoOperacion.Deposito;

        public List<LineaComprobante> Detalle { get; private set; } = new();

        public PaginaOperaciones()
        {
            InitializeComponent();
            BindingContext = this;
            Seleccionar(TipoOperacion.Deposito);
        }

        private void Segmento_Clicked(object? sender, EventArgs e)
        {
            Seleccionar(sender == SegmentoRetiro ? TipoOperacion.Retiro
                    : sender == SegmentoTransferencia ? TipoOperacion.Transferencia
                    : TipoOperacion.Deposito);
        }

        private void Seleccionar(TipoOperacion nuevo)
        {
            tipo = nuevo;
            foreach ((Button boton, TipoOperacion valor) in new[] { (SegmentoDeposito, TipoOperacion.Deposito),
                    (SegmentoRetiro, TipoOperacion.Retiro), (SegmentoTransferencia, TipoOperacion.Transferencia) })
            {
                bool activo = valor == tipo;
                boton.BackgroundColor = activo ? Colors.White : Colors.Transparent;
                boton.TextColor = activo ? Color.FromArgb("#1D4ED8") : Color.FromArgb("#64748B");
            }

            TextoDescripcion.Text = tipo switch
            {
                TipoOperacion.Deposito => "Abono de efectivo a una cuenta activa.",
                TipoOperacion.Retiro => "Retiro validado con la clave y el saldo disponible.",
                _ => "Traspaso entre cuentas de la misma moneda."
            };
            EtiquetaCuenta.Text = tipo == TipoOperacion.Transferencia ? "CUENTA DE ORIGEN" : "CUENTA";
            BloqueClave.IsVisible = tipo != TipoOperacion.Deposito;
            BloqueDestino.IsVisible = tipo == TipoOperacion.Transferencia;
            BotonRegistrar.Text = "Registrar " + tipo.ToString().ToLowerInvariant();
            TextoNota.Text = tipo == TipoOperacion.Transferencia
                    ? "Cada cuenta paga el costo por movimiento: S/ 2.00 en soles y US$ 0.60 en dolares."
                    : "Se aplica el costo por movimiento: S/ 2.00 en soles y US$ 0.60 en dolares.";
            PanelComprobante.IsVisible = false;
        }

        private async void BotonRegistrar_Clicked(object? sender, EventArgs e)
        {
            string? error = Validar(out decimal importe);
            if (error is not null)
            {
                MostrarComprobante(false, "Revise los datos", error, new List<LineaComprobante>());
                return;
            }

            BotonRegistrar.IsEnabled = false;
            try
            {
                string cuenta = CampoCuenta.Text.Trim();
                RespuestaOperacion respuesta = tipo switch
                {
                    TipoOperacion.Deposito => await App.Servicio.DepositarAsync(cuenta, importe),
                    TipoOperacion.Retiro => await App.Servicio.RetirarAsync(cuenta, CampoClave.Text, importe),
                    _ => await App.Servicio.TransferirAsync(cuenta, CampoClave.Text, CampoDestino.Text.Trim(), importe)
                };
                string moneda = (await App.Servicio.ConsultarMovimientosAsync(respuesta.Cuenta)).Moneda;

                List<LineaComprobante> lineas = new()
                {
                    new() { Etiqueta = "Operacion", Valor = tipo.ToString().ToUpperInvariant() },
                    new() { Etiqueta = tipo == TipoOperacion.Transferencia ? "Origen" : "Cuenta", Valor = respuesta.Cuenta }
                };
                if (tipo == TipoOperacion.Transferencia)
                {
                    lineas.Add(new() { Etiqueta = "Destino", Valor = CampoDestino.Text.Trim() });
                }
                lineas.Add(new() { Etiqueta = "Importe", Valor = Formato.Dinero(moneda, importe) });
                lineas.Add(new() { Etiqueta = "Nuevo saldo", Valor = Formato.Dinero(moneda, respuesta.Saldo), Destacado = true });
                lineas.Add(new() { Etiqueta = "Fecha y hora", Valor = DateTime.Now.ToString("yyyy-MM-dd HH:mm") });

                MostrarComprobante(true, "Operacion exitosa", respuesta.Mensaje, lineas);
                CampoImporte.Text = string.Empty;
                CampoClave.Text = string.Empty;
            }
            catch (ExcepcionServicio ex)
            {
                if (ex.RequiereLogin)
                {
                    App.VolverAlLogin(ex.Message);
                    return;
                }
                MostrarComprobante(false, "Operacion rechazada", ex.Message,
                        new List<LineaComprobante> { new() { Etiqueta = "Codigo", Valor = ex.Codigo } });
            }
            finally
            {
                BotonRegistrar.IsEnabled = true;
            }
        }

        private string? Validar(out decimal importe)
        {
            importe = 0;
            if (!EsCuenta(CampoCuenta.Text))
            {
                return "La cuenta debe tener 8 digitos.";
            }
            if (tipo != TipoOperacion.Deposito && string.IsNullOrEmpty(CampoClave.Text))
            {
                return "Ingrese la clave de la cuenta.";
            }
            if (tipo == TipoOperacion.Transferencia && !EsCuenta(CampoDestino.Text))
            {
                return "La cuenta de destino debe tener 8 digitos.";
            }
            return Formato.IntentarLeerImporte(CampoImporte.Text ?? string.Empty, out importe)
                    ? null
                    : "Ingrese un importe mayor a cero (ej. 150.50).";
        }

        private static bool EsCuenta(string? texto) => texto is not null && texto.Trim().Length == 8 && texto.Trim().All(char.IsDigit);

        private async void MostrarComprobante(bool exito, string titulo, string mensaje, List<LineaComprobante> lineas)
        {
            IconoResultado.Source = exito ? "icono_exito.png" : "icono_error.png";
            TextoResultado.Text = titulo;
            TextoMensaje.Text = mensaje;
            Detalle = lineas;
            OnPropertyChanged(nameof(Detalle));
            PanelComprobante.IsVisible = true;

            // Lleva la vista al comprobante
            if (Content is ScrollView desplazamiento)
            {
                await desplazamiento.ScrollToAsync(PanelComprobante, ScrollToPosition.Start, true);
            }
        }
    }
}
