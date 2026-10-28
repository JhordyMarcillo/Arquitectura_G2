using System;
using System.Drawing;
using System.Globalization;
using System.Threading.Tasks;
using System.Windows.Forms;
using CONUNI_SOAP_DOTNET_CLIESC_G02.modelo;

namespace CONUNI_SOAP_DOTNET_CLIESC_G02.vista
{
    /// <summary>
    /// Panel base de conversión (tema Monsters Inc.): tarjeta centrada con valor, tipo de conversión,
    /// botón invertir, botón convertir y resultado destacado. Las conversiones van en pares
    /// (índice par = ida, índice impar = vuelta) para poder invertir con un clic.
    /// </summary>
    public abstract class PanelConversionBase : Panel
    {
        protected static readonly Color AZUL = Color.FromArgb(33, 169, 218);
        protected static readonly Color ROJO = Color.FromArgb(246, 105, 113);
        protected static readonly Color PURPURA = Color.FromArgb(140, 107, 205);
        protected static readonly Color AZUL_CLARO = Color.FromArgb(159, 220, 250);
        protected static readonly Color GRIS_OSCURO = Color.FromArgb(44, 62, 80);
        protected static readonly Color GRIS_TEXTO = Color.FromArgb(127, 140, 141);
        protected static readonly Color FONDO = Color.FromArgb(236, 240, 241);

        private readonly Color _acento;
        private readonly string _titulo;
        private readonly string _placeholder;
        private readonly string[] _conversiones;

        private Panel _tarjeta;
        private TextBox _txtValor;
        private ComboBox _cmbConversion;
        private Button _btnInvertir;
        private Button _btnConvertir;
        private Label _lblEtiquetaResultado;
        private Label _lblResultado;
        private Panel _panelResultado;
        private Button _btnCopiar;
        private string _ultimoResultado;

        /// <summary>Ejecuta la conversión elegida (índice del combo) de forma síncrona contra el servicio SOAP.</summary>
        protected abstract ConversionResponse Ejecutar(int indice, double valor);

        protected PanelConversionBase(string titulo, string placeholder, Color acento, string[] conversiones)
        {
            _titulo = titulo;
            _placeholder = placeholder;
            _acento = acento;
            _conversiones = conversiones;
            DoubleBuffered = true;
            Dock = DockStyle.Fill;
            BackColor = Color.White;
            Construir();
        }

