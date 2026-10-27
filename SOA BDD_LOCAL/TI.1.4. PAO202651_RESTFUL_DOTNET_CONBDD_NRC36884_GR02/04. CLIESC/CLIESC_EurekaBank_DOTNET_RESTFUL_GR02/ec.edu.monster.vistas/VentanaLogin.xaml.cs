using ec.edu.monster.modelos;
using ec.edu.monster.utilidades;
using System.Windows;
using System.Windows.Input;

namespace ec.edu.monster.vistas
{
    public partial class VentanaLogin : Window
    {
        public VentanaLogin()
        {
            InitializeComponent();
            CampoServidor.Text = App.Servicio.UrlServidor;
            Loaded += (_, _) => CampoUsuario.Focus();
        }

        private async void BotonIngresar_Click(object sender, RoutedEventArgs e)
        {
            if (string.IsNullOrWhiteSpace(CampoUsuario.Text) || string.IsNullOrEmpty(CampoClave.Password))
            {
                MostrarError("Ingrese el usuario y la clave.");
                return;
            }

            EstablecerOcupado(true);
            try
            {
                App.Servicio.UrlServidor = CampoServidor.Text;
                await App.Servicio.IniciarSesionAsync(CampoUsuario.Text.Trim(), CampoClave.Password);
                Preferencias.GuardarUrlServidor(App.Servicio.UrlServidor);

                new VentanaPrincipal().Show();
                Close();
            }
            catch (ExcepcionServicio ex)
            {
                MostrarError(ex.Message);
                CampoClave.Clear();
                CampoClave.Focus();
            }
            finally
            {
                EstablecerOcupado(false);
            }
        }

        private void MostrarError(string mensaje)
        {
            TextoError.Text = mensaje;
            PanelError.Visibility = Visibility.Visible;
        }

        private void EstablecerOcupado(bool ocupado)
        {
            BotonIngresar.IsEnabled = !ocupado;
            BotonIngresar.Content = ocupado ? "Verificando..." : "Ingresar";
            Cursor = ocupado ? Cursors.Wait : null;
            if (ocupado)
            {
                PanelError.Visibility = Visibility.Collapsed;
            }
        }
    }
}
