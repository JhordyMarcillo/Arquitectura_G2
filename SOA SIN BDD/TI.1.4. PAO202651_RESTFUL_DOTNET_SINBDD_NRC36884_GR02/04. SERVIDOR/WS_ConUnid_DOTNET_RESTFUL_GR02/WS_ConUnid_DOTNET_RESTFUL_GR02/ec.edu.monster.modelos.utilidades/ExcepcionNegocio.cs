namespace ec.edu.monster.modelos.utilidades
{
    public class ExcepcionNegocio : Exception
    {
        public string Codigo { get; }
        public int EstadoHttp { get; }

        public ExcepcionNegocio(string codigo, string mensaje, int estadoHttp = StatusCodes.Status400BadRequest)
            : base(mensaje)
        {
            Codigo = codigo;
            EstadoHttp = estadoHttp;
        }
    }
}
