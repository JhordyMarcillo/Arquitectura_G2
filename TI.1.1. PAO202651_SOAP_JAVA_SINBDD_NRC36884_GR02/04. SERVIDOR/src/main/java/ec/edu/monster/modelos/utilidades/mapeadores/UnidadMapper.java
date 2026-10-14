package ec.edu.monster.modelos.utilidades.mapeadores;

import ec.edu.monster.modelos.utilidades.ExcepcionValidacion;
import ec.edu.monster.modelos.utilidades.enums.CodigoRespuesta;
import ec.edu.monster.modelos.utilidades.enums.CategoriaUnidad;
import ec.edu.monster.modelos.utilidades.enums.UnidadTemperatura;
import ec.edu.monster.modelos.utilidades.enums.UnidadMasa;
import ec.edu.monster.modelos.utilidades.enums.UnidadLongitud;

public class UnidadMapper {

    public static UnidadLongitud toLongitud(String unidad) {
        try {
            return UnidadLongitud.valueOf(unidad.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ExcepcionValidacion(CodigoRespuesta.UNIDAD_NO_SOPORTADA,
                    "Unidad de longitud no soportada: " + unidad);
        }
    }

    public static UnidadMasa toMasa(String unidad) {
        try {
            return UnidadMasa.valueOf(unidad.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ExcepcionValidacion(CodigoRespuesta.UNIDAD_NO_SOPORTADA,
                    "Unidad de masa no soportada: " + unidad);
        }
    }

    public static UnidadTemperatura toTemperatura(String unidad) {
        try {
            return UnidadTemperatura.valueOf(unidad.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ExcepcionValidacion(CodigoRespuesta.UNIDAD_NO_SOPORTADA,
                    "Unidad de temperatura no soportada: " + unidad);
        }
    }

    public static CategoriaUnidad toCategoria(String categoria) {
        if (categoria == null || categoria.isBlank()) {
            throw new ExcepcionValidacion(CodigoRespuesta.DATOS_INCONSISTENTES,
                    "La categoria es obligatoria");
        }
        try {
            return CategoriaUnidad.valueOf(categoria.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ExcepcionValidacion(CodigoRespuesta.DATOS_INCONSISTENTES,
                    "Categoria inexistente: " + categoria);
        }
    }
}
