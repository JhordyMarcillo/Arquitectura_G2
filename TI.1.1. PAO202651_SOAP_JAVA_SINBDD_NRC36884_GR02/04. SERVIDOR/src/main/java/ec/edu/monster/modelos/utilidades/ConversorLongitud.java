package ec.edu.monster.modelos.utilidades;

import ec.edu.monster.modelos.utilidades.enums.UnidadLongitud;
import java.util.Map;

public class ConversorLongitud implements IConversor<UnidadLongitud> {

    private final Map<UnidadLongitud, Double> factores = Map.of(
        UnidadLongitud.METRO, 1.0,
        UnidadLongitud.KILOMETRO, 1000.0,
        UnidadLongitud.CENTIMETRO, 0.01,
        UnidadLongitud.MILIMETRO, 0.001,
        UnidadLongitud.MILLA, 1609.344,
        UnidadLongitud.PIE, 0.3048
    );

    public ConversorLongitud() {}

    @Override
    public double convertir(double valor, UnidadLongitud unidadOrigen, UnidadLongitud unidadFinal) {

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
