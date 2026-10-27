using System.Runtime.Serialization;

namespace ec.edu.monster.modelos.utilidades
{
    [DataContract(Namespace = EspacioNombres.WS)]
    public class DatosMovimiento
    {
        [DataMember(Name = "numero", Order = 1)]
        public int Numero { get; set; }

        [DataMember(Name = "fecha", Order = 2)]
        public string Fecha { get; set; } = string.Empty;

        [DataMember(Name = "tipo", Order = 3)]
        public string Tipo { get; set; } = string.Empty;

        [DataMember(Name = "descripcion", Order = 4)]
        public string Descripcion { get; set; } = string.Empty;

        [DataMember(Name = "accion", Order = 5)]
        public string Accion { get; set; } = string.Empty;

        [DataMember(Name = "importe", Order = 6)]
        public decimal Importe { get; set; }

        [DataMember(Name = "cuentaReferencia", Order = 7)]
        public string? CuentaReferencia { get; set; }
    }
}
