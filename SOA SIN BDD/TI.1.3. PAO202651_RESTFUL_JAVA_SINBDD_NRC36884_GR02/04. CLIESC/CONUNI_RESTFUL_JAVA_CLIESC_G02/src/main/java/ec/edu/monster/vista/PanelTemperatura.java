package ec.edu.monster.vista;

import ec.edu.monster.modelo.ConversionResponse;
import java.awt.Color;

/** Panel de conversiones de temperatura (Monsters Inc.). */
public class PanelTemperatura extends PanelConversionBase {

    public PanelTemperatura() {
        super("CONVERSIONES DE TEMPERATURA", new Color(33, 169, 218), new String[][] {
            {"Celsius → Fahrenheit", "celsiusAFahrenheit"}, {"Fahrenheit → Celsius", "fahrenheitACelsius"},
            {"Celsius → Kelvin", "celsiusAKelvin"},         {"Kelvin → Celsius", "kelvinACelsius"},
            {"Fahrenheit → Kelvin", "fahrenheitAKelvin"},   {"Kelvin → Fahrenheit", "kelvinAFahrenheit"}
        });
    }

    @Override
    protected ConversionResponse ejecutar(String operacion, double valor) {
        return clienteREST.convertirTemperatura(operacion, valor);
    }
}
