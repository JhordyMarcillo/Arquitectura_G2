package ec.edu.monster.servicios;

import ec.edu.monster.modelos.utilidades.enums.CategoriaUnidad;
import java.util.Arrays;
import java.util.List;

public class ServicioCatalogo {

    public List<String> obtenerCategorias() {
        return Arrays.stream(CategoriaUnidad.values())
                .map(Enum::name)
                .toList();
    }

    public List<String> obtenerUnidades(CategoriaUnidad categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("La categoria no puede ser null");
        }
        return categoria.getUnidades();
    }
}
