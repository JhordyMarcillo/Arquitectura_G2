namespace ec.edu.monster.modelos.utilidades
{
    public class RespuestaUnidades : Respuesta
    {
        public string? Categoria { get; set; }
        public List<string> Unidades { get; set; } = new();
    }
}
