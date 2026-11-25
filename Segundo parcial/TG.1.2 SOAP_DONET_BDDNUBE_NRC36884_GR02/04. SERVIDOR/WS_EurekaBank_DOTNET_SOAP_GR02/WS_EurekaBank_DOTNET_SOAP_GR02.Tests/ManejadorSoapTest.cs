using ec.edu.monster.controladores.manejadoresws;
using ec.edu.monster.modelos.utilidades;
using Xunit;

public class ManejadorSoapTest
{
    [Fact]
    public void OperacionSinTokenDevuelveAUTH001SinFault()
    {
        RespuestaCuentas respuesta = ManejadorSoap.Ejecutar<RespuestaCuentas>(null, _ => throw new InvalidOperationException());

        Assert.Equal(CodigosRespuesta.ESTADO_ERROR, respuesta.Estado);
        Assert.Equal(CodigosRespuesta.TOKEN_INVALIDO, respuesta.Codigo);
    }

    [Fact]
    public void ExcepcionDeNegocioSeDevuelveComoRespuesta()
    {
        RespuestaAutenticacion respuesta = ManejadorSoap.Ejecutar<RespuestaAutenticacion>(null, _ =>
                throw new ExcepcionNegocio(CodigosRespuesta.CREDENCIALES_INCORRECTAS, "Usuario o clave incorrectos"), exigeToken: false);

        Assert.Equal(CodigosRespuesta.CREDENCIALES_INCORRECTAS, respuesta.Codigo);
        Assert.Equal("Usuario o clave incorrectos", respuesta.Mensaje);
    }
}
