using ec.edu.monster.modelos.utilidades.enums;
using static ec.edu.monster.modelos.utilidades.enums.UnidadTemperatura;

namespace ec.edu.monster.modelos.utilidades
{
    public class ConversorTemperatura : IConversor<UnidadTemperatura>
    {
        public ConversorTemperatura() { }

        public double Convertir(double valor, UnidadTemperatura origen, UnidadTemperatura destino)
        {
            if (origen == destino)
            {
                return valor;
            }

            return origen switch
            {
                CELSIUS => destino switch
                {
                    FAHRENHEIT => (valor * 9 / 5) + 32,
                    KELVIN => valor + 273.15,
                    _ => throw new ArgumentException("Conversion no soportada")
                },

                FAHRENHEIT => destino switch
                {
                    CELSIUS => (valor - 32) * 5 / 9,
                    KELVIN => (valor - 32) * 5 / 9 + 273.15,
                    _ => throw new ArgumentException("Conversion no soportada")
                },

                KELVIN => destino switch
                {
                    CELSIUS => valor - 273.15,
                    FAHRENHEIT => (valor - 273.15) * 9 / 5 + 32,
                    _ => throw new ArgumentException("Conversion no soportada")
                },

                _ => throw new ArgumentException("Unidad no soportada")
            };
        }
    }
}
