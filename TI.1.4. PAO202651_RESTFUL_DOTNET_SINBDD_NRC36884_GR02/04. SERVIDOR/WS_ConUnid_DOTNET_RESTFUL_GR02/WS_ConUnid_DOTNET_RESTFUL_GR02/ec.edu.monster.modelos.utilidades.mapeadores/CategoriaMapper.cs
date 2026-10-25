using ec.edu.monster.modelos.utilidades.enums;

namespace ec.edu.monster.modelos.utilidades.mapeadores
{
    public static class CategoriaMapper
    {
        public static Categoria ToCategoria(string categoria)
        {
            string nombre = categoria.Trim().ToUpperInvariant();

            if (!Enum.GetNames<Categoria>().Contains(nombre))
            {
                throw new ExcepcionNegocio(CodigosRespuesta.DATOS_INCONSISTENTES, "Categoria inexistente: " + categoria);
            }
            return Enum.Parse<Categoria>(nombre);
        }
    }
}
