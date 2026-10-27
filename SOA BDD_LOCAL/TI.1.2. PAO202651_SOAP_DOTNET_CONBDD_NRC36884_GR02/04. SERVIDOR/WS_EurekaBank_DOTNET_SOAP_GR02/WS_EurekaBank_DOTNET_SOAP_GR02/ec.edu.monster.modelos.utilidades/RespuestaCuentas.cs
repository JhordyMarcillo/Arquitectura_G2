using System.Runtime.Serialization;

namespace ec.edu.monster.modelos.utilidades
{
    [DataContract(Namespace = EspacioNombres.WS)]
    public class RespuestaCuentas : Respuesta
    {
        [DataMember(Name = "cuentas", Order = 1)]
        public List<DatosCuenta> Cuentas { get; set; } = new();
    }
}
