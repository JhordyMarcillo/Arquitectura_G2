using System.Drawing;
using CONUNI_SOAP_DOTNET_CLIESC_G09.modelo;
using CONUNI_SOAP_DOTNET_CLIESC_G09.servicio;

namespace CONUNI_SOAP_DOTNET_CLIESC_G09.vista
{
    /// <summary>Panel de conversiones de longitud (Monsters Inc.).</summary>
    public class PanelLongitud : PanelConversionBase
    {
        private readonly ClienteConversionSoap _cliente;

        public PanelLongitud(ClienteConversionSoap cliente)
            : base("CONVERSIONES DE LONGITUD", "Ingrese la longitud", Color.FromArgb(140, 107, 205), new[]
            {
                "Metros → Pies", "Pies → Metros",
                "Metros → Pulgadas", "Pulgadas → Metros",
                "Kilómetros → Millas", "Millas → Kilómetros"
            })
        {
            _cliente = cliente;
        }

        protected override ConversionResponse Ejecutar(int indice, double valor)
        {
            switch (indice)
            {
                case 0: return _cliente.MetrosAPies(valor);
                case 1: return _cliente.PiesAMetros(valor);
                case 2: return _cliente.MetrosAPulgadas(valor);
                case 3: return _cliente.PulgadasAMetros(valor);
                case 4: return _cliente.KilometrosAMillas(valor);
                default: return _cliente.MillasAKilometros(valor);
            }
        }
    }
}
