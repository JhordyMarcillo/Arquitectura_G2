using ec.edu.monster.modelos;
using ec.edu.monster.utilidades;
using System.Windows;
using System.Windows.Controls;

namespace ec.edu.monster.vistas
{
    public partial class PaginaMovimientos : UserControl
    {
        /// <summary>Movimiento preparado para mostrarse en la tabla.</summary>
        public record FilaMovimiento(int Numero, string Fecha, string Descripcion, string Referencia, string ImporteFormateado, bool EsIngreso);

        private readonly VentanaPrincipal ventana;

        public PaginaMovimientos(VentanaPrincipal ventana, string? cuenta)
        {
            InitializeComponent();
            this.ventana = ventana;
            Loaded += async (_, _) =>
            {
                CampoCuenta.Focus();
                if (cuenta is not null)
                {
                    CampoCuenta.Text = cuenta;
                    await ConsultarAsync();
                }
            };
        }

        private async void BotonConsultar_Click(object sender, RoutedEventArgs e) => await ConsultarAsync();

        private async Task ConsultarAsync()
        {
            string cuenta = CampoCuenta.Text.Trim();
            if (cuenta.Length != 8 || !cuenta.All(char.IsDigit))
            {
                ventana.Notificar("La cuenta debe tener 8 digitos.", esError: true);
                return;
            }

            try
            {
                RespuestaMovimientos datos = await App.Servicio.ConsultarMovimientosAsync(cuenta);
                string moneda = datos.Moneda;

                TextoCuenta.Text = $"Cuenta {datos.Cuenta}  ·  {moneda}";
                TextoSaldo.Text = Formato.Dinero(moneda, datos.Saldo);

                List<DatosMovimiento> ingresos = datos.Movimientos.Where(m => m.EsIngreso).ToList();
                List<DatosMovimiento> salidas = datos.Movimientos.Where(m => !m.EsIngreso).ToList();
                TextoIngresos.Text = Formato.Dinero(moneda, ingresos.Sum(m => m.Importe));
                TextoCantidadIngresos.Text = $"{ingresos.Count} movimientos";
                TextoSalidas.Text = Formato.Dinero(moneda, salidas.Sum(m => m.Importe));
                TextoCantidadSalidas.Text = $"{salidas.Count} movimientos";

                // Lo mas reciente primero
                TablaMovimientos.ItemsSource = datos.Movimientos.OrderByDescending(m => m.Numero)
                        .Select(m => new FilaMovimiento(m.Numero, m.Fecha, m.Descripcion, m.CuentaReferencia ?? "-",
                                (m.EsIngreso ? "+ " : "- ") + Formato.Dinero(moneda, m.Importe), m.EsIngreso))
                        .ToList();

                PanelVacio.Visibility = Visibility.Collapsed;
                PanelResultado.Visibility = Visibility.Visible;
            }
            catch (ExcepcionServicio e)
            {
                ventana.ManejarError(e);
            }
        }
    }
}
