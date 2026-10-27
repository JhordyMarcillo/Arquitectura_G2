using ec.edu.monster.modelos.utilidades;
using ec.edu.monster.modelos.utilidades.enums;
using ec.edu.monster.modelos.utilidades.mapeadores;

namespace ec.edu.monster.servicios
{
    public class ServicioConversion
    {
        private readonly ServicioCatalogo servicioCatalogo = new();

        private readonly ServicioConversor<UnidadLongitud> servicioLongitud =
                new(new ConversorLongitud());

        private readonly ServicioConversor<UnidadMasa> servicioMasa =
                new(new ConversorMasa());

        private readonly ServicioConversor<UnidadTemperatura> servicioTemperatura =
                new(new ConversorTemperatura());

        public RespuestaConversion Convertir(SolicitudConversion solicitud)
        {
            if (string.IsNullOrWhiteSpace(solicitud.Categoria))
            {
                throw new ExcepcionNegocio(CodigosRespuesta.DATOS_INCONSISTENTES, "La categoria es obligatoria");
            }
            if (string.IsNullOrWhiteSpace(solicitud.UnidadOrigen))
            {
                throw new ExcepcionNegocio(CodigosRespuesta.DATOS_INCONSISTENTES, "La unidad de origen es obligatoria");
            }
            if (string.IsNullOrWhiteSpace(solicitud.UnidadDestino))
            {
                throw new ExcepcionNegocio(CodigosRespuesta.DATOS_INCONSISTENTES, "La unidad de destino es obligatoria");
            }
            if (solicitud.Valor == null || !double.IsFinite(solicitud.Valor.Value))
            {
                throw new ExcepcionNegocio(CodigosRespuesta.DATOS_INCONSISTENTES, "El valor es obligatorio y debe ser numerico");
            }

            Categoria categoria = CategoriaMapper.ToCategoria(solicitud.Categoria);
            ValidarUnidad(categoria, solicitud.UnidadOrigen, "origen");
            ValidarUnidad(categoria, solicitud.UnidadDestino, "destino");

            double valor = solicitud.Valor.Value;

            double resultado = categoria switch
            {
                Categoria.LONGITUD => servicioLongitud.Convertir(valor,
                        UnidadMapper.ToLongitud(solicitud.UnidadOrigen),
                        UnidadMapper.ToLongitud(solicitud.UnidadDestino)),

                Categoria.MASA => servicioMasa.Convertir(valor,
                        UnidadMapper.ToMasa(solicitud.UnidadOrigen),
                        UnidadMapper.ToMasa(solicitud.UnidadDestino)),

                Categoria.TEMPERATURA => servicioTemperatura.Convertir(valor,
                        UnidadMapper.ToTemperatura(solicitud.UnidadOrigen),
                        UnidadMapper.ToTemperatura(solicitud.UnidadDestino)),

                _ => throw new ExcepcionNegocio(CodigosRespuesta.DATOS_INCONSISTENTES, "Categoria inexistente: " + solicitud.Categoria)
            };

            return new RespuestaConversion
            {
                Estado = CodigosRespuesta.ESTADO_OK,
                Codigo = CodigosRespuesta.EXITO,
                Mensaje = "Conversion realizada correctamente",
                Resultado = resultado,
                UnidadOrigen = solicitud.UnidadOrigen.Trim().ToUpperInvariant(),
                UnidadDestino = solicitud.UnidadDestino.Trim().ToUpperInvariant()
            };
        }

        private void ValidarUnidad(Categoria categoria, string unidad, string rol)
        {
            Categoria? categoriaDeUnidad = servicioCatalogo.BuscarCategoriaDeUnidad(unidad);

            if (categoriaDeUnidad == null)
            {
                throw new ExcepcionNegocio(CodigosRespuesta.UNIDAD_NO_SOPORTADA, "Unidad de " + rol + " no soportada: " + unidad);
            }
            if (categoriaDeUnidad != categoria)
            {
                throw new ExcepcionNegocio(CodigosRespuesta.DATOS_INCONSISTENTES,
                        "La unidad de " + rol + " " + unidad + " no pertenece a la categoria " + categoria);
            }
        }
    }
}
