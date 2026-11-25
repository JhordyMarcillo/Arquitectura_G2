package ec.edu.monster.modelos.utilidades;

import ec.edu.monster.modelos.utilidades.enums.CategoriaUnidad;
import ec.edu.monster.modelos.utilidades.enums.CodigoRespuesta;
import ec.edu.monster.modelos.utilidades.enums.UnidadTemperatura;

public final class ValidadorConversion {

    private ValidadorConversion() {
    }

    public static void validarValor(Double valor) {
        if (valor == null) {
            throw new ExcepcionValidacion(CodigoRespuesta.DATOS_INCONSISTENTES, "El valor es obligatorio");
        }
        if (valor.isNaN() || valor.isInfinite()) {
            throw new ExcepcionValidacion(CodigoRespuesta.DATOS_INCONSISTENTES, "El valor no es un numero valido");
        }
    }

    public static void validarUnidad(String unidad, String posicion, CategoriaUnidad categoria) {
        if (unidad == null || unidad.isBlank()) {
            throw new ExcepcionValidacion(CodigoRespuesta.DATOS_INCONSISTENTES,
                    "La unidad de " + posicion + " es obligatoria");
        }

        String nombre = unidad.trim().toUpperCase();

        if (categoria.contieneUnidad(nombre)) {
            return;
        }

        for (CategoriaUnidad otraCategoria : CategoriaUnidad.values()) {
            if (otraCategoria.contieneUnidad(nombre)) {
                throw new ExcepcionValidacion(CodigoRespuesta.DATOS_INCONSISTENTES,
                        "Unidades de categorias incompatibles: " + nombre + " pertenece a "
                        + otraCategoria + " y no a " + categoria);
            }
        }

        throw new ExcepcionValidacion(CodigoRespuesta.UNIDAD_NO_SOPORTADA,
                "Unidad de " + posicion + " no soportada: " + unidad);
    }

    public static void validarRango(double valor, CategoriaUnidad categoria, String unidadOrigen) {
        switch (categoria) {
            case LONGITUD, MASA -> {
                if (valor < 0) {
                    throw new ExcepcionValidacion(CodigoRespuesta.DATOS_INCONSISTENTES,
                            "El valor no puede ser negativo en la categoria " + categoria);
                }
            }
            case TEMPERATURA -> {
                double kelvin = new ConversorTemperatura().convertir(valor,
                        UnidadTemperatura.valueOf(unidadOrigen.trim().toUpperCase()),
                        UnidadTemperatura.KELVIN);
                if (kelvin < -1e-9) {
                    throw new ExcepcionValidacion(CodigoRespuesta.DATOS_INCONSISTENTES,
                            "La temperatura no puede ser menor al cero absoluto");
                }
            }
        }
    }
}
