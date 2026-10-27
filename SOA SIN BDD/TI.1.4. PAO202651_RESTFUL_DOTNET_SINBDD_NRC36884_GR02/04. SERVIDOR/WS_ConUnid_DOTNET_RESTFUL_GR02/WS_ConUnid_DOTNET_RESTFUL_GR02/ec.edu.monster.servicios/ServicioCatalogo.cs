using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.modelos.utilidades.enums;
using ec.edu.monster.modelos.utilidades.mapeadores;

namespace ec.edu.monster.servicios
{
    public class ServicioCatalogo
    {
        public RespuestaCatalogo ConsultarCategorias()
        {
            return new RespuestaCatalogo
            {
                Estado = CodigosRespuesta.ESTADO_OK,
                Codigo = CodigosRespuesta.EXITO,
                Mensaje = "Categorias consultadas correctamente",
                Categorias = Enum.GetNames<Categoria>().ToList()
            };
        }

        public RespuestaUnidades ConsultarUnidades(string categoria)
        {
            Categoria tipo = CategoriaMapper.ToCategoria(categoria);

            return new RespuestaUnidades
            {
                Estado = CodigosRespuesta.ESTADO_OK,
                Codigo = CodigosRespuesta.EXITO,
                Mensaje = "Unidades consultadas correctamente",
                Categoria = tipo.ToString(),
                Unidades = ObtenerUnidades(tipo)
            };
        }

        public List<string> ObtenerUnidades(Categoria categoria)
        {
            return categoria switch
            {
                Categoria.LONGITUD => Enum.GetNames<UnidadLongitud>().ToList(),
                Categoria.MASA => Enum.GetNames<UnidadMasa>().ToList(),
                Categoria.TEMPERATURA => Enum.GetNames<UnidadTemperatura>().ToList(),
                _ => throw new ExcepcionNegocio(CodigosRespuesta.DATOS_INCONSISTENTES, "Categoria inexistente: " + categoria)
            };
        }

        public Categoria? BuscarCategoriaDeUnidad(string unidad)
        {
            string nombre = unidad.Trim().ToUpperInvariant();

            foreach (Categoria categoria in Enum.GetValues<Categoria>())
            {
                if (ObtenerUnidades(categoria).Contains(nombre))
                {
                    return categoria;
                }
            }
            return null;
        }
    }
}
