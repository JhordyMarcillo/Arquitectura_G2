namespace ec.edu.monster.modelos.utilidades
{
    public interface IConversor<T>
    {
        public double Convertir(double valor, T unidadOrigen, T unidadFinal);
    }
}
