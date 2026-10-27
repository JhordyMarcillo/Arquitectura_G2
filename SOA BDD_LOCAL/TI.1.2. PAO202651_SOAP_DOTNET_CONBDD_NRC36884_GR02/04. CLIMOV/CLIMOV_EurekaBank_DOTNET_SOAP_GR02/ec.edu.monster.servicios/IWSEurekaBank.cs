using System.Runtime.Serialization;
using System.ServiceModel;

namespace ec.edu.monster.servicios
{
    /// <summary>
    /// Contrato SOAP del servicio WSEurekaBank, espejo del publicado por el servidor
    /// (espacio de nombres http://ws.monster.edu.ec/, ver CONTRATO_EUREKABANK_GR02.md).
    /// </summary>
    [ServiceContract(Name = "WSEurekaBank", Namespace = MensajesSoap.ESPACIO_NOMBRES)]
    public interface IWSEurekaBank
    {
        [OperationContract(Name = "iniciarSesion")]
        Task<SoapRespuestaSesion> IniciarSesionAsync(string usuario, string clave);

        [OperationContract(Name = "listarCuentas")]
        Task<SoapRespuestaCuentas> ListarCuentasAsync(string token);

        [OperationContract(Name = "consultarMovimientos")]
        Task<SoapRespuestaMovimientos> ConsultarMovimientosAsync(string token, string cuenta);

        [OperationContract(Name = "registrarDeposito")]
        Task<SoapRespuestaOperacion> RegistrarDepositoAsync(string token, string cuenta, decimal importe);

        [OperationContract(Name = "registrarRetiro")]
        Task<SoapRespuestaOperacion> RegistrarRetiroAsync(string token, string cuenta, string clave, decimal importe);

        [OperationContract(Name = "registrarTransferencia")]
        Task<SoapRespuestaOperacion> RegistrarTransferenciaAsync(string token, string cuentaOrigen, string claveOrigen,
                string cuentaDestino, decimal importe);
    }

    public static class MensajesSoap
    {
        public const string ESPACIO_NOMBRES = "http://ws.monster.edu.ec/";
    }

    // ---------------------------------------------------------------- mensajes (mismo XML que el servidor)

    [DataContract(Name = "Respuesta", Namespace = MensajesSoap.ESPACIO_NOMBRES)]
    public class SoapRespuesta
    {
        [DataMember(Name = "estado", Order = 1)] public string Estado { get; set; } = string.Empty;
        [DataMember(Name = "codigo", Order = 2)] public string Codigo { get; set; } = string.Empty;
        [DataMember(Name = "mensaje", Order = 3)] public string Mensaje { get; set; } = string.Empty;
    }

    [DataContract(Name = "RespuestaSesion", Namespace = MensajesSoap.ESPACIO_NOMBRES)]
    public class SoapRespuestaSesion : SoapRespuesta
    {
        [DataMember(Name = "token", Order = 1)] public string Token { get; set; } = string.Empty;
        [DataMember(Name = "expiracion", Order = 2)] public string Expiracion { get; set; } = string.Empty;
        [DataMember(Name = "codigoEmpleado", Order = 3)] public string CodigoEmpleado { get; set; } = string.Empty;
        [DataMember(Name = "nombreEmpleado", Order = 4)] public string NombreEmpleado { get; set; } = string.Empty;
    }

    [DataContract(Name = "RespuestaCuentas", Namespace = MensajesSoap.ESPACIO_NOMBRES)]
    public class SoapRespuestaCuentas : SoapRespuesta
    {
        [DataMember(Name = "cuentas", Order = 1)] public List<SoapDatosCuenta> Cuentas { get; set; } = new();
    }

    [DataContract(Name = "RespuestaMovimientos", Namespace = MensajesSoap.ESPACIO_NOMBRES)]
    public class SoapRespuestaMovimientos : SoapRespuesta
    {
        [DataMember(Name = "cuenta", Order = 1)] public string Cuenta { get; set; } = string.Empty;
        [DataMember(Name = "moneda", Order = 2)] public string Moneda { get; set; } = string.Empty;
        [DataMember(Name = "saldo", Order = 3)] public decimal Saldo { get; set; }
        [DataMember(Name = "movimientos", Order = 4)] public List<SoapDatosMovimiento> Movimientos { get; set; } = new();
    }

    [DataContract(Name = "RespuestaOperacion", Namespace = MensajesSoap.ESPACIO_NOMBRES)]
    public class SoapRespuestaOperacion : SoapRespuesta
    {
        [DataMember(Name = "cuenta", Order = 1)] public string Cuenta { get; set; } = string.Empty;
        [DataMember(Name = "saldo", Order = 2)] public decimal Saldo { get; set; }
    }

    [DataContract(Name = "DatosCuenta", Namespace = MensajesSoap.ESPACIO_NOMBRES)]
    public class SoapDatosCuenta
    {
        [DataMember(Name = "codigo", Order = 1)] public string Codigo { get; set; } = string.Empty;
        [DataMember(Name = "cliente", Order = 2)] public string Cliente { get; set; } = string.Empty;
        [DataMember(Name = "moneda", Order = 3)] public string Moneda { get; set; } = string.Empty;
        [DataMember(Name = "saldo", Order = 4)] public decimal Saldo { get; set; }
        [DataMember(Name = "estado", Order = 5)] public string Estado { get; set; } = string.Empty;
    }

    [DataContract(Name = "DatosMovimiento", Namespace = MensajesSoap.ESPACIO_NOMBRES)]
    public class SoapDatosMovimiento
    {
        [DataMember(Name = "numero", Order = 1)] public int Numero { get; set; }
        [DataMember(Name = "fecha", Order = 2)] public string Fecha { get; set; } = string.Empty;
        [DataMember(Name = "tipo", Order = 3)] public string Tipo { get; set; } = string.Empty;
        [DataMember(Name = "descripcion", Order = 4)] public string Descripcion { get; set; } = string.Empty;
        [DataMember(Name = "accion", Order = 5)] public string Accion { get; set; } = string.Empty;
        [DataMember(Name = "importe", Order = 6)] public decimal Importe { get; set; }
        [DataMember(Name = "cuentaReferencia", Order = 7)] public string? CuentaReferencia { get; set; }
    }
}
