package ec.edu.monster.modelos.utilidades.enums;

import java.util.Arrays;
import java.util.List;

public enum CategoriaUnidad {
    LONGITUD(UnidadLongitud.values()),
    MASA(UnidadMasa.values()),
    TEMPERATURA(UnidadTemperatura.values());

    private final List<String> unidades;

    CategoriaUnidad(Enum<?>[] unidades) {
        this.unidades = Arrays.stream(unidades).map(Enum::name).toList();
    }

    public List<String> getUnidades() {
        return unidades;
    }

    public boolean contieneUnidad(String unidad) {
        return unidades.contains(unidad);
    }
}
