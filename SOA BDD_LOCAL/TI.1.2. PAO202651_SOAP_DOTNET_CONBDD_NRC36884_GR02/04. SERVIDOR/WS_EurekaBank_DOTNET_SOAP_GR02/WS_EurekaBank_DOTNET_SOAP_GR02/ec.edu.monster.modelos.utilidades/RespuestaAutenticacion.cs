using System.Runtime.Serialization;

namespace ec.edu.monster.modelos.utilidades
{
    [DataContract(Name = "RespuestaSesion", Namespace = EspacioNombres.WS)]
    public class RespuestaAutenticacion : Respuesta
    {
        [DataMember(Name = "token", Order = 1)]
        public string Token { get; set; } = string.Empty;

        [DataMember(Name = "expiracion", Order = 2)]
        public string Expiracion { get; set; } = string.Empty;

        [DataMember(Name = "codigoEmpleado", Order = 3)]
        public string CodigoEmpleado { get; set; } = string.Empty;

        [DataMember(Name = "nombreEmpleado", Order = 4)]
        public string NombreEmpleado { get; set; } = string.Empty;
    }
}
