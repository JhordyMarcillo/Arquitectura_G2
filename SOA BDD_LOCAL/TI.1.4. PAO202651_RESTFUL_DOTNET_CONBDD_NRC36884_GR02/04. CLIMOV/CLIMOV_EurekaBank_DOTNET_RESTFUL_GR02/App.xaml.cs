using ec.edu.monster.servicios;
using ec.edu.monster.vistas;

namespace ec.edu.monster
{
    public partial class App : Application
    {
        /// <summary>Servicio compartido por todas las pantallas (una sola sesion).</summary>
        public static IServicioEurekaBank Servicio { get; } = new ServicioEurekaBankRest();

        public const string SUBTITULO = "RESTful .NET  ·  GR02";
        private const string CLAVE_URL = "UrlServidor";

        public App()
        {
            InitializeComponent();
            UserAppTheme = AppTheme.Light;
            Servicio.UrlServidor = Preferences.Default.Get(CLAVE_URL, UrlPorDefecto());
        }

        protected override Window CreateWindow(IActivationState? activationState) =>
                new(new PaginaLogin()) { Title = "EurekaBank" };

        public static void GuardarUrlServidor() => Preferences.Default.Set(CLAVE_URL, Servicio.UrlServidor);

        /// <summary>Pasa a las pestanas principales despues de iniciar sesion.</summary>
        public static void MostrarPrincipal() => Current!.Windows[0].Page = new AppShell();

        public static void VolverAlLogin(string? aviso = null)
        {
            Servicio.CerrarSesion();
            Current!.Windows[0].Page = new PaginaLogin(aviso);
        }

        /// <summary>
        /// El emulador de Android llega a la PC por 10.0.2.2; en un celular real se escribe
        /// la IP de la maquina que tiene el servidor.
        /// </summary>
        private static string UrlPorDefecto()
        {
            string url = Servicio.UrlServidor;
            return DeviceInfo.Platform == DevicePlatform.Android ? url.Replace("localhost", "10.0.2.2") : url;
        }
    }
}
