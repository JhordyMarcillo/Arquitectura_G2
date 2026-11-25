using System.Runtime.Serialization;

namespace ec.edu.monster.modelos.utilidades
{
    [DataContract(Namespace = EspacioNombres.WS)]
    public class Respuesta
    {
        [DataMember(Name = "estado", Order = 1)]
        public string Estado { get; set; } = string.Empty;

        [DataMember(Name = "codigo", Order = 2)]
        public string Codigo { get; set; } = string.Empty;

        [DataMember(Name = "mensaje", Order = 3)]
        public string Mensaje { get; set; } = string.Empty;

        public static T Exito<T>(string mensaje) where T : Respuesta, new()
        {
            return new T { Estado = CodigosRespuesta.ESTADO_OK, Codigo = CodigosRespuesta.EXITO, Mensaje = mensaje };
        }

        public static T Error<T>(string codigo, string mensaje) where T : Respuesta, new()
        {
            return new T { Estado = CodigosRespuesta.ESTADO_ERROR, Codigo = codigo, Mensaje = mensaje };
        }
    }
}
