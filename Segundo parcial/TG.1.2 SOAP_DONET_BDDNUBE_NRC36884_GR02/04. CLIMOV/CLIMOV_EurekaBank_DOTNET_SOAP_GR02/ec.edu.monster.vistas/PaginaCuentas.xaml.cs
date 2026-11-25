using ec.edu.monster.modelos;
using ec.edu.monster.utilidades;

namespace ec.edu.monster.vistas
{
    public partial class PaginaCuentas : ContentPage
    {
        public PaginaCuentas()
        {
            InitializeComponent();
            TextoEmpleado.Text = App.Servicio.Sesion?.NombreEmpleado;
        }

        protected override async void OnAppearing()
        {
            base.OnAppearing();
            await CargarAsync();
        }

        private async Task CargarAsync()
        {
            try
            {
                List<DatosCuenta> cuentas = (await App.Servicio.ListarCuentasAsync()).Cuentas;
                List<DatosCuenta> activas = cuentas.Where(c => c.EstaActiva).ToList();

                TextoSoles.Text = Formato.Dinero("Soles", activas.Where(c => c.SimboloMoneda == "S/").Sum(c => c.Saldo));
                TextoDolares.Text = Formato.Dinero("Dolares", activas.Where(c => c.SimboloMoneda == "US$").Sum(c => c.Saldo));
                TextoActivas.Text = $"{activas.Count} de {cuentas.Count} activas";
                ListaCuentas.ItemsSource = cuentas.Select(c => new FilaCuenta
                {
                    Codigo = c.Codigo,
                    Cliente = c.Cliente,
                    Moneda = c.Moneda,
                    Saldo = Formato.Dinero(c.Moneda, c.Saldo),
                    Estado = c.Estado,
                    EstaActiva = c.EstaActiva
                }).ToList();
                PanelError.IsVisible = false;
            }
            catch (ExcepcionServicio e)
            {
                if (e.RequiereLogin)
                {
                    App.VolverAlLogin(e.Message);
                    return;
                }
                TextoError.Text = e.Message;
                PanelError.IsVisible = true;
            }
            finally
            {
                Refrescar.IsRefreshing = false;
            }
        }

        private async void Refrescar_Refreshing(object? sender, EventArgs e) => await CargarAsync();

        private async void ListaCuentas_SelectionChanged(object? sender, SelectionChangedEventArgs e)
        {
            if (e.CurrentSelection.FirstOrDefault() is FilaCuenta fila)
            {
                ListaCuentas.SelectedItem = null;
                await Shell.Current.GoToAsync($"//movimientos?cuenta={fila.Codigo}");
            }
        }

        private async void BotonSalir_Clicked(object? sender, EventArgs e)
        {
            if (await DisplayAlertAsync("Cerrar sesion", "¿Desea salir de EurekaBank?", "Salir", "Cancelar"))
            {
                App.VolverAlLogin();
            }
        }
    }
}
