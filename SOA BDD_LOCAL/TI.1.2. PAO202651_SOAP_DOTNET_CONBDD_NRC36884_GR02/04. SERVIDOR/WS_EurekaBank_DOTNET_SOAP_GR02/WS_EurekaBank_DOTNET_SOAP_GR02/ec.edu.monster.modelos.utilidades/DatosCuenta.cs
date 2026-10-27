using System.Runtime.Serialization;

namespace ec.edu.monster.modelos.utilidades
{
    [DataContract(Namespace = EspacioNombres.WS)]
    public class DatosCuenta
    {
        [DataMember(Name = "codigo", Order = 1)]
        public string Codigo { get; set; } = string.Empty;

        [DataMember(Name = "cliente", Order = 2)]
        public string Cliente { get; set; } = string.Empty;

        [DataMember(Name = "moneda", Order = 3)]
        public string Moneda { get; set; } = string.Empty;

        [DataMember(Name = "saldo", Order = 4)]
        public decimal Saldo { get; set; }

        [DataMember(Name = "estado", Order = 5)]
        public string Estado { get; set; } = string.Empty;
    }
}
