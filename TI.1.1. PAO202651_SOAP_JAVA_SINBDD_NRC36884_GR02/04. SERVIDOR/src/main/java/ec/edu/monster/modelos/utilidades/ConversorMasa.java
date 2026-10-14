package ec.edu.monster.modelos.utilidades;

import ec.edu.monster.modelos.utilidades.enums.UnidadMasa;
import java.util.Map;

public class ConversorMasa implements IConversor<UnidadMasa> {

    private final Map<UnidadMasa, Double> factores = Map.of(
        UnidadMasa.KILOGRAMO, 1.0,
        UnidadMasa.GRAMO, 0.001,
        UnidadMasa.MILIGRAMO, 0.000001,
        UnidadMasa.LIBRA, 0.45359237
    );

    public ConversorMasa() {}

    @Override
    public double convertir(double valor, UnidadMasa unidadOrigen, UnidadMasa unidadFinal) {

        if (unidadOrigen == null || unidadFinal == null) {
            throw new IllegalArgumentException("Las unidades no pueden ser null");
        }
        if (!factores.containsKey(unidadOrigen) || !factores.containsKey(unidadFinal)) {
            throw new IllegalArgumentException("Unidad no soportada");
        }

        double valorEnBase = valor * factores.get(unidadOrigen);

        return valorEnBase / factores.get(unidadFinal);
    }
}
