package ec.edu.monster.modelos.utilidades;

import ec.edu.monster.modelos.utilidades.enums.UnidadTemperatura;
import static ec.edu.monster.modelos.utilidades.enums.UnidadTemperatura.CELSIUS;
import static ec.edu.monster.modelos.utilidades.enums.UnidadTemperatura.FAHRENHEIT;
import static ec.edu.monster.modelos.utilidades.enums.UnidadTemperatura.KELVIN;

public class ConversorTemperatura implements IConversor<UnidadTemperatura> {

    public ConversorTemperatura() {}

    @Override
    public double convertir(double valor, UnidadTemperatura origen, UnidadTemperatura destino) {

        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Las unidades no pueden ser null");
        }
        if (origen == destino) {
            return valor;
        }

        return switch (origen) {

            case CELSIUS -> switch (destino) {
                case FAHRENHEIT -> (valor * 9 / 5) + 32;
                case KELVIN -> valor + 273.15;
                default -> throw new IllegalArgumentException("Conversion no soportada");
            };

            case FAHRENHEIT -> switch (destino) {
                case CELSIUS -> (valor - 32) * 5 / 9;
                case KELVIN -> (valor - 32) * 5 / 9 + 273.15;
                default -> throw new IllegalArgumentException("Conversion no soportada");
            };

            case KELVIN -> switch (destino) {
                case CELSIUS -> valor - 273.15;
                case FAHRENHEIT -> (valor - 273.15) * 9 / 5 + 32;
                default -> throw new IllegalArgumentException("Conversion no soportada");
            };
        };
    }
}
