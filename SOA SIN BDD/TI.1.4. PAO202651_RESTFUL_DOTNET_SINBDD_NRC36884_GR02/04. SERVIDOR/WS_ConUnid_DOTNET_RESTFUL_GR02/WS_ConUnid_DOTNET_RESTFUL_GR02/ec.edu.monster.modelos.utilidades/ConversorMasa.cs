using ec.edu.monster.modelos.utilidades.enums;

namespace ec.edu.monster.modelos.utilidades
{
    public class ConversorMasa : IConversor<UnidadMasa>
    {
        private readonly Dictionary<UnidadMasa, double> factores = new()
        {
            { UnidadMasa.MILIGRAMO, 0.000001 },
            { UnidadMasa.GRAMO, 0.001 },
            { UnidadMasa.KILOGRAMO, 1.0 },
            { UnidadMasa.LIBRA, 0.45359237 }
        };

        public ConversorMasa() { }

        public double Convertir(double valor, UnidadMasa unidadOrigen, UnidadMasa unidadFinal)
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
