using CoreWCF;
using ec.edu.monster.modelos.utilidades;

namespace ec.edu.monster.controladores.ws
{
    /// <summary>Contrato SOAP comun (ver CONTRATO_EUREKABANK_GR02.md, seccion 4).</summary>
    [ServiceContract(Name = "WSEurekaBank", Namespace = EspacioNombres.WS)]
    public interface IWSEurekaBank
    {
        [OperationContract(Name = "iniciarSesion")]
        RespuestaAutenticacion IniciarSesion(string usuario, string clave);

        [OperationContract(Name = "listarCuentas")]
        RespuestaCuentas ListarCuentas(string token);

        [OperationContract(Name = "consultarMovimientos")]
        RespuestaMovimientos ConsultarMovimientos(string token, string cuenta);

        [OperationContract(Name = "registrarDeposito")]
        RespuestaOperacion RegistrarDeposito(string token, string cuenta, decimal importe);

        [OperationContract(Name = "registrarRetiro")]
        RespuestaOperacion RegistrarRetiro(string token, string cuenta, string clave, decimal importe);

        [OperationContract(Name = "registrarTransferencia")]
        RespuestaOperacion RegistrarTransferencia(string token, string cuentaOrigen, string claveOrigen,
                string cuentaDestino, decimal importe);
    }
}
