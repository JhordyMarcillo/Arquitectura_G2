using System.Text.Json.Serialization;

namespace ec.edu.monster.modelos.utilidades
{
    public class Respuesta
    {
        [JsonPropertyOrder(-3)]
        public string Estado { get; set; } = string.Empty;

        [JsonPropertyOrder(-2)]
        public string Codigo { get; set; } = string.Empty;

        [JsonPropertyOrder(-1)]
        public string Mensaje { get; set; } = string.Empty;

        public static T Exito<T>(string mensaje) where T : Respuesta, new()
        {
            return new T { Estado = CodigosRespuesta.ESTADO_OK, Codigo = CodigosRespuesta.EXITO, Mensaje = mensaje };
        }
    }
}
