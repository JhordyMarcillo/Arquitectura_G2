namespace ec.edu.monster.modelos.utilidades
{
    public class RespuestaAutenticacion : Respuesta
    {
        public string? Token { get; set; }
        public DateTime? Expiracion { get; set; }
    }
}
