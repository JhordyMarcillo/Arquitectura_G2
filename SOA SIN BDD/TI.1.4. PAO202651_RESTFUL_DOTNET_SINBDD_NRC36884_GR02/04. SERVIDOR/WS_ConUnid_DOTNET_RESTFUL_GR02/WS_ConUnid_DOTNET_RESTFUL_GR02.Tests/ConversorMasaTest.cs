using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.modelos.utilidades.enums;

public class ConversorMasaTest
{
    private readonly ConversorMasa conversor = new();

    [Theory]
    [InlineData(1, UnidadMasa.KILOGRAMO, UnidadMasa.GRAMO, 1000.0)]
    [InlineData(1, UnidadMasa.GRAMO, UnidadMasa.MILIGRAMO, 1000.0)]
    [InlineData(1, UnidadMasa.LIBRA, UnidadMasa.KILOGRAMO, 0.45359237)]
    public void DeberiaConvertirCorrectamente(double valor,
                                              UnidadMasa origen,
                                              UnidadMasa destino,
                                              double esperado)
    {
        double resultado = conversor.Convertir(valor, origen, destino);

        Assert.Equal(esperado, resultado, 4);
    }
}
