using System.Drawing;
using CONUNI_SOAP_DOTNET_CLIESC_G09.modelo;
using CONUNI_SOAP_DOTNET_CLIESC_G09.servicio;

namespace CONUNI_SOAP_DOTNET_CLIESC_G09.vista
{
    /// <summary>Panel de conversiones de peso/masa (Monsters Inc.).</summary>
    public class PanelPeso : PanelConversionBase
    {
        private readonly ClienteConversionSoap _cliente;

        public PanelPeso(ClienteConversionSoap cliente)
            : base("CONVERSIONES DE PESO / MASA", "Ingrese el peso", Color.FromArgb(246, 105, 113), new[]
            {
                "Kilogramos → Libras", "Libras → Kilogramos",
                "Gramos → Onzas", "Onzas → Gramos"
            })
        {
            _cliente = cliente;
        }

        protected override ConversionResponse Ejecutar(int indice, double valor)
        {
            switch (indice)
            {
                case 0: return _cliente.KilogramosALibras(valor);
                case 1: return _cliente.LibrasAKilogramos(valor);
                case 2: return _cliente.GramosAOnzas(valor);
                default: return _cliente.OnzasAGramos(valor);
            }
        }
    }
}
