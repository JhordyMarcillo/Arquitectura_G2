using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.servicios;

public class ServicioConversionTest
{
    private readonly ServicioConversion servicio = new();

    [Fact]
    public void DeberiaConvertirConRespuestaEstructurada()
    {
        var solicitud = new SolicitudConversion { Categoria = "longitud", UnidadOrigen = "kilometro", UnidadDestino = "metro", Valor = 5 };

        RespuestaConversion respuesta = servicio.Convertir(solicitud);

        Assert.Equal(CodigosRespuesta.ESTADO_OK, respuesta.Estado);
        Assert.Equal(CodigosRespuesta.EXITO, respuesta.Codigo);
        Assert.Equal(5000.0, respuesta.Resultado!.Value, 4);
    }

    [Theory]
    [InlineData("LONGITUD", "XYZ", "METRO", CodigosRespuesta.UNIDAD_NO_SOPORTADA)]
    [InlineData("LONGITUD", "METRO", "CELSIUS", CodigosRespuesta.DATOS_INCONSISTENTES)]
    [InlineData("VOLUMEN", "LITRO", "MILILITRO", CodigosRespuesta.DATOS_INCONSISTENTES)]
    [InlineData("MASA", "", "GRAMO", CodigosRespuesta.DATOS_INCONSISTENTES)]
    public void DeberiaRechazarSolicitudInvalida(string categoria, string origen, string destino, string codigoEsperado)
    {
        var solicitud = new SolicitudConversion { Categoria = categoria, UnidadOrigen = origen, UnidadDestino = destino, Valor = 1 };

        var error = Assert.Throws<ExcepcionNegocio>(() => servicio.Convertir(solicitud));

        Assert.Equal(codigoEsperado, error.Codigo);
    }
}
