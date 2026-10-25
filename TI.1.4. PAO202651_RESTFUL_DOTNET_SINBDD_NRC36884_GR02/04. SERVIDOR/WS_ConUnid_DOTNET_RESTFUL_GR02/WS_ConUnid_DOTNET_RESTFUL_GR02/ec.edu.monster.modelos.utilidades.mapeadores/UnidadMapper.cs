using ec.edu.monster.modelos.utilidades.enums;

namespace ec.edu.monster.modelos.utilidades.mapeadores
{
    public static class UnidadMapper
    {
        public static UnidadLongitud ToLongitud(string unidad)
        {
            return Mapear<UnidadLongitud>(unidad, "Unidad de longitud no soportada: ");
        }

        public static UnidadMasa ToMasa(string unidad)
        {
            return Mapear<UnidadMasa>(unidad, "Unidad de masa no soportada: ");
        }

        public static UnidadTemperatura ToTemperatura(string unidad)
        {
            return Mapear<UnidadTemperatura>(unidad, "Unidad de temperatura no soportada: ");
        }

        private static T Mapear<T>(string unidad, string mensaje) where T : struct, Enum
        {
            string nombre = unidad.Trim().ToUpperInvariant();

            if (!Enum.GetNames<T>().Contains(nombre))
            {
                throw new ExcepcionNegocio(CodigosRespuesta.UNIDAD_NO_SOPORTADA, mensaje + unidad);
            }
            return Enum.Parse<T>(nombre);
        }
    }
}
