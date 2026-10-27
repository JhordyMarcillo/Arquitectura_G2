using ec.edu.monster.modelos.utilidades;

namespace ec.edu.monster.servicios
{
    public class ServicioConversor<T>
    {
        private readonly IConversor<T> conversor;

        public ServicioConversor(IConversor<T> conversor)
        {
            this.conversor = conversor;
        }

        public double Convertir(double valor, T origen, T destino)
        {
            if (conversor == null)
            {
                throw new InvalidOperationException("No se ha inyectado un conversor");
            }
            return conversor.Convertir(valor, origen, destino);
        }
    }
}
