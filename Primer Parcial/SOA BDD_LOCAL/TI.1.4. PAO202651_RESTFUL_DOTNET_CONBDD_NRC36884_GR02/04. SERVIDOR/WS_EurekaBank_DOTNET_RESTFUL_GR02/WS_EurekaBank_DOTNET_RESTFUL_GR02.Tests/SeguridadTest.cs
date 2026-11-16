using ec.edu.monster.modelos.utilidades.enums;
using ec.edu.monster.seguridad;
using Xunit;

public class SeguridadTest
{
    [Fact]
    public void Sha1CoincideConElSHADeMySQL()
    {
        // SHA('chicho') en MySQL = valor guardado para cromero en Carga_Datos.sql
        Assert.Equal("1c0108a5c6cda33ab8dca984c29c7fe86b5ccbf9", CifradoClave.Sha1("chicho"));
    }

    [Fact]
    public void TokenGeneradoEsValidoYGuardaElEmpleado()
    {
        string token = AdministradorTokens.GenerarToken("0001", "Carlos Romero");

        Assert.Equal(EstadoToken.VALIDO, AdministradorTokens.Validar(token, out SesionEmpleado? sesion));
        Assert.Equal("0001", sesion!.CodigoEmpleado);
    }

    [Theory]
    [InlineData(null)]
    [InlineData("")]
    [InlineData("token-que-no-existe")]
    public void TokenDesconocidoEsInvalido(string? token)
    {
        Assert.Equal(EstadoToken.INVALIDO, AdministradorTokens.Validar(token, out _));
    }
}
