using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.modelos.utilidades.enums;
using ec.edu.monster.servicios;
using Xunit;

public class ReglasOperacionesTest
{
    private static CuentaInterna Cuenta(decimal saldo) => new()
    {
        Codigo = "00100001", Saldo = saldo, Clave = "123456", CostoMovimiento = 2.00m, Estado = EstadoCuenta.ACTIVO
    };

    [Theory]
    [InlineData(0)]
    [InlineData(-5)]
    [InlineData(10.555)]
    public void ImporteInvalidoLanzaDATA001(decimal importe)
    {
        ExcepcionNegocio e = Assert.Throws<ExcepcionNegocio>(() => ServicioOperaciones.ValidarImporte(importe));
        Assert.Equal(CodigosRespuesta.DATOS_INCONSISTENTES, e.Codigo);
    }

    [Fact]
    public void SaldoDebeCubrirImporteMasCosto()
    {
        ServicioOperaciones.ValidarSaldo(Cuenta(102.00m), 100.00m);

        ExcepcionNegocio e = Assert.Throws<ExcepcionNegocio>(() => ServicioOperaciones.ValidarSaldo(Cuenta(101.99m), 100.00m));
        Assert.Equal(CodigosRespuesta.SALDO_INSUFICIENTE, e.Codigo);
    }

    [Fact]
    public void ClaveIncorrectaLanzaCTA003()
    {
        ExcepcionNegocio e = Assert.Throws<ExcepcionNegocio>(() => ServicioOperaciones.ValidarClave(Cuenta(10), "000000"));
        Assert.Equal(CodigosRespuesta.CLAVE_CUENTA_INCORRECTA, e.Codigo);
    }
}
