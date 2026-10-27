using ec.edu.monster.modelos;
using ec.edu.monster.utilidades;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Threading;

namespace ec.edu.monster.vistas
{
    public enum TipoOperacion
    {
        Deposito,
        Retiro,
        Transferencia
    }

    public partial class VentanaPrincipal : Window
    {
        private readonly DispatcherTimer temporizadorNotificacion = new() { Interval = TimeSpan.FromSeconds(4) };
        private bool cerrandoSesion;

        public VentanaPrincipal()
        {
            InitializeComponent();

            RespuestaAutenticacion sesion = App.Servicio.Sesion!;
            TextoEmpleado.Text = sesion.NombreEmpleado;
            TextoCodigoEmpleado.Text = "Empleado " + sesion.CodigoEmpleado;
            TextoIniciales.Text = Formato.Iniciales(sesion.NombreEmpleado);
            TextoServidor.Text = App.SUBTITULO;
            TextoServidor.ToolTip = App.Servicio.UrlServidor;

            temporizadorNotificacion.Tick += (_, _) =>
            {
                Notificacion.Visibility = Visibility.Collapsed;
                temporizadorNotificacion.Stop();
            };

            MostrarCuentas();
        }

        // ------------------------------------------------------------------ navegacion

        private void Menu_Click(object sender, RoutedEventArgs e)
        {
            if (sender == MenuCuentas) MostrarCuentas();
            else if (sender == MenuMovimientos) MostrarMovimientos(null);
            else if (sender == MenuDeposito) MostrarOperacion(TipoOperacion.Deposito);
            else if (sender == MenuRetiro) MostrarOperacion(TipoOperacion.Retiro);
            else MostrarOperacion(TipoOperacion.Transferencia);
        }

        public void MostrarCuentas()
        {
            Navegar(MenuCuentas, "Cuentas", "Cartera de cuentas de ahorro y su estado actual.", new PaginaCuentas(this));
        }

        public void MostrarMovimientos(string? cuenta)
        {
            Navegar(MenuMovimientos, "Movimientos", "Historial de movimientos y saldo de una cuenta.", new PaginaMovimientos(this, cuenta));
        }

        public void MostrarOperacion(TipoOperacion tipo)
        {
            (Button menu, string titulo, string subtitulo) = tipo switch
            {
                TipoOperacion.Deposito => (MenuDeposito, "Deposito", "Abono de efectivo a una cuenta activa."),
                TipoOperacion.Retiro => (MenuRetiro, "Retiro", "Retiro de efectivo validado con la clave de la cuenta."),
                _ => (MenuTransferencia, "Transferencia", "Traspaso entre cuentas de la misma moneda.")
            };
            Navegar(menu, titulo, subtitulo, new PaginaOperacion(this, tipo));
        }

        private void Navegar(Button menu, string titulo, string subtitulo, UserControl pagina)
        {
            foreach (Button boton in new[] { MenuCuentas, MenuMovimientos, MenuDeposito, MenuRetiro, MenuTransferencia })
            {
                boton.Tag = boton == menu ? "Activo" : null;
            }
            TextoTituloPagina.Text = titulo;
            TextoSubtituloPagina.Text = subtitulo;
            ContenedorPagina.Content = pagina;
        }

        // ------------------------------------------------------------------ notificaciones y errores

        public void Notificar(string mensaje, bool esError = false)
        {
            IconoNotificacion.Text = esError ? "" : "";
            IconoNotificacion.Foreground = esError ? (Brush)FindResource("Rojo") : new SolidColorBrush(Color.FromRgb(52, 211, 153));
            TextoNotificacion.Text = mensaje;
            Notificacion.Visibility = Visibility.Visible;
            temporizadorNotificacion.Stop();
            temporizadorNotificacion.Start();
        }

        /// <summary>Muestra el error; si la sesion expiro vuelve al inicio de sesion.</summary>
        public void ManejarError(ExcepcionServicio error)
        {
            if (error.RequiereLogin)
            {
                MessageBox.Show(this, error.Message + "\n\nInicie sesion nuevamente.", "EurekaBank",
                        MessageBoxButton.OK, MessageBoxImage.Information);
                VolverAlLogin();
                return;
            }
            Notificar(error.Message, esError: true);
        }

        private void BotonCerrarSesion_Click(object sender, RoutedEventArgs e) => VolverAlLogin();

        private void VolverAlLogin()
        {
            if (cerrandoSesion)
            {
                return;
            }
            cerrandoSesion = true;
            App.Servicio.CerrarSesion();
            new VentanaLogin().Show();
            Close();
        }
    }
}
