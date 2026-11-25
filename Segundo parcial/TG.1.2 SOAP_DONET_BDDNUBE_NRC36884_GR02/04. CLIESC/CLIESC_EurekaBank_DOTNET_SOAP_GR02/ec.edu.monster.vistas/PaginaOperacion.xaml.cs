using ec.edu.monster.modelos;
using ec.edu.monster.utilidades;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;

namespace ec.edu.monster.vistas
{
    /// <summary>Formulario y comprobante de deposito, retiro o transferencia.</summary>
    public partial class PaginaOperacion : UserControl
    {
        private readonly VentanaPrincipal ventana;
        private readonly TipoOperacion tipo;

        public PaginaOperacion(VentanaPrincipal ventana, TipoOperacion tipo)
        {
            InitializeComponent();
            this.ventana = ventana;
            this.tipo = tipo;

            (string icono, string titulo, string descripcion, string boton) = tipo switch
            {
                TipoOperacion.Deposito => ("", "Registrar deposito", "El importe se abona a la cuenta.", "Registrar deposito"),
                TipoOperacion.Retiro => ("", "Registrar retiro", "Se valida la clave y el saldo disponible.", "Registrar retiro"),
                _ => ("", "Registrar transferencia", "Ambas cuentas deben ser de la misma moneda.", "Registrar transferencia")
            };
            IconoOperacion.Text = icono;
            TextoTituloFormulario.Text = titulo;
            TextoDescripcion.Text = descripcion;
            BotonRegistrar.Content = boton;
            EtiquetaCuenta.Text = tipo == TipoOperacion.Transferencia ? "CUENTA DE ORIGEN" : "CUENTA";
            BloqueClave.Visibility = tipo == TipoOperacion.Deposito ? Visibility.Collapsed : Visibility.Visible;
            BloqueDestino.Visibility = tipo == TipoOperacion.Transferencia ? Visibility.Visible : Visibility.Collapsed;
            TextoNota.Text = tipo == TipoOperacion.Transferencia
                    ? "Cada cuenta paga el costo por movimiento: S/ 2.00 en soles y US$ 0.60 en dolares."
                    : "Se aplica el costo por movimiento: S/ 2.00 en cuentas en soles y US$ 0.60 en dolares.";

            Loaded += (_, _) => CampoCuenta.Focus();
        }

        private async void BotonRegistrar_Click(object sender, RoutedEventArgs e)
        {
            string? error = Validar(out decimal importe);
            if (error is not null)
            {
                ventana.Notificar(error, esError: true);
                return;
            }

            BotonRegistrar.IsEnabled = false;
            try
            {
                string cuenta = CampoCuenta.Text.Trim();
                RespuestaOperacion respuesta = tipo switch
                {
                    TipoOperacion.Deposito => await App.Servicio.DepositarAsync(cuenta, importe),
                    TipoOperacion.Retiro => await App.Servicio.RetirarAsync(cuenta, CampoClave.Password, importe),
                    _ => await App.Servicio.TransferirAsync(cuenta, CampoClave.Password, CampoDestino.Text.Trim(), importe)
                };

                string moneda = await MonedaDeAsync(respuesta.Cuenta);
                MostrarExito(respuesta, moneda, importe);
                ventana.Notificar(respuesta.Mensaje);
                CampoImporte.Clear();
                CampoClave.Clear();
            }
            catch (ExcepcionServicio ex)
            {
                if (ex.RequiereLogin)
                {
                    ventana.ManejarError(ex);
                    return;
                }
                MostrarError(ex);
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
            if (tipo != TipoOperacion.Deposito && string.IsNullOrEmpty(CampoClave.Password))
            {
                return "Ingrese la clave de la cuenta.";
            }
            if (tipo == TipoOperacion.Transferencia && !EsCuenta(CampoDestino.Text))
            {
                return "La cuenta de destino debe tener 8 digitos.";
            }
            if (!Formato.IntentarLeerImporte(CampoImporte.Text, out importe))
            {
                return "Ingrese un importe mayor a cero (ej. 150.50).";
            }
            return null;
        }

        private static bool EsCuenta(string texto) => texto.Trim().Length == 8 && texto.Trim().All(char.IsDigit);

        private static async Task<string> MonedaDeAsync(string cuenta)
        {
            try
            {
                return (await App.Servicio.ConsultarMovimientosAsync(cuenta)).Moneda;
            }
            catch (ExcepcionServicio)
            {
                return "Soles";
            }
        }

        // ------------------------------------------------------------------ comprobante

        private void MostrarExito(RespuestaOperacion respuesta, string moneda, decimal importe)
        {
            PrepararComprobante("", (Brush)FindResource("Verde"), "Operacion exitosa", respuesta.Mensaje);
            AgregarDetalle("Operacion", TextoTituloFormulario.Text.Replace("Registrar ", "").ToUpperInvariant());
            AgregarDetalle(tipo == TipoOperacion.Transferencia ? "Cuenta de origen" : "Cuenta", respuesta.Cuenta);
            if (tipo == TipoOperacion.Transferencia)
            {
                AgregarDetalle("Cuenta de destino", CampoDestino.Text.Trim());
            }
            AgregarDetalle("Importe", Formato.Dinero(moneda, importe));
            AgregarDetalle("Nuevo saldo", Formato.Dinero(moneda, respuesta.Saldo), destacado: true);
            AgregarDetalle("Empleado", App.Servicio.Sesion?.NombreEmpleado ?? "-");
            AgregarDetalle("Fecha y hora", DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss"));
        }

        private void MostrarError(ExcepcionServicio error)
        {
            PrepararComprobante("", (Brush)FindResource("Rojo"), "Operacion rechazada", error.Message);
            AgregarDetalle("Codigo", error.Codigo);
        }

        private void PrepararComprobante(string icono, Brush color, string titulo, string mensaje)
        {
            PanelEspera.Visibility = Visibility.Collapsed;
            PanelComprobante.Visibility = Visibility.Visible;
            CirculoResultado.Background = color;
            IconoResultado.Text = icono;
            TextoResultado.Text = titulo;
            TextoMensaje.Text = mensaje;
            TablaDetalle.Children.Clear();
            TablaDetalle.RowDefinitions.Clear();
        }

        private void AgregarDetalle(string etiqueta, string valor, bool destacado = false)
        {
            int fila = TablaDetalle.RowDefinitions.Count;
            TablaDetalle.RowDefinitions.Add(new RowDefinition { Height = GridLength.Auto });

            TextBlock textoEtiqueta = new() { Text = etiqueta, Foreground = (Brush)FindResource("TextoSecundario"), Margin = new Thickness(0, 6, 0, 6) };
            TextBlock textoValor = new()
            {
                Text = valor,
                FontWeight = FontWeights.SemiBold,
                FontSize = destacado ? 18 : 13,
                Foreground = destacado ? (Brush)FindResource("Primario") : (Brush)FindResource("Texto"),
                Margin = new Thickness(0, 6, 0, 6)
            };
            Grid.SetRow(textoEtiqueta, fila);
            Grid.SetRow(textoValor, fila);
            Grid.SetColumn(textoValor, 1);
            TablaDetalle.Children.Add(textoEtiqueta);
            TablaDetalle.Children.Add(textoValor);
        }
    }
}
