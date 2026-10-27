using ec.edu.monster.servicios;
using ec.edu.monster.utilidades;
using System.IO;
using System.Windows;
using System.Windows.Threading;

namespace ec.edu.monster
{
    public partial class App : Application
    {
        /// <summary>Servicio compartido por todas las ventanas (una sola sesion por aplicacion).</summary>
        public static IServicioEurekaBank Servicio { get; } = new ServicioEurekaBankSoap();

        public const string SUBTITULO = "SOAP .NET  ·  GR02";

        private static readonly string ARCHIVO_REGISTRO = Path.Combine(Path.GetTempPath(), "EurekaBankEscritorio.log");

        protected override void OnStartup(StartupEventArgs e)
        {
            DispatcherUnhandledException += ErrorNoControlado;
            Servicio.UrlServidor = Preferencias.LeerUrlServidor(Servicio.UrlServidor);
            base.OnStartup(e);
        }

        /// <summary>Evita que la aplicacion se cierre sin aviso y deja el detalle en un registro.</summary>
        private void ErrorNoControlado(object sender, DispatcherUnhandledExceptionEventArgs e)
        {
            File.AppendAllText(ARCHIVO_REGISTRO, $"[{DateTime.Now:yyyy-MM-dd HH:mm:ss}] {e.Exception}{Environment.NewLine}");
            MessageBox.Show("Ocurrio un error inesperado:\n\n" + e.Exception.Message + "\n\nDetalle en " + ARCHIVO_REGISTRO,
                    "EurekaBank", MessageBoxButton.OK, MessageBoxImage.Error);
            e.Handled = true;
        }
    }
}