        private void Construir()
        {
            _tarjeta = new Panel { Size = new Size(640, 470), BackColor = Color.White, Anchor = AnchorStyles.None };
            _tarjeta.Paint += (s, e) =>
            {
                using (var pen = new Pen(Color.FromArgb(220, 226, 230), 2))
                    e.Graphics.DrawRectangle(pen, 1, 1, _tarjeta.Width - 3, _tarjeta.Height - 3);
                using (var br = new SolidBrush(_acento))
                    e.Graphics.FillRectangle(br, 0, 0, _tarjeta.Width, 8);
            };

            var lblTitulo = new Label
            {
                Text = _titulo, Font = new Font("Segoe UI", 17F, FontStyle.Bold), ForeColor = GRIS_OSCURO,
                TextAlign = ContentAlignment.MiddleCenter, Location = new Point(3, 24), Size = new Size(634, 48)
            };

            var lblValor = Etiqueta("Valor a convertir", 40, 88);
            _txtValor = new TextBox
            {
                Font = new Font("Segoe UI", 16F), Location = new Point(40, 116), Size = new Size(270, 36),
                TextAlign = HorizontalAlignment.Center, BorderStyle = BorderStyle.FixedSingle
            };
            _txtValor.KeyPress += (s, e) => { if (e.KeyChar == (char)Keys.Enter) { e.Handled = true; Convertir(); } };
            AplicarPlaceholder(_txtValor, _placeholder);

            var lblConv = Etiqueta("Tipo de conversión", 330, 88);
            _cmbConversion = new ComboBox
            {
                Font = new Font("Segoe UI", 12F), DropDownStyle = ComboBoxStyle.DropDownList,
                Location = new Point(330, 118), Size = new Size(215, 32), FlatStyle = FlatStyle.Flat
            };
            _cmbConversion.Items.AddRange(_conversiones);
            _cmbConversion.SelectedIndex = 0;

            _btnInvertir = new Button
            {
                Text = "⇄", Font = new Font("Segoe UI", 14F, FontStyle.Bold), ForeColor = PURPURA, BackColor = Color.White,
                FlatStyle = FlatStyle.Flat, Cursor = Cursors.Hand, Location = new Point(553, 116), Size = new Size(46, 36)
            };
            _btnInvertir.FlatAppearance.BorderColor = PURPURA;
            _btnInvertir.FlatAppearance.BorderSize = 2;
            _btnInvertir.Click += (s, e) => _cmbConversion.SelectedIndex ^= 1;
            new ToolTip().SetToolTip(_btnInvertir, "Invertir conversión");

            _btnConvertir = new Button
            {
                Text = "CONVERTIR", Font = new Font("Segoe UI", 14F, FontStyle.Bold), ForeColor = Color.White, BackColor = _acento,
                FlatStyle = FlatStyle.Flat, Cursor = Cursors.Hand, Location = new Point(40, 180), Size = new Size(560, 52)
            };
            _btnConvertir.FlatAppearance.BorderSize = 0;
            _btnConvertir.Click += (s, e) => Convertir();
            _btnConvertir.MouseEnter += (s, e) => { if (_btnConvertir.Enabled) _btnConvertir.BackColor = Oscurecer(_acento); };
            _btnConvertir.MouseLeave += (s, e) => { if (_btnConvertir.Enabled) _btnConvertir.BackColor = _acento; };

            _panelResultado = new Panel
            {
                Location = new Point(40, 256), Size = new Size(560, 170), BackColor = FONDO
            };
            _lblEtiquetaResultado = new Label
            {
                Font = new Font("Segoe UI", 11F), ForeColor = GRIS_TEXTO, TextAlign = ContentAlignment.MiddleCenter,
                Dock = DockStyle.Top, Height = 40, Text = "El resultado aparecerá aquí"
            };
            _lblResultado = new Label
            {
                Font = new Font("Segoe UI", 28F, FontStyle.Bold), ForeColor = AZUL, TextAlign = ContentAlignment.MiddleCenter,
                Dock = DockStyle.Fill, Text = ""
            };
            _btnCopiar = new Button
            {
                Text = "Copiar", Font = new Font("Segoe UI", 10F, FontStyle.Bold), ForeColor = Color.FromArgb(46, 125, 50),
                BackColor = Color.White, FlatStyle = FlatStyle.Flat, Cursor = Cursors.Hand, Visible = false,
                Dock = DockStyle.Bottom, Height = 34
            };
            _btnCopiar.FlatAppearance.BorderColor = Color.FromArgb(76, 175, 80);
            _btnCopiar.Click += (s, e) =>
            {
                if (_ultimoResultado == null) return;
                try { Clipboard.SetText(_ultimoResultado); _btnCopiar.Text = "Copiado ✓"; } catch { }
                var t = new Timer { Interval = 1500 };
                t.Tick += (a, b) => { _btnCopiar.Text = "Copiar"; t.Stop(); t.Dispose(); };
                t.Start();
            };
            _panelResultado.Controls.Add(_lblResultado);
            _panelResultado.Controls.Add(_btnCopiar);
            _panelResultado.Controls.Add(_lblEtiquetaResultado);

            _tarjeta.Controls.AddRange(new Control[]
            {
                lblTitulo, lblValor, _txtValor, lblConv, _cmbConversion, _btnInvertir, _btnConvertir, _panelResultado
            });

            Controls.Add(_tarjeta);
            Resize += (s, e) => Centrar();
            Centrar();
        }

        private static Label Etiqueta(string texto, int x, int y)
        {
            return new Label
            {
                Text = texto, Font = new Font("Segoe UI", 10.5F, FontStyle.Bold), ForeColor = GRIS_OSCURO,
                Location = new Point(x, y), Size = new Size(250, 24)
            };
        }

