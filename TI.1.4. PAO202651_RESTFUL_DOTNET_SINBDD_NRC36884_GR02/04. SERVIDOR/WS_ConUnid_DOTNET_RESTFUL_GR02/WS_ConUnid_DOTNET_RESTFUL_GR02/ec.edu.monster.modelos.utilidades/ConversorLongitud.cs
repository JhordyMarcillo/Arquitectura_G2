using ec.edu.monster.modelos.utilidades.enums;

namespace ec.edu.monster.modelos.utilidades
{
    public class ConversorLongitud : IConversor<UnidadLongitud>
    {
        private readonly Dictionary<UnidadLongitud, double> factores = new()
        {
            { UnidadLongitud.MILIMETRO, 0.001 },
            { UnidadLongitud.CENTIMETRO, 0.01 },
            { UnidadLongitud.METRO, 1.0 },
            { UnidadLongitud.KILOMETRO, 1000.0 },
            { UnidadLongitud.MILLA, 1609.344 },
            { UnidadLongitud.PIE, 0.3048 }
        };

        public ConversorLongitud() { }

        public double Convertir(double valor, UnidadLongitud unidadOrigen, UnidadLongitud unidadFinal)
        {
            if (!factores.ContainsKey(unidadOrigen) || !factores.ContainsKey(unidadFinal))
            {
                throw new ArgumentException("Unidad no soportada");
            }

            double valorEnBase = valor * factores[unidadOrigen];

            return valorEnBase / factores[unidadFinal];
        }
    }
}
