using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.modelos.utilidades.enums;

public class ConversorLongitudTest
{
    private readonly ConversorLongitud conversor = new();

    [Theory]
    [InlineData(5, UnidadLongitud.KILOMETRO, UnidadLongitud.METRO, 5000.0)]
    [InlineData(100, UnidadLongitud.CENTIMETRO, UnidadLongitud.METRO, 1.0)]
    [InlineData(1, UnidadLongitud.MILLA, UnidadLongitud.KILOMETRO, 1.609344)]
    public void DeberiaConvertirCorrectamente(double valor,
                                              UnidadLongitud origen,
                                              UnidadLongitud destino,
                                              double esperado)
    {
        double resultado = conversor.Convertir(valor, origen, destino);

        Assert.Equal(esperado, resultado, 4);
    }
}
