using ec.edu.monster.modelos;
using ec.edu.monster.utilidades;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;

namespace ec.edu.monster.vistas
{
    public partial class PaginaCuentas : UserControl
    {
        /// <summary>Cuenta preparada para mostrarse en la tabla.</summary>
        public record FilaCuenta(string Codigo, string Cliente, string Moneda, string SaldoFormateado, string Estado, bool EstaActiva);

        private readonly VentanaPrincipal ventana;

        public PaginaCuentas(VentanaPrincipal ventana)
        {
            InitializeComponent();
            this.ventana = ventana;
            Loaded += async (_, _) => await CargarAsync();
        }

        private async Task CargarAsync()
        {
            try
            {
                List<DatosCuenta> cuentas = (await App.Servicio.ListarCuentasAsync()).Cuentas;

                TablaCuentas.ItemsSource = cuentas.Select(c => new FilaCuenta(c.Codigo, c.Cliente, c.Moneda,
                        Formato.Dinero(c.Moneda, c.Saldo), c.Estado, c.EstaActiva)).ToList();

                List<DatosCuenta> activas = cuentas.Where(c => c.EstaActiva).ToList();
                TextoActivas.Text = activas.Count.ToString();
                TextoTotalCuentas.Text = $"de {cuentas.Count} cuentas registradas";
                TextoSaldoSoles.Text = Formato.Dinero("Soles", activas.Where(c => c.SimboloMoneda == "S/").Sum(c => c.Saldo));
                TextoSaldoDolares.Text = Formato.Dinero("Dolares", activas.Where(c => c.SimboloMoneda == "US$").Sum(c => c.Saldo));
            }
            catch (ExcepcionServicio e)
            {
                ventana.ManejarError(e);
            }
        }

        private async void BotonActualizar_Click(object sender, RoutedEventArgs e)
        {
            await CargarAsync();
            ventana.Notificar("Cuentas actualizadas");
        }

        private void TablaCuentas_MouseDoubleClick(object sender, MouseButtonEventArgs e)
        {
            if (TablaCuentas.SelectedItem is FilaCuenta fila)
            {
                ventana.MostrarMovimientos(fila.Codigo);
            }
        }
    }
}
