using System.Runtime.Serialization;

namespace ec.edu.monster.modelos.utilidades
{
    [DataContract(Namespace = EspacioNombres.WS)]
    public class RespuestaMovimientos : Respuesta
    {
        [DataMember(Name = "cuenta", Order = 1)]
        public string Cuenta { get; set; } = string.Empty;

        [DataMember(Name = "moneda", Order = 2)]
        public string Moneda { get; set; } = string.Empty;

        [DataMember(Name = "saldo", Order = 3)]
        public decimal Saldo { get; set; }

        [DataMember(Name = "movimientos", Order = 4)]
        public List<DatosMovimiento> Movimientos { get; set; } = new();
    }
}
