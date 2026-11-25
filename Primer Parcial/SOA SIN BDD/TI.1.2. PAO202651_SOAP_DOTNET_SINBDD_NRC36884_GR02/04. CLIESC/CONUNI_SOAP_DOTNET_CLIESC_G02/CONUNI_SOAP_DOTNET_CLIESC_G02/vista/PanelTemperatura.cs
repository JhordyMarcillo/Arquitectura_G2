using System.Drawing;
using CONUNI_SOAP_DOTNET_CLIESC_G02.modelo;
using CONUNI_SOAP_DOTNET_CLIESC_G02.servicio;

namespace CONUNI_SOAP_DOTNET_CLIESC_G02.vista
{
    /// <summary>Panel de conversiones de temperatura (Monsters Inc.).</summary>
    public class PanelTemperatura : PanelConversionBase
    {
        private readonly ClienteConversionSoap _cliente;

        public PanelTemperatura(ClienteConversionSoap cliente)
            : base("CONVERSIONES DE TEMPERATURA", "Ingrese la temperatura", Color.FromArgb(33, 169, 218), new[]
            {
                "Celsius → Fahrenheit", "Fahrenheit → Celsius",
                "Celsius → Kelvin", "Kelvin → Celsius",
                "Fahrenheit → Kelvin", "Kelvin → Fahrenheit"
            })
        {
            _cliente = cliente;
        }

        protected override ConversionResponse Ejecutar(int indice, double valor)
        {
            switch (indice)
            {
                case 0: return _cliente.CelsiusAFahrenheit(valor);
                case 1: return _cliente.FahrenheitACelsius(valor);
                case 2: return _cliente.CelsiusAKelvin(valor);
                case 3: return _cliente.KelvinACelsius(valor);
                case 4: return _cliente.FahrenheitAKelvin(valor);
                default: return _cliente.KelvinAFahrenheit(valor);
            }
        }
    }
}