        private void Centrar()
        {
            _tarjeta.Location = new Point(Math.Max(0, (Width - _tarjeta.Width) / 2), Math.Max(0, (Height - _tarjeta.Height) / 2));
        }

        private static Color Oscurecer(Color c)
        {
            return Color.FromArgb(Math.Max(0, c.R - 25), Math.Max(0, c.G - 25), Math.Max(0, c.B - 25));
        }

        private static void AplicarPlaceholder(TextBox box, string texto)
        {
            box.ForeColor = GRIS_TEXTO;
            box.Text = texto;
            box.Font = new Font("Segoe UI", 12F);
            box.GotFocus += (s, e) => { if (box.ForeColor == GRIS_TEXTO) { box.Text = ""; box.ForeColor = GRIS_OSCURO; box.Font = new Font("Segoe UI", 16F); } };
            box.LostFocus += (s, e) => { if (box.Text.Length == 0) { box.ForeColor = GRIS_TEXTO; box.Text = texto; box.Font = new Font("Segoe UI", 12F); } };
        }

        private bool TryLeerValor(out double valor)
        {
            valor = 0;
            if (_txtValor.ForeColor == GRIS_TEXTO) return false;
            string t = _txtValor.Text.Trim().Replace(',', '.');
            return double.TryParse(t, NumberStyles.Float, CultureInfo.InvariantCulture, out valor);
        }

        private void Convertir()
        {
            double valor;
            if (!TryLeerValor(out valor)) { MostrarError("Ingrese un valor numérico válido"); return; }

            int indice = _cmbConversion.SelectedIndex;
            _btnConvertir.Enabled = false;
            _btnConvertir.BackColor = AZUL_CLARO;
            _btnConvertir.Text = "CONVIRTIENDO...";
            _lblEtiquetaResultado.Text = "Procesando conversión...";
            _lblResultado.Text = "";
            _btnCopiar.Visible = false;

            Task.Run(() =>
            {
                ConversionResponse r = null; string error = null;
                try { r = Ejecutar(indice, valor); } catch (Exception ex) { error = "Error al enviar la solicitud: " + ex.Message; }

                if (IsDisposed) return;
                BeginInvoke(new Action(() =>
                {
                    if (error == null && r != null && r.Exito) MostrarResultado(r);
                    else MostrarError(error ?? (r != null ? r.Mensaje : "Error desconocido"));
                    _btnConvertir.Enabled = true;
                    _btnConvertir.BackColor = _acento;
                    _btnConvertir.Text = "CONVERTIR";
                }));
            });
        }

        private void MostrarResultado(ConversionResponse r)
        {
            _ultimoResultado = r.ValorConvertido.ToString("0.####", CultureInfo.InvariantCulture);
            _panelResultado.BackColor = Color.FromArgb(232, 245, 232);
            _lblEtiquetaResultado.ForeColor = GRIS_TEXTO;
            _lblEtiquetaResultado.Text = string.Format(CultureInfo.CurrentCulture, "{0:0.####} {1} equivale a", r.ValorOriginal, r.UnidadOrigen);
            _lblResultado.ForeColor = AZUL;
            _lblResultado.Font = new Font("Segoe UI", 28F, FontStyle.Bold);
            _lblResultado.Text = string.Format(CultureInfo.CurrentCulture, "{0:0.####} {1}", r.ValorConvertido, r.UnidadDestino);
            _btnCopiar.Text = "Copiar";
            _btnCopiar.Visible = true;
        }

        public void MostrarError(string error)
        {
            _panelResultado.BackColor = Color.FromArgb(255, 240, 240);
            _lblEtiquetaResultado.ForeColor = ROJO;
            _lblEtiquetaResultado.Text = "No se pudo convertir";
            _lblResultado.ForeColor = ROJO;
            _lblResultado.Font = new Font("Segoe UI", 12F, FontStyle.Bold);
            _lblResultado.Text = error;
            _btnCopiar.Visible = false;
        }
    }
}
