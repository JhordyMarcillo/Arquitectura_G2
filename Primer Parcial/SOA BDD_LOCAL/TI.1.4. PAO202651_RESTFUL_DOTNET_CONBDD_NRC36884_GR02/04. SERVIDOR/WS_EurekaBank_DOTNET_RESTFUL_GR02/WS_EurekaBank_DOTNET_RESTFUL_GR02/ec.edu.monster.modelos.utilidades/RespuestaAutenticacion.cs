namespace ec.edu.monster.modelos.utilidades
{
    public class RespuestaAutenticacion : Respuesta
    {
        public string Token { get; set; } = string.Empty;
        public string Expiracion { get; set; } = string.Empty;
        public string CodigoEmpleado { get; set; } = string.Empty;
        public string NombreEmpleado { get; set; } = string.Empty;
    }
}
