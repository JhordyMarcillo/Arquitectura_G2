using System.Runtime.Serialization;

namespace ec.edu.monster.modelos.utilidades
{
    [DataContract(Namespace = EspacioNombres.WS)]
    public class RespuestaOperacion : Respuesta
    {
        [DataMember(Name = "cuenta", Order = 1)]
        public string Cuenta { get; set; } = string.Empty;

        [DataMember(Name = "saldo", Order = 2)]
        public decimal Saldo { get; set; }
    }
}
