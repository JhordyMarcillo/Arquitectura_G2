using ec.edu.monster.modelos;

namespace ec.edu.monster.vistas
{
    public partial class PaginaLogin : ContentPage
    {
        public PaginaLogin(string? aviso = null)
        {
            InitializeComponent();
            CampoServidor.Text = App.Servicio.UrlServidor;
            TextoTecnologia.Text = App.SUBTITULO;
            if (aviso is not null)
            {
                TextoAviso.Text = aviso;
                PanelAviso.IsVisible = true;
            }
        }

        private async void BotonIngresar_Clicked(object? sender, EventArgs e)
        {
            if (string.IsNullOrWhiteSpace(CampoUsuario.Text) || string.IsNullOrEmpty(CampoClave.Text))
            {
                MostrarError("Ingrese el usuario y la clave.");
                return;
            }

            EstablecerOcupado(true);
            try
            {
                App.Servicio.UrlServidor = CampoServidor.Text ?? string.Empty;
                await App.Servicio.IniciarSesionAsync(CampoUsuario.Text.Trim(), CampoClave.Text);
                App.GuardarUrlServidor();
                App.MostrarPrincipal();
            }
            catch (ExcepcionServicio ex)
            {
                MostrarError(ex.Message);
                CampoClave.Text = string.Empty;
            }
            finally
            {
                EstablecerOcupado(false);
            }
        }

        private void MostrarError(string mensaje)
        {
            TextoError.Text = mensaje;
            PanelError.IsVisible = true;
        }

        private void EstablecerOcupado(bool ocupado)
        {
            BotonIngresar.IsEnabled = !ocupado;
            BotonIngresar.Text = ocupado ? "Verificando..." : "Ingresar";
            Cargando.IsRunning = Cargando.IsVisible = ocupado;
            if (ocupado)
            {
                PanelError.IsVisible = false;
                PanelAviso.IsVisible = false;
            }
        }
    }
}
