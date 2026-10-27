using ec.edu.monster.modelos;
using ec.edu.monster.utilidades;

namespace ec.edu.monster.vistas
{
    /// <summary>Recibe la cuenta desde la pagina de cuentas: //movimientos?cuenta=00100001</summary>
    [QueryProperty(nameof(Cuenta), "cuenta")]
    public partial class PaginaMovimientos : ContentPage
    {
        public PaginaMovimientos()
        {
            InitializeComponent();
        }

        public string? Cuenta
        {
            set
            {
                if (!string.IsNullOrWhiteSpace(value))
                {
                    CampoCuenta.Text = value;
                    _ = ConsultarAsync();
                }
            }
        }

        private async void BotonConsultar_Clicked(object? sender, EventArgs e) => await ConsultarAsync();

        private async Task ConsultarAsync()
        {
            string cuenta = CampoCuenta.Text?.Trim() ?? string.Empty;
            if (cuenta.Length != 8 || !cuenta.All(char.IsDigit))
            {
                MostrarError("La cuenta debe tener 8 digitos.");
                return;
            }
            CampoCuenta.Unfocus();

            try
            {
                RespuestaMovimientos datos = await App.Servicio.ConsultarMovimientosAsync(cuenta);
                string moneda = datos.Moneda;

                TextoCuenta.Text = $"Cuenta {datos.Cuenta}  ·  {moneda}";
                TextoSaldo.Text = Formato.Dinero(moneda, datos.Saldo);
                TextoIngresos.Text = Formato.Dinero(moneda, datos.Movimientos.Where(m => m.EsIngreso).Sum(m => m.Importe));
                TextoSalidas.Text = Formato.Dinero(moneda, datos.Movimientos.Where(m => !m.EsIngreso).Sum(m => m.Importe));
                ListaMovimientos.ItemsSource = datos.Movimientos.OrderByDescending(m => m.Numero).Select(m => new FilaMovimiento
                {
                    Descripcion = m.Descripcion,
                    Detalle = m.CuentaReferencia is null ? $"N° {m.Numero}  ·  {m.Fecha}" : $"N° {m.Numero}  ·  {m.Fecha}  ·  {m.CuentaReferencia}",
                    Importe = (m.EsIngreso ? "+ " : "- ") + Formato.Dinero(moneda, m.Importe),
                    EsIngreso = m.EsIngreso
                }).ToList();

                PanelError.IsVisible = false;
                PanelResumen.IsVisible = true;
            }
            catch (ExcepcionServicio e)
            {
                if (e.RequiereLogin)
                {
                    App.VolverAlLogin(e.Message);
                    return;
                }
                MostrarError(e.Message);
            }
        }

        private void MostrarError(string mensaje)
        {
            TextoError.Text = mensaje;
            PanelError.IsVisible = true;
            PanelResumen.IsVisible = false;
            ListaMovimientos.ItemsSource = null;
        }
    }
}
