using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.modelos.utilidades.enums;

public class ConversorTemperaturaTest
{
    private readonly ConversorTemperatura conversor = new();

    [Theory]
    [InlineData(100, UnidadTemperatura.CELSIUS, UnidadTemperatura.FAHRENHEIT, 212.0)]
    [InlineData(212, UnidadTemperatura.FAHRENHEIT, UnidadTemperatura.CELSIUS, 100.0)]
    [InlineData(0, UnidadTemperatura.CELSIUS, UnidadTemperatura.KELVIN, 273.15)]
    [InlineData(273.15, UnidadTemperatura.KELVIN, UnidadTemperatura.CELSIUS, 0.0)]
    [InlineData(32, UnidadTemperatura.FAHRENHEIT, UnidadTemperatura.KELVIN, 273.15)]
    [InlineData(273.15, UnidadTemperatura.KELVIN, UnidadTemperatura.FAHRENHEIT, 32.0)]
    public void DeberiaConvertirTemperatura(double valor,
                                            UnidadTemperatura origen,
                                            UnidadTemperatura destino,
                                            double esperado)
    {
        double resultado = conversor.Convertir(valor, origen, destino);

        Assert.Equal(esperado, resultado, 4);
    }
}
